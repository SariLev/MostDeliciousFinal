package com.example.mostdelicious.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserRegisterForm(
    val email: String,
    val password: String,
    val fullName: String
)