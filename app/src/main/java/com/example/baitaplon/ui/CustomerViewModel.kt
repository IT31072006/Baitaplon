package com.example.baitaplon.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baitaplon.data.remote.CartItem
import com.example.baitaplon.data.remote.CustomerApiService
import com.example.baitaplon.data.remote.Dish
import com.example.baitaplon.data.remote.ReservationRequest
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class CustomerUiState(
    val dishes: List<Dish> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedDish: Dish? = null,
    val isReservationSuccess: Boolean = false
)

class CustomerViewModel : ViewModel() {
    private val _uiState = mutableStateOf(CustomerUiState())
    val uiState: State<CustomerUiState> = _uiState

    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> = _cartItems

    private val apiService: CustomerApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/api/") // Cập nhật cổng 8080
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CustomerApiService::class.java)
    }

    init {
        fetchDishes()
    }

    fun fetchDishes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = apiService.getDishes()
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        dishes = response.body() ?: emptyList(),
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Không thể tải danh sách món ăn: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Đã xảy ra lỗi kết nối"
                )
            }
        }
    }

    fun selectDish(dish: Dish?) {
        _uiState.value = _uiState.value.copy(selectedDish = dish)
    }

    fun addToCart(dish: Dish, quantity: Int, note: String) {
        val existingItem = _cartItems.find { it.dish.id == dish.id }
        if (existingItem != null) {
            existingItem.quantity += quantity
            existingItem.note = note
        } else {
            _cartItems.add(CartItem(dish, quantity, note))
        }
        selectDish(null)
    }

    fun removeFromCart(cartItem: CartItem) {
        _cartItems.remove(cartItem)
    }

    fun makeReservation() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val total = _cartItems.sumOf { it.dish.price * it.quantity }
                val request = ReservationRequest(
                    items = _cartItems.toList(),
                    totalAmount = total,
                    customerId = "current_user_id"
                )
                val response = apiService.makeReservation(request)
                if (response.isSuccessful) {
                    _cartItems.clear()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isReservationSuccess = true
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Đặt bàn thất bại: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Đã xảy ra lỗi khi đặt bàn"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun resetReservationStatus() {
        _uiState.value = _uiState.value.copy(isReservationSuccess = false)
    }
}
