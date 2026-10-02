<?php
require_once "../includes/auth.php";
require_role('QualityController');
require_once "../connect_db.php";

// Fetch quality statistics
$stats = [];

// Today's inspections
$sql = "SELECT COUNT(*) as count FROM quality_inspection WHERE DATE(InspectionDate) = CURDATE()";
$result = $conn->query($sql);
$stats['today_inspections'] = $result->fetch_assoc()['count'] ?? 0;

// Pending batches for inspection
$sql = "SELECT COUNT(*) as count FROM production_batch WHERE QualityStatus = 'Pending'";
$result = $conn->query($sql);
$stats['pending_batches'] = $result->fetch_assoc()['count'] ?? 0;

// Pass rate this month
$sql = "SELECT 
            COUNT(*) as total,
            SUM(CASE WHEN Result = 'Pass' THEN 1 ELSE 0 END) as passed
        FROM quality_inspection 
        WHERE MONTH(InspectionDate) = MONTH(CURRENT_DATE())";
$result = $conn->query($sql);
$data = $result->fetch_assoc();
$stats['pass_rate'] = $data['total'] > 0 ? round(($data['passed'] / $data['total']) * 100, 1) : 0;

// Recent inspections
$sql = "SELECT i.*, b.BatchNumber, p.ProductName 
        FROM quality_inspection i
        JOIN production_batch b ON i.BatchID = b.BatchID
        LEFT JOIN finished_good p ON b.ProductID = p.ProductID
        ORDER BY i.InspectionDate DESC LIMIT 10";
$recent_inspections = $conn->query($sql);
?>
<?php include "../includes/layout.php"; ?>

<div class="page-header">
    <h1>Quality Control Dashboard</h1>
    <p>Welcome back, <strong><?php echo h($_SESSION['full_name']); ?></strong>. Monitor and manage quality inspections.</p>
</div>

<div class="dashboard-grid">
    <div class="stat-card">
        <div class="stat-icon"><span class="material-icons">fact_check</span></div>
        <div class="stat-info">
            <h3><?php echo $stats['today_inspections']; ?></h3>
            <p>Inspections Today</p>
        </div>
    </div>
    
    <div class="stat-card">
        <div class="stat-icon"><span class="material-icons">pending</span></div>
        <div class="stat-info">
            <h3><?php echo $stats['pending_batches']; ?></h3>
            <p>Pending Batches</p>
        </div>
        <?php if ($stats['pending_batches'] > 0): ?>
        <div class="stat-change warning">
            <span class="material-icons">priority_high</span>
            <span>Requires attention</span>
        </div>
        <?php endif; ?>
    </div>
    
    <div class="stat-card">
        <div class="stat-icon"><span class="material-icons">verified</span></div>
        <div class="stat-info">
            <h3><?php echo $stats['pass_rate']; ?>%</h3>
            <p>Pass Rate (MTD)</p>
        </div>
        <?php if ($stats['pass_rate'] >= 95): ?>
        <div class="stat-change positive">
            <span class="material-icons">trending_up</span>
            <span>Excellent</span>
        </div>
        <?php elseif ($stats['pass_rate'] < 80): ?>
        <div class="stat-change warning">
            <span class="material-icons">trending_down</span>
            <span>Needs improvement</span>
        </div>
        <?php endif; ?>
    </div>
</div>

<div class="dashboard-grid mt-2">
    <a href="log_inspection.php" class="action-card">
        <span class="material-icons">add_task</span>
        <h3>Log New Inspection</h3>
        <p>Record quality checks for batches</p>
        <?php if ($stats['pending_batches'] > 0): ?>
        <span class="notification-badge"><?php echo $stats['pending_batches']; ?></span>
        <?php endif; ?>
        <span class="action-link">Start Inspection <span class="material-icons">arrow_forward</span></span>
    </a>
    
    <a href="qc_batches.php" class="action-card">
        <span class="material-icons">approval</span>
        <h3>Approve Batches</h3>
        <p>Review and approve pending batches</p>
        <span class="action-link">Review Now <span class="material-icons">arrow_forward</span></span>
    </a>
    
    <a href="qc_history.php" class="action-card">
        <span class="material-icons">history</span>
        <h3>Inspection History</h3>
        <p>View past quality records</p>
        <span class="action-link">View All <span class="material-icons">arrow_forward</span></span>
    </a>
</div>

<div class="card mt-2">
    <div class="card-header">
        <h3><span class="material-icons">recent_actors</span> Recent Inspections</h3>
    </div>
    <div class="table-container">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Batch</th>
                    <th>Product</th>
                    <th>Result</th>
                    <th>Defect Type</th>
                    <th>Inspector</th>
                    <th>Date</th>
                </tr>
            </thead>
            <tbody>
                <?php while ($ins = $recent_inspections->fetch_assoc()): ?>
                <tr>
                    <td><?php echo h($ins['BatchNumber']); ?></td>
                    <td><?php echo h($ins['ProductName'] ?? 'N/A'); ?></td>
                    <td>
                        <span class="badge badge-<?php echo strtolower($ins['Result']); ?>">
                            <?php echo $ins['Result']; ?>
                        </span>
                    </td>
                    <td><?php echo h($ins['DefectType'] ?? '-'); ?></td>
                    <td><?php echo h($_SESSION['full_name']); ?></td>
                    <td><?php echo date('d M Y', strtotime($ins['InspectionDate'])); ?></td>
                </tr>
                <?php endwhile; ?>
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
.badge {
    display: inline-block;
    padding: 4px 10px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 500;
}
.badge-pass {
    background: #e8f5e9;
    color: #2e7d32;
}
.badge-fail {
    background: #ffebee;
    color: #c62828;
}
.badge-conditional {
    background: #e3f2fd;
    color: #0288d1;
}
.mt-2 { margin-top: 20px; }
</style>

<?php include "../includes/footer.php"; ?>