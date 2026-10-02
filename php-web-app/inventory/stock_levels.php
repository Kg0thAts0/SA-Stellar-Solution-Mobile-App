<?php
require_once "../includes/auth.php";
require_role('InventoryClerk');
require_once "../connect_db.php";

// Helper functions
function format_number($num) {
    return number_format($num ?? 0);
}

// Fetch stock summary with error handling
$raw_summary = ['total' => 0, 'total_units' => 0, 'total_value' => 0];
$fg_summary = ['total' => 0, 'total_units' => 0, 'total_value' => 0];

// Raw materials summary
$sql_raw = "SELECT COUNT(*) as total, SUM(CurrentStock) as total_units, SUM(CurrentStock * UnitCost) as total_value FROM raw_material";
$result = $conn->query($sql_raw);
if ($result && $result->num_rows > 0) {
    $raw_summary = $result->fetch_assoc();
}

// Finished goods summary
$sql_fg = "SELECT COUNT(*) as total, SUM(CurrentStock) as total_units, SUM(CurrentStock * UnitPrice) as total_value FROM finished_good";
$result = $conn->query($sql_fg);
if ($result && $result->num_rows > 0) {
    $fg_summary = $result->fetch_assoc();
}

// Fetch critical stock items
$sql_critical = "SELECT MaterialID, MaterialName, CurrentStock, MinimumStock, Unit, 'Raw Material' as Type 
                 FROM raw_material 
                 WHERE CurrentStock <= ReorderLevel
                 UNION
                 SELECT ProductID, ProductName, CurrentStock, 0 as MinimumStock, Unit, 'Finished Good' as Type 
                 FROM finished_good 
                 WHERE CurrentStock <= 50
                 ORDER BY CurrentStock ASC LIMIT 10";
$critical_items = $conn->query($sql_critical);
?>
<?php include "../includes/layout.php"; ?>

<div class="page-header">
    <h1>Stock Levels Dashboard</h1>
    <p>Monitor real-time inventory availability across all categories</p>
</div>

<!-- Summary Cards -->
<div class="dashboard-grid">
    <div class="stat-card">
        <div class="stat-icon"><span class="material-icons">inventory_2</span></div>
        <div class="stat-info">
            <h3><?php echo format_number($raw_summary['total'] ?? 0); ?></h3>
            <p>Raw Material Types</p>
            <small><?php echo format_number($raw_summary['total_units'] ?? 0); ?> total units</small>
        </div>
    </div>
    <div class="stat-card">
        <div class="stat-icon"><span class="material-icons">attach_money</span></div>
        <div class="stat-info">
            <h3>R<?php echo format_number($raw_summary['total_value'] ?? 0); ?></h3>
            <p>Raw Material Value</p>
        </div>
    </div>
    <div class="stat-card">
        <div class="stat-icon"><span class="material-icons">checkroom</span></div>
        <div class="stat-info">
            <h3><?php echo format_number($fg_summary['total'] ?? 0); ?></h3>
            <p>Finished Products</p>
            <small><?php echo format_number($fg_summary['total_units'] ?? 0); ?> total units</small>
        </div>
    </div>
    <div class="stat-card">
        <div class="stat-icon"><span class="material-icons">monetization_on</span></div>
        <div class="stat-info">
            <h3>R<?php echo format_number($fg_summary['total_value'] ?? 0); ?></h3>
            <p>Finished Goods Value</p>
        </div>
    </div>
</div>

<!-- Critical Stock Alert -->
<?php if ($critical_items && $critical_items->num_rows > 0): ?>
<div class="card mt-2 critical-alert-card">
    <div class="card-header">
        <h3><span class="material-icons">warning</span> Critical Stock Alert</h3>
        <span class="alert-badge">Action Required</span>
    </div>
    <div class="table-container">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Item Type</th>
                    <th>Item Name</th>
                    <th>Current Stock</th>
                    <th>Minimum Required</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <?php while ($item = $critical_items->fetch_assoc()): ?>
                <tr class="critical-row">
                    <td><?php echo htmlspecialchars($item['Type'] ?? '', ENT_QUOTES, 'UTF-8'); ?></td>
                    <td><?php echo htmlspecialchars($item['MaterialName'] ?? $item['ProductName'] ?? '', ENT_QUOTES, 'UTF-8'); ?></td>
                    <td class="critical-value"><?php echo format_number($item['CurrentStock'] ?? 0); ?> <?php echo htmlspecialchars($item['Unit'] ?? '', ENT_QUOTES, 'UTF-8'); ?></td>
                    <td><?php echo format_number($item['MinimumStock'] ?? 0); ?> <?php echo htmlspecialchars($item['Unit'] ?? '', ENT_QUOTES, 'UTF-8'); ?></td>
                    <td><span class="badge badge-critical">Critical</span></td>
                    <td>
                        <a href="<?php echo ($item['Type'] ?? '') === 'Raw Material' ? 'raw_materials.php' : 'finished_goods.php'; ?>" class="btn btn-sm btn-outline">
                            Reorder Now
                        </a>
                    </td>
                </tr>
                <?php endwhile; ?>
            </tbody>
        </table>
    </div>
</div>
<?php endif; ?>

