<?php
require_once "includes/auth.php";
require_once "connect_db.php";

// Check if user is logged in
if (!isset($_SESSION['employee_id'])) {
    header("Location: index.php");
    exit();
}

// Get user details
$employee_id = $_SESSION['employee_id'];
$sql = "SELECT EmployeeID, FullName, EmailAddress, Role, EmployeeStatus, CreatedAt FROM employee WHERE EmployeeID = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $employee_id);
$stmt->execute();
$result = $stmt->get_result();
$user = $result->fetch_assoc();

// Handle profile update
$message = '';
$message_type = '';

if ($_SERVER["REQUEST_METHOD"] === "POST") {
    if (isset($_POST['update_profile'])) {
        $fullname = trim($_POST['fullname']);
        
        if (!empty($fullname)) {
            $update_sql = "UPDATE employee SET FullName = ? WHERE EmployeeID = ?";
            $update_stmt = $conn->prepare($update_sql);
            $update_stmt->bind_param("si", $fullname, $employee_id);
            
            if ($update_stmt->execute()) {
                $_SESSION['full_name'] = $fullname;
                $message = "Profile updated successfully.";
                $message_type = "success";
                $user['FullName'] = $fullname;
            } else {
                $message = "Failed to update profile.";
                $message_type = "error";
            }
        }
    }
    
    if (isset($_POST['change_password'])) {
        $current_password = $_POST['current_password'];
        $new_password = $_POST['new_password'];
        $confirm_password = $_POST['confirm_password'];
        
        $pass_sql = "SELECT Password FROM employee WHERE EmployeeID = ?";
        $pass_stmt = $conn->prepare($pass_sql);
        $pass_stmt->bind_param("i", $employee_id);
        $pass_stmt->execute();
        $pass_result = $pass_stmt->get_result();
        $pass_row = $pass_result->fetch_assoc();
        
        if (password_verify($current_password, $pass_row['Password'])) {
            if ($new_password === $confirm_password) {
                if (strlen($new_password) >= 8) {
                    $new_hash = password_hash($new_password, PASSWORD_DEFAULT);
                    $update_sql = "UPDATE employee SET Password = ? WHERE EmployeeID = ?";
                    $update_stmt = $conn->prepare($update_sql);
                    $update_stmt->bind_param("si", $new_hash, $employee_id);
                    
                    if ($update_stmt->execute()) {
                        $message = "Password changed successfully.";
                        $message_type = "success";
                    } else {
                        $message = "Failed to change password.";
                        $message_type = "error";
                    }
                } else {
                    $message = "Password must be at least 8 characters.";
                    $message_type = "error";
                }
            } else {
                $message = "Passwords do not match.";
                $message_type = "error";
            }
        } else {
            $message = "Current password is incorrect.";
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
    <link rel="stylesheet" href="css/style.css">
    <title>My Profile | QA Factory</title>
    <style>
        .profile-container {
            max-width: 900px;
            margin: 80px auto 40px;
            padding: 0 20px;
        }
        .profile-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
            gap: 24px;
        }
        .card {
            background: white;
            border-radius: 16px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
            overflow: hidden;
        }
        .card-header {
            padding: 20px 24px;
            border-bottom: 1px solid #e0e0e0;
            background: #f8f9fa;
        }
        .card-header h2 {
            display: flex;
            align-items: center;
            gap: 10px;
            font-size: 18px;
        }
        .card-header .material-icons { color: #c9a03d; }
        .card-body { padding: 24px; }
        .info-row {
            display: flex;
            justify-content: space-between;
            padding: 12px 0;
            border-bottom: 1px solid #eee;
        }
        .info-label { font-weight: 500; color: #6c757d; }
        .info-value { font-weight: 600; color: #2c3e50; }
        .form-group { margin-bottom: 20px; }
        .form-group label {
            display: block;
            font-size: 13px;
            font-weight: 500;
            margin-bottom: 6px;
        }
        .form-group input {
            width: 100%;
            padding: 10px 12px;
            border: 1px solid #ddd;
            border-radius: 8px;
            font-size: 14px;
        }
        .form-actions {
            display: flex;
            gap: 12px;
            margin-top: 24px;
            padding-top: 16px;
            border-top: 1px solid #eee;
        }
        .btn {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            padding: 10px 20px;
            border-radius: 8px;
            font-size: 14px;
            font-weight: 500;
            cursor: pointer;
            border: none;
            text-decoration: none;
        }
        .btn-primary { background: #1a2634; color: white; }
        .btn-primary:hover { background: #2c3e4e; }
        .btn-secondary { background: #e0e0e0; color: #2c3e50; }
        .btn-danger { background: #c62828; color: white; }
        .alert {
            padding: 12px 16px;
            border-radius: 8px;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        .alert-success { background: #e8f5e9; color: #2e7d32; border-left: 4px solid #2e7d32; }
        .alert-error { background: #ffebee; color: #c62828; border-left: 4px solid #c62828; }
        .badge {
            display: inline-block;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 12px;
        }
        .badge-active { background: #e8f5e9; color: #2e7d32; }
        .badge-pending { background: #fff4e5; color: #ed6c02; }
        .badge-inactive { background: #ffebee; color: #c62828; }
        hr { margin: 16px 0; border: none; border-top: 1px solid #eee; }
        small { display: block; margin-top: 4px; font-size: 11px; color: #6c757d; }
        @media (max-width: 768px) { .profile-grid { grid-template-columns: 1fr; } }
    </style>
</head>
<body>

<!-- Top Navigation -->
<nav class="top-nav">
    <div class="nav-container">
        <div class="nav-brand">
            <span class="brand-name">QA Clothing Factory System</span>
        </div>
        <div class="nav-user">
            <span class="user-name"><?php echo h($_SESSION['full_name']); ?></span>
            <span class="user-role"><?php echo h($_SESSION['role']); ?></span>
            <a href="profile.php" class="profile-link">
                <span class="material-icons">account_circle</span>
            </a>
            <a href="logout.php" class="logout-link" onclick="return confirm('Logout?')">
                <span class="material-icons">logout</span>
            </a>
        </div>
    </div>
</nav>

<!-- Sidebar -->
<aside class="sidebar" id="sidebar">
    <div class="sidebar-header">
        <h3>QA Factory</h3>
    </div>
    <nav class="sidebar-nav">
        <ul class="sidebar-menu">
            <li class="sidebar-item">
                <?php
                $dashboard_link = '';
                switch ($_SESSION['role']) {
                    case 'Admin': $dashboard_link = 'admin/dashboard.php'; break;
                    case 'ProductionManager': $dashboard_link = 'production/dashboard.php'; break;
                    case 'QualityController': $dashboard_link = 'quality/dashboard.php'; break;
                    case 'InventoryClerk': $dashboard_link = 'inventory/dashboard.php'; break;
                    case 'Supervisor': $dashboard_link = 'supervisor/dashboard.php'; break;
                    default: $dashboard_link = 'index.php';
                }
                ?>
                <a href="<?php echo $dashboard_link; ?>" class="sidebar-link">
                    <span class="material-icons">dashboard</span>
                    <span class="sidebar-text">Dashboard</span>
                </a>
            </li>
            <li class="sidebar-divider">Account</li>
            <li class="sidebar-item">
                <a href="profile.php" class="sidebar-link active">
                    <span class="material-icons">account_circle</span>
                    <span class="sidebar-text">My Profile</span>
                </a>
            </li>
            <li class="sidebar-item">
                <a href="logout.php" class="sidebar-link" onclick="return confirm('Logout?')">
                    <span class="material-icons">logout</span>
                    <span class="sidebar-text">Logout</span>
                </a>
            </li>
        </ul>
    </nav>
</aside>

<!-- Main Content -->
<main class="main-content">
    <div class="container-fluid">
        <div class="profile-container" style="margin-top: 0;">
            <div class="page-header">
                <h1>My Profile</h1>
                <p>View and manage your personal information</p>
            </div>
            
            <?php if ($message): ?>
            <div class="alert alert-<?php echo $message_type; ?>">
                <span class="material-icons"><?php echo $message_type === 'success' ? 'check_circle' : 'error'; ?></span>
                <span><?php echo h($message); ?></span>
            </div>
            <?php endif; ?>
            
            <div class="profile-grid">
                <!-- Profile Card -->
                <div class="card">
                    <div class="card-header">
                        <h2><span class="material-icons">account_circle</span> Profile Information</h2>
                    </div>
                    <div class="card-body">
                        <div class="info-row"><span class="info-label">Employee ID</span><span class="info-value"><?php echo h($user['EmployeeID']); ?></span></div>
                        <div class="info-row"><span class="info-label">Full Name</span><span class="info-value"><?php echo h($user['FullName']); ?></span></div>
                        <div class="info-row"><span class="info-label">Email Address</span><span class="info-value"><?php echo h($user['EmailAddress']); ?></span></div>
                        <div class="info-row"><span class="info-label">Role</span><span class="info-value"><?php echo h($user['Role']); ?></span></div>
                        <div class="info-row"><span class="info-label">Status</span><span class="info-value"><span class="badge badge-<?php echo strtolower($user['EmployeeStatus']); ?>"><?php echo h($user['EmployeeStatus']); ?></span></span></div>
                        <div class="info-row"><span class="info-label">Member Since</span><span class="info-value"><?php echo date('d M Y', strtotime($user['CreatedAt'])); ?></span></div>
                    </div>
                </div>
                
                <!-- Update Profile Card -->
                <div class="card">
                    <div class="card-header">
                        <h2><span class="material-icons">edit</span> Update Profile</h2>
                    </div>
                    <div class="card-body">
                        <form method="POST" action="">
                            <div class="form-group">
                                <label>Full Name</label>
                                <input type="text" name="fullname" value="<?php echo h($user['FullName']); ?>" required>
                            </div>
                            <div class="form-group">
                                <label>Email Address</label>
                                <input type="email" value="<?php echo h($user['EmailAddress']); ?>" disabled>
                                <small>Email cannot be changed. Contact administrator.</small>
                            </div>
                            <div class="form-actions">
                                <button type="submit" name="update_profile" class="btn btn-primary">Update Profile</button>
                            </div>
                        </form>
                    </div>
                </div>
                
                <!-- Change Password Card -->
                <div class="card">
                    <div class="card-header">
                        <h2><span class="material-icons">lock</span> Change Password</h2>
                    </div>
                    <div class="card-body">
                        <form method="POST" action="" onsubmit="return validatePassword()">
                            <div class="form-group">
                                <label>Current Password</label>
                                <input type="password" name="current_password" id="current_password" required>
                            </div>
                            <div class="form-group">
                                <label>New Password</label>
                                <input type="password" name="new_password" id="new_password" required>
                                <small>Minimum 8 characters</small>
                            </div>
                            <div class="form-group">
                                <label>Confirm Password</label>
                                <input type="password" name="confirm_password" id="confirm_password" required>
                                <span id="password_match_msg" style="font-size: 11px;"></span>
                            </div>
                            <div class="form-actions">
                                <button type="submit" name="change_password" class="btn btn-primary">Change Password</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

<button class="sidebar-toggle" id="sidebarToggle">
    <span class="material-icons">menu</span>
</button>

<script>
function validatePassword() {
    var newPass = document.getElementById('new_password').value;
    var confirmPass = document.getElementById('confirm_password').value;
    var msgSpan = document.getElementById('password_match_msg');
    
    if (newPass !== confirmPass) {
        msgSpan.innerHTML = 'Passwords do not match';
        msgSpan.style.color = '#c62828';
        return false;
    } else if (confirmPass.length > 0) {
        msgSpan.innerHTML = 'Passwords match';
        msgSpan.style.color = '#2e7d32';
    }
    
    if (newPass.length > 0 && newPass.length < 8) {
        alert('Password must be at least 8 characters');
        return false;
    }
    return true;
}

document.getElementById('sidebarToggle')?.addEventListener('click', function() {
    document.getElementById('sidebar')?.classList.toggle('collapsed');
    document.body.classList.toggle('sidebar-collapsed');
});
</script>

</body>
</html>