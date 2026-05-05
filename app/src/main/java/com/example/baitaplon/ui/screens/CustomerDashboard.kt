package com.example.baitaplon.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baitaplon.data.remote.Dish
import com.example.baitaplon.ui.CustomerViewModel
import com.example.baitaplon.ui.theme.BaiTapLonTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDashboard(viewModel: CustomerViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val uiState by viewModel.uiState
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Trang chủ") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { 
                        BadgedBox(badge = {
                            if (viewModel.cartItems.isNotEmpty()) {
                                Badge { Text(viewModel.cartItems.size.toString()) }
                            }
                        }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        }
                    },
                    label = { Text("Giỏ hàng") }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> HomeScreen(viewModel) { dish ->
                    viewModel.selectDish(dish)
                    showBottomSheet = true
                }
                1 -> CartScreen(viewModel)
            }
        }

        if (showBottomSheet && uiState.selectedDish != null) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                DishDetailBottomSheet(
                    dish = uiState.selectedDish!!,
                    onAddToCart = { quantity, note ->
                        viewModel.addToCart(uiState.selectedDish!!, quantity, note)
                        showBottomSheet = false
                    }
                )
            }
        }
    }
}

@Composable
fun HomeScreen(viewModel: CustomerViewModel, onDishClick: (Dish) -> Unit) {
    val uiState by viewModel.uiState

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Khám phá món ngon",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.dishes) { dish ->
                    DishCard(dish = dish, onClick = { onDishClick(dish) })
                }
            }
        }
    }
}

@Composable
fun DishCard(dish: Dish, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color.LightGray)
            )
            
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = dish.name, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(text = "${dish.price} VNĐ", color = MaterialTheme.colorScheme.secondary, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun DishDetailBottomSheet(dish: Dish, onAddToCart: (Int, String) -> Unit) {
    var quantity by remember { mutableIntStateOf(1) }
    var note by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(text = dish.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(text = dish.description, color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Số lượng", fontWeight = FontWeight.Medium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { if (quantity > 1) quantity-- }) {
                    Icon(Icons.Default.Remove, contentDescription = null)
                }
                Text(text = quantity.toString(), modifier = Modifier.padding(horizontal = 8.dp))
                IconButton(onClick = { quantity++ }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Ghi chú cho nhà hàng") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onAddToCart(quantity, note) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Thêm vào giỏ - ${dish.price * quantity} VNĐ")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun CartScreen(viewModel: CustomerViewModel) {
    val cartItems = viewModel.cartItems

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Giỏ hàng của bạn", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        if (cartItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Giỏ hàng trống")
            }
        } else {
            Column(modifier = Modifier.weight(1f)) {
                cartItems.forEach { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = item.dish.name, fontWeight = FontWeight.Bold)
                                Text(text = "Số lượng: ${item.quantity}", fontSize = 14.sp)
                                if (item.note.isNotEmpty()) {
                                    Text(text = "Ghi chú: ${item.note}", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                            Text(text = "${item.dish.price * item.quantity} VNĐ", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.makeReservation() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("XÁC NHẬN ĐẶT BÀN NGAY", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDishCard() {
    BaiTapLonTheme {
        DishCard(
            dish = Dish("1", "Phở Bò", "Phở bò truyền thống", 55000.0, "", "Main"),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDishDetail() {
    BaiTapLonTheme {
        DishDetailBottomSheet(
            dish = Dish("1", "Phở Bò", "Phở bò truyền thống cực ngon với nước dùng đậm đà", 55000.0, "", "Main"),
            onAddToCart = { _, _ -> }
        )
    }
}
