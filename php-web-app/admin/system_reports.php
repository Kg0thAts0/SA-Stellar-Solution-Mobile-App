<?php
require_once "../includes/auth.php";
require_role('Admin');
require_once "../connect_db.php";

// Helper function to format numbers
function format_number($num) {
    return number_format($num ?? 0);
}

// Helper function to calculate percentage
function calculate_percentage($part, $total) {
    if ($total == 0) return 0;
    return round(($part / $total) * 100, 1);
}

// REMOVED: function h() - already defined in auth.php

// Fetch summary statistics with error handling
$stats = [];

// Total employees
$sql = "SELECT COUNT(*) as total FROM employee";
$result = $conn->query($sql);
if ($result && $result->num_rows > 0) {
    $stats['total_employees'] = $result->fetch_assoc()['total'];
} else {
    $stats['total_employees'] = 0;
}

// Employees by role
$sql = "SELECT Role, COUNT(*) as count FROM employee GROUP BY Role";
$result = $conn->query($sql);
$stats['employees_by_role'] = [];
if ($result && $result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $stats['employees_by_role'][$row['Role']] = $row['count'];
    }
}

// Employees by status
$sql = "SELECT EmployeeStatus, COUNT(*) as count FROM employee GROUP BY EmployeeStatus";
$result = $conn->query($sql);
$stats['employees_by_status'] = [];
if ($result && $result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $stats['employees_by_status'][$row['EmployeeStatus']] = $row['count'];
    }
}

// Active vs Inactive employees
$stats['active_employees'] = $stats['employees_by_status']['Active'] ?? 0;
$stats['inactive_employees'] = ($stats['employees_by_status']['Inactive'] ?? 0) + ($stats['employees_by_status']['Pending'] ?? 0);

// Recent employee additions (last 30 days)
$stats['new_employees_30d'] = 0;
$check_column = $conn->query("SHOW COLUMNS FROM employee LIKE 'CreatedAt'");
if ($check_column && $check_column->num_rows > 0) {
    $sql = "SELECT COUNT(*) as count FROM employee WHERE CreatedAt >= DATE_SUB(NOW(), INTERVAL 30 DAY)";
    $result = $conn->query($sql);
    if ($result && $result->num_rows > 0) {
        $stats['new_employees_30d'] = $result->fetch_assoc()['count'];
    }
}

// Check if production_batch table exists
$production_exists = false;
$check_table = $conn->query("SHOW TABLES LIKE 'production_batch'");
if ($check_table && $check_table->num_rows > 0) {
    $production_exists = true;
    
    $sql = "SELECT COUNT(*) as count, SUM(QuantityProduced) as total_quantity 
            FROM production_batch 
            WHERE MONTH(ProductionDate) = MONTH(CURRENT_DATE()) 
            AND YEAR(ProductionDate) = YEAR(CURRENT_DATE())";
    $result = $conn->query($sql);
    if ($result && $result->num_rows > 0) {
        $prod_data = $result->fetch_assoc();
        $stats['monthly_production_count'] = $prod_data['count'] ?? 0;
        $stats['monthly_production_quantity'] = $prod_data['total_quantity'] ?? 0;
    } else {
        $stats['monthly_production_count'] = 0;
        $stats['monthly_production_quantity'] = 0;
    }
} else {
    $stats['monthly_production_count'] = 0;
    $stats['monthly_production_quantity'] = 0;
}

// Check if quality_inspection table exists
$quality_exists = false;
$check_table = $conn->query("SHOW TABLES LIKE 'quality_inspection'");
if ($check_table && $check_table->num_rows > 0) {
    $quality_exists = true;
    
    $sql = "SELECT 
                COUNT(*) as total,
                SUM(CASE WHEN Result = 'Pass' THEN 1 ELSE 0 END) as passed,
                SUM(CASE WHEN Result = 'Fail' THEN 1 ELSE 0 END) as failed
            FROM quality_inspection 
            WHERE MONTH(InspectionDate) = MONTH(CURRENT_DATE()) 
            AND YEAR(InspectionDate) = YEAR(CURRENT_DATE())";
    $result = $conn->query($sql);
    if ($result && $result->num_rows > 0) {
        $quality_data = $result->fetch_assoc();
        $stats['monthly_inspections'] = $quality_data['total'] ?? 0;
        $stats['monthly_passed'] = $quality_data['passed'] ?? 0;
        $stats['monthly_failed'] = $quality_data['failed'] ?? 0;
        $stats['quality_pass_rate'] = calculate_percentage($stats['monthly_passed'], $stats['monthly_inspections']);
    } else {
        $stats['monthly_inspections'] = 0;
        $stats['monthly_passed'] = 0;
        $stats['monthly_failed'] = 0;
        $stats['quality_pass_rate'] = 0;
    }
} else {
    $stats['monthly_inspections'] = 0;
    $stats['monthly_passed'] = 0;
    $stats['monthly_failed'] = 0;
    $stats['quality_pass_rate'] = 0;
}

