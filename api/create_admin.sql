-- SQL Script tạo tài khoản Admin mặc định
-- Chạy script này trong phpMyAdmin hoặc MySQL client

USE baitaplon;

-- Xóa admin cũ nếu tồn tại (tuỳ chọn)
-- DELETE FROM users WHERE email = 'admin@test.com' AND role = 'ADMIN';

-- Tạo tài khoản Admin mặc định
-- Password hashed: password_hash('admin123', PASSWORD_BCRYPT)
-- Tương ứng với: 123456 (thay đổi sau)
INSERT INTO users (email, password, role, full_name) VALUES
('admin@restaurant.com', '$2y$10$Qi3UKPVJLaXsHNYCEPNNXONEu2eE70F4F/2RKCLTdpJKRsZSuP/i.', 'ADMIN', 'Admin Nhà Hàng');

-- Kiểm tra
SELECT * FROM users WHERE role = 'ADMIN';

-- Hàng dưới là user để test với mật khẩu:
-- Email: admin@restaurant.com
-- Password: admin123
-- Role: ADMIN

