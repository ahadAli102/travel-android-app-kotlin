package com.ahad.travelapp.util

sealed class AuthResponse<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : AuthResponse<T>(data)

    class Error<T>(message: String, data: T? = null) : AuthResponse<T>(data, message)

    class Loading<T> : AuthResponse<T>()
}