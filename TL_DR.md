# ⚡ TL;DR - Cách Sửa Nhanh Nhất

## 🔴 VẤN ĐỀ
- [ ] Đăng ký không lưu CSDL ❌
- [ ] Login admin vào customer dashboard ❌  
- [ ] Không đăng nhập được ❌

## 🟢 GIẢI PHÁP (3 BƯỚC)

### **BƯỚC 1: Rebuild App (1 phút)**
```
Android Studio: Build → Rebuild Project
```

### **BƯỚC 2: Setup Database (2 phút)**
```powershell
# Mở PowerShell ở folder BaiTapLon
cd C:\Users\Acer\AndroidStudioProjects\BaiTapLon
.\setup.ps1
```

### **BƯỚC 3: Run App (1 phút)**
```
Android Studio: Run (Shift+F10)
```

---

## ✅ TEST (2 PHÚT)

**Register:**
```
Email: test@example.com
Password: 123456
Role: Khách hàng (mặc định)
```

**Login Admin:**
```
Email: admin@restaurant.com
Password: admin123
Role: Admin
```

**Expected:**
```
✅ Register → "Đăng ký thành công"
✅ Login Customer → Customer Dashboard
✅ Login Admin → Admin Dashboard (NOT customer!)
```

---

## 🆘 NẾU VẪN LỖI

**1. PHP Server chạy?**
```bash
php -S localhost:8080
```

**2. Database có?**
```bash
mysql -u root -p -e "USE baitaplon; SELECT * FROM users;"
```

**3. Admin có?**
```bash
mysql -u root -p -e "USE baitaplon; SELECT * FROM users WHERE role='ADMIN';"
# Nên thấy admin@restaurant.com
```

**4. Logcat lỗi gì?**
```bash
adb logcat | grep "LOGIN_DEBUG\|AUTH\|ERROR"
```

---

**Done! ✅**

