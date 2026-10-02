<?php
header('Content-Type: application/json');
require_once "../connect_db.php";
session_start();

$type = $_GET['type'] ?? '';

if ($type === 'production') {
    $data = ['months' => [], 'totals' => []];
    for ($i = 5; $i >= 0; $i--) {
        $month = date('Y-m', strtotime("-$i months"));
        $data['months'][] = date('M Y', strtotime("-$i months"));
        $result = $conn->query("SELECT SUM(QuantityProduced) as total FROM production_batch WHERE DATE_FORMAT(ProductionDate, '%Y-%m') = '$month'");
        $row = $result->fetch_assoc();
        $data['totals'][] = $row['total'] ?? 0;
    }
    echo json_encode($data);
}
elseif ($type === 'quality') {
    $result = $conn->query("SELECT 
        SUM(CASE WHEN Result = 'Pass' THEN 1 ELSE 0 END) as pass,
        SUM(CASE WHEN Result = 'Fail' THEN 1 ELSE 0 END) as fail,
        SUM(CASE WHEN Result = 'Conditional' THEN 1 ELSE 0 END) as conditional
        FROM quality_inspection WHERE MONTH(InspectionDate) = MONTH(CURRENT_DATE())");
    $row = $result->fetch_assoc();
    echo json_encode(['pass' => $row['pass'] ?? 0, 'fail' => $row['fail'] ?? 0, 'conditional' => $row['conditional'] ?? 0]);
}
?>