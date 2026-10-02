<?php
require_once "../includes/auth.php";
require_role('Supervisor');
require_once "../connect_db.php";

// Fetch raw materials with stock status
$sql = "SELECT MaterialName, CurrentStock, MinimumStock, ReorderLevel, Unit 
        FROM raw_material 
        ORDER BY 
            CASE 
                WHEN CurrentStock <= ReorderLevel THEN 0
                WHEN CurrentStock <= MinimumStock THEN 1
                ELSE 2
            END, 
            MaterialName ASC";
$materials = $conn->query($sql);

// Summary statistics
$total_items = 0;
$critical_items = 0;
$low_items = 0;
$healthy_items = 0;

$materials_list = [];
while ($row = $materials->fetch_assoc()) {
    $total_items++;
    
    // Determine status based on stock levels
    if ($row['CurrentStock'] <= $row['ReorderLevel']) {
        $status = 'Critical';
        $critical_items++;
    } elseif ($row['CurrentStock'] <= $row['MinimumStock']) {
        $status = 'Low';
        $low_items++;
    } else {
        $status = 'Healthy';
        $healthy_items++;
    }
    
    $row['status'] = $status;
    $materials_list[] = $row;
}
?>
<?php include "../includes/layout.php"; ?>

<div class="page-header">
    <h1>Inventory Snapshot</h1>
    <p>Quick view of current stock levels</p>
</div>

<!-- Summary Cards -->
<div class="summary-cards">
    <div class="summary-card">
        <span class="summary-number"><?php echo $total_items; ?></span>
        <span class="summary-label">Total Items</span>
    </div>
    <div class="summary-card <?php echo $critical_items > 0 ? 'danger' : 'success'; ?>">
        <span class="summary-number"><?php echo $critical_items; ?></span>
        <span class="summary-label">Critical Stock</span>
    </div>
    <div class="summary-card <?php echo $low_items > 0 ? 'warning' : ''; ?>">
        <span class="summary-number"><?php echo $low_items; ?></span>
        <span class="summary-label">Low Stock</span>
    </div>
    <div class="summary-card success">
        <span class="summary-number"><?php echo $healthy_items; ?></span>
        <span class="summary-label">Healthy Stock</span>
    </div>
</div>

<!-- Stock Status Distribution Bar -->
<div class="status-distribution">
    <div class="status-segment critical" style="width: <?php echo $total_items > 0 ? round(($critical_items / $total_items) * 100) : 0; ?>%;">
        <?php if ($critical_items > 0): ?>
        <span>Critical <?php echo $critical_items; ?></span>
        <?php endif; ?>
    </div>
    <div class="status-segment low" style="width: <?php echo $total_items > 0 ? round(($low_items / $total_items) * 100) : 0; ?>%;">
        <?php if ($low_items > 0): ?>
        <span>Low <?php echo $low_items; ?></span>
        <?php endif; ?>
    </div>
    <div class="status-segment healthy" style="width: <?php echo $total_items > 0 ? round(($healthy_items / $total_items) * 100) : 0; ?>%;">
        <?php if ($healthy_items > 0): ?>
        <span>Healthy <?php echo $healthy_items; ?></span>
        <?php endif; ?>
    </div>
</div>

<!-- Inventory Table -->
<div class="card mt-2">
    <div class="card-header">
        <h3><span class="material-icons">inventory</span> Stock Levels</h3>
        <div class="legend">
            <span class="legend-item"><span class="legend-dot critical"></span> Critical</span>
            <span class="legend-item"><span class="legend-dot low"></span> Low</span>
            <span class="legend-item"><span class="legend-dot healthy"></span> Healthy</span>
        </div>
    </div>
    <div class="table-container">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Material</th>
                    <th>Current Stock</th>
                    <th>Minimum Required</th>
                    <th>Reorder Level</th>
                    <th>Status</th>
                    <th>Unit</th>
                </tr>
            </thead>
            <tbody>
                <?php if (!empty($materials_list)): ?>
                    <?php foreach ($materials_list as $material): ?>
                    <tr class="<?php echo strtolower($material['status']); ?>-row">
                        <td><?php echo h($material['MaterialName']); ?></td>
                        <td class="<?php echo strtolower($material['status']); ?>-value">
                            <?php echo number_format($material['CurrentStock']); ?>
                        </td>
                        <td><?php echo number_format($material['MinimumStock']); ?></td>
                        <td><?php echo number_format($material['ReorderLevel']); ?></td>
                        <td>
                            <span class="badge badge-<?php echo strtolower($material['status']); ?>">
                                <?php echo $material['status']; ?>
                            </span>
                        </td>
                        <td><?php echo h($material['Unit']); ?></td>
                    </tr>
                    <?php endforeach; ?>
                <?php else: ?>
                    <tr>
                        <td colspan="6" class="text-center">No inventory items found.</td>
                    </tr>
                <?php endif; ?>
            </tbody>
        </table>
    </div>
</div>

<style>
.summary-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 16px;
    margin-bottom: 20px;
}
.summary-card {
    background: white;
    border-radius: 12px;
    padding: 16px;
    text-align: center;
    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}
.summary-card.success {
    border-top: 4px solid #2e7d32;
}
.summary-card.warning {
    border-top: 4px solid #ed6c02;
}
.summary-card.danger {
    border-top: 4px solid #c62828;
}
.summary-number {
    display: block;
    font-size: 28px;
    font-weight: 700;
    color: #1a2634;
}
.summary-label {
    display: block;
    font-size: 13px;
    color: #6c757d;
    margin-top: 4px;
}
.status-distribution {
    display: flex;
    height: 36px;
    border-radius: 8px;
    overflow: hidden;
    background: #f8f9fa;
    margin-bottom: 20px;
}
.status-segment {
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    font-weight: 600;
    color: white;
    transition: width 0.5s ease;
    min-width: 40px;
}
.status-segment.critical {
    background: #c62828;
}
.status-segment.low {
    background: #ed6c02;
}
.status-segment.healthy {
    background: #2e7d32;
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
.legend {
    display: flex;
    gap: 16px;
}
.legend-item {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 12px;
    color: #6c757d;
}
.legend-dot {
    width: 12px;
    height: 12px;
    border-radius: 50%;
}
.legend-dot.critical {
    background: #c62828;
}
.legend-dot.low {
    background: #ed6c02;
}
.legend-dot.healthy {
    background: #2e7d32;
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
.critical-row {
    background: #ffebee;
}
.low-row {
    background: #fff4e5;
}
.healthy-row {
    background: #e8f5e9;
}
.critical-value {
    color: #c62828;
    font-weight: 700;
}
.low-value {
    color: #ed6c02;
    font-weight: 600;
}
.badge {
    display: inline-block;
    padding: 4px 12px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 500;
}
.badge-critical {
    background: #ffebee;
    color: #c62828;
}
.badge-low {
    background: #fff4e5;
    color: #ed6c02;
}
.badge-healthy {
    background: #e8f5e9;
    color: #2e7d32;
}
.mt-2 {
    margin-top: 20px;
}
.text-center {
    text-align: center;
    padding: 20px;
}
</style>

<?php include "../includes/footer.php"; ?>,