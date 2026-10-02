<?php
/**
 * Audit Logging Function
 * Include this file to log user actions
 */

function log_activity($action, $table_name = null, $record_id = null, $old_value = null, $new_value = null) {
    global $conn;
    
    if (!isset($_SESSION['employee_id'])) {
        return false;
    }
    
    $employee_id = $_SESSION['employee_id'];
    $employee_name = $_SESSION['full_name'] ?? 'Unknown';
    $ip_address = $_SERVER['REMOTE_ADDR'] ?? '0.0.0.0';
    $user_agent = $_SERVER['HTTP_USER_AGENT'] ?? '';
    
    // Truncate long values
    if (is_array($old_value)) $old_value = json_encode($old_value);
    if (is_array($new_value)) $new_value = json_encode($new_value);
    if (strlen($old_value) > 500) $old_value = substr($old_value, 0, 500) . '...';
    if (strlen($new_value) > 500) $new_value = substr($new_value, 0, 500) . '...';
    
    $sql = "INSERT INTO audit_log (EmployeeID, EmployeeName, Action, TableName, RecordID, OldValue, NewValue, IPAddress, UserAgent) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("isssissss", $employee_id, $employee_name, $action, $table_name, $record_id, $old_value, $new_value, $ip_address, $user_agent);
    
    return $stmt->execute();
}

// Log login activity
function log_login($email, $success) {
    $action = $success ? 'Login Successful' : 'Login Failed';
    log_activity($action, 'employee', null, null, "Email: $email");
}

// Log logout activity
function log_logout() {
    log_activity('Logout', null, null, null, null);
}

// Log employee actions
function log_employee_action($action, $employee_id, $employee_name, $old_status = null, $new_status = null) {
    log_activity($action, 'employee', $employee_id, $old_status, $new_status);
}
?>