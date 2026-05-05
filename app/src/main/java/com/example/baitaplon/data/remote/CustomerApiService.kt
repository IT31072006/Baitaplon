package com.example.baitaplon.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CustomerApiService {
    @GET("dishes")
    suspend fun getDishes(): Response<List<Dish>>

    @POST("reservations")
    suspend fun makeReservation(@Body request: ReservationRequest): Response<Unit>
}