// Check if raw_material table exists
$inventory_exists = false;
$check_table = $conn->query("SHOW TABLES LIKE 'raw_material'");
if ($check_table && $check_table->num_rows > 0) {
    $inventory_exists = true;
    
    $sql = "SELECT COUNT(*) as count FROM raw_material WHERE CurrentStock <= ReorderLevel";
    $result = $conn->query($sql);
    if ($result && $result->num_rows > 0) {
        $stats['low_stock_items'] = $result->fetch_assoc()['count'];
    } else {
        $stats['low_stock_items'] = 0;
    }
    
    $sql = "SELECT SUM(CurrentStock * UnitCost) as total_value FROM raw_material";
    $result = $conn->query($sql);
    if ($result && $result->num_rows > 0) {
        $stats['inventory_value'] = $result->fetch_assoc()['total_value'] ?? 0;
    } else {
        $stats['inventory_value'] = 0;
    }
} else {
    $stats['low_stock_items'] = 0;
    $stats['inventory_value'] = 0;
}

// Get recent employees
$sql = "SELECT EmployeeID, FullName, EmailAddress, Role, EmployeeStatus, CreatedAt 
        FROM employee 
        ORDER BY EmployeeID DESC 
        LIMIT 10";
$recent_employees = $conn->query($sql);
?>
<?php include "../includes/layout.php"; ?>

<div class="page-header">
    <h1>System Reports</h1>
    <p>View comprehensive analytics and reports for factory operations</p>
</div>

<!-- Statistics Cards Row -->
<div class="dashboard-grid">
    <div class="stat-card">
        <div class="stat-icon">
            <span class="material-icons">people</span>
        </div>
        <div class="stat-info">
            <h3><?php echo format_number($stats['total_employees']); ?></h3>
            <p>Total Employees</p>
        </div>
        <div class="stat-change positive">
            <span class="material-icons">trending_up</span>
            <span>+<?php echo $stats['new_employees_30d']; ?> this month</span>
        </div>
    </div>
    
    <div class="stat-card">
        <div class="stat-icon">
            <span class="material-icons">check_circle</span>
        </div>
        <div class="stat-info">
            <h3><?php echo format_number($stats['active_employees']); ?></h3>
            <p>Active Employees</p>
        </div>
        <div class="stat-change">
            <span class="material-icons">person_off</span>
            <span><?php echo format_number($stats['inactive_employees']); ?> inactive</span>
        </div>
    </div>
    
    <?php if ($production_exists): ?>
    <div class="stat-card">
        <div class="stat-icon">
            <span class="material-icons">factory</span>
        </div>
        <div class="stat-info">
            <h3><?php echo format_number($stats['monthly_production_quantity'] ?? 0); ?></h3>
            <p>Units Produced (MTD)</p>
        </div>
        <div class="stat-change">
            <span class="material-icons">production_quantity_limits</span>
            <span><?php echo format_number($stats['monthly_production_count'] ?? 0); ?> batches</span>
        </div>
    </div>
    <?php endif; ?>
    
    <?php if ($quality_exists): ?>
    <div class="stat-card">
        <div class="stat-icon">
            <span class="material-icons">verified</span>
        </div>
        <div class="stat-info">
            <h3><?php echo $stats['quality_pass_rate'] ?? 0; ?>%</h3>
            <p>Quality Pass Rate</p>
        </div>
        <div class="stat-change <?php echo ($stats['quality_pass_rate'] ?? 0) >= 95 ? 'positive' : 'warning'; ?>">
            <span class="material-icons">fact_check</span>
            <span><?php echo format_number($stats['monthly_inspections'] ?? 0); ?> inspections</span>
        </div>
    </div>
    <?php endif; ?>
    
    <?php if ($inventory_exists): ?>
    <div class="stat-card">
        <div class="stat-icon">
            <span class="material-icons">inventory</span>
        </div>
        <div class="stat-info">
            <h3>R<?php echo format_number($stats['inventory_value'] ?? 0); ?></h3>
            <p>Inventory Value</p>
        </div>
        <div class="stat-change <?php echo ($stats['low_stock_items'] ?? 0) > 0 ? 'warning' : 'positive'; ?>">
            <span class="material-icons">warning</span>
            <span><?php echo format_number($stats['low_stock_items'] ?? 0); ?> low stock items</span>
        </div>
    </div>
    <?php endif; ?>
