package com.ahad.travelapp.util

sealed class Constant{
    class User{
        companion object{
            const val NAME = "name"
            const val EMAIL = "email"
            const val IMAGE_URL = "image_url"
        }
    }
    class Post{
        companion object{
            const val LOCATION = "location"
            const val DESCRIPTION = "description"
            const val IMAGES = "images"
            const val VIDEOS = "videos"
            const val TIME = "time"
            const val USER_ID = "userId"
        }
    }
    class Comment{
        companion object{
            const val COMMENT = "comment"
            const val TIME = "time"
            const val USER_NAME = "userName"
            const val USER_IMAGE = "userImage"
        }
    }

}
