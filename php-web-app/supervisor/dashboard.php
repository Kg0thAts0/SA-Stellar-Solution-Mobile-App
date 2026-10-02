<?php
require_once "../includes/auth.php";
require_role('Supervisor');
require_once "../connect_db.php";

// Fetch supervisor statistics
$stats = [];

// Today's production
$sql = "SELECT SUM(QuantityProduced) as total FROM production_batch WHERE DATE(ProductionDate) = CURDATE()";
$result = $conn->query($sql);
$stats['today_production'] = $result->fetch_assoc()['total'] ?? 0;

// Today's batches
$sql = "SELECT COUNT(*) as count FROM production_batch WHERE DATE(ProductionDate) = CURDATE()";
$result = $conn->query($sql);
$stats['today_batches'] = $result->fetch_assoc()['count'] ?? 0;

// Open QC issues (Pending or Conditional)
$sql = "SELECT COUNT(*) as count FROM production_batch WHERE QualityStatus IN ('Pending', 'Conditional')";
$result = $conn->query($sql);
$stats['open_qc_issues'] = $result->fetch_assoc()['count'] ?? 0;

// Rejected batches
$sql = "SELECT COUNT(*) as count FROM production_batch WHERE QualityStatus = 'Rejected'";
$result = $conn->query($sql);
$stats['rejected_batches'] = $result->fetch_assoc()['count'] ?? 0;

// Low stock items (raw_material table)
$sql = "SELECT COUNT(*) as count FROM raw_material WHERE CurrentStock <= ReorderLevel";
$result = $conn->query($sql);
$stats['low_stock_items'] = $result->fetch_assoc()['count'] ?? 0;

// Out of stock items
$sql = "SELECT COUNT(*) as count FROM raw_material WHERE CurrentStock = 0";
$result = $conn->query($sql);
$stats['out_of_stock_items'] = $result->fetch_assoc()['count'] ?? 0;

// Recent QC issues (Failed inspections)
$sql = "SELECT i.*, b.BatchNumber, p.ProductName
        FROM quality_inspection i
        JOIN production_batch b ON i.BatchID = b.BatchID
        LEFT JOIN finished_good p ON b.ProductID = p.ProductID
        WHERE i.Result = 'Fail'
        ORDER BY i.InspectionDate DESC LIMIT 5";
$qc_issues = $conn->query($sql);

// Weekly production trend
$weekly_data = [];
for ($i = 6; $i >= 0; $i--) {
    $date = date('Y-m-d', strtotime("-$i days"));
    $label = date('D', strtotime("-$i days"));
    $sql = "SELECT SUM(QuantityProduced) as total FROM production_batch WHERE DATE(ProductionDate) = '$date'";
    $result = $conn->query($sql);
    $total = $result->fetch_assoc()['total'] ?? 0;
    $weekly_data[] = ['label' => $label, 'total' => $total];
}
?>
<?php include "../includes/layout.php"; ?>

<div class="page-header">
    <h1>Supervisor Dashboard</h1>
    <p>Welcome back, <strong><?php echo h($_SESSION['full_name']); ?></strong>. Monitor factory operations and key metrics.</p>
</div>

<!-- Statistics Cards -->
<div class="dashboard-grid">
    <div class="stat-card">
        <div class="stat-icon"><span class="material-icons">factory</span></div>
        <div class="stat-info">
            <h3><?php echo number_format($stats['today_production']); ?></h3>
            <p>Today's Production</p>
            <small><?php echo $stats['today_batches']; ?> batches</small>
        </div>
        <?php if ($stats['today_production'] > 0): ?>
        <div class="stat-change positive">
            <span class="material-icons">trending_up</span>
            <span>Active</span>
        </div>
        <?php else: ?>
        <div class="stat-change warning">
            <span class="material-icons">pause</span>
            <span>No output</span>
        </div>
        <?php endif; ?>
    </div>
    
    <div class="stat-card">
        <div class="stat-icon"><span class="material-icons">warning</span></div>
        <div class="stat-info">
            <h3><?php echo $stats['open_qc_issues']; ?></h3>
            <p>Open QC Issues</p>
            <small><?php echo $stats['rejected_batches']; ?> rejected</small>
        </div>
        <?php if ($stats['open_qc_issues'] > 0): ?>
        <div class="stat-change warning">
            <span class="material-icons">priority_high</span>
            <span>Action needed</span>
        </div>
        <?php else: ?>
        <div class="stat-change positive">
            <span class="material-icons">check_circle</span>
            <span>All clear</span>
        </div>
        <?php endif; ?>
    </div>
    
    <div class="stat-card">
        <div class="stat-icon"><span class="material-icons">inventory</span></div>
        <div class="stat-info">
            <h3><?php echo $stats['low_stock_items']; ?></h3>
            <p>Low Stock Items</p>
            <small><?php echo $stats['out_of_stock_items']; ?> out of stock</small>
        </div>
        <?php if ($stats['low_stock_items'] > 0): ?>
        <div class="stat-change warning">
            <span class="material-icons">priority_high</span>
            <span>Reorder needed</span>
        </div>
        <?php else: ?>
        <div class="stat-change positive">
            <span class="material-icons">check_circle</span>
            <span>Stock healthy</span>
        </div>
        <?php endif; ?>
    </div>
</div>

