<?php
session_start();
require_once "connect_db.php";

$message = '';
$message_type = '';

if ($_SERVER["REQUEST_METHOD"] === "POST") {
    $email = trim($_POST['email'] ?? '');
    
    if (empty($email)) {
        $message = "Please enter your email address.";
        $message_type = "error";
    } elseif (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $message = "Invalid email format.";
        $message_type = "error";
    } else {
        $stmt = $conn->prepare("SELECT EmployeeID, FullName FROM employee WHERE EmailAddress = ?");
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $result = $stmt->get_result();
        
        if ($result->num_rows > 0) {
            $user = $result->fetch_assoc();
            $token = bin2hex(random_bytes(32));
            $expires = date('Y-m-d H:i:s', strtotime('+1 hour'));
            
            $stmt2 = $conn->prepare("INSERT INTO password_resets (Email, Token, ExpiresAt) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE Token = ?, ExpiresAt = ?");
            $stmt2->bind_param("sssss", $email, $token, $expires, $token, $expires);
            $stmt2->execute();
            
            $reset_link = "http://" . $_SERVER['HTTP_HOST'] . "/QAClothingFactory/reset_password.php?token=" . $token;
            
            $message = "Password reset link has been sent. <a href='$reset_link'>Click here to reset password</a>";
            $message_type = "success";
        } else {
            $message = "No account found with that email address.";
            $message_type = "error";
        }
    }
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <title>Forgot Password | QA Factory</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Inter', sans-serif; background: linear-gradient(135deg, #1a2634 0%, #2c3e4e 100%); min-height: 100vh; display: flex; align-items: center; justify-content: center; padding: 20px; }
        .card { background: white; border-radius: 16px; padding: 40px; max-width: 450px; width: 100%; box-shadow: 0 20px 40px rgba(0,0,0,0.2); }
        .card h1 { font-size: 24px; margin-bottom: 8px; }
        .card p { color: #6c757d; margin-bottom: 24px; }
        .form-group { margin-bottom: 20px; }
        label { display: block; font-size: 13px; font-weight: 500; margin-bottom: 6px; }
        input { width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 8px; font-size: 14px; }
        button { width: 100%; padding: 12px; background: #1a2634; color: white; border: none; border-radius: 8px; font-size: 14px; cursor: pointer; }
        button:hover { background: #2c3e4e; }
        .alert { padding: 12px; border-radius: 8px; margin-bottom: 20px; }
        .alert-success { background: #e8f5e9; color: #2e7d32; border-left: 4px solid #2e7d32; }
        .alert-error { background: #ffebee; color: #c62828; border-left: 4px solid #c62828; }
        .back-link { display: block; text-align: center; margin-top: 20px; color: #6c757d; text-decoration: none; font-size: 13px; }
        .back-link:hover { color: #1a2634; }
    </style>
</head>
<body>
    <div class="card">
        <h1>Forgot Password</h1>
        <p>Enter your email address to receive a password reset link.</p>
        
        <?php if ($message): ?>
        <div class="alert alert-<?php echo $message_type; ?>"><?php echo $message; ?></div>
        <?php endif; ?>
        
        <form method="POST" action="">
            <div class="form-group">
                <label>Email Address</label>
                <input type="email" name="email" required autofocus>
            </div>
            <button type="submit">Send Reset Link</button>
        </form>
        <a href="index.php" class="back-link">Back to Login</a>
    </div>
</body>
</html>