# 🔧 Thêm PHP & MySQL vào Windows PATH

## Nếu bạn dùng XAMPP

### BƯỚC 1: Tìm PHP path
```
C:\xampp\php (thường là đây)
```

### BƯỚC 2: Mở Environment Variables

**Cách A - Nhanh nhất:**
```
Windows + R
Gõ: sysdm.cpl
Enter
```

**Cách B - Bình thường:**
```
Control Panel 
→ System and Security 
→ System 
→ Advanced system settings 
→ Environment Variables
```

### BƯỚC 3: Thêm PHP vào PATH

1. Click "Edit" ở mục "Path" (System variables)
2. Click "New"
3. Paste: `C:\xampp\php`
4. Click OK

### BƯỚC 4: Thêm MySQL vào PATH

1. Click "New" lại
2. Paste: `C:\xampp\mysql\bin`
3. Click OK, rồi OK lần nữa

### BƯỚC 5: Restart Command Prompt / PowerShell

**Đóng và mở lại terminal**

### BƯỚC 6: Test

```bash
php -v
mysql -u root -e "SELECT VERSION();"
```

**✅ Nên bắt đầu hoạt động**

---

## Nếu bạn dùng WAMP

### Paths:
```
PHP: C:\wamp64\bin\php\php*.* (thay * bằng phiên bản, vd php8.1.0)
MySQL: C:\wamp64\bin\mysql\mysql*.* (tương tự)
```

### Thêm vào PATH:
1. Mở Environment Variables (sysdm.cpl)
2. Add `C:\wamp64\bin\php\php8.1.0` (thay 8.1.0 bằng version của bạn)
3. Add `C:\wamp64\bin\mysql\mysql8.0` (tương tự)

---

## Nếu bạn dùng Laragon

### Paths:
```
PHP: C:\laragon\bin\php
MySQL: C:\laragon\bin\mysql\bin
```

### Thêm vào PATH:
1. Mở Environment Variables
2. Add `C:\laragon\bin\php`
3. Add `C:\laragon\bin\mysql\bin`

---

## Xác Minh Setup

```bash
# Mở Command Prompt mới

# Test PHP
php -v

# Test MySQL
mysql -u root -e "SELECT VERSION();"

# Nên thấy version info nếu setup đúng
```

---

## Nếu Vẫn Không Hoạt Động

1. **Restart Computer** (quan trọng!)
   ```
   Mở Command Prompt mới sau khi restart
   ```

2. **Kiểm tra PATH:**
   ```bash
   echo %PATH%
   # Nên thấy paths vừa add
   ```

3. **Kiểm tra Full Path:**
   ```bash
   C:\xampp\php\php.exe -v
   # Nếu cái này hoạt động, PATH chưa add đúng
   ```

---

## Alternative: Kiểm Tra Paths Hiện Tại

```bash
# Get-Command (PowerShell)
Get-Command php
Get-Command mysql

# Hoặc (Command Prompt)
where php
where mysql

# Sẽ show paths của PHP/MySQL (nếu trong PATH)
```

---

**Hoàn thành! Sau này có thể dùng `php` và `mysql` trực tiếp** ✅

---

**HOẶC** dùng script `setup-smart.ps1` mà không cần thêm vào PATH

