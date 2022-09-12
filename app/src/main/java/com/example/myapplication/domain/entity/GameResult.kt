package com.example.myapplication.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class GameResult(
    val winner: Boolean, //выводим смайлик грустный или веселый
    val countOfRightAnswers: Int, //выводим результат правильно отвеченных вопросов
    val countOfQuestions: Int, //колво вопросов
    val gameSettings: GameSettings //из настроек игры получим, мин количество правильных вопросов
) : Parcelable