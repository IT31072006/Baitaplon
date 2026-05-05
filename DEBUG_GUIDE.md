# 🔧 DEBUG GUIDE - Sửa Lỗi Đăng Nhập & Đăng Ký

## ⚠️ CÁC BƯỚC KIỂM TRA TRƯỚC TIÊN

### **1️⃣ Kiểm Tra PHP Server**
```bash
# Mở Command Prompt/PowerShell
# Di chuyển vào folder api
cd C:\Users\Acer\AndroidStudioProjects\BaiTapLon\api

# Start PHP server
php -S localhost:8080

# ✅ Nếu thấy dòng này là OK:
# [Mon May 05 21:00:00 2026] PHP 8.0.0 Development Server started at http://localhost:8080
```

### **2️⃣ Kiểm Tra Database Tồn Tại**
```bash
# Mở Command Prompt/PowerShell
mysql -u root -p

# Nhập password (để trống nếu không có)

# Chạy lệnh:
SHOW DATABASES;
USE baitaplon;
SHOW TABLES;
SELECT * FROM users;
```

**✅ Output nên thấy:**
```
mysql> SHOW DATABASES;
+--------------------+
| Database           |
+--------------------+
| baitaplon         | ← OK nếu thấy cái này
| mysql              |
| information_schema |
+--------------------+

mysql> SHOW TABLES;
+---------------------+
| Tables_in_baitaplon |
+---------------------+
| dishes              |
| reservations        |
| reservation_items   |
| restaurants         |
| tables              |
| users               | ← OK nếu thấy users table
+---------------------+

mysql> SELECT * FROM users;
Empty set (0 rows in set) ← OK nếu trống lúc đầu
```

### **3️⃣ Tạo Admin Account**
```bash
# Nếu users table trống, chạy script tạo admin:
mysql -u root -p baitaplon < api/create_admin.sql

# Hoặc paste vào MySQL client:
INSERT INTO users (email, password, role, full_name) VALUES
('admin@restaurant.com', 
 '$2y$10$Qi3UKPVJLaXsHNYCEPNNXONEu2eE70F4F/2RKCLTdpJKRsZSuP/i.', 
 'ADMIN', 
 'Admin Nhà Hàng');

# Kiểm tra:
SELECT id, email, role FROM users;
```

**✅ Output nên có:**
```
+----+------------------------+----------+
| id | email                  | role     |
+----+------------------------+----------+
| 1  | admin@restaurant.com   | ADMIN    |
+----+------------------------+----------+
```

### **4️⃣ Test API Trực Tiếp (Dùng curl/Postman)**

#### **Test Register API**
```bash
# PowerShell
$body = @{
    email = "test@example.com"
    password = "123456"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/auth_api.php?action=register" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body

# ✅ Response nên có:
# {
#   "userId": 2,
#   "email": "test@example.com",
#   "role": "CUSTOMER",
#   "status": "success",
#   "message": "Đăng ký thành công..."
# }
```

#### **Test Login Admin API**
```bash
$body = @{
    email = "admin@restaurant.com"
    password = "admin123"
    role = "ADMIN"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/auth_api.php?action=login" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body

# ✅ Response nên có:
# {
#   "userId": 1,
#   "email": "admin@restaurant.com",
#   "role": "ADMIN",
#   "status": "success"
# }
```

---

## 🔍 VẤN ĐỀ & GIẢI PHÁP

### ❌ Error: "Connection failed" hoặc "Network error"

**Nguyên nhân:** Server PHP không chạy  
**Giải pháp:**
1. ✅ Start PHP server:
```bash
cd api
php -S localhost:8080
```
2. ✅ Kiểm tra task manager: Python/PHP process có đang chạy không?

---

### ❌ Error: "No such database 'baitaplon'"

**Nguyên nhân:** Database chưa tạo  
**Giải pháp:**
```bash
mysql -u root -p
CREATE DATABASE baitaplon;
USE baitaplon;
SOURCE api/baitaplon.sql;
SOURCE api/create_admin.sql;
```

---

### ❌ Register không lưu vào CSDL

**Nguyên nhân:** Có thể API endpoint không call đúng  
**Giải pháp:**
1. Test API trực tiếp (xem bước 4 trên)
2. Check logcat:
```
adb logcat | grep "Auth API"
```
3. Check error_log PHP (nếu có file error.log)

---

### ❌ Login Admin nhưng vào Customer Dashboard