<!-- Quick Action Cards -->
<div class="dashboard-grid mt-2">
    <a href="production_overview.php" class="action-card">
        <span class="material-icons">visibility</span>
        <h3>Production Overview</h3>
        <p>View daily output and line status</p>
        <span class="action-link">View Details <span class="material-icons">arrow_forward</span></span>
    </a>
    
    <a href="qc_issues.php" class="action-card">
        <span class="material-icons">report_problem</span>
        <h3>QC Issues</h3>
        <p>Monitor rejected batches</p>
        <?php if ($stats['rejected_batches'] > 0): ?>
        <span class="notification-badge"><?php echo $stats['rejected_batches']; ?></span>
        <?php endif; ?>
        <span class="action-link">View Issues <span class="material-icons">arrow_forward</span></span>
    </a>
    
    <a href="inventory_snapshot.php" class="action-card">
        <span class="material-icons">snapshot</span>
        <h3>Inventory Snapshot</h3>
        <p>Check stock levels</p>
        <?php if ($stats['low_stock_items'] > 0): ?>
        <span class="notification-badge"><?php echo $stats['low_stock_items']; ?></span>
        <?php endif; ?>
        <span class="action-link">View Snapshot <span class="material-icons">arrow_forward</span></span>
    </a>
</div>

<!-- Weekly Production Chart -->
<div class="card mt-2">
    <div class="card-header">
        <h3><span class="material-icons">show_chart</span> Weekly Production Trend</h3>
    </div>
    <div class="card-body">
        <canvas id="weeklyChart" height="200"></canvas>
    </div>
</div>

<!-- Recent QC Issues -->
<?php if ($qc_issues && $qc_issues->num_rows > 0): ?>
<div class="card mt-2">
    <div class="card-header">
        <h3><span class="material-icons">error</span> Recent Quality Issues</h3>
        <span class="badge badge-danger"><?php echo $qc_issues->num_rows; ?> issues</span>
    </div>
    <div class="table-container">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Batch</th>
                    <th>Product</th>
                    <th>Defect Type</th>
                    <th>Date</th>
                    <th>Remarks</th>
                </tr>
            </thead>
            <tbody>
                <?php while ($issue = $qc_issues->fetch_assoc()): ?>
                <tr class="warning-row">
                    <td><?php echo h($issue['BatchNumber']); ?></td>
                    <td><?php echo h($issue['ProductName'] ?? 'N/A'); ?></td>
                    <td><?php echo h($issue['DefectType'] ?? 'N/A'); ?></td>
                    <td><?php echo date('d M Y', strtotime($issue['InspectionDate'])); ?></td>
                    <td><?php echo h($issue['Remarks'] ?? '-'); ?></td>
                </tr>
                <?php endwhile; ?>
            </tbody>
        </table>
    </div>
</div>
<?php endif; ?>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
// Weekly Production Chart
const ctx = document.getElementById('weeklyChart').getContext('2d');
const weeklyData = <?php 
    $labels = array_column($weekly_data, 'label');
    $values = array_column($weekly_data, 'total');
    echo json_encode(['labels' => $labels, 'values' => $values]);
?>;

new Chart(ctx, {
    type: 'bar',
    data: {
        labels: weeklyData.labels,
        datasets: [{
            label: 'Units Produced',
            data: weeklyData.values,
            backgroundColor: '#c9a03d',
            borderRadius: 4,
            barPercentage: 0.6
        }]
    },
    options: {
        responsive: true,
        maintainAspectRatio: true,
        plugins: {
            legend: {
                display: false
            }
        },
        scales: {
            y: {
                beginAtZero: true,
                ticks: {
                    callback: function(value) {
                        return value.toLocaleString();
                    }
                }
            }
        }
    }
});
</script>

<style>
.dashboard-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
    gap: 20px;
    margin-bottom: 20px;
}
.stat-card {
    background: white;
    border-radius: 12px;
    padding: 20px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
    position: relative;
}
.stat-icon {
    width: 52px;
    height: 52px;
    background: #f8f9fa;
    border-radius: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
}
.stat-icon .material-icons {
    font-size: 28px;
    color: #c9a03d;
}
.stat-info {
    flex: 1;
    margin-left: 15px;
}
.stat-info h3 {
    font-size: 28px;
    font-weight: 700;
    margin-bottom: 4px;
}
.stat-info p {
    font-size: 13px;
    color: #6c757d;
    margin: 0;
}
.stat-info small {
    font-size: 11px;
    color: #6c757d;
}
.stat-change {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 11px;
    padding: 4px 10px;
    border-radius: 20px;
    position: absolute;
    bottom: 12px;
    right: 12px;
}
.stat-change.positive {
    color: #2e7d32;
    background: #e8f5e9;
}
.stat-change.warning {
    color: #ed6c02;
    background: #fff4e5;
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
    position: relative;
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
    margin-bottom: 8px;
}
.action-card p {
    font-size: 13px;
    color: #6c757d;
    margin-bottom: 16px;
}
.action-link {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    font-size: 13px;
    font-weight: 500;
    color: #c9a03d;
}
.notification-badge {
    position: absolute;
    top: 12px;
    right: 12px;
    background: #c62828;
    color: white;
    font-size: 11px;
    font-weight: bold;
    padding: 2px 8px;
    border-radius: 20px;
}
.card {
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
    overflow: hidden;
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
    margin: 0;
}
.card-header .material-icons {
    color: #c9a03d;
}
.card-body {
    padding: 20px;
}
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
    background: #fff4e5;
}
.badge {
    display: inline-block;
    padding: 4px 10px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 500;
}
.badge-danger {
    background: #ffebee;
    color: #c62828;
}
.mt-2 { margin-top: 20px; }
</style>

<?php include "../includes/footer.php"; ?>