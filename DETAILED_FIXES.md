# 📋 Tóm Tắt Tất Cả Lỗi Đã Sửa Chữa

## ✅ Tìm Tắt Các Sửa Chữa

### 1️⃣ **Android Kotlin - AdminViewModel.kt**

**Lỗi:** Không có phương thức xóa lỗi/thông báo  
**Sửa chữa:** Thêm hai hàm mới:
```kotlin
fun clearError() {
    _uiState.value = _uiState.value.copy(error = null)
}

fun clearSuccessMessage() {
    _uiState.value = _uiState.value.copy(successMessage = null)
}
```
**Lợi ích:** Xóa state thông báo sau khi hiển thị Toast → tránh lặp lại

---

### 2️⃣ **Android Kotlin - AdminDashboard.kt**

#### 🔹 LaunchedEffect Toast Handler
**Lỗi:** Toast hiển thị vô hạn vì không xóa state  
**Sửa chữa:** 
```kotlin
LaunchedEffect(uiState.error, uiState.successMessage) {
    uiState.error?.let {
        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        viewModel.clearError()  // ← Thêm dòng này
    }
    uiState.successMessage?.let {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        viewModel.clearSuccessMessage()  // ← Thêm dòng này
    }
}
```

#### 🔹 DishDialog Form Validation
**Lỗi:** Không validate input, cho phép giá tiền âm, tên trống  
**Sửa chữa:** Thêm `validateForm()` function:
```kotlin
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
```
**Hiển thị lỗi:**
```kotlin
if (errorMessage.isNotEmpty()) {
    Text(
        text = errorMessage,
        color = MaterialTheme.colorScheme.error,
        fontSize = 12.sp
    )
}
```
**Submit button:**
```kotlin
Button(
    onClick = { 
        if (validateForm()) {  // ← Validate trước khi submit
            onConfirm(name, price.toDoubleOrNull() ?: 0.0, desc, imageUri)
        }
    }
) { ... }
```

---

### 3️⃣ **PHP Backend - admin_api.php** 🔴 **CRITICAL**

#### 🔹 SQL Injection Vulnerability
**Lỗi:**
```php
// ❌ KHÔNG AN TOÀN
$sql = "UPDATE dishes SET name='$name', price='$price' WHERE id=$id";
$conn->query($sql);
```

**Sửa chữa:** Dùng Prepared Statements
```php
// ✅ AN TOÀN
$stmt = $conn->prepare("UPDATE dishes SET name=?, price=?, description=?, image_url=? WHERE id=?");
$stmt->execute([$name, $price, $desc, $img_url, $id]);
```

#### 🔹 Input Validation
**Thêm validation function:**
```php
function validateDish($name, $price, $description) {
    $errors = [];
    if (empty(trim($name))) $errors[] = "Tên món không được để trống";
    if (!is_numeric($price) || $price < 0) $errors[] = "Giá phải là số dương";
    if (empty(trim($description))) $errors[] = "Mô tả không được để trống";
    return $errors;
}
```

**Gọi validation:**
```php
$errors = validateDish($name, $price, $desc);
if (!empty($errors)) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => implode(", ", $errors)]);
    break;
}
```

#### 🔹 File Upload Security
**Lỗi:** Không check loại file, kích thước, tên file unsafe
**Sửa chữa:**
```php
// Kiểm tra loại file
$allowed = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
if (!in_array($_FILES['image']['type'], $allowed)) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Chỉ hỗ trợ JPG, PNG, GIF, WebP"]);
    break;
}

// Kiểm tra kích thước (5MB max)
if ($_FILES['image']['size'] > 5 * 1024 * 1024) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "File quá lớn (max 5MB)"]);
    break;
}

// Generate tên file an toàn
$ext = pathinfo($_FILES['image']['name'], PATHINFO_EXTENSION);
$path = "uploads/" . time() . "_" . bin2hex(random_bytes(5)) . "." . $ext;
```

#### 🔹 Error Handling
**Thêm try-catch:**
```php
try {
    // API logic here
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Lỗi server"]);
}
```

---

### 4️⃣ **PHP Backend - auth_api.php** 🔴 **CRITICAL**

#### 🔹 Password Hashing
**Lỗi:** 
```php
// ❌ Plain text password!
$stmt = $conn->prepare("INSERT INTO users (email, password, role) VALUES (?, ?, ?)");
$stmt->execute([$email, $pass, $role]);
```

**Sửa chữa:**
```php
// ✅ Bcrypt hashing
$hashedPass = password_hash($pass, PASSWORD_BCRYPT);
$stmt = $conn->prepare("INSERT INTO users (email, password, role) VALUES (?, ?, ?)");
$stmt->execute([$email, $hashedPass, $role]);
```

