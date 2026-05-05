# 📋 Hướng Dẫn Setup Admin Account

## ⚡ QUICK START (3 Bước)

### **Bước 1: Tạo Admin Account**
Chạy SQL script tạo admin:
```sql
-- SSH vào MySQL hoặc dùng phpMyAdmin
mysql -u root -p baitaplon < api/create_admin.sql
```

### **Bước 2: Start Server PHP**
```bash
cd api
php -S localhost:8080
```

### **Bước 3: Test Login**
- **Khách hàng (Customer):**
  1. Register email/password mới
  2. Login với email/password vừa đăng ký
  3. Chọn "Khách hàng"
  
- **Admin:**
  1. Login với email: `admin@restaurant.com`
  2. Password: `admin123`
  3. Chọn "Admin"

---

### 1. **RegisterScreen.kt** ✅
- ✅ Ẩn tùy chọn ADMIN - chỉ cho đăng ký CUSTOMER
- ✅ Tự động set role = CUSTOMER khi register
- ✅ Hiển thị tin nhắn lỗi nếu đăng ký thất bại
- ✅ Gọi callback `onRegisterSuccess()` khi thành công

### 2. **auth_api.php Register** ✅
- ✅ Luôn register với role = CUSTOMER (không được chọn)
- ✅ Fix INSERT statement - kiểm tra execute result properly
- ✅ Add error handling chi tiết (Duplicate email, validation)
- ✅ Add userId vào response
- ✅ Password hashing với PASSWORD_BCRYPT
- ✅ Email validation

### 3. **auth_api.php Login** ✅
- ✅ Trả về userId, email, role chính xác từ DB
- ✅ Trả về status "success"
- ✅ Khớp role từ LoginRequest với DB (case-insensitive)
- ✅ Password verification với password_verify()

### 4. **AuthModels.kt** ✅
- ✅ Thêm userId, email vào LoginResponse
- ✅ Match với PHP response

### 5. **AuthViewModel.kt** ✅
- ✅ Simplified register() - Luôn gửi CUSTOMER (server override)
- ✅ Tốt hơn error handling trong register()

### 6. **create_admin.sql** ✅
- ✅ Script SQL tạo tài khoản admin mặc định
- ✅ Password đã được hash (admin123)

---

## 🚀 Cách Chạy Setup

### **Bước 1: Tạo Tài Khoản Admin (Chọn 1 trong 2)**

#### **Cách A: Dùng phpMyAdmin**
1. Mở `http://localhost/phpmyadmin`
2. Chọn database `baitaplon`
3. Click tab "SQL"
4. Copy nội dung từ `api/create_admin.sql` vào
5. Click "Execute"

#### **Cách B: Dùng MySQL Command Line**
```bash
mysql -u root -p baitaplon < api/create_admin.sql
```

#### **Cách C: Dùng Dòng Lệnh SQL**
Chạy lệnh:
```sql
INSERT INTO users (email, password, role, full_name) VALUES 
('admin@restaurant.com', '$2y$10$Qi3UKPVJLaXsHNYCEPNNXONEu2eE70F4F/2RKCLTdpJKRsZSuP/i.', 'ADMIN', 'Admin Nhà Hàng');
```

### **Bước 2: Kiểm Tra**
Sau khi insert, kiểm tra:
```sql
SELECT email, role FROM users;
```

Bạn sẽ thấy:
```
admin@restaurant.com | ADMIN
```

---

## 🔐 Thông Tin Tài Khoản Admin

| Trường | Giá Trị |
|--------|--------|
| Email | `admin@restaurant.com` |
| Password | `admin123` |
| Role | `ADMIN` |
| Full Name | Admin Nhà Hàng |

---

## 📱 Hướng Dẫn Sử Dụng App

### **1. Đăng Ký (CUSTOMER)**
- Nhập Email
- Nhập Password (min 6 ký tự)
- ❌ **Không**có tùy chọn Admin
- Click "REGISTER"
- Tài khoản được lưu vào CSDL với role = CUSTOMER

### **2. Đăng Nhập (ADMIN)**
- Nhập Email: `admin@restaurant.com`
- Nhập Password: `admin123`
- **Chọn "Admin"** (Radio button)
- Click "LOGIN"
- ✅ Sẽ vào **Admin Dashboard** (không phải Customer)

### **3. Đăng Nhập (CUSTOMER)**
- Nhập Email (của tài khoản vừa đăng ký)
- Nhập Password
- **Chọn "Khách hàng"** (Radio button)
- Click "LOGIN"
- ✅ Sẽ vào **Customer Dashboard**

---

## 🔧 Database Schema Check

Chạy query để xem bảng users:
```sql
DESC users;
```

Kết quả sẽ hiển thị:
```
+----------+--|-------|-------|----+-------+
| Field    | Type    | Null  | Key | Extra |
+----------+---------+-------+-----+-------+
| id       | int(11) | NO    | PRI | AUTO  |
| email    | varchar | NO    | UNI |       |
| password | varchar | NO    |     |       |
| role     | enum    | NO    |     |       |
| fullname | varchar | YES   |     |       |
| created  | time    | NO    |     | def   |
+----------+---------+-------+-----+-------+
```

---

## 🐛 Troubleshooting Toàn Diện

### ❌ "Email đã tồn tại" khi register
**Nguyên nhân:** Email này đã được đăng ký rồi  
**Giải pháp:**
1. Dùng email mới (khác với các email trước)
2. Hoặc xóa email cũ từ DB:
```sql
DELETE FROM users WHERE email = 'email@example.com';
```

