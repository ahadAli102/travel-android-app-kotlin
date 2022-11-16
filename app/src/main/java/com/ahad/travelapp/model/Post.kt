package com.ahad.travelapp.model

import java.io.Serializable

data class Post(var id:String,
                val location:String,
                val description:String,
                val images:List<String>,
                val videos:List<String>,
                val time:Long,
                val user: User
):Serializable