</div>

<!-- Employee Distribution Section -->
<div class="reports-grid">
    <div class="card">
        <div class="card-header">
            <h3>
                <span class="material-icons">pie_chart</span>
                Employee Distribution by Role
            </h3>
        </div>
        <div class="card-body">
            <?php if (!empty($stats['employees_by_role'])): ?>
            <div class="chart-container">
                <canvas id="roleChart" width="400" height="300"></canvas>
            </div>
            <div class="chart-legend mt-2">
                <?php foreach ($stats['employees_by_role'] as $role => $count): ?>
                <div class="legend-item">
                    <span class="legend-color" style="background: <?php echo getRoleColor($role); ?>"></span>
                    <span><?php echo h($role); ?>: <?php echo $count; ?> (<?php echo calculate_percentage($count, $stats['total_employees']); ?>%)</span>
                </div>
                <?php endforeach; ?>
            </div>
            <?php else: ?>
            <p class="text-center">No employee role data available.</p>
            <?php endif; ?>
        </div>
    </div>
    
    <div class="card">
        <div class="card-header">
            <h3>
                <span class="material-icons">donut_large</span>
                Employee Status Distribution
            </h3>
        </div>
        <div class="card-body">
            <?php if (!empty($stats['employees_by_status'])): ?>
            <div class="chart-container">
                <canvas id="statusChart" width="400" height="300"></canvas>
            </div>
            <div class="chart-legend mt-2">
                <?php foreach ($stats['employees_by_status'] as $status => $count): ?>
                <div class="legend-item">
                    <span class="legend-color" style="background: <?php echo getStatusColor($status); ?>"></span>
                    <span><?php echo h($status); ?>: <?php echo $count; ?> (<?php echo calculate_percentage($count, $stats['total_employees']); ?>%)</span>
                </div>
                <?php endforeach; ?>
            </div>
            <?php else: ?>
            <p class="text-center">No employee status data available.</p>
            <?php endif; ?>
        </div>
    </div>
</div>

<!-- Recent Employees Table -->
<div class="card mt-2">
    <div class="card-header">
        <h3>
            <span class="material-icons">recent_actors</span>
            Recently Added Employees
        </h3>
    </div>
    <div class="table-container">
        <table class="data-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Full Name</th>
                    <th>Email Address</th>
                    <th>Role</th>
                    <th>Status</th>
                    <th>Date Added</th>
                </tr>
            </thead>
            <tbody>
                <?php if ($recent_employees && $recent_employees->num_rows > 0): ?>
                    <?php while ($employee = $recent_employees->fetch_assoc()): ?>
                    <tr>
                        <td><?php echo h($employee['EmployeeID']); ?></td>
                        <td><?php echo h($employee['FullName']); ?></td>
                        <td><?php echo h($employee['EmailAddress']); ?></td>
                        <td><?php echo h($employee['Role']); ?></td>
                        <td>
                            <span class="badge badge-<?php echo strtolower(h($employee['EmployeeStatus'])); ?>">
                                <?php echo h($employee['EmployeeStatus']); ?>
                            </span>
                        </td>
                        <td><?php echo isset($employee['CreatedAt']) ? date('d M Y', strtotime($employee['CreatedAt'])) : 'N/A'; ?></td>
                    </tr>
                    <?php endwhile; ?>
                <?php else: ?>
                    <tr>
                        <td colspan="6" class="text-center">No employee records found.</td>
                    </tr>
                <?php endif; ?>
            </tbody>
        </table>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
