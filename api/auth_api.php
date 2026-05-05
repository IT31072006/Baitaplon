<?php
include 'db_config.php';
header('Content-Type: application/json');

// Log incoming request for debugging
error_log("Auth API Request - Method: " . $_SERVER['REQUEST_METHOD'] . ", GET action: " . ($_GET['action'] ?? 'none'));

$action = $_GET['action'] ?? '';
$data = json_decode(file_get_contents('php://input'), true) ?? [];

// Debug log
error_log("Action: $action, Data: " . json_encode($data));

if ($action == 'register') {
    try {
        $email = $data['email'] ?? '';
        $pass = $data['password'] ?? '';
        // LUÔN register thành CUSTOMER, không cho user chọn
        $role = 'CUSTOMER';

        // Validate
        if (empty($email) || empty($pass)) {
            http_response_code(400);
            echo json_encode(["status" => "error", "message" => "Email và Password không được trống"]);
            exit;
        }

        if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
            http_response_code(400);
            echo json_encode(["status" => "error", "message" => "Email không hợp lệ"]);
            exit;
        }

        if (strlen($pass) < 6) {
            http_response_code(400);
            echo json_encode(["status" => "error", "message" => "Mật khẩu phải ít nhất 6 ký tự"]);
            exit;
        }

        // Hash password
        $hashedPass = password_hash($pass, PASSWORD_BCRYPT);

        $stmt = $conn->prepare("INSERT INTO users (email, password, role) VALUES (?, ?, ?)");
        $success = $stmt->execute([$email, $hashedPass, $role]);

        if ($success) {
            echo json_encode([
                "userId" => 0,
                "email" => $email,
                "token" => "reg_token_" . bin2hex(random_bytes(16)),
                "role" => $role,
                "status" => "success",
                "message" => "Đăng ký thành công. Vui lòng đăng nhập!"
            ]);
        } else {
            http_response_code(500);
            echo json_encode(["status" => "error", "message" => "Không thể tạo tài khoản"]);
        }
    } catch (PDOException $e) {
        http_response_code(400);
        if (strpos($e->getMessage(), 'Duplicate') !== false || strpos($e->getMessage(), 'UNIQUE') !== false) {
            echo json_encode(["status" => "error", "message" => "Email này đã được đăng ký"]);
        } else {
            echo json_encode(["status" => "error", "message" => "Lỗi cơ sở dữ liệu"]);
        }
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(["status" => "error", "message" => "Lỗi server: " . $e->getMessage()]);
    }
} else if ($action == 'login') {
    try {
        $email = $data['email'] ?? '';
        $pass = $data['password'] ?? '';
        $role = isset($data['role']) ? strtoupper($data['role']) : 'CUSTOMER';

        // Validate
        if (empty($email) || empty($pass)) {
            http_response_code(400);
            echo json_encode(["status" => "error", "message" => "Email và Password không được trống"]);
            exit;
        }

        if (!in_array($role, ['ADMIN', 'CUSTOMER'])) {
            $role = 'CUSTOMER';
        }

        $stmt = $conn->prepare("SELECT * FROM users WHERE email=? AND role=?");
        $stmt->execute([$email, $role]);
        $user = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($user && password_verify($pass, $user['password'])) {
            echo json_encode([
                "userId" => (int)$user['id'],
                "email" => $user['email'],
                "token" => "login_token_" . bin2hex(random_bytes(16)),
                "role" => strtoupper($user['role']),
                "status" => "success"
            ]);
        } else {
            http_response_code(401);
            echo json_encode(["status" => "error", "message" => "Sai thông tin đăng nhập"]);
        }
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(["status" => "error", "message" => "Lỗi server"]);
    }
} else {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Action không hợp lệ"]);
}
