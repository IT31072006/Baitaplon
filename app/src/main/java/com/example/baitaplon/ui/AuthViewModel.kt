package com.example.baitaplon.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baitaplon.data.remote.AuthApiService
import com.example.baitaplon.data.remote.LoginRequest
import com.example.baitaplon.data.remote.LoginResponse
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

enum class UserRole {
    CUSTOMER, ADMIN
}

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val role: UserRole = UserRole.CUSTOMER,
    val isLoading: Boolean = false,
    val loginResponse: LoginResponse? = null,
    val error: String? = null
)

class AuthViewModel : ViewModel() {
    private val _uiState = mutableStateOf(AuthUiState())
    val uiState: State<AuthUiState> = _uiState

    private val apiService: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/api/") // Đã thêm cổng 8080
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun onRoleChange(role: UserRole) {
        _uiState.value = _uiState.value.copy(role = role)
    }

    fun login() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val roleString = if (_uiState.value.role == UserRole.ADMIN) "ADMIN" else "CUSTOMER"
                val response = apiService.login(
                    LoginRequest(
                        email = _uiState.value.email,
                        password = _uiState.value.password,
                        role = roleString
                    )
                )
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        loginResponse = response.body()
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Sai tài khoản hoặc Role không đúng"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Lỗi kết nối: ${e.localizedMessage}")
            }
        }
    }

    fun register() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // Luôn register thành CUSTOMER (server sẽ force role = CUSTOMER)
                val response = apiService.register(
                    LoginRequest(
                        email = _uiState.value.email,
                        password = _uiState.value.password,
                        role = "CUSTOMER" // Không quan trọng, server sẽ override
                    )
                )
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        loginResponse = response.body() // Để LaunchedEffect ở RegisterScreen nhận biết thành công
                    )
                } else {
                    val errorMsg = response.body()?.message ?: "Email đã tồn tại hoặc lỗi server"
                    _uiState.value = _uiState.value.copy(isLoading = false, error = errorMsg)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Lỗi kết nối: ${e.localizedMessage}")
            }
        }
    }
    
    fun resetState() {
        _uiState.value = _uiState.value.copy(loginResponse = null, error = null)
    }
}
