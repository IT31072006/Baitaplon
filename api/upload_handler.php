<?php
// api/upload_handler.php
function uploadImage($file, $target_dir = "uploads/")
{
    // Hỗ trợ loại file
    $allowed = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];

    // Validate file
    if (!isset($file['tmp_name']) || !file_exists($file['tmp_name'])) {
        return ["error" => "File không tồn tại"];
    }

    // Kiểm tra loại file
    $finfo = finfo_open(FILEINFO_MIME_TYPE);
    $mime = finfo_file($finfo, $file['tmp_name']);
    finfo_close($finfo);

    if (!in_array($mime, $allowed)) {
        return ["error" => "Chỉ hỗ trợ JPG, PNG, GIF, WebP"];
    }

    // Kiểm tra kích thước (5MB max)
    $max_size = 5 * 1024 * 1024;
    if ($file['size'] > $max_size) {
        return ["error" => "File quá lớn (max 5MB)"];
    }

    // Tạo thư mục upload nếu chưa tồn tại
    if (!is_dir($target_dir)) {
        mkdir($target_dir, 0755, true);
    }

    // Generate tên file an toàn
    $file_extension = strtolower(pathinfo($file["name"], PATHINFO_EXTENSION));
    $safe_name = bin2hex(random_bytes(8));
    $new_file_name = time() . "_" . $safe_name . "." . $file_extension;
    $target_file = $target_dir . $new_file_name;

    // Upload file
    if (move_uploaded_file($file["tmp_name"], $target_file)) {
        // Đặt quyền file
        chmod($target_file, 0644);

        $url = "http://" . $_SERVER['SERVER_ADDR'] . "/api/" . $target_file;
        return ["success" => true, "url" => $url];
    }

    return ["error" => "Không thể upload file"];
}
