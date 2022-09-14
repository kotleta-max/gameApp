package com.example.myapplication.presentation

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.R
import com.example.myapplication.data.GameRepositoryImpl
import com.example.myapplication.domain.entity.GameResult
import com.example.myapplication.domain.entity.GameSettings
import com.example.myapplication.domain.entity.Level
import com.example.myapplication.domain.entity.Question
import com.example.myapplication.domain.usecases.GenerateQuestionUseCase
import com.example.myapplication.domain.usecases.GetGameSettingsUseCase

class GameViewModel(application: Application) : AndroidViewModel(application) {

    /*Аннотация
    При запуске экрана мы стартуем игру startGame()
    Получаем настройки игры getGameSettings()
    Запускаем таймер startTimer()
    И сгенерируется вопрос generateQuestion()

    Из фрагмента можно подписать на этот таймер - val formattedTime: LiveData<String>
    На вопрос -  val question: LiveData<Question>
    Также отобразим %правильных ответов, который будет отображаться в прогресс баре - val percentOfRightAnswers: LiveData<Int>
    Прогресс с ответами, строка в которой отображается какое количество правильных ответов и какое колво их должно быть мин -  val progressAnswers: LiveData<String>
    И две лайв даты, меняющие цвет бара*/

    private lateinit var level: Level
    private lateinit var gameSettings: GameSettings

    private var timer: CountDownTimer? = null
    private val repository = GameRepositoryImpl

    //получаем context
    private val context = application

    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)

    //создаем лайвдату которая будет хранить отформатированное время, полученное с таймера в строке
    private val _formattedTime = MutableLiveData<String>()
    val formattedTime: LiveData<String>
        get() = _formattedTime

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question>
        get() = _question

    private val _percentOfRightAnswers = MutableLiveData<Int>()
    val percentOfRightAnswers: LiveData<Int>
        get() = _percentOfRightAnswers

    private val _progressAnswers = MutableLiveData<String>()
    val progressAnswers: LiveData<String>
        get() = _progressAnswers

    //создаем две лайвдаты благодаря которым будет менять цвет, которые будут хранить достаточное количество правильных ответов и достаточный процент правильных ответов
    private val _enoughCountOfRightAnswers = MutableLiveData<Boolean>()
    val enoughCountOfRightAnswers: LiveData<Boolean>
        get() = _enoughCountOfRightAnswers

    private val _enoughPercentOfRightAnswers = MutableLiveData<Boolean>()
    val enoughPercentOfRightAnswers: LiveData<Boolean>
        get() = _enoughPercentOfRightAnswers

    //создаем лайв дату которая отображает серым прогресс который нужно достичь по ответам(становится зеленым) или не достичь(красным), и получаем это значение в тот момент когда загружаем настройки getGameSettings
    private val _minPercent = MutableLiveData<Int>()
    val minPercent: LiveData<Int>
        get() = _minPercent

    //В конце чтобы завершить игру, сформируем объект gameResult на который мы подпишемся из фрагмента, и когда он туда прилетит мы откроем экран с завершением игры
    private val _gameResult = MutableLiveData<GameResult>()
    val gameResult: LiveData<GameResult>
        get() = _gameResult

    private var countOfRightAnswers = 0
    private var countOfQuestions = 0


    //при вызове этого метода у нас во вьюмодели будут настройки игры и сам уровень
    fun startGame(level: Level) {
        getGameSettings(level)
        startTimer()
        generateQuestion()
        updateProgress()
    }

    //метод отображащий прогресс
    private fun updateProgress() {
        //получаем строку с колвом правильных ответов
        val percent = calculatePercentOfRightAnswers()
        _percentOfRightAnswers.value = percent
        /*далее получаем строку с прогрессом по ответам(для этого нужно получить эту строку из
        строковых ресурсов, для этого нужен context во viewmodel ==> наследуемся от androidviewmodel)*/
        _progressAnswers.value = String.format(
            context.resources.getString(R.string.progress_answers),
            countOfRightAnswers,
            gameSettings.minCountOfRightAnswers
        )
        _enoughCountOfRightAnswers.value = countOfRightAnswers >= gameSettings.minCountOfRightAnswers
        _enoughPercentOfRightAnswers.value = percent >= gameSettings.minPercentOfRightAnswers

    }

    //cоздаем метод который вычисляет прогресс ( в нем приводим одно число к типу double, иначе результатом всегда будет 0 либо 1)
    private fun calculatePercentOfRightAnswers(): Int {
        if (countOfQuestions == 0) {
            return 0
        }
        return ((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()
    }

    private fun getGameSettings(level: Level) {
        this.level = level
        this.gameSettings = getGameSettingsUseCase(level)
        //присваем значение secondary бару
        _minPercent.value = gameSettings.minPercentOfRightAnswers
    }

    //далее когда мы получили настройки игры запускаем таймер из вьюмодели (1 параметр, общее время отсчета, 2 интервал)
    private fun startTimer() {
        timer = object : CountDownTimer(
            gameSettings.gameTimeInSeconds * MILLIS_IN_SECONDS,
            MILLIS_IN_SECONDS
        ) {
            //в этом методе нужно привести кол-во миллисекунд в норм вид и отправить это значение в переменную formattedtime
            override fun onTick(millisUntilFinished: Long) {
                _formattedTime.value = formatTime(millisUntilFinished)
            }

            override fun onFinish() {
                finishGame()
            }
        }
        timer?.start()
    }

    //создаем метод генерирующий вопрос
    private fun generateQuestion() {
        _question.value = generateQuestionUseCase(gameSettings.maxSumValue)
    }

    //создаем метод позволяютщий отвечать на вопросы, куда будем передавать выбранный вариант ответа
    fun chooseAnswer(number: Int) {
        checkAnswer(number)
        //обновляем прогресс после каждого ответа
        updateProgress()
        //далее нужно сгенерировать след вопрос
        generateQuestion()

    }

    //вынесем проверку правильного ответа в отдельный метод из метода chooseQuestion()
    private fun checkAnswer(number: Int) {
        //получаем правильный ответ из объекта question
        val rightAnswer = question.value?.rightAnswer
        if (number == rightAnswer) {
            countOfRightAnswers++
        }
        countOfQuestions++
    }

    //создаем метод, который будет форматировать время
    private fun formatTime(millisUntilFinished: Long): String {
        val seconds = millisUntilFinished / MILLIS_IN_SECONDS
        val minutes = seconds / SECONDS_IN_MINUTES
        val leftSeconds = seconds - (minutes * SECONDS_IN_MINUTES)
        return String.format("%02d:%02d", minutes, leftSeconds)
    }

    //создает метод с завершением игры
    private fun finishGame() {
        _gameResult.value = GameResult(
            winner = enoughCountOfRightAnswers.value == true && enoughPercentOfRightAnswers.value == true,
            countOfRightAnswers = countOfRightAnswers,
            countOfQuestions = countOfQuestions,
            gameSettings = gameSettings
        )
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

    companion object {
        private const val MILLIS_IN_SECONDS = 1000L
        private const val SECONDS_IN_MINUTES = 60
    }
}