### ❌ "Mật khẩu phải ít nhất 6 ký tự" - Register fail
**Nguyên nhân:** Password quá ngắn  
**Giải pháp:** Nhập password ít nhất 6 ký tự

### ❌ "Email không hợp lệ" - Register fail
**Nguyên nhân:** Email format sai  
**Giải pháp:** Nhập email đúng format (email@domain.com)

### ❌ "Sai thông tin đăng nhập" khi login
**Nguyên nhân:** Email/Password không đúng hoặc Role không khớp  
**Giải pháp:** Kiểm tra:
- ✓ Email chính xác không? (phải là email đã đăng ký)
- ✓ Password chính xác không? (phải là password khi đăng ký)
- ✓ Role chọn đúng không?
  - **Khách hàng:** Chọn "Khách hàng"
  - **Admin:** Chọn "Admin"

### ❌ Admin login nhưng vào giao diện Customer (hoặc ngược lại)
**Nguyên nhân:** Role return từ API không đúng, hoặc MainActivity so sánh sai  
**Giải pháp:**
1. **Debug bằng Logcat:**
   ```kotlin
   // Trong MainActivity.kt - thêm dòng này trước khi navigate
   Log.d("LOGIN_DEBUG", "loginResponse: $loginRes, role: '${loginRes?.role}'")
   ```

2. **Check response từ API:**
   - Dùng Postman hoặc curl:
   ```bash
   curl -X POST http://localhost:8080/api/auth_api.php?action=login \
     -H "Content-Type: application/json" \
     -d '{"email":"admin@restaurant.com","password":"admin123","role":"ADMIN"}'
   ```
   - Response nên có `"role":"ADMIN"`

3. **Check MainActivity.kt logic:**
   ```kotlin
   if (role.trim().equals("ADMIN", ignoreCase = true)) {
       // Vào Admin Dashboard
   } else {
       // Vào Customer Dashboard
   }
   ```

### ❌ Register không lưu vào DB
**Nguyên nhân:** Connection DB sai, hoặc có lỗi SQL  
**Giải pháp:**
1. **Kiểm tra kết nối MySQL:**
   ```bash
   # Mở MySQL client
   mysql -u root -p
   use baitaplon;
   SELECT * FROM users;
   ```

2. **Kiểm tra db_config.php:** 
   - Host: `localhost` ✓
   - Database: `baitaplon` ✓
   - User: `root` ✓
   - Password: `` (rỗng nếu dev) ✓

3. **Test Register API trực tiếp:**
   ```bash
   curl -X POST http://localhost:8080/api/auth_api.php?action=register \
     -H "Content-Type: application/json" \
     -d '{"email":"test@example.com","password":"123456"}'
   ```

### ❌ Admin account không login được
**Nguyên nhân:** Admin account chưa tạo, hoặc password không đúng  
**Giải pháp:**
1. **Kiểm tra admin có tồn tại không:**
   ```sql
   SELECT * FROM users WHERE email = 'admin@restaurant.com';
   ```
   
2. **Nếu không có, chạy create script:**
   - Dùng phpMyAdmin hoặc:
   ```bash
   mysql -u root -p baitaplon < api/create_admin.sql
   ```

3. **Kiểm tra password hash:**
   - Password `admin123` nên hash thành:
   ```
   $2y$10$Qi3UKPVJLaXsHNYCEPNNXONEu2eE70F4F/2RKCLTdpJKRsZSuP/i.
   ```

### ❌ Lỗi kết nối: "Connection refused" hoặc "Network error"
**Nguyên nhân:** Server PHP không chạy, hoặc URL sai  
**Giải pháp:**
1. **Chắc chắn server PHP đang chạy:**
   ```bash
   # Từ thư mục api:
   php -S localhost:8080
   ```

2. **Test URL trong browser:**
   - Vào: `http://localhost:8080/api/auth_api.php?action=login`
   - Nên thấy JSON response (có thể là error)

3. **Check AuthViewModel baseUrl:**
   ```kotlin
   baseUrl("http://10.0.2.2:8080/api/") // Để emulator kết nối host
   ```

### ❌ Màu chữ đỏ ở auth_api.php (VS Code)
**Nguyên nhân:** `break;` trong try-catch  
**Giải pháp:** Thay thành `exit;` - Đã fix trong version này ✅

---

## 📝 SQL Script Full (Tạo Lại Nếu Cần)

```sql
-- Xóa tài khoản cũ nếu cần
DELETE FROM users WHERE email = 'admin@restaurant.com';

-- Tạo tài khoản admin mới
INSERT INTO users (email, password, role, full_name) VALUES 
('admin@restaurant.com', '$2y$10$Qi3UKPVJLaXsHNYCEPNNXONEu2eE70F4F/2RKCLTdpJKRsZSuP/i.', 'ADMIN', 'Admin Nhà Hàng');

-- Kiểm tra
SELECT * FROM users WHERE role = 'ADMIN';
```

---

## ✨ Các File Đã Sửa

| File | Sửa Chữa |
|------|---------|
| `RegisterScreen.kt` | Ẩn ADMIN option |
| `auth_api.php` | Register/Login fixes |
| `AuthModels.kt` | Add userId, email |
| `create_admin.sql` | New file |

---

**Status:** ✅ All fixes completed - Ready for testing!

