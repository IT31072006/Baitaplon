package com.example.baitaplon.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface AdminApiService {
    // Restaurant Management
    @GET("admin_api.php?action=list_restaurants")
    suspend fun getRestaurants(): Response<List<Restaurant>>

    @Multipart
    @POST("admin_api.php?action=save_restaurant")
    suspend fun saveRestaurant(
        @Part("id") id: RequestBody?,
        @Part("name") name: RequestBody,
        @Part("address") address: RequestBody,
        @Part("description") description: RequestBody,
        @Part("open_time") openTime: RequestBody,
        @Part("close_time") closeTime: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<AdminResponse>

    // Menu Management
    @GET("admin_api.php?action=list_dishes")
    suspend fun getDishes(@Query("restaurant_id") restaurantId: String): Response<List<AdminDish>>

    @Multipart
    @POST("admin_api.php?action=save_dish")
    suspend fun saveDish(
        @Part("id") id: RequestBody?,
        @Part("restaurant_id") restaurantId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("price") price: RequestBody,
        @Part("description") description: RequestBody,
        @Part("existing_image") existingImage: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Response<AdminResponse>

    @GET("admin_api.php?action=delete_dish")
    suspend fun deleteDish(@Query("id") id: String): Response<AdminResponse>

    // Table Layout Management
    @POST("admin_api.php?action=save_layout")
    suspend fun saveTableLayout(@Body request: TableSaveRequest): Response<AdminResponse>

    @GET("admin_api.php?action=get_layout")
    suspend fun getTables(@Query("restaurant_id") restaurantId: String): Response<List<TableDesign>>
}
