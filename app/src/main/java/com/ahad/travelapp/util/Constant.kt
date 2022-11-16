package com.ahad.travelapp.util

sealed class Constant{
    class User{
        companion object{
            const val NAME = "name"
            const val EMAIL = "email"
            const val IMAGE_URL = "image_url"
        }
    }
}
