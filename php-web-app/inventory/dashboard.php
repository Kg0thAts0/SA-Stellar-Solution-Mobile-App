<?php
// Start session
session_start();

// Include database connection - using absolute path to be safe
require_once __DIR__ . '/../connect_db.php';

// Check if connection exists
if (!isset($conn)) {
    die("Database connection failed. Please check connect_db.php");
}

// Helper function for safe output
function h($str) {
    return htmlspecialchars($str ?? '', ENT_QUOTES, 'UTF-8');
}

// Helper function to format numbers
function format_number($num) {
    return number_format($num ?? 0);
}

// Access control
if (!isset($_SESSION['employee_id']) || $_SESSION['role'] !== 'InventoryClerk') {
    header("Location: ../index.php");
    exit();
}

// Fetch inventory statistics
$stats = [];

// Total raw materials
$sql = "SELECT COUNT(*) as total FROM raw_material";
$result = $conn->query($sql);
if ($result && $result->num_rows > 0) {
    $row = $result->fetch_assoc();
    $stats['total_raw_materials'] = $row['total'];
} else {
    $stats['total_raw_materials'] = 0;
}

// Total finished goods
$sql = "SELECT COUNT(*) as total FROM finished_good";
$result = $conn->query($sql);
if ($result && $result->num_rows > 0) {
    $row = $result->fetch_assoc();
    $stats['total_finished_goods'] = $row['total'];
} else {
    $stats['total_finished_goods'] = 0;
}

// Low stock items
$sql = "SELECT COUNT(*) as count FROM raw_material WHERE CurrentStock <= ReorderLevel";
$result = $conn->query($sql);
if ($result && $result->num_rows > 0) {
    $row = $result->fetch_assoc();
    $stats['low_stock_count'] = $row['count'];
} else {
    $stats['low_stock_count'] = 0;
}

// Total inventory value
$sql = "SELECT SUM(CurrentStock * UnitCost) as total_value FROM raw_material";
$result = $conn->query($sql);
if ($result && $result->num_rows > 0) {
    $row = $result->fetch_assoc();
    $stats['inventory_value'] = $row['total_value'] ?? 0;
} else {
    $stats['inventory_value'] = 0;
}

// Low stock items list
$sql = "SELECT MaterialID, MaterialName, CurrentStock, ReorderLevel, Unit
        FROM raw_material 
        WHERE CurrentStock <= ReorderLevel 
        ORDER BY (CurrentStock / ReorderLevel) ASC 
        LIMIT 5";
