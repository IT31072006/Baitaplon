# ✅ FINAL FIX INSTRUCTIONS - Hoàn Toàn

## 📌 TÓM TẮT CÁC FIX

| # | File | Sửa |
|---|------|-----|
| 1 | AuthApiService.kt | ✅ Sửa @Query parameter |
| 2 | auth_api.php | ✅ Thêm logging |
| 3 | MainActivity.kt | ✅ Thêm debug log |
| 4 | DEBUG_GUIDE.md | ✅ Tạo hướng dẫn debug |
| 5 | setup.ps1 | ✅ Tạo script setup |

---

## 🚀 CÁC BƯỚC THỰC HIỆN (THEO THỨ TỰ)

### **BƯỚC 1: Rebuild App**
```
Android Studio:
1. Click "Build" → "Rebuild Project"
2. Đợi build xong (khoảng 1-2 phút)
3. Kiểm tra không có error (nếu có, báo cho tôi)
```

### **BƯỚC 2: Setup Database & Tạo Admin Account**

**Cách A - Dùng PowerShell Script (Recommend):**
```powershell
# Mở PowerShell ở folder BaiTapLon
cd C:\Users\Acer\AndroidStudioProjects\BaiTapLon

# Chạy script setup
.\setup.ps1

# Script sẽ:
# ✅ Kiểm tra PHP
# ✅ Kiểm tra MySQL
# ✅ Tạo database
# ✅ Import schema
# ✅ Tạo admin account
# ✅ Start PHP server
```

**Cách B - Manual:**
```bash
# 1. Mở Command Prompt/PowerShell

# 2. Tạo database
mysql -u root -p

# Nhập password (Enter nếu không có)
# Sau đó paste các lệnh này:

DROP DATABASE IF EXISTS baitaplon;
CREATE DATABASE baitaplon;
USE baitaplon;
SOURCE C:\Users\Acer\AndroidStudioProjects\BaiTapLon\api\baitaplon.sql;
SOURCE C:\Users\Acer\AndroidStudioProjects\BaiTapLon\api\create_admin.sql;

# 3. Verify admin account
SELECT * FROM users WHERE role='ADMIN';

# Nên thấy:
# id | email | password | role | full_name | created_at
# 1  | admin@restaurant.com | $2y$... | ADMIN | Admin Nhà Hàng | ...

# 4. Exit MySQL
exit
```

### **BƯỚC 3: Start PHP Server**

**PowerShell/Command Prompt:**
```bash
# Di chuyển vào folder api
cd C:\Users\Acer\AndroidStudioProjects\BaiTapLon\api

# Start server
php -S localhost:8080

# ✅ Nếu thấy dòng này là OK:
# [Mon May 05 21:00:00 2026] PHP 8.0.0 Development Server started at http://localhost:8080
```

**⚠️ GIỮ TERMINAL NÀY MỞ - Đừng đóng!**

### **BƯỚC 4: Run App**

**Android Studio:**
1. Chọn emulator hoặc device
2. Click "Run" (Shift + F10)
3. Đợi app load lên (khoảng 30 giây)

### **BƯỚC 5: TEST**

**Test 1 - Register (Customer):**
```
1. Tên: "Don't have an account? Register"
2. Email: test123@example.com
3. Password: 123456
4. Click REGISTER
✅ Nên thấy: "Đăng ký thành công"
✅ Auto redirect về Login screen
```

**Test 2 - Login (Customer):**
```
1. Email: test123@example.com
2. Password: 123456
3. Role: "Khách hàng"
4. Click LOGIN
✅ Nên vào Customer Dashboard
```

**Test 3 - Login (Admin):**
```
1. Email: admin@restaurant.com
2. Password: admin123
3. Role: "Admin"
4. Click LOGIN
✅ Nên vào Admin Dashboard (không phải customer)
```

---

## 🔍 KIỂM TRA KHI CÓ LỖI

### **Nếu Register không lưu CSDL:**

**Step 1: Check Database có tồn tại không**
```bash
mysql -u root -p
SHOW DATABASES;  # Nên thấy baitaplon
USE baitaplon;
SHOW TABLES;     # Nên thấy 6 tables
SELECT * FROM users;  # Xem users hiện tại
exit
```

**Step 2: Check PHP Server chạy không**
```bash
# Mở terminal khác, test API
curl http://localhost:8080/api/auth_api.php?action=register

# Nếu lỗi connection: PHP server không chạy
# Nếu thấy JSON response: OK
```

**Step 3: Check Logcat**
```bash
adb logcat | grep "Auth API"

# Nên thấy:
# Auth API Request - Method: POST, GET action: register
# Action: register, Data: {...}
```

### **Nếu Login Admin Nhưng Vào Customer Dashboard:**

