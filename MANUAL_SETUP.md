# 🔧 MANUAL SETUP - Tự Làm Từng Bước

**Dùng guide này nếu script PowerShell/Batch không hoạt động**

---

## BƯỚC 1: Mở MySQL Command Line

```bash
# Mở Command Prompt / PowerShell
# Chạy lệnh:

mysql -u root

# Nhấn Enter (không nhập password nếu dev setup mặc định)
```

**✅ Nên thấy:**
```
Welcome to the MySQL monitor.
mysql>
```

---

## BƯỚC 2: Tạo Database

**Trong MySQL prompt, chạy:**

```sql
DROP DATABASE IF EXISTS baitaplon;
CREATE DATABASE baitaplon;
USE baitaplon;
```

---

## BƯỚC 3: Import Schema

**Lựa chọn A - Dùng SOURCE command:**

```sql
SOURCE C:\Users\Acer\AndroidStudioProjects\BaiTapLon\api\baitaplon.sql;
```

**Lựa chọn B - Nếu A không hoạt động:**

1. Exit MySQL: `exit`
2. Chạy từ command prompt:
```bash
mysql -u root baitaplon < "C:\Users\Acer\AndroidStudioProjects\BaiTapLon\api\baitaplon.sql"
```

3. Rồi vào lại: `mysql -u root`

---

## BƯỚC 4: Tạo Admin Account

**Trong MySQL prompt (nếu chưa exit), chạy:**

```sql
INSERT INTO users (email, password, role, full_name) VALUES
('admin@restaurant.com', '$2y$10$Qi3UKPVJLaXsHNYCEPNNXONEu2eE70F4F/2RKCLTdpJKRsZSuP/i.', 'ADMIN', 'Admin Nhà Hàng');
```

---

## BƯỚC 5: Kiểm Tra 

**Trong MySQL prompt:**

```sql
SELECT id, email, role FROM users;
```

**✅ Output nên có:**
```
+----+------------------------+--------+
| id | email                  | role   |
+----+------------------------+--------+
| 1  | admin@restaurant.com   | ADMIN  |
+----+------------------------+--------+
```

---

## BƯỚC 6: Exit MySQL

```sql
exit
```

---

## BƯỚC 7: Start PHP Server

**Mở command prompt mới, chạy:**

```bash
cd C:\Users\Acer\AndroidStudioProjects\BaiTapLon\api
php -S localhost:8080
```

**✅ Nên thấy:**
```
[timestamp] PHP 8.0.0 Development Server started at http://localhost:8080
```

**⚠️ GIỮ TERMINAL NÀY MỞ**

---

## BƯỚC 8: Rebuild App

**Android Studio:**
```
Build → Rebuild Project
```

---

## BƯỚC 9: Run App

**Android Studio:**
```
Run (Shift+F10)
```

---

## BƯỚC 10: Test

**Test 1 - Register:**
- Email: test@example.com
- Password: 123456
- ✅ "Đăng ký thành công"

**Test 2 - Login Customer:**
- Email: test@example.com
- Password: 123456
- Role: "Khách hàng"
- ✅ Customer Dashboard

**Test 3 - Login Admin:**
- Email: admin@restaurant.com
- Password: admin123
- Role: "Admin"
- ✅ Admin Dashboard

---

## ✅ If Everything Works

🎉 Ứng dụng sẵn sàng!

---

## 🆘 Troubleshooting

### "Access denied for user 'root'"
```bash
# Nhập password (nếu có)
mysql -u root -p
# Sau đó nhập password khi được hỏi
```

### "Unknown database 'baitaplon'"
```sql
-- SQL chạy lại
SHOW DATABASES;
CREATE DATABASE baitaplon;
USE baitaplon;
```

### "Table 'users' doesn't exist"
```sql
-- Import lại schema
SOURCE C:\Users\Acer\AndroidStudioProjects\BaiTapLon\api\baitaplon.sql;
```

### PHP Server không start
```bash
# Kiểm tra port 8080 có đang dùng không
netstat -ano | findstr :8080

# Nếu có, dùng port khác
php -S localhost:8081
```

---

**Hoàn thành! ✅**

