# Bug Fixes Report

## Lỗi Đã Sửa Chữa - Dự Án BaiTapLon

### 1. **AdminViewModel.kt** ✅
**Vấn đề:** Không có phương thức xóa lỗi/thông báo thành công
**Sửa chữa:** 
- Thêm hàm `clearError()` - xóa thông báo lỗi
- Thêm hàm `clearSuccessMessage()` - xóa thông báo thành công

### 2. **AdminDashboard.kt** ✅
**Vấn đề:** Toast hiển thị liên tục vì LaunchedEffect không làm mới state
**Sửa chữa:**
- Gọi `viewModel.clearError()` sau khi hiển thị error toast
- Gọi `viewModel.clearSuccessMessage()` sau khi hiển thị success toast
- Thêm form validation trong **DishDialog**:
  - Kiểm tra tên món không trống
  - Kiểm tra giá hợp lệ (phải là số, không âm)
  - Kiểm tra mô tả không trống
  - Hiển thị lỗi validation trong dialog

### 3. **admin_api.php** ✅
**Vấn đề:** SQL Injection vulnerability, không validate input, không check file upload
**Sửa chữa:**
- Sử dụng **Prepared Statements** thay vì string concatenation
- Thêm validation input cho dish (tên, giá, mô tả)
- Validate file upload:
  - Kiểm tra MIME type (JPG, PNG, GIF, WebP)
  - Kiểm tra kích thước file (max 5MB)
  - Generate tên file an toàn (random bytes)
- Thêm try-catch exception handling
- Thêm proper HTTP response codes
- Thêm xử lý cho action không tồn tại

### 4. **auth_api.php** ✅
**Vấn đề:** Password không mã hóa, không validate email, SQL Injection risk
**Sửa chữa:**
- Hash password bằng **bcrypt** khi register
- Verify password bằng **password_verify()** khi login
- Validate email format (filter_var)
- Validate password length (minimum 6 characters)
- Validate input không trống
- Generate secure token (bin2hex(random_bytes))
- Thêm proper error handling và HTTP codes

### 5. **upload_handler.php** ✅
**Vấn đề:** Không validate file type/size, tên file không an toàn
**Sửa chữa:**
- Kiểm tra MIME type (sử dụng finfo)
- Kiểm tra kích thước file
- Generate tên file an toàn
- Trả về JSON response với error handling
- Đặt quyền file hợp lý

## Lỗi Bảo Mật Đã Sửa

| Lỗi | Mức Độ | Sửa Chữa |
|-----|--------|---------|
| SQL Injection | 🔴 Cao | Prepared Statements |
| Plain-text Password | 🔴 Cao | bcrypt Hashing |
| Input Validation | 🟠 Trung | Validation Rules |
| File Upload Risk | 🟠 Trung | Type & Size Check |
| Toast Duplicate | 🟡 Thấp | State Cleanup |

## Testing Checklist

- [ ] Kiểm tra thêm/sửa/xóa món ăn
- [ ] Kiểm tra thiết kế bàn
- [ ] Kiểm tra upload ảnh
- [ ] Kiểm tra đăng nhập/đăng ký
- [ ] Kiểm tra form validation
- [ ] Kiểm tra lỗi API handling

## API Endpoints

```
Backend: http://10.0.2.2:8080/api/

GET  /admin_api.php?action=list_dishes&restaurant_id=ID
POST /admin_api.php?action=save_dish
GET  /admin_api.php?action=delete_dish&id=ID
POST /admin_api.php?action=save_layout
GET  /admin_api.php?action=get_layout&restaurant_id=ID

POST /auth_api.php?action=register
POST /auth_api.php?action=login
```

## Ghi Chú Quan Trọng

1. **Database:** Hãy chắc chắn MySQL charset là UTF8MB4
2. **File Upload:** Tạo thư mục `api/uploads/` với quyền write
3. **Password Hashing:** Tất cả password cũ cần reset hoặc update schema
4. **API Security:** Nên thêm JWT authentication trong tương lai