**Step 1: Check Logcat**
```bash
adb logcat | grep "LOGIN_DEBUG"

# Nên thấy:
# LOGIN_DEBUG: Role from response: 'ADMIN' (length=5)
# LOGIN_DEBUG: Role matches ADMIN - navigating to admin_dashboard
```

**Nếu thấy khác:**
- Role không phải "ADMIN" → Database role sai
- Role là "CUSTOMER" → API return sai role

**Step 2: Verify Database**
```bash
mysql -u root -p
USE baitaplon;
SELECT id, email, role FROM users;

# Kiểm tra role column - nên là ký tự hoa 'ADMIN'
```

**Step 3: Test API Trực Tiếp**
```bash
# PowerShell
$body = @{
    email = "admin@restaurant.com"
    password = "admin123"
    role = "ADMIN"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/auth_api.php?action=login" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body | ConvertTo-Json

# Nên thấy response có "role":"ADMIN"
```

---

## 🆘 QUICK DEBUG CHECKLIST

```
Nếu vẫn lỗi, kiểm tra:

□ PHP server đang chạy? 
  → Terminal hiển thị "Development Server started"
  
□ Database baitaplon tồn tại?
  → mysql -u root -p -e "SHOW DATABASES;"
  
□ Users table tồn tại?
  → mysql -u root -p -e "USE baitaplon; SHOW TABLES;"
  
□ Admin account tạo rồi?
  → mysql -u root -p -e "USE baitaplon; SELECT * FROM users WHERE role='ADMIN';"
  
□ API response đúng format?
  → curl http://localhost:8080/api/auth_api.php?action=login
  
□ App rebuild xong?
  → Android Studio: Build → Rebuild Project
  
□ Emulator/Device kết nối đúng?
  → adb devices (nên thấy emulator)
```

---

## 📝 LOG ANALYSIS

**Nếu bạn gặp lỗi, cung cấp:**

1. **PHP Server Output:**
   ```
   [Paste terminal output từ php -S localhost:8080]
   ```

2. **Logcat Output:**
   ```bash
   adb logcat | grep "AUTH\|LOGIN\|ERROR"
   ```

3. **Database Query Result:**
   ```bash
   mysql -u root -p -e "USE baitaplon; SELECT * FROM users;"
   ```

4. **API Response (curl):**
   ```bash
   curl -X POST http://localhost:8080/api/auth_api.php?action=login \
     -H "Content-Type: application/json" \
     -d '{"email":"admin@restaurant.com","password":"admin123","role":"ADMIN"}'
   ```

---

## ✅ EXPECTED RESULTS

### **Register Success:**
```
Response:
{
  "userId": 2,
  "email": "test@example.com",
  "token": "reg_token_...",
  "role": "CUSTOMER",
  "status": "success",
  "message": "Đăng ký thành công. Vui lòng đăng nhập!"
}

DB:
mysql> SELECT * FROM users;
+----+------------------------+-------+----------+-------+-----------+
| id | email                  | role  | password | fn    | created   |
+----+------------------------+-------+----------+-------+-----------+
| 1  | admin@rest.com         | ADMIN | $2y$... | Admin | 2026-...  |
| 2  | test@example.com       |CUST   | $2y$... | NULL  | 2026-...  |
+----+------------------------+-------+----------+-------+-----------+
```

### **Login Admin Success:**
```
Response:
{
  "userId": 1,
  "email": "admin@restaurant.com",
  "role": "ADMIN",
  "status": "success"
}

Logcat:
LOGIN_DEBUG: LoginResponse: LoginResponse(userId=1, email=admin@restaurant.com, role=ADMIN, ...)
LOGIN_DEBUG: Role from response: 'ADMIN' (length=5)
LOGIN_DEBUG: Role matches ADMIN - navigating to admin_dashboard

App Navigation:
✅ GOES TO ADMIN DASHBOARD (not customer)
```

---

## 🎯 SUMMARY

**Tất cả các file đã được sửa:**
1. ✅ AuthApiService.kt - @Query parameter fix
2. ✅ auth_api.php - Logging added
3. ✅ MainActivity.kt - Debug logs added
4. ✅ DEBUG_GUIDE.md - Created
5. ✅ setup.ps1 - Created

**Tiếp theo bạn cần:**
1. Rebuild app
2. Run setup.ps1 để setup database
3. Test register & login
4. Kiểm tra Logcat nếu lỗi

**Nếu vẫn lỗi:**
- Chạy các lệnh debug ở mục "QUICK DEBUG CHECKLIST"
- Share logcat output với tôi
- Tôi sẽ debug tiếp

---

**Ready? Bắt đầu bước 1: Rebuild App** ✅

