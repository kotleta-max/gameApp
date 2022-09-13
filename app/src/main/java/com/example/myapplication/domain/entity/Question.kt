package com.example.myapplication.domain.entity

data class Question(
    val sum: Int,
    val visibleNumber: Int,
    val options: List<Int>
) {
    //создаем правильный вариант ответа
    val rightAnswer: Int
    get() = sum - visibleNumber
}
