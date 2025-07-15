package ru.chantreck.myapplication.model

enum class ExecutionState {
    IDLE,
    RUNNING,
    SUCCESS,
    ERROR
}

sealed class ExecutionResult {
    data class Success(val message: String) : ExecutionResult()
    data class Error(val exception: Exception) : ExecutionResult()
}