<?php if (!empty($stats['employees_by_role'])): ?>
// Role Chart
const roleCtx = document.getElementById('roleChart').getContext('2d');
new Chart(roleCtx, {
    type: 'pie',
    data: {
        labels: <?php echo json_encode(array_keys($stats['employees_by_role'])); ?>,
        datasets: [{
            data: <?php echo json_encode(array_values($stats['employees_by_role'])); ?>,
            backgroundColor: ['#1a2634', '#2c3e4e', '#3d5a6c', '#c9a03d', '#e0b85b'],
            borderWidth: 0
        }]
    },
    options: {
        responsive: true,
        maintainAspectRatio: true,
        plugins: {
            legend: {
                display: false
            }
        }
    }
});
<?php endif; ?>

<?php if (!empty($stats['employees_by_status'])): ?>
// Status Chart
const statusCtx = document.getElementById('statusChart').getContext('2d');
new Chart(statusCtx, {
    type: 'doughnut',
    data: {
        labels: <?php echo json_encode(array_keys($stats['employees_by_status'])); ?>,
        datasets: [{
            data: <?php echo json_encode(array_values($stats['employees_by_status'])); ?>,
            backgroundColor: ['#2e7d32', '#ed6c02', '#c62828'],
            borderWidth: 0
        }]
    },
    options: {
        responsive: true,
        maintainAspectRatio: true,
        plugins: {
            legend: {
                display: false
            }
        }
    }
});
<?php endif; ?>
</script>

<style>
.stat-card {
    background: var(--bg-white);
    border-radius: 12px;
    padding: 20px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    box-shadow: var(--box-shadow);
    transition: var(--transition);
    position: relative;
}

.stat-card:hover {
    transform: translateY(-2px);
    box-shadow: var(--box-shadow-hover);
}

.stat-icon {
    width: 48px;
    height: 48px;
    background: var(--bg-gray);
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.stat-icon .material-icons {
    font-size: 28px;
    color: var(--accent-gold);
}

.stat-info {
    flex: 1;
    margin-left: 15px;
}

.stat-info h3 {
    font-size: 28px;
    font-weight: 700;
    color: var(--text-dark);
    margin-bottom: 4px;
}

.stat-info p {
    font-size: 13px;
    color: var(--text-gray);
    margin: 0;
}

.stat-change {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 12px;
    padding: 4px 8px;
    border-radius: 20px;
    background: var(--bg-gray);
}

.stat-change.positive {
    color: var(--success);
    background: var(--success-bg);
}

.stat-change.warning {
    color: var(--warning);
    background: var(--warning-bg);
}

.stat-change .material-icons {
    font-size: 14px;
}

.reports-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
    gap: 24px;
    margin-top: 24px;
}

.chart-container {
    max-width: 300px;
    margin: 0 auto;
}

.chart-legend {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 16px;
    margin-top: 16px;
}

.legend-item {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 13px;
}

.legend-color {
    width: 12px;
    height: 12px;
    border-radius: 3px;
}

.dashboard-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
    gap: 20px;
    margin-bottom: 20px;
}

.mt-2 {
    margin-top: 20px;
}

.text-center {
    text-align: center;
    padding: 20px;
    color: var(--text-gray);
}

.badge-active {
    background: var(--success-bg);
    color: var(--success);
}
.badge-pending {
    background: var(--warning-bg);
    color: var(--warning);
}
.badge-inactive {
    background: var(--error-bg);
    color: var(--error);
}

@media (max-width: 768px) {
    .reports-grid {
        grid-template-columns: 1fr;
    }
}
</style>

<?php
function getRoleColor($role) {
    $colors = [
        'Admin' => '#1a2634',
        'ProductionManager' => '#2c3e4e',
        'QualityController' => '#3d5a6c',
        'InventoryClerk' => '#c9a03d',
        'Supervisor' => '#e0b85b'
    ];
    return $colors[$role] ?? '#95a5a6';
}

function getStatusColor($status) {
    $colors = [
        'Active' => '#2e7d32',
        'Pending' => '#ed6c02',
        'Inactive' => '#c62828'
    ];
    return $colors[$status] ?? '#95a5a6';
}
?>

<?php include "../includes/footer.php"; ?>