**Nguyên nhân:** API trả về role sai hoặc MainActivity logic sai  
**Giải pháp:**

**Step 1: Kiểm tra logcat**
```bash
# Khi login, xem logcat
adb logcat | grep "LOGIN_DEBUG"

# Nên thấy:
# LOGIN_DEBUG: LoginResponse: ...
# LOGIN_DEBUG: Role from response: 'ADMIN' (length=5)
# LOGIN_DEBUG: Role matches ADMIN - navigating to admin_dashboard
```

**Step 2: Kiểm tra API response**
```bash
# Test API trực tiếp
curl -X POST http://localhost:8080/api/auth_api.php?action=login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@restaurant.com","password":"admin123","role":"ADMIN"}'

# Response PHẢI có "role":"ADMIN" (ký tự hoa)
```

**Step 3: Kiểm tra Database**
```sql
SELECT * FROM users WHERE email='admin@restaurant.com';

-- Role column phải là 'ADMIN' (ký tự hoa)
```

---

### ❌ Login không response (App hang/freeze)

**Nguyên nhân:** API call timeout, server không response  
**Giải pháp:**
1. Chắc chắn PHP server đang chạy
2. Test URL trực tiếp:
```bash
curl http://localhost:8080/api/auth_api.php?action=login
```
3. Check firewall - có chặn port 8080 không?

---

### ❌ "Sai thông tin đăng nhập" - nhưng email/password đúng

**Nguyên nhân:** Role không match hoặc password hash sai  
**Giải pháp:**
1. Kiểm tra role chọn đúng không? (ADMIN hay Khách hàng?)
2. Kiểm tra password đúng không?
3. Để password admin là: `admin123`

---

## 📋 CHECKLIST TRƯỚC KHI TEST

```
□ PHP server đang chạy (php -S localhost:8080)
☐ Database baitaplon tồn tại
□ Users table tồn tại
□ Admin account đã tạo (admin@restaurant.com)
□ API test trực tiếp response đúng JSON
□ App compile không có lỗi
□ AndroidManifest.xml có INTERNET permission
□ AuthViewModel URL đúng (http://10.0.2.2:8080/api/)
□ AuthApiService có @Query annotation
```

---

## 📊 VERIFICATION COMMANDS

**Một lệnh check all:**
```bash
# 1. Check PHP server
curl http://localhost:8080/api/auth_api.php || echo "PHP server not running"

# 2. Check database
mysql -u root -p -e "USE baitaplon; SELECT COUNT(*) FROM users;"

# 3. Check admin
mysql -u root -p -e "USE baitaplon; SELECT email, role FROM users WHERE role='ADMIN';"
```

---

## 🎯 QUICK TEST FLOW

1. **Start server**
   ```bash
   cd api && php -S localhost:8080
   ```

2. **Create admin** (if needed)
   ```bash
   mysql -u root -p baitaplon < api/create_admin.sql
   ```

3. **Open app**
   - Run on emulator/device

4. **Test Register**
   - Email: newtest@example.com
   - Password: 123456
   - ✅ Should see "Đăng ký thành công"
   - ✅ Auto redirect to login

5. **Test Login Customer**
   - Email: newtest@example.com (from step 4)
   - Password: 123456
   - Role: "Khách hàng"
   - ✅ Should see Customer Dashboard

6. **Test Login Admin**
   - Email: admin@restaurant.com
   - Password: admin123
   - Role: "Admin"
   - ✅ Should see Admin Dashboard

---

## 🆘 EMERGENCY RESET

Nếu muốn reset lại từ đầu:

```bash
# 1. Drop & recreate database
mysql -u root -p -e "DROP DATABASE baitaplon; CREATE DATABASE baitaplon;"

# 2. Import schema
mysql -u root -p baitaplon < api/baitaplon.sql

# 3. Create admin
mysql -u root -p baitaplon < api/create_admin.sql

# 4. Verify
mysql -u root -p -e "USE baitaplon; SELECT * FROM users;"
```

---

## 📞 If Still Not Working

**Provide:**
1. PHP server output (screenshot)
2. Database query result (screenshot)
3. API response from curl (screenshot)
4. Logcat output (share full logs)
5. Error message from app (screenshot)

**File locations:**
- Database: MySQL
- PHP config: `api/db_config.php`
- Auth API: `api/auth_api.php`
- App config: `app/src/main/java/com/example/baitaplon/ui/AuthViewModel.kt`

