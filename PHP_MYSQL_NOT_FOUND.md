# ⚡ QUICK START - PHP & MySQL Not Found

## 🚨 Vấn Đề
```
❌ php : The term 'php' is not recognized...
❌ mysql : The term 'mysql' is not recognized...
```

## ✅ Giải Pháp (3 Lựa Chọn)

---

## OPTION 1: Dùng Script Auto-Detect (Nhanh nhất) ⭐

```powershell
# PowerShell
cd C:\Users\Acer\AndroidStudioProjects\BaiTapLon
.\setup-smart.ps1
```

**Lợi ích:**
- ✅ Tự động tìm PHP & MySQL
- ✅ Không cần setup PATH
- ✅ Chạy 1 lệnh xong

---

## OPTION 2: Tìm Paths & Update Script

### BƯỚC 1: Tìm paths
```powershell
cd C:\Users\Acer\AndroidStudioProjects\BaiTapLon
.\find_paths.ps1
```

### BƯỚC 2: Copy paths output
```
Giả sử output là:
✅ Found php.exe: C:\xampp\php\php.exe
✅ Found mysql.exe: C:\xampp\mysql\bin\mysql.exe
```

### BƯỚC 3: Edit file (Notepad)
```
Mở: setup-smart.ps1
Tìm dòng 20: 
    "C:\xampp\php\php.exe",
Tìm dòng 38:
    "C:\xampp\mysql\bin\mysql.exe",
Thay bằng paths bạn vừa tìm được
Lưu file
```

### BƯỚC 4: Chạy
```powershell
.\setup-smart.ps1
```

---

## OPTION 3: Thêm vào Windows PATH (Lâu Dài)

1. Đọc file: `ADD_TO_PATH.md`
2. Làm theo hướng dẫn
3. Restart Windows
4. Chạy: `.\setup.ps1`

---

## OPTION 4: Manual Setup (Không dùng Script)

1. Đọc file: `MANUAL_SETUP.md`
2. Làm từng bước

---

## 🎯 My Recommendation

**Thử theo thứ tự:**

```
1. Chạy .\setup-smart.ps1 (nhanh nhất)
   ↓ (Nếu lỗi)
2. Chạy .\find_paths.ps1 rồi update script
   ↓ (Nếu vẫn lỗi)
3. Thêm vào Windows PATH (hành động dài hạn)
   ↓ (Nếu không muốn script)
4. Làm manual setup
```

---

## ✅ SAU SETUP

1. **Rebuild App**
   ```
   Android Studio: Build → Rebuild Project
   ```

2. **Run App**
   ```
   Shift+F10
   ```

3. **Test**
   - Register email/password mới
   - Login với email vừa register (role: "Khách hàng")
   - Login với email: admin@restaurant.com, password: admin123 (role: "Admin")

---

## 🆘 If Still Not Working

**Run này:**
```powershell
cd C:\Users\Acer\AndroidStudioProjects\BaiTapLon
.\find_paths.ps1
```

**Báo cho tôi kết quả:**
- Câu lệnh hiển thị những gì?
- Có tìm thấy PHP không?
- Có tìm thấy MySQL không?

---

**Ready? Start with Option 1! 💪**

