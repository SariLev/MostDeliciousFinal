package com.example.mostdelicious.dto

import android.net.Uri

data class PostDto(
    val id: String? = null,
    val uri: Uri? = null,
    val mealName: String? = null,
    val content:String? = null
)