<!-- Raw Materials Table -->
<div class="card mt-2">
    <div class="card-header">
        <h3><span class="material-icons">inventory_2</span> Raw Materials Stock Status</h3>
    </div>
    <div class="table-container">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Material Name</th>
                    <th>Current Stock</th>
                    <th>Unit</th>
                    <th>Unit Cost</th>
                    <th>Total Value</th>
                    <th>Stock Status</th>
                </tr>
            </thead>
            <tbody>
                <?php
                $sql = "SELECT MaterialName, CurrentStock, Unit, UnitCost, (CurrentStock * UnitCost) as TotalValue,
                        CASE 
                            WHEN CurrentStock <= ReorderLevel THEN 'Critical'
                            WHEN CurrentStock <= MinimumStock * 1.5 THEN 'Low'
                            ELSE 'Healthy'
                        END as Status
                        FROM raw_material
                        ORDER BY Status = 'Critical' DESC, Status = 'Low' DESC, MaterialName ASC";
                $raw_materials = $conn->query($sql);
                if ($raw_materials && $raw_materials->num_rows > 0):
                    while ($item = $raw_materials->fetch_assoc()):
                        $status_class = strtolower($item['Status'] ?? 'healthy');
                ?>
                <tr class="<?php echo $status_class; ?>-row">
                    <td><?php echo htmlspecialchars($item['MaterialName'] ?? '', ENT_QUOTES, 'UTF-8'); ?></td>
                    <td class="<?php echo $status_class; ?>-value"><?php echo format_number($item['CurrentStock'] ?? 0); ?> <?php echo htmlspecialchars($item['Unit'] ?? '', ENT_QUOTES, 'UTF-8'); ?></td>
                    <td><?php echo htmlspecialchars($item['Unit'] ?? '', ENT_QUOTES, 'UTF-8'); ?></td>
                    <td>R<?php echo format_number($item['UnitCost'] ?? 0); ?></td>
                    <td>R<?php echo format_number($item['TotalValue'] ?? 0); ?></td>
                    <td><span class="badge badge-<?php echo $status_class; ?>"><?php echo $item['Status'] ?? 'Healthy'; ?></span></td>
                </tr>
                <?php 
                    endwhile; 
                else: 
                ?>
                <tr>
                    <td colspan="6" class="text-center">No raw materials found.</td>
                </tr>
                <?php endif; ?>
            </tbody>
        </table>
    </div>
</div>

<!-- Finished Goods Table -->
<div class="card mt-2">
    <div class="card-header">
        <h3><span class="material-icons">checkroom</span> Finished Goods Stock Status</h3>
    </div>
    <div class="table-container">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Product Name</th>
                    <th>Current Stock</th>
                    <th>Unit</th>
                    <th>Unit Price</th>
                    <th>Total Value</th>
                    <th>Stock Status</th>
                </tr>
            </thead>
            <tbody>
                <?php
                $sql = "SELECT ProductName, CurrentStock, Unit, UnitPrice, (CurrentStock * UnitPrice) as TotalValue,
                        CASE 
                            WHEN CurrentStock <= 50 THEN 'Low'
                            ELSE 'Healthy'
                        END as Status
                        FROM finished_good
                        ORDER BY Status = 'Low' DESC, ProductName ASC";
                $finished_goods = $conn->query($sql);
                if ($finished_goods && $finished_goods->num_rows > 0):
                    while ($item = $finished_goods->fetch_assoc()):
                        $status_class = strtolower($item['Status'] ?? 'healthy');
                ?>
                <tr class="<?php echo $status_class; ?>-row">
                    <td><?php echo htmlspecialchars($item['ProductName'] ?? '', ENT_QUOTES, 'UTF-8'); ?></td>
                    <td class="<?php echo $status_class; ?>-value"><?php echo format_number($item['CurrentStock'] ?? 0); ?> <?php echo htmlspecialchars($item['Unit'] ?? '', ENT_QUOTES, 'UTF-8'); ?></td>
                    <td><?php echo htmlspecialchars($item['Unit'] ?? '', ENT_QUOTES, 'UTF-8'); ?></td>
                    <td>R<?php echo format_number($item['UnitPrice'] ?? 0); ?></td>
                    <td>R<?php echo format_number($item['TotalValue'] ?? 0); ?></td>
                    <td><span class="badge badge-<?php echo $status_class; ?>"><?php echo $item['Status'] ?? 'Healthy'; ?></span></td>
                </tr>
                <?php 
                    endwhile; 
                else: 
                ?>
                <tr>
                    <td colspan="6" class="text-center">No finished goods found.</td>
                </tr>
                <?php endif; ?>
            </tbody>
        </table>
    </div>
</div>

<style>
/* Stats Cards */
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
    gap: 16px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
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
}
.stat-info h3 {
    font-size: 28px;
    font-weight: 700;
    margin: 0;
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

/* Critical Alert */
.critical-alert-card {
    border-left: 4px solid #c62828;
}
.alert-badge {
    display: inline-block;
    padding: 4px 12px;
    background: #ffebee;
    color: #c62828;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 500;
}
.critical-row {
    background-color: #ffebee;
}
.low-row {
    background-color: #fff4e5;
}
.critical-value {
    color: #c62828;
    font-weight: 700;
}
.low-value {
    color: #ed6c02;
    font-weight: 600;
}

/* Badges */
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
.badge-low {
    background: #fff4e5;
    color: #ed6c02;
}
.badge-healthy {
    background: #e8f5e9;
    color: #2e7d32;
}

/* Cards */
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
.mt-2 {
    margin-top: 20px;
}

/* Tables */
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
.text-center {
    text-align: center;
    padding: 20px;
}
</style>

<?php include "../includes/footer.php"; ?>