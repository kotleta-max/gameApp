package com.example.myapplication.data

import com.example.myapplication.domain.entity.GameSettings
import com.example.myapplication.domain.entity.Level
import com.example.myapplication.domain.entity.Question
import com.example.myapplication.domain.repository.GameRepository
import java.lang.Integer.max
import kotlin.math.min
import kotlin.random.Random

object GameRepositoryImpl : GameRepository {

    private const val MIN_SUM_VALUE = 2
    private const val MIN_ANSWER_VALUE = 1

    override fun generateQuestion(maxSumValue: Int, countOfOptions: Int): Question {
        //генерируем значение суммы
        val sum = Random.nextInt(MIN_SUM_VALUE, maxSumValue + 1)
        //далее получаем значение видимого числа, слева в квадрате, до sum(невключительно)
        val visibleNumber = Random.nextInt(MIN_ANSWER_VALUE, sum)
        //далее генерируем варианты ответов. Чтобы среди вариантов ответа не было одинаковых значений нужно использовать hashSet
        val options = HashSet<Int>()
        //получаем значение правильного ответа и кладем его в коллекцию options
        val rightAnswer = sum - visibleNumber
        options.add(rightAnswer)
        //далее до тех пор пока количество вариантов options не равно countOfOptions, нужно генерировать варианты ответов
        val from = max(rightAnswer - countOfOptions, MIN_ANSWER_VALUE) //если разность будет меньше 1(правильный ответ будет 4 например), то будет использовано минимальное значение
        val to = min(maxSumValue, rightAnswer + countOfOptions)
        while (options.size < countOfOptions) {
            options.add(Random.nextInt(from, to))
        }
        return Question(sum, visibleNumber, options.toList())
    }

    override fun getGameSettings(level: Level): GameSettings {
        //Приемущество использования enum в красоте ниже
        return when (level) {
            Level.TEST -> {
                GameSettings(10, 3, 50, 8)
            }
            Level.EASY -> {
                GameSettings(10, 10, 70, 60)
            }
            Level.NORMAL -> {
                GameSettings(20, 20, 80, 40)
            }
            Level.HARD -> {
                GameSettings(40, 40, 90, 20)
            }
        }
    }
}

