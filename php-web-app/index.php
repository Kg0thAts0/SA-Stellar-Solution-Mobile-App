<?php
session_start();

// Redirect if already logged in
if (isset($_SESSION['employee_id'])) {
    $role = $_SESSION['role'];
    $redirects = [
        'Admin' => 'admin/dashboard.php',
        'ProductionManager' => 'production/dashboard.php',
        'QualityController' => 'quality/dashboard.php',
        'InventoryClerk' => 'inventory/dashboard.php',
        'Supervisor' => 'supervisor/dashboard.php'
    ];
    $redirect = $redirects[$role] ?? 'dashboard.php';
    header("Location: $redirect");
    exit();
}

// Generate CSRF token for login form
if (empty($_SESSION['csrf_token'])) {
    $_SESSION['csrf_token'] = bin2hex(random_bytes(32));
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="css/style.css">
    <title>Login | QA Clothing Factory System</title>
</head>
<body class="login-page">
    <div class="login-card">
        <img src="assets/logo.jpg" alt="QA Clothing Factory Logo" class="login-logo">
        <h2>QA Clothing Factory System</h2>
        <p class="subtitle">Internal staff access only</p>
        
        <?php if (isset($_SESSION['error'])): ?>
        <div class="alert alert-error">
            <span class="material-icons">error</span>
            <span><?php echo htmlspecialchars($_SESSION['error'], ENT_QUOTES, 'UTF-8'); ?></span>
        </div>
        <?php unset($_SESSION['error']); endif; ?>
        
        <form method="POST" action="login_process.php" class="login-form">
            <input type="hidden" name="csrf_token" value="<?php echo $_SESSION['csrf_token']; ?>">
            
            <div class="form-group">
                <label>Email Address</label>
                <input type="email" name="email" class="form-control" placeholder="employee@qafactory.com" required autofocus>
            </div>
            
            <div class="form-group">
                <label>Password</label>
                <input type="password" name="password" class="form-control" placeholder="Enter your password" required>
            </div>
            
            <button type="submit" class="btn btn-primary login-btn">
                <span class="material-icons">login</span>
                Sign In
            </button>
        </form>
    </div>
</body>
</html>