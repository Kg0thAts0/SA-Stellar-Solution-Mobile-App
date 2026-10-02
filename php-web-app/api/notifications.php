<?php
header('Content-Type: application/json');
require_once "../connect_db.php";
session_start();

$notifications = [];

// Low stock alerts
$result = $conn->query("SELECT COUNT(*) as count FROM raw_material WHERE CurrentStock <= ReorderLevel");
$row = $result->fetch_assoc();
if ($row['count'] > 0) {
    $notifications[] = ['type' => 'warning', 'message' => $row['count'] . ' items are low on stock', 'icon' => 'warning', 'link' => 'inventory/raw_materials.php'];
}

// Pending QC approvals
$result = $conn->query("SELECT COUNT(*) as count FROM production_batch WHERE QualityStatus = 'Pending'");
$row = $result->fetch_assoc();
if ($row['count'] > 0) {
    $notifications[] = ['type' => 'info', 'message' => $row['count'] . ' batches pending QC approval', 'icon' => 'fact_check', 'link' => 'quality/qc_batches.php'];
}

// New employees this week
$result = $conn->query("SELECT COUNT(*) as count FROM employee WHERE CreatedAt >= DATE_SUB(NOW(), INTERVAL 7 DAY) AND Role != 'Admin'");
$row = $result->fetch_assoc();
if ($row['count'] > 0) {
    $notifications[] = ['type' => 'success', 'message' => $row['count'] . ' new employees joined this week', 'icon' => 'person_add', 'link' => 'admin/manage_employees.php'];
}

echo json_encode($notifications);
?>