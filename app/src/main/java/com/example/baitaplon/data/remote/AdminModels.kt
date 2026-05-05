package com.example.baitaplon.data.remote

data class Restaurant(
    val id: String? = null,
    val name: String,
    val address: String,
    val image_url: String? = null,
    val description: String,
    val open_time: String = "08:00:00",
    val close_time: String = "22:00:00"
)

data class AdminDish(
    val id: String? = null,
    val restaurant_id: String,
    val name: String,
    val price: Double,
    val image_url: String? = null,
    val description: String
)

data class TableDesign(
    val id: String? = null,
    val restaurant_id: String,
    val table_code: String,
    val capacity: Int,
    val pos_x: Int,
    val pos_y: Int
)

data class TableSaveRequest(
    val restaurant_id: String,
    val tables: List<TableDesign>
)

data class AdminResponse(val status: String, val message: String? = null)
