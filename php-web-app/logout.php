<?php
/**
 * Secure Logout Handler
 * QA Clothing Factory System
 */

session_start();

// Security headers
header("X-Frame-Options: DENY");
header("X-Content-Type-Options: nosniff");

// Regenerate session ID for security
session_regenerate_id(true);

// Store user info for potential logging before destruction
$employee_id = $_SESSION['employee_id'] ?? null;
$full_name = $_SESSION['full_name'] ?? null;

// Handle POST request (direct logout)
if ($_SERVER["REQUEST_METHOD"] === "POST") {
    // Validate CSRF token if present
    if (isset($_SESSION['csrf_token'])) {
        if (!isset($_POST['csrf_token']) || $_POST['csrf_token'] !== $_SESSION['csrf_token']) {
            // Log CSRF failure but still proceed with logout for security
            error_log("CSRF validation failed during logout for user: " . ($full_name ?? 'unknown'));
        }
    }
    
    // Clear all session variables
    $_SESSION = array();
    
    // Delete session cookie
    if (ini_get("session.use_cookies")) {
        $params = session_get_cookie_params();
        setcookie(
            session_name(),
            '',
            time() - 42000,
            $params["path"],
            $params["domain"],
            $params["secure"],
            $params["httponly"]
        );
    }
    
    // Destroy the session
    session_destroy();
    
    // Redirect to login page
    header("Location: index.php?message=logged_out");
    exit();
}

// Handle GET request (show confirmation page)
if ($_SERVER["REQUEST_METHOD"] === "GET") {
    // If not logged in, redirect to login
    if (!isset($_SESSION['employee_id'])) {
        header("Location: index.php");
        exit();
    }
    ?>
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="robots" content="noindex, nofollow">
        <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="css/style.css">
        <title>Logout | QA Clothing Factory System</title>
        <style>
            .logout-container {
                min-height: 100vh;
                display: flex;
                align-items: center;
                justify-content: center;
                background: linear-gradient(135deg, #1a2634 0%, #2c3e4e 100%);
                padding: 20px;
            }
            .logout-card {
                background: #ffffff;
                border-radius: 16px;
                padding: 40px;
                text-align: center;
                max-width: 450px;
                width: 100%;
                box-shadow: 0 20px 40px rgba(0,0,0,0.2);
                animation: fadeInUp 0.4s ease;
            }
            @keyframes fadeInUp {
                from { opacity: 0; transform: translateY(20px); }
                to { opacity: 1; transform: translateY(0); }
            }
            .logout-icon {
                width: 80px;
                height: 80px;
                background: #ffebee;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                margin: 0 auto 24px;
            }
            .logout-icon .material-icons {
                font-size: 48px;
                color: #c62828;
            }
            .logout-card h2 {
                font-size: 24px;
                color: #2c3e50;
                margin-bottom: 12px;
            }
            .logout-card p {
                color: #6c757d;
                margin-bottom: 28px;
                line-height: 1.6;
            }
            .user-info {
                background: #f8f9fa;
                border-radius: 12px;
                padding: 16px;
                margin-bottom: 28px;
                text-align: left;
            }
            .user-info-item {
                display: flex;
                justify-content: space-between;
                margin-bottom: 8px;
                font-size: 14px;
            }
            .user-info-item:last-child {
                margin-bottom: 0;
            }
            .user-info-label {
                color: #6c757d;
                font-weight: 500;
            }
            .user-info-value {
                color: #2c3e50;
                font-weight: 600;
            }
            .logout-actions {
                display: flex;
                gap: 12px;
                justify-content: center;
            }
            .btn-cancel {
                background: transparent;
                border: 1px solid #6c757d;
                color: #6c757d;
            }
            .btn-cancel:hover {
                background: #f8f9fa;
                border-color: #2c3e50;
                color: #2c3e50;
            }
            .btn-logout {
                background: #c62828;
                color: white;
            }
            .btn-logout:hover {
                background: #b71c1c;
            }
            .btn {
                display: inline-flex;
                align-items: center;
                gap: 8px;
                padding: 12px 24px;
                border-radius: 8px;
                font-size: 14px;
                font-weight: 500;
                text-decoration: none;
                transition: all 0.3s ease;
                border: none;
                cursor: pointer;
            }
            @media (max-width: 480px) {
                .logout-card { padding: 28px 20px; }
                .logout-actions { flex-direction: column; }
                .btn { justify-content: center; }
            }
        </style>
    </head>
    <body>
        <div class="logout-container">
            <div class="logout-card">
                <div class="logout-icon">
                    <span class="material-icons">logout</span>
                </div>
                <h2>Confirm Logout</h2>
                <p>Are you sure you want to log out of the QA Clothing Factory System?</p>
                <div class="user-info">
                    <div class="user-info-item">
                        <span class="user-info-label">Full Name</span>
                        <span class="user-info-value"><?php echo htmlspecialchars($_SESSION['full_name'] ?? 'User', ENT_QUOTES, 'UTF-8'); ?></span>
                    </div>
                    <div class="user-info-item">
                        <span class="user-info-label">Role</span>
                        <span class="user-info-value"><?php echo htmlspecialchars($_SESSION['role'] ?? 'N/A', ENT_QUOTES, 'UTF-8'); ?></span>
                    </div>
                    <div class="user-info-item">
                        <span class="user-info-label">Employee ID</span>
                        <span class="user-info-value"><?php echo htmlspecialchars($_SESSION['employee_id'] ?? 'N/A', ENT_QUOTES, 'UTF-8'); ?></span>
                    </div>
                </div>
                <div class="logout-actions">
                    <button onclick="window.history.back();" class="btn btn-cancel">
                        <span class="material-icons">close</span>
                        Cancel
                    </button>
                    <form method="POST" action="" style="display: inline;">
                        <input type="hidden" name="csrf_token" value="<?php echo $_SESSION['csrf_token'] ?? ''; ?>">
                        <button type="submit" class="btn btn-logout">
                            <span class="material-icons">logout</span>
                            Yes, Logout
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </body>
    </html>
    <?php
    exit();
}

// If other request methods
header("HTTP/1.0 405 Method Not Allowed");
header("Location: index.php");
exit();
?>