package com.example.baitaplon.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApiService {
    @POST("auth_api.php")
    suspend fun login(
        @Query("action") action: String = "login",
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("auth_api.php")
    suspend fun register(
        @Query("action") action: String = "register",
        @Body request: LoginRequest
    ): Response<LoginResponse>
}
