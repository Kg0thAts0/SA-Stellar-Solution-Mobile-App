<?php
session_start();
require_once "connect_db.php";

$token = $_GET['token'] ?? '';
$message = '';
$message_type = '';

// Verify token
$stmt = $conn->prepare("SELECT Email, ExpiresAt FROM password_resets WHERE Token = ? AND ExpiresAt > NOW()");
$stmt->bind_param("s", $token);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    die("Invalid or expired reset link. Please request a new one.");
}

$reset = $result->fetch_assoc();
$email = $reset['Email'];

if ($_SERVER["REQUEST_METHOD"] === "POST") {
    $password = $_POST['password'];
    $confirm = $_POST['confirm_password'];
    
    if (strlen($password) < 8) {
        $message = "Password must be at least 8 characters.";
        $message_type = "error";
    } elseif ($password !== $confirm) {
        $message = "Passwords do not match.";
        $message_type = "error";
    } else {
        $hashed = password_hash($password, PASSWORD_DEFAULT);
        $stmt2 = $conn->prepare("UPDATE employee SET Password = ? WHERE EmailAddress = ?");
        $stmt2->bind_param("ss", $hashed, $email);
        $stmt2->execute();
        
        $stmt3 = $conn->prepare("DELETE FROM password_resets WHERE Token = ?");
        $stmt3->bind_param("s", $token);
        $stmt3->execute();
        
        header("Location: index.php?message=password_reset");
        exit();
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
    <title>Reset Password | QA Factory</title>
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
        .alert-error { background: #ffebee; color: #c62828; border-left: 4px solid #c62828; }
        small { display: block; margin-top: 4px; font-size: 11px; color: #6c757d; }
    </style>
</head>
<body>
    <div class="card">
        <h1>Reset Password</h1>
        <p>Enter your new password for <?php echo htmlspecialchars($email); ?></p>
        
        <?php if ($message): ?>
        <div class="alert alert-error"><?php echo $message; ?></div>
        <?php endif; ?>
        
        <form method="POST" action="">
            <div class="form-group">
                <label>New Password</label>
                <input type="password" name="password" required minlength="8">
                <small>Minimum 8 characters</small>
            </div>
            <div class="form-group">
                <label>Confirm Password</label>
                <input type="password" name="confirm_password" required>
            </div>
            <button type="submit">Reset Password</button>
        </form>
    </div>
</body>
</html>