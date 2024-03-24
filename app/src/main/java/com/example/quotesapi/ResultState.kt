package com.example.quotesapi

sealed class ResultState<out T>(loading: Loading) {
    object Loading:ResultState<Nothing>(Loading)
    data class Success<T>(val repository: T):ResultState<T>(Loading)
    data class Error(val error: Throwable):ResultState<Nothing>(Loading)
}