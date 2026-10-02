<?php
/**
 * Add Employee Process Handler
 * QA Clothing Factory System
 */

// Start session if not already started
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

// Security headers
header("X-Frame-Options: DENY");
header("X-Content-Type-Options: nosniff");

// Include authentication and database
require_once "../includes/auth.php";
require_role('Admin');
require_once "../connect_db.php";

// Only allow POST requests
if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    header("Location: add_employee.php");
    exit();
}

// Validate CSRF token
validate_csrf();

// Get and sanitize inputs
$fullname = trim($_POST['fullname'] ?? '');
$email = trim($_POST['email'] ?? '');
$password = $_POST['password'] ?? '';
$confirm_password = $_POST['confirm_password'] ?? '';
$role = $_POST['role'] ?? '';
$status = $_POST['status'] ?? 'Pending';

// Validation
$errors = [];

// Check for empty fields
if (empty($fullname)) {
    $errors[] = "Full name is required.";
}
if (empty($email)) {
    $errors[] = "Email address is required.";
}
if (empty($password)) {
    $errors[] = "Password is required.";
}
if (empty($role)) {
    $errors[] = "Role is required.";
}

// Validate email format
if (!empty($email) && !filter_var($email, FILTER_VALIDATE_EMAIL)) {
    $errors[] = "Invalid email format.";
}

// Validate password match
if (!empty($password) && $password !== $confirm_password) {
    $errors[] = "Passwords do not match.";
}

// Validate password strength
if (!empty($password) && strlen($password) < 8) {
    $errors[] = "Password must be at least 8 characters long.";
}

// Validate role against whitelist
$allowed_roles = ['ProductionManager', 'QualityController', 'InventoryClerk', 'Supervisor'];
if (!empty($role) && !in_array($role, $allowed_roles)) {
    $errors[] = "Invalid role selected.";
}

// Validate status against whitelist
$allowed_statuses = ['Pending', 'Active', 'Inactive'];
if (!in_array($status, $allowed_statuses)) {
    $status = 'Pending';
}

// Check if email already exists
if (!empty($email)) {
    $check_sql = "SELECT EmployeeID FROM employee WHERE EmailAddress = ?";
    $check_stmt = $conn->prepare($check_sql);
    
    if ($check_stmt) {
        $check_stmt->bind_param("s", $email);
        $check_stmt->execute();
        $check_stmt->store_result();
        
        if ($check_stmt->num_rows > 0) {
            $errors[] = "An employee with this email address already exists.";
        }
        $check_stmt->close();
    } else {
        $errors[] = "Database error: " . $conn->error;
    }
}

// If errors exist, redirect back with error messages
if (!empty($errors)) {
    $_SESSION['error'] = implode(" ", $errors);
    header("Location: add_employee.php");
    exit();
}

// Hash the password
$hashed_password = password_hash($password, PASSWORD_DEFAULT);

// Check if CreatedAt column exists (for compatibility)
$created_at_column = false;
$check_column = $conn->query("SHOW COLUMNS FROM employee LIKE 'CreatedAt'");
if ($check_column && $check_column->num_rows > 0) {
    $created_at_column = true;
}

// Insert into database based on available columns
if ($created_at_column) {
    $sql = "INSERT INTO employee (FullName, EmailAddress, Password, Role, EmployeeStatus, CreatedAt, CreatedBy) 
            VALUES (?, ?, ?, ?, ?, NOW(), ?)";
    $stmt = $conn->prepare($sql);
    
    if ($stmt) {
        $stmt->bind_param("sssssi", $fullname, $email, $hashed_password, $role, $status, $_SESSION['employee_id']);
    }
} else {
    $sql = "INSERT INTO employee (FullName, EmailAddress, Password, Role, EmployeeStatus, CreatedBy) 
            VALUES (?, ?, ?, ?, ?, ?)";
    $stmt = $conn->prepare($sql);
    
    if ($stmt) {
        $stmt->bind_param("sssssi", $fullname, $email, $hashed_password, $role, $status, $_SESSION['employee_id']);
    }
}

// Check if statement was prepared successfully
if (!$stmt) {
    $_SESSION['error'] = "Database error: " . $conn->error;
    error_log("Prepare failed: " . $conn->error);
    header("Location: add_employee.php");
    exit();
}

// Execute the statement
if ($stmt->execute()) {
    $_SESSION['success'] = "Employee added successfully. They can now log in with their credentials.";
    header("Location: manage_employees.php");
    exit();
} else {
    $_SESSION['error'] = "Failed to add employee: " . $stmt->error;
    error_log("Execute failed: " . $stmt->error);
    header("Location: add_employee.php");
    exit();
}

$stmt->close();
$conn->close();
?>