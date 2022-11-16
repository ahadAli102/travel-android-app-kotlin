package com.ahad.travelapp.util

sealed class MainResponse<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : MainResponse<T>(data)

    class Error<T>(message: String, data: T? = null) : MainResponse<T>(data, message)

    class Loading<T> : MainResponse<T>()
}