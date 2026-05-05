package com.example.baitaplon.data.remote

data class Dish(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val category: String
)

data class CartItem(
    val dish: Dish,
    var quantity: Int,
    var note: String = ""
)

data class ReservationRequest(
    val items: List<CartItem>,
    val totalAmount: Double,
    val customerId: String
)
