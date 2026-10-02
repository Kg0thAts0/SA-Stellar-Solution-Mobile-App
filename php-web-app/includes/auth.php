<?php
/**
 * Secure Authentication Handler
 * QA Clothing Factory System
 */

// Start session only if not already started
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

// Security Headers
header("X-Frame-Options: DENY");
header("X-Content-Type-Options: nosniff");
header("X-XSS-Protection: 1; mode=block");

// Session Timeout (30 minutes)
$timeout = 1800;
if (isset($_SESSION['last_activity']) && (time() - $_SESSION['last_activity'] > $timeout)) {
    $_SESSION = array();
    
    if (isset($_COOKIE[session_name()])) {
        setcookie(session_name(), '', time() - 3600, '/');
    }
    
    session_destroy();
    header("Location: ../index.php?error=session_expired");
    exit();
}
$_SESSION['last_activity'] = time();

// CSRF Token Management
if (empty($_SESSION['csrf_token'])) {
    $_SESSION['csrf_token'] = bin2hex(random_bytes(32));
}

/**
 * Safe Output Function - Check if not already declared
 */
if (!function_exists('h')) {
    function h($str) {
        return htmlspecialchars($str ?? '', ENT_QUOTES, 'UTF-8');
    }
}

/**
 * Validate CSRF Token
 */
function validate_csrf() {
    if (!isset($_POST['csrf_token']) || $_POST['csrf_token'] !== $_SESSION['csrf_token']) {
        $_SESSION['error'] = "Invalid request. Please try again.";
        header("Location: " . ($_SERVER['HTTP_REFERER'] ?? '../index.php'));
        exit();
    }
}

/**
 * Check if user is logged in
 */
function require_login() {
    if (!isset($_SESSION['employee_id'])) {
        header("Location: ../index.php");
        exit();
    }
}

/**
 * Check for specific role
 */
function require_role($role) {
    if (!isset($_SESSION['employee_id']) || $_SESSION['role'] !== $role) {
        header("Location: ../index.php");
        exit();
    }
}

/**
 * Check for multiple roles
 */
function require_any_role($roles) {
    if (!isset($_SESSION['employee_id']) || !in_array($_SESSION['role'], $roles)) {
        header("Location: ../index.php");
        exit();
    }
}

// Include database connection
require_once __DIR__ . '/../connect_db.php';
?>