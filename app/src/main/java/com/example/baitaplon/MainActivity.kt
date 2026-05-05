package com.example.baitaplon

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.baitaplon.ui.AdminViewModel
import com.example.baitaplon.ui.AuthViewModel
import com.example.baitaplon.ui.CustomerViewModel
import com.example.baitaplon.ui.screens.AdminDashboard
import com.example.baitaplon.ui.screens.CustomerDashboard
import com.example.baitaplon.ui.screens.LoginScreen
import com.example.baitaplon.ui.screens.RegisterScreen
import com.example.baitaplon.ui.theme.BaiTapLonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BaiTapLonTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val customerViewModel: CustomerViewModel = viewModel()
    val adminViewModel: AdminViewModel = viewModel()
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate("register") },
                onLoginSuccess = {
                    val loginRes = authViewModel.uiState.value.loginResponse
                    val role = loginRes?.role ?: ""
                    
                    // DEBUG LOG
                    Log.d("LOGIN_DEBUG", "LoginResponse: $loginRes")
                    Log.d("LOGIN_DEBUG", "Role from response: '$role' (length=${role.length})")
                    
                    // So sánh không phân biệt hoa thường và xóa khoảng trắng thừa
                    if (role.trim().equals("ADMIN", ignoreCase = true)) {
                        Log.d("LOGIN_DEBUG", "Role matches ADMIN - navigating to admin_dashboard")
                        Toast.makeText(context, "Chào mừng Admin!", Toast.LENGTH_SHORT).show()
                        navController.navigate("admin_dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        Log.d("LOGIN_DEBUG", "Role does not match ADMIN: '$role' - navigating to customer_dashboard")
                        Toast.makeText(context, "Chào mừng Khách hàng!", Toast.LENGTH_SHORT).show()
                        navController.navigate("customer_dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                    authViewModel.resetState()
                }
            )
        }
        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.navigate("login") },
                onRegisterSuccess = {
                    Toast.makeText(context, "Đăng ký thành công! Hãy đăng nhập.", Toast.LENGTH_LONG).show()
                    navController.navigate("login")
                }
            )
        }
        composable("customer_dashboard") {
            CustomerDashboard(viewModel = customerViewModel)
        }
        composable("admin_dashboard") {
            // Mặc định ID nhà hàng là 1
            AdminDashboard(viewModel = adminViewModel, restaurantId = "1")
        }
    }
}
