package com.example.myapplication.domain.usecases

import com.example.myapplication.domain.entity.GameSettings
import com.example.myapplication.domain.entity.Level
import com.example.myapplication.domain.repository.GameRepository

class GetGameSettingsUseCase(private val repository: GameRepository) {

    operator fun invoke(level: Level): GameSettings{
        return repository.getGameSettings(level)
    }
}