$low_stock_items = $conn->query($sql);
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <title>Inventory Dashboard | QA Factory</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Inter', sans-serif;
            background-color: #f4f6f9;
            color: #2c3e50;
        }
        
        /* Top Navigation */
        .top-nav {
            background: #1a2634;
            color: white;
            padding: 12px 24px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            z-index: 100;
        }
        
        .nav-left {
            display: flex;
            align-items: center;
            gap: 12px;
        }
        
        .nav-left span {
            font-size: 16px;
            font-weight: 600;
        }
        
        .nav-right span {
            font-size: 14px;
        }
        
        /* Main Container */
        .dashboard-container {
            max-width: 1200px;
            margin: 80px auto 40px;
            padding: 0 20px;
        }
        
        .page-header {
            margin-bottom: 24px;
        }
        
        .page-header h1 {
            font-size: 28px;
            font-weight: 600;
            margin-bottom: 8px;
        }
        
        .page-header p {
            color: #6c757d;
        }
        
        /* Stats Grid */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        
        .stat-card {
            background: white;
            border-radius: 12px;
            padding: 20px;
            display: flex;
            align-items: center;
            gap: 16px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        }
        
        .stat-icon {
            width: 50px;
            height: 50px;
            background: #f8f9fa;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        
        .stat-icon .material-icons {
            font-size: 28px;
            color: #c9a03d;
        }
        
        .stat-info h3 {
            font-size: 28px;
            font-weight: 700;
            margin-bottom: 4px;
        }
        
        .stat-info p {
            font-size: 13px;
            color: #6c757d;
        }
        
        /* Action Cards */
        .action-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        
        .action-card {
            background: white;
            border-radius: 12px;
            padding: 24px;
            text-align: center;
            text-decoration: none;
            border: 1px solid #e0e0e0;
            transition: all 0.3s ease;
            display: block;
        }
        
        .action-card:hover {
            transform: translateY(-4px);
            border-color: #c9a03d;
            box-shadow: 0 4px 16px rgba(0,0,0,0.12);
        }
        
        .action-card .material-icons:first-child {
            font-size: 48px;
            color: #c9a03d;
            margin-bottom: 16px;
        }
        
        .action-card h3 {
            font-size: 18px;
            color: #2c3e50;
            margin-bottom: 8px;
        }
        
        .action-card p {
            font-size: 13px;
            color: #6c757d;
        }
        
        /* Card */
        .card {
            background: white;
            border-radius: 12px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
            overflow: hidden;
            margin-bottom: 30px;
        }
        
        .card-header {
            padding: 16px 20px;
            border-bottom: 1px solid #e0e0e0;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .card-header h3 {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 16px;
        }
        
        .card-header .material-icons {
            color: #c9a03d;
        }
        
        /* Table */
        .table-container {
            overflow-x: auto;
        }
        
        .data-table {
            width: 100%;
            border-collapse: collapse;
        }
        
        .data-table th {
            background: #1a2634;
            color: white;
            padding: 12px 16px;
            text-align: left;
            font-size: 13px;
        }
        
        .data-table td {
            padding: 10px 16px;
            border-bottom: 1px solid #eee;
            font-size: 14px;
        }
        
        .data-table tr:hover {
            background: #f8f9fa;
        }
        
        .warning-row {
            background-color: #fff4e5;
        }
        
        .critical-value {
            color: #c62828;
            font-weight: 700;
        }
        
        .badge {
            display: inline-block;
            padding: 4px 10px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 500;
        }
        
        .badge-critical {
            background: #ffebee;
            color: #c62828;
        }
        
        .btn-sm {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            padding: 6px 12px;
            font-size: 12px;
            background: #1a2634;
            color: white;
            text-decoration: none;
            border-radius: 6px;
        }
        
        .btn-outline {
            background: transparent;
            border: 1px solid #c9a03d;
            color: #c9a03d;
        }
        
        .alert-badge {
            display: inline-block;
            padding: 4px 12px;
            background: #ffebee;
            color: #c62828;
            border-radius: 20px;
            font-size: 12px;
        }
        
        .critical-alert {
            border-left: 4px solid #c62828;
        }
        
        @media (max-width: 768px) {
            .stats-grid {
                grid-template-columns: repeat(2, 1fr);
            }
            .stat-info h3 {
                font-size: 22px;
            }
        }
    </style>
</head>
<body>

<header class="top-nav">
    <div class="nav-left">
        <span>QA Clothing Factory System</span>
    </div>
    <div class="nav-right">
        <span><?php echo h($_SESSION['full_name']); ?></span>
    </div>
</header>

<div class="dashboard-container">
    <div class="page-header">
        <h1>Inventory Dashboard</h1>
        <p>Welcome back, <strong><?php echo h($_SESSION['full_name']); ?></strong>. Monitor and manage factory inventory.</p>
    </div>

    <!-- Statistics Cards -->
    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-icon"><span class="material-icons">inventory_2</span></div>
            <div class="stat-info">
                <h3><?php echo format_number($stats['total_raw_materials']); ?></h3>
                <p>Raw Materials</p>
            </div>
        </div>
        
        <div class="stat-card">
            <div class="stat-icon"><span class="material-icons">checkroom</span></div>
            <div class="stat-info">
                <h3><?php echo format_number($stats['total_finished_goods']); ?></h3>
                <p>Finished Goods</p>
            </div>
        </div>
        
        <div class="stat-card">
            <div class="stat-icon"><span class="material-icons">warning</span></div>
            <div class="stat-info">
                <h3><?php echo format_number($stats['low_stock_count']); ?></h3>
                <p>Low Stock Items</p>
            </div>
        </div>
        
        <div class="stat-card">
            <div class="stat-icon"><span class="material-icons">monetization_on</span></div>
            <div class="stat-info">
                <h3>R<?php echo format_number($stats['inventory_value']); ?></h3>
                <p>Total Value</p>
            </div>
        </div>
    </div>

    <!-- Action Cards -->
    <div class="action-grid">
        <a href="raw_materials.php" class="action-card">
            <span class="material-icons">inventory_2</span>
            <h3>Manage Raw Materials</h3>
            <p>Add, update, and track materials</p>
        </a>
        
        <a href="finished_goods.php" class="action-card">
            <span class="material-icons">checkroom</span>
            <h3>Record Finished Goods</h3>
            <p>Log completed production items</p>
        </a>
        
        <a href="stock_levels.php" class="action-card">
            <span class="material-icons">warehouse</span>
            <h3>Stock Levels</h3>
            <p>Monitor inventory availability</p>
        </a>
    </div>

    <!-- Low Stock Alert -->
    <?php if ($low_stock_items && $low_stock_items->num_rows > 0): ?>
    <div class="card critical-alert">
        <div class="card-header">
            <h3><span class="material-icons">priority_high</span> Low Stock Alert</h3>
            <span class="alert-badge">Requires Attention</span>
        </div>
        <div class="table-container">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Material</th>
                        <th>Current Stock</th>
                        <th>Reorder Level</th>
                        <th>Unit</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <?php while ($item = $low_stock_items->fetch_assoc()): ?>
                    <tr class="warning-row">
                        <td><?php echo h($item['MaterialName']); ?></td>
                        <td class="critical-value"><?php echo format_number($item['CurrentStock']); ?></td>
                        <td><?php echo format_number($item['ReorderLevel']); ?></td>
                        <td><?php echo h($item['Unit']); ?></td>
                        <td><a href="raw_materials.php" class="btn-sm btn-outline">Reorder</a></td>
                    </tr>
                    <?php endwhile; ?>
                </tbody>
            </table>
        </div>
    </div>
    <?php else: ?>
    <div class="card">
        <div class="card-header">
            <h3><span class="material-icons">check_circle</span> Stock Status</h3>
        </div>
        <div class="table-container">
            <p style="padding: 20px; text-align: center; color: #2e7d32;">All stock levels are healthy. No low stock items.</p>
        </div>
    </div>
    <?php endif; ?>
</div>

</body>
</html>