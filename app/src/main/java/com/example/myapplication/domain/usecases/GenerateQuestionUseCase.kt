package com.example.myapplication.domain.usecases

import com.example.myapplication.domain.entity.Question
import com.example.myapplication.domain.repository.GameRepository

class GenerateQuestionUseCase(private val repository: GameRepository) {

    //Максимальное значение которое нужно сгенерировать, передаем в параметры
    operator fun invoke(maxSumValue: Int): Question {
        return repository.generateQuestion((maxSumValue, COUNT_OF_OPTIONS)

    }

    //создаем константу которая будет хранить количество вариантов ответов
    private companion object {
        private const val COUNT_OF_OPTIONS = 6
    }
}