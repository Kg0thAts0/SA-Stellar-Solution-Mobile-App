<?php
session_start();

require_once "includes/auth.php";
require_once "connect_db.php";

// Only allow POST requests
if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    header("Location: index.php");
    exit();
}

// Validate CSRF token
validate_csrf();

// Get and sanitize inputs
$email = trim($_POST['email'] ?? '');
$password = $_POST['password'] ?? '';

// Validate inputs
if (empty($email) || empty($password)) {
    $_SESSION['error'] = "Email and password are required.";
    header("Location: index.php");
    exit();
}

// Validate email format
if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    $_SESSION['error'] = "Invalid email format.";
    header("Location: index.php");
    exit();
}

// Prepare and execute query
$sql = "SELECT EmployeeID, FullName, Role, Password, EmployeeStatus
        FROM Employee
        WHERE EmailAddress = ?";
        
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows !== 1) {
    $_SESSION['error'] = "Invalid login credentials.";
    header("Location: index.php");
    exit();
}

$user = $result->fetch_assoc();

// Verify password
if (!password_verify($password, $user['Password'])) {
    $_SESSION['error'] = "Invalid login credentials.";
    header("Location: index.php");
    exit();
}

// Check account status
if ($user['EmployeeStatus'] !== 'Active') {
    $_SESSION['error'] = "Your account is not active. Please contact administration.";
    header("Location: index.php");
    exit();
}

// Regenerate session ID for security
session_regenerate_id(true);

// Set session variables
$_SESSION['employee_id'] = $user['EmployeeID'];
$_SESSION['full_name'] = $user['FullName'];
$_SESSION['role'] = $user['Role'];
$_SESSION['employee_status'] = $user['EmployeeStatus'];
$_SESSION['last_activity'] = time();

// Role-based redirection
$redirects = [
    'Admin' => 'admin/dashboard.php',
    'ProductionManager' => 'production/dashboard.php',
    'QualityController' => 'quality/dashboard.php',
    'InventoryClerk' => 'inventory/dashboard.php',
    'Supervisor' => 'supervisor/dashboard.php'
];

$redirect = $redirects[$user['Role']] ?? 'dashboard.php';

$stmt->close();
$conn->close();

header("Location: $redirect");
exit();
?>