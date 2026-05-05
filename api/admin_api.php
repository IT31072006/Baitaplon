<?php
include 'db_config.php';
header('Content-Type: application/json');

$action = $_GET['action'] ?? '';

// Hàm validate
function validateDish($name, $price, $description) {
    $errors = [];
    if (empty(trim($name))) {
        $errors[] = "Tên món không được để trống";
    }
    if (!is_numeric($price) || $price < 0) {
        $errors[] = "Giá phải là số dương";
    }
    if (empty(trim($description))) {
        $errors[] = "Mô tả không được để trống";
    }
    return $errors;
}

switch ($action) {
    // API Quản lý món ăn - dùng Prepared Statements
    case 'save_dish':
        try {
            $id = $_POST['id'] ?? null;
            $res_id = $_POST['restaurant_id'] ?? null;
            $name = $_POST['name'] ?? '';
            $price = $_POST['price'] ?? 0;
            $desc = $_POST['description'] ?? '';
            $img_url = $_POST['existing_image'] ?? "";

            // Validate input
            $errors = validateDish($name, $price, $desc);
            if (!empty($errors)) {
                http_response_code(400);
                echo json_encode(["status" => "error", "message" => implode(", ", $errors)]);
                break;
            }

            // Xử lý upload file
            if (isset($_FILES['image']) && $_FILES['image']['size'] > 0) {
                // Kiểm tra loại file
                $allowed = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
                if (!in_array($_FILES['image']['type'], $allowed)) {
                    http_response_code(400);
                    echo json_encode(["status" => "error", "message" => "Chỉ hỗ trợ JPG, PNG, GIF, WebP"]);
                    break;
                }

                // Kiểm tra kích thước (5MB max)
                if ($_FILES['image']['size'] > 5 * 1024 * 1024) {
                    http_response_code(400);
                    echo json_encode(["status" => "error", "message" => "File quá lớn (max 5MB)"]);
                    break;
                }

                $ext = pathinfo($_FILES['image']['name'], PATHINFO_EXTENSION);
                $path = "uploads/" . time() . "_" . bin2hex(random_bytes(5)) . "." . $ext;
                if (move_uploaded_file($_FILES['image']['tmp_name'], $path)) {
                    $img_url = "http://10.0.2.2:8080/api/" . $path;
                }
            }

            // Cập nhật hoặc Thêm mới dùng Prepared Statements
            if ($id) {
                $stmt = $conn->prepare("UPDATE dishes SET name=?, price=?, description=?, image_url=? WHERE id=?");
                $stmt->execute([$name, $price, $desc, $img_url, $id]);
            } else {
                $stmt = $conn->prepare("INSERT INTO dishes (restaurant_id, name, price, description, image_url) VALUES (?, ?, ?, ?, ?)");
                $stmt->execute([$res_id, $name, $price, $desc, $img_url]);
            }

            echo json_encode(["status" => "success"]);
        } catch (Exception $e) {
            http_response_code(500);
            echo json_encode(["status" => "error", "message" => "Lỗi server: " . $e->getMessage()]);
        }
        break;

    case 'delete_dish':
        try {
            $id = $_GET['id'] ?? null;
            if (!$id || !is_numeric($id)) {
                http_response_code(400);
                echo json_encode(["status" => "error", "message" => "ID không hợp lệ"]);
                break;
            }

            $stmt = $conn->prepare("DELETE FROM dishes WHERE id=?");
            $stmt->execute([$id]);
            echo json_encode(["status" => "success"]);
        } catch (Exception $e) {
            http_response_code(500);
            echo json_encode(["status" => "error", "message" => "Lỗi server"]);
        }
        break;

    case 'list_dishes':
        try {
            $res_id = $_GET['restaurant_id'] ?? null;
            if (!$res_id) {
                http_response_code(400);
                echo json_encode(["status" => "error", "message" => "Thiếu restaurant_id"]);
                break;
            }

            $stmt = $conn->prepare("SELECT * FROM dishes WHERE restaurant_id=?");
            $stmt->execute([$res_id]);
            echo json_encode($stmt->fetchAll(PDO::FETCH_ASSOC));
        } catch (Exception $e) {
            http_response_code(500);
            echo json_encode(["status" => "error", "message" => "Lỗi server"]);
        }
        break;

    // API Quản lý sơ đồ bàn - dùng Prepared Statements
    case 'save_layout':
        try {
            $data = json_decode(file_get_contents('php://input'), true);
            $res_id = $data['restaurant_id'] ?? null;
            $tables = $data['tables'] ?? [];

            if (!$res_id) {
                http_response_code(400);
                echo json_encode(["status" => "error", "message" => "Thiếu restaurant_id"]);
                break;
            }

            // Xóa bàn cũ
            $stmt = $conn->prepare("DELETE FROM tables WHERE restaurant_id=?");
            $stmt->execute([$res_id]);

            // Thêm bàn mới
            $stmt = $conn->prepare("INSERT INTO tables (restaurant_id, table_code, capacity, pos_x, pos_y) VALUES (?, ?, ?, ?, ?)");
            foreach ($tables as $t) {
                if (isset($t['table_code']) && isset($t['capacity']) && isset($t['pos_x']) && isset($t['pos_y'])) {
                    $stmt->execute([$res_id, $t['table_code'], $t['capacity'], $t['pos_x'], $t['pos_y']]);
                }
            }
            echo json_encode(["status" => "success"]);
        } catch (Exception $e) {
            http_response_code(500);
            echo json_encode(["status" => "error", "message" => "Lỗi server"]);
        }
        break;

    case 'get_layout':
        try {
            $res_id = $_GET['restaurant_id'] ?? null;
            if (!$res_id) {
                http_response_code(400);
                echo json_encode(["status" => "error", "message" => "Thiếu restaurant_id"]);
                break;
            }

            $stmt = $conn->prepare("SELECT * FROM tables WHERE restaurant_id=?");
            $stmt->execute([$res_id]);
            echo json_encode($stmt->fetchAll(PDO::FETCH_ASSOC));
        } catch (Exception $e) {
            http_response_code(500);
            echo json_encode(["status" => "error", "message" => "Lỗi server"]);
        }
        break;

    default:
        http_response_code(400);
        echo json_encode(["status" => "error", "message" => "Action không hợp lệ"]);
}
