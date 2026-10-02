<?php
session_start();
require_once "includes/auth.php";
require_role('Admin');
require_once "connect_db.php";

$type = $_GET['type'] ?? 'employees';
$format = $_GET['format'] ?? 'csv';

if ($format === 'csv') {
    header('Content-Type: text/csv');
    header('Content-Disposition: attachment; filename="' . $type . '_report_' . date('Y-m-d') . '.csv"');
    
    $output = fopen('php://output', 'w');
    
    if ($type === 'employees') {
        fputcsv($output, ['ID', 'Full Name', 'Email', 'Role', 'Status', 'Date Joined']);
        $result = $conn->query("SELECT EmployeeID, FullName, EmailAddress, Role, EmployeeStatus, CreatedAt FROM employee");
        while ($row = $result->fetch_assoc()) {
            fputcsv($output, $row);
        }
    }
    elseif ($type === 'raw_materials') {
        fputcsv($output, ['Code', 'Name', 'Category', 'Stock', 'Unit', 'Unit Cost', 'Total Value']);
        $result = $conn->query("SELECT MaterialCode, MaterialName, Category, CurrentStock, Unit, UnitCost, (CurrentStock * UnitCost) as TotalValue FROM raw_material");
        while ($row = $result->fetch_assoc()) {
            fputcsv($output, $row);
        }
    }
    elseif ($type === 'production') {
        fputcsv($output, ['Batch', 'Product', 'Quantity', 'Defective', 'Shift', 'Date', 'Status']);
        $result = $conn->query("SELECT b.BatchNumber, p.ProductName, b.QuantityProduced, b.QuantityDefective, b.Shift, b.ProductionDate, b.QualityStatus 
                                FROM production_batch b LEFT JOIN finished_good p ON b.ProductID = p.ProductID");
        while ($row = $result->fetch_assoc()) {
            fputcsv($output, $row);
        }
    }
    
    fclose($output);
    exit();
}
?>