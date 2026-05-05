package com.example.baitaplon.ui

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baitaplon.data.remote.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream

data class AdminUiState(
    val dishes: List<AdminDish> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class AdminViewModel : ViewModel() {
    private val _uiState = mutableStateOf(AdminUiState())
    val uiState: State<AdminUiState> = _uiState

    private val _designTables = mutableStateListOf<TableDesign>()
    val designTables: List<TableDesign> = _designTables

    private val apiService: AdminApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AdminApiService::class.java)
    }

    fun fetchDishes(restaurantId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val response = apiService.getDishes(restaurantId)
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(dishes = response.body() ?: emptyList(), isLoading = false)
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to load dishes")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.localizedMessage)
            }
        }
    }

    fun saveDish(
        context: Context,
        id: String?,
        restaurantId: String,
        name: String,
        price: Double,
        description: String,
        imageUri: Uri?,
        existingImageUrl: String?
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val idPart = id?.toRequestBody("text/plain".toMediaTypeOrNull())
                val resIdPart = restaurantId.toRequestBody("text/plain".toMediaTypeOrNull())
                val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
                val pricePart = price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val descPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
                val existingImagePart = existingImageUrl?.toRequestBody("text/plain".toMediaTypeOrNull())

                var imagePart: MultipartBody.Part? = null
                imageUri?.let { uri ->
                    val file = uriToFile(context, uri)
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
                }

                val response = apiService.saveDish(
                    idPart, resIdPart, namePart, pricePart, descPart, existingImagePart, imagePart
                )
                
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(isLoading = false, successMessage = "Đã lưu món ăn")
                    fetchDishes(restaurantId)
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Lỗi: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.localizedMessage)
            }
        }
    }

    fun deleteDish(id: String, restaurantId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.deleteDish(id)
                if (response.isSuccessful) {
                    fetchDishes(restaurantId)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.localizedMessage)
            }
        }
    }

    fun toggleTable(restaurantId: String, x: Int, y: Int, capacity: Int) {
        val existing = _designTables.find { it.pos_x == x && it.pos_y == y }
        if (existing != null) {
            _designTables.remove(existing)
        } else {
            val code = "B${x}${y}"
            _designTables.add(TableDesign(restaurant_id = restaurantId, table_code = code, capacity = capacity, pos_x = x, pos_y = y))
        }
    }

    fun saveTables(restaurantId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                apiService.saveTableLayout(TableSaveRequest(restaurantId, _designTables.toList()))
                _uiState.value = _uiState.value.copy(isLoading = false, successMessage = "Đã lưu sơ đồ bàn")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.localizedMessage)
            }
        }
    }

    fun fetchTables(restaurantId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getTables(restaurantId)
                if (response.isSuccessful) {
                    _designTables.clear()
                    _designTables.addAll(response.body() ?: emptyList())
                }
            } catch (e: Exception) { }
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output -> input.copyTo(output) }
        }
        return file
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }
}
