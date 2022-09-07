package com.example.myapplication.domain.repository

import com.example.myapplication.domain.entity.GameSettings
import com.example.myapplication.domain.entity.Level
import com.example.myapplication.domain.entity.Question

interface GameRepository  {

    fun generateQuestion(
        maxSumValue:Int,
        countOfOptions:Int
    ): Question

    fun getGameSettings(level: Level):GameSettings
}