#### 🔹 Password Verification
**Lỗi:**
```php
// ❌ Direct comparison
$stmt = $conn->prepare("SELECT * FROM users WHERE email=? AND password=? AND role=?");
$stmt->execute([$email, $pass, $role]);
```

**Sửa chữa:**
```php
// ✅ password_verify()
$stmt = $conn->prepare("SELECT * FROM users WHERE email=? AND role=?");
$stmt->execute([$email, $role]);
$user = $stmt->fetch(PDO::FETCH_ASSOC);
if ($user && password_verify($pass, $user['password'])) {
    // Login success
}
```

#### 🔹 Email Validation
```php
if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Email không hợp lệ"]);
    break;
}
```

#### 🔹 Password Strength
```php
if (strlen($pass) < 6) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Mật khẩu phải ít nhất 6 ký tự"]);
    break;
}
```

#### 🔹 Secure Token Generation
```php
// ❌ Weak
"token" => "login_token_" . time()

// ✅ Cryptographically secure
"token" => "login_token_" . bin2hex(random_bytes(16))
```

---

### 5️⃣ **PHP Backend - upload_handler.php**

#### 🔹 File Type Validation
```php
$finfo = finfo_open(FILEINFO_MIME_TYPE);
$mime = finfo_file($finfo, $file['tmp_name']);
finfo_close($finfo);

$allowed = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
if (!in_array($mime, $allowed)) {
    return ["error" => "Chỉ hỗ trợ JPG, PNG, GIF, WebP"];
}
```

#### 🔹 File Size Validation
```php
$max_size = 5 * 1024 * 1024;  // 5MB
if ($file['size'] > $max_size) {
    return ["error" => "File quá lớn (max 5MB)"];
}
```

#### 🔹 Safe Filename Generation
```php
// ❌ Unsafe
$new_file_name = time() . "_" . $_FILES['image']['name'];

// ✅ Safe
$file_extension = strtolower(pathinfo($file["name"], PATHINFO_EXTENSION));
$safe_name = bin2hex(random_bytes(8));
$new_file_name = time() . "_" . $safe_name . "." . $file_extension;
```

#### 🔹 Permission Setting
```php
chmod($target_file, 0644);  // rw-r--r--
```

---

## 🔒 Security Improvements

| Lỗi | Mức Độ | Loại | Sửa Chữa |
|---|---|---|---|
| SQL Injection | 🔴 Critical | Backend | Prepared Statements |
| Plain-text Password | 🔴 Critical | Backend | bcrypt Hashing |
| File Type Bypass | 🟠 High | Backend | MIME Type Check |
| Unicode Form | 🟠 High | Backend | UTF8MB4 Support |
| Input Validation | 🟡 Medium | Both | Validation Functions |
| Error Messages | 🟡 Medium | Both | Proper HTTP Codes |
| Toast Loop | 🟢 Low | Android | State Cleanup |

---

## 🧪 Testing Checklist

- [ ] **Đăng ký:** Email validation, password hashing
- [ ] **Đăng nhập:** password_verify works
- [ ] **Thêm Món:** Validation, upload, DB update
- [ ] **Sửa Món:** Keep old image nếu không upload mới
- [ ] **Xóa Món:** Soft delete or hard delete check
- [ ] **Thiết Kế Bàn:** Grid update, TableDesign save
- [ ] **Toast:** Không lặp, clear đúng
- [ ] **File Upload:** Reject > 5MB, Invalid type
- [ ] **SQL:** Prepared statements for all queries

---

## 📝 Migration Notes

### Database Schema Update Needed
Nếu sử dụng password hashing mới, cần reset tất cả passwords cũ:
```sql
UPDATE users SET password = '' WHERE role = 'ADMIN';
-- Hoặc drop và recreate table users
```

### API Endpoints (Updated)
```
POST /auth_api.php?action=register
POST /auth_api.php?action=login
GET  /admin_api.php?action=list_dishes?restaurant_id=1
POST /admin_api.php?action=save_dish
GET  /admin_api.php?action=delete_dish?id=1
POST /admin_api.php?action=save_layout
GET  /admin_api.php?action=get_layout?restaurant_id=1
```

---

## 🎯 Kế Hoạch Tiếp Theo

1. **JWT Authentication** - Thay vì simple token
2. **Rate Limiting** - Chống brute force
3. **CORS Configuration** - Middleware bảo vệ
4. **Logging & Monitoring** - Audit trail
5. **Database Encryption** - For sensitive data
6. **Two-Factor Authentication** - Extra security

---

**Ngày Sửa:** 5/5/2026  
**Trạng Thái:** ✅ Hoàn Thành

