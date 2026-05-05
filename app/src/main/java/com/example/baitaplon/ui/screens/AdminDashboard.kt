package com.example.baitaplon.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.baitaplon.data.remote.AdminDish
import com.example.baitaplon.ui.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(viewModel: AdminViewModel, restaurantId: String) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val uiState by viewModel.uiState
    val context = LocalContext.current

    // Hiển thị thông báo Toast khi có lỗi hoặc thành công từ ViewModel
    LaunchedEffect(uiState.error, uiState.successMessage) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
        uiState.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearSuccessMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý Nhà hàng", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.RestaurantMenu, contentDescription = null) },
                    label = { Text("Thực đơn") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.GridView, contentDescription = null) },
                    label = { Text("Sơ đồ bàn") }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (selectedTab) {
                0 -> MenuManagementScreen(viewModel, restaurantId)
                1 -> TableDesignScreen(viewModel, restaurantId)
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun MenuManagementScreen(viewModel: AdminViewModel, restaurantId: String) {
    val uiState by viewModel.uiState
    val context = LocalContext.current
    var showDishDialog by remember { mutableStateOf(false) }
    var selectedDish by remember { mutableStateOf<AdminDish?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchDishes(restaurantId)
    }

    val filteredDishes = uiState.dishes.filter { 
        it.name.contains(searchQuery, ignoreCase = true) 
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Thanh tìm kiếm
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            placeholder = { Text("Tìm kiếm món ăn...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 80.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredDishes) { dish ->
                AdminDishCard(
                    dish = dish,
                    onEdit = { 
                        selectedDish = dish
                        showDishDialog = true 
                    },
                    onDelete = { viewModel.deleteDish(dish.id!!, restaurantId) }
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(
            onClick = { 
                selectedDish = null
                showDishDialog = true 
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Dish")
        }

        if (showDishDialog) {
            DishDialog(
                dish = selectedDish,
                onDismiss = { showDishDialog = false },
                onConfirm = { name, price, desc, uri ->
                    viewModel.saveDish(
                        context = context,
                        id = selectedDish?.id,
                        restaurantId = restaurantId,
                        name = name,
                        price = price,
                        description = desc,
                        imageUri = uri,
                        existingImageUrl = selectedDish?.image_url
                    )
                    showDishDialog = false
                }
            )
        }
    }
}

@Composable
fun AdminDishCard(dish: AdminDish, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = dish.image_url,
                contentDescription = null,
                modifier = Modifier.size(90.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = dish.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "${dish.price} VNĐ", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                Text(text = dish.description, fontSize = 12.sp, color = Color.Gray, maxLines = 2)
            }
            Column {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                }
            }
        }
    }
}

@Composable
fun DishDialog(
    dish: AdminDish?,
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String, Uri?) -> Unit
) {
    var name by remember { mutableStateOf(dish?.name ?: "") }
    var price by remember { mutableStateOf(dish?.price?.toString() ?: "") }
    var desc by remember { mutableStateOf(dish?.description ?: "") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    fun validateForm(): Boolean {
        errorMessage = when {
            name.isBlank() -> "Tên món không được để trống"
            price.toDoubleOrNull() == null -> "Giá phải là số"
            price.toDoubleOrNull() ?: 0.0 < 0 -> "Giá không được âm"
            desc.isBlank() -> "Mô tả không được để trống"
            else -> ""
        }
        return errorMessage.isEmpty()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if(dish == null) "Thêm món mới" else "Chỉnh sửa món", 
                    fontSize = 22.sp, 
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.FaintGray())
                        .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp))
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        AsyncImage(model = imageUri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    } else if (dish?.image_url != null) {
                        AsyncImage(model = dish.image_url, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    } else {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(32.dp), tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Tên món") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Giá (VNĐ)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Mô tả") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), minLines = 2)

                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Hủy") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (validateForm()) {
                                onConfirm(name, price.toDoubleOrNull() ?: 0.0, desc, imageUri)
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if(dish == null) "Thêm món" else "Cập nhật")
                    }
                }
            }
        }
    }
}

// Extension function cho màu xám nhạt
@Composable
fun Color.Companion.FaintGray() = Color(0xFFF5F5F5)

@Composable
fun TableDesignScreen(viewModel: AdminViewModel, restaurantId: String) {
    val gridSize = 10
    var selectedCapacity by remember { mutableIntStateOf(2) }

    LaunchedEffect(Unit) {
        viewModel.fetchTables(restaurantId)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Thiết kế sơ đồ bàn", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(text = "Chọn loại bàn bên dưới rồi chạm vào lưới để đặt", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(2, 4, 6).forEach { cap ->
                FilterChip(
                    selected = selectedCapacity == cap,
                    onClick = { selectedCapacity = cap },
                    label = { Text("$cap chỗ") },
                    leadingIcon = if (selectedCapacity == cap) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.weight(1f).fillMaxWidth().border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)).padding(4.dp)) {
            Column {
                for (y in 0 until gridSize) {
                    Row(modifier = Modifier.weight(1f)) {
                        for (x in 0 until gridSize) {
                            val table = viewModel.designTables.find { it.pos_x == x && it.pos_y == y }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(2.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (table != null) {
                                            when (table.capacity) {
                                                2 -> Color(0xFF81C784)
                                                4 -> Color(0xFF64B5F6)
                                                else -> Color(0xFFBA68C8)
                                            }
                                        } else Color(0xFFEEEEEE)
                                    )
                                    .clickable { viewModel.toggleTable(restaurantId, x, y, selectedCapacity) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (table != null) {
                                    Text(text = table.table_code, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.saveTables(restaurantId) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("LƯU SƠ ĐỒ BÀN", fontWeight = FontWeight.Bold)
        }
    }
}
