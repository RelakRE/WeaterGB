package ru.GB.weathergb.viewmodel

sealed class BasicAppState {
    object DefaultState : BasicAppState()
    object LoadingState : BasicAppState()
    class Success<out T>(val data: List<T>) : BasicAppState()
    object ErrorState : BasicAppState()
}