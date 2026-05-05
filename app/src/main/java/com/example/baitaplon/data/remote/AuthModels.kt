package com.example.baitaplon.data.remote

data class LoginRequest(
    val email: String,
    val password: String,
    val role: String
)

data class LoginResponse(
    val userId: Int? = null,
    val email: String? = null,
    val token: String? = null,
    val role: String? = null,
    val message: String? = null,
    val status: String? = null
)
