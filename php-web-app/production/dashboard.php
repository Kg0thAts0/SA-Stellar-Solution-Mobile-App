<?php
require_once "../includes/auth.php";
require_role('ProductionManager');
require_once "../connect_db.php";

function format_number($num) {
    return number_format($num ?? 0);
}

$stats = [];

// Today's production
$sql = "SELECT SUM(QuantityProduced) as total, COUNT(*) as batches 
        FROM production_batch 
        WHERE DATE(ProductionDate) = CURDATE()";
$result = $conn->query($sql);
$today = $result->fetch_assoc();
$stats['today_quantity'] = $today['total'] ?? 0;
$stats['today_batches'] = $today['batches'] ?? 0;

// Weekly production
$sql = "SELECT SUM(QuantityProduced) as total 
        FROM production_batch 
        WHERE ProductionDate >= DATE_SUB(NOW(), INTERVAL 7 DAY)";
$result = $conn->query($sql);
$stats['weekly_quantity'] = $result->fetch_assoc()['total'] ?? 0;

// Monthly production
$sql = "SELECT SUM(QuantityProduced) as total 
        FROM production_batch 
        WHERE MONTH(ProductionDate) = MONTH(CURRENT_DATE()) 
        AND YEAR(ProductionDate) = YEAR(CURRENT_DATE())";
$result = $conn->query($sql);
$stats['monthly_quantity'] = $result->fetch_assoc()['total'] ?? 0;

// Defective rate
$sql = "SELECT SUM(QuantityDefective) as defective, SUM(QuantityProduced) as total 
        FROM production_batch 
        WHERE ProductionDate >= DATE_SUB(NOW(), INTERVAL 30 DAY)";
$result = $conn->query($sql);
$data = $result->fetch_assoc();
$stats['defective_rate'] = $data['total'] > 0 ? round(($data['defective'] / $data['total']) * 100, 1) : 0;

// Recent batches
$sql = "SELECT b.*, p.ProductName 
        FROM production_batch b
        LEFT JOIN finished_good p ON b.ProductID = p.ProductID
        ORDER BY b.ProductionDate DESC 
        LIMIT 10";
$recent_batches = $conn->query($sql);
?>
<?php include "../includes/layout.php"; ?>

<div class="page-header">
    <h1>Production Dashboard</h1>
    <p>Welcome back, <strong><?php echo h($_SESSION['full_name']); ?></strong>. Monitor and manage production operations.</p>
</div>

<div class="dashboard-grid">
    <div class="stat-card">
        <div class="stat-icon"><span class="material-icons">today</span></div>
        <div class="stat-info">
            <h3><?php echo format_number($stats['today_quantity']); ?></h3>
            <p>Today's Production</p>
            <small><?php echo $stats['today_batches']; ?> batches</small>
        </div>
    </div>
    
    <div class="stat-card">
        <div class="stat-icon"><span class="material-icons">date_range</span></div>
        <div class="stat-info">
            <h3><?php echo format_number($stats['weekly_quantity']); ?></h3>
            <p>This Week</p>
        </div>
    </div>
    
    <div class="stat-card">
        <div class="stat-icon"><span class="material-icons">calendar_month</span></div>
        <div class="stat-info">
            <h3><?php echo format_number($stats['monthly_quantity']); ?></h3>
            <p>This Month</p>
        </div>
    </div>
    
    <div class="stat-card">
        <div class="stat-icon"><span class="material-icons">warning</span></div>
        <div class="stat-info">
            <h3><?php echo $stats['defective_rate']; ?>%</h3>
            <p>Defective Rate (30d)</p>
        </div>
        <?php if ($stats['defective_rate'] > 5): ?>
        <div class="stat-change warning">Above target</div>
        <?php else: ?>
        <div class="stat-change positive">Within target</div>
        <?php endif; ?>
    </div>
</div>

<div class="dashboard-grid mt-2">
    <a href="record_output.php" class="action-card">
        <span class="material-icons">add_circle</span>
        <h3>Record Production Output</h3>
        <p>Log daily production results</p>
        <span class="action-link">Record Now <span class="material-icons">arrow_forward</span></span>
    </a>
    
    <a href="performance.php" class="action-card">
        <span class="material-icons">analytics</span>
        <h3>Line Performance</h3>
        <p>View production efficiency stats</p>
        <span class="action-link">View Reports <span class="material-icons">arrow_forward</span></span>
    </a>
    
    <a href="inventory.php" class="action-card">
        <span class="material-icons">inventory</span>
        <h3>Raw Materials</h3>
        <p>Check material availability</p>
        <span class="action-link">Check Stock <span class="material-icons">arrow_forward</span></span>
    </a>
</div>

<div class="card mt-2">
    <div class="card-header">
        <h3><span class="material-icons">history</span> Recent Production Batches</h3>
    </div>
    <div class="table-container">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Batch Number</th>
                    <th>Product</th>
                    <th>Quantity</th>
                    <th>Defective</th>
                    <th>Quality Status</th>
                    <th>Date</th>
                </tr>
            </thead>
            <tbody>
                <?php if ($recent_batches && $recent_batches->num_rows > 0): ?>
                    <?php while ($batch = $recent_batches->fetch_assoc()): ?>
                    <tr>
                        <td><?php echo h($batch['BatchNumber']); ?></td>
                        <td><?php echo h($batch['ProductName'] ?? 'N/A'); ?></td>
                        <td><?php echo format_number($batch['QuantityProduced']); ?></td>
                        <td class="<?php echo $batch['QuantityDefective'] > 0 ? 'negative' : ''; ?>">
                            <?php echo format_number($batch['QuantityDefective']); ?>
                        </td>
                        <td>
                            <span class="badge badge-<?php echo strtolower($batch['QualityStatus']); ?>">
                                <?php echo h($batch['QualityStatus']); ?>
                            </span>
                        </td>
                        <td><?php echo date('d M Y', strtotime($batch['ProductionDate'])); ?></td>
                    </tr>
                    <?php endwhile; ?>
                <?php else: ?>
                    <tr>
                        <td colspan="6" class="text-center">No production records found.</td>
                    </tr>
                <?php endif; ?>
            </tbody>
        </table>
    </div>
</div>

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
    padding: 4px 8px;
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
.text-center { text-align: center; padding: 20px; }
.mt-2 { margin-top: 20px; }
.negative { color: #c62828; font-weight: 600; }
.badge {
    display: inline-block;
    padding: 4px 10px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 500;
}
.badge-approved { background: #e8f5e9; color: #2e7d32; }
.badge-pending { background: #fff4e5; color: #ed6c02; }
.badge-rejected { background: #ffebee; color: #c62828; }
.badge-conditional { background: #e3f2fd; color: #0288d1; }
</style>

<?php include "../includes/footer.php"; ?>