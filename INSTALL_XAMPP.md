# 📥 Cài Đặt PHP & MySQL - XAMPP (Nhanh Nhất)

## ⚡ OPTION: Cài XAMPP (Recommended) - 2 PHÚT

**XAMPP** bao gồm cả:
- ✅ PHP
- ✅ MySQL (MariaDB)
- ✅ Apache (nếu cần)

### BƯỚC 1: Download XAMPP

Vào: https://www.apachefriends.org/download.html

**Chọn phiên bản Windows:**
- Chọn "XAMPP for Windows"
- Version mới nhất (8.0 trở lên) tốt nhất

### BƯỚC 2: Cài Đặt

1. Mở file .exe vừa download
2. Click "Next" → "Next" → "Next"
3. Khi hỏi "Select Components", chọn:
   - ✅ Apache (tuỳ chọn, không bắt buộc)
   - ✅ MySQL (BẮT BUỘC)
   - ✅ PHP (BẮT BUỘC)
4. Click "Install"
5. Chờ cài (~2 phút)

### BƯỚC 3: Verify Cài Đặt

Mở PowerShell:
```powershell
cd C:\Users\Acer\AndroidStudioProjects\BaiTapLon
.\check_installations.ps1
```

**✅ Nên thấy:**
```
✅ PHP found!
   Path: C:\xampp\php\php.exe
   
✅ MySQL found!
   Path: C:\xampp\mysql\bin\mysql.exe
```

---

## BƯỚC 4: Chạy Setup Script

```powershell
.\setup-smart.ps1
```

---

## ✅ Thế là xong!

XAMPP đã cài PHP + MySQL.

---

## 🆘 Troubleshooting Setup XAMPP

### "UAC (User Account Control) hỏi permission"
→ Click "Yes" để cho phép cài đặt

### "XAMPP already installed"
→ Vô tư, nó sẽ update version mới

### "Cannot install to C:\xampp"
→ Click "Use a different folder"
→ Chọn folder khác (vd: C:\Users\YourName\xampp)

---

## 📋 Sau Khi Cài XAMPP

**Reset PATH (Optional nhưng nên làm):**

1. Thêm vào Windows PATH:
   - `C:\xampp\php`
   - `C:\xampp\mysql\bin`

2. Restart PowerShell

3. Test:
   ```powershell
   php -v
   mysql -u root -e "SELECT VERSION();"
   ```

---

**Cài XAMPP xong chưa? Báo tôi!** 😊

