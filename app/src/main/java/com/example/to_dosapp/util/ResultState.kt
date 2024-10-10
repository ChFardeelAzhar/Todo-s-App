package com.example.to_dosapp.util

sealed class ResultState<out T> {

    data class Success<R>(val data: R) : ResultState<R>()
    data class Failure(val error: Throwable) : ResultState<Nothing>()
    object Loading : ResultState<Nothing>()

}
