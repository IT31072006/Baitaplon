# 🔧 3 CÁCH SETUP - CHỌN 1 CÁCH PHỤC HỢP

## ⚡ OPTION 1: Tự Động - PowerShell Script ⭐ **RECOMMENDED**

**Dễ nhất, nhanh nhất**

```powershell
# PowerShell
cd C:\Users\Acer\AndroidStudioProjects\BaiTapLon
.\setup.ps1
```

**Lợi ích:**
- ✅ Tự động tạo DB, import schema, tạo admin
- ✅ Tự động start PHP server
- ✅ Chỉ cần chạy 1 lệnh

**Nếu lỗi "< operator":**
- Đã fix rồi ✅ Hãy chạy lại

---

## ⚡ OPTION 2: Tự Động - Batch Script

**Dành cho Command Prompt (nếu PowerShell không hoạt động)**

```bash
# Command Prompt (cmd.exe)
cd C:\Users\Acer\AndroidStudioProjects\BaiTapLon
setup.bat
```

**Lợi ích:**
- ✅ Hoạt động với Command Prompt
- ✅ Tương tự PowerShell script
- ✅ Backup nếu PowerShell lỗi

---

## ⚡ OPTION 3: Manual Setup

**Dành cho người chưa dùng script**

```bash
# Mở Command Prompt / PowerShell
mysql -u root
```

**Rồi làm theo file:** `MANUAL_SETUP.md`

**Lợi ích:**
- ✅ Hiểu từng bước làm gì
- ✅ Debug dễ hơn
- ✅ Tịnh chỉ được từng phần nếu cần

---

## 🚀 MY RECOMMENDATION

**Thử theo thứ tự này:**

```
1. Thử OPTION 1 (PowerShell Script) - nhanh nhất
   ↓ (Nếu lỗi)
2. Thử OPTION 2 (Batch Script)
   ↓ (Nếu lỗi)
3. Làm OPTION 3 (Manual Setup)
```

---

## ✅ VERIFY SETUP

**Sau khi setup (dù cách nào), kiểm tra:**

```bash
# Command Prompt
mysql -u root -e "USE baitaplon; SELECT * FROM users WHERE role='ADMIN';"

# ✅ Nên thấy admin@restaurant.com
```

---

## 📋 FULL SETUP FLOW

```
1. Chọn 1 trong 3 cách setup
   ↓
2. Rebuild App (Android Studio: Build → Rebuild Project)
   ↓
3. Run App (Android Studio: Run - Shift+F10)
   ↓
4. Test Register + Login
```

---

## 🎯 AFTER SETUP

**Hãy đọc tiếp:**
- `FINAL_INSTRUCTIONS.md` - Hướng dẫn test chi tiết
- `TL_DR.md` - Phiên bản ngắn nhất
- `DEBUG_GUIDE.md` - Nếu có lỗi

---

**Ready? Chọn 1 cách setup ở trên! 💪**

