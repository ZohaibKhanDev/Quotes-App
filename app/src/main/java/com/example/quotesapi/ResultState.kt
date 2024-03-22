package com.example.quotesapi

import java.lang.Error

sealed class ResultState<out T> {
    object Loading:ResultState<Nothing>()
    data class Success<T>(val repository: T):ResultState<T>()
    data class Error(val error: Throwable):ResultState<Nothing>()
}