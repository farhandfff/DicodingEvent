package com.example.dicodingevent.data
sealed class Results<out R> {
    data class Success<out T>(val data: T) : Results<T>()
    data class Error(val error: String) : Results<Nothing>()
    data object Loading : Results<Nothing>()
}

