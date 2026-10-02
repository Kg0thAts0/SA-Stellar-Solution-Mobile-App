<?php
require_once "../includes/auth.php";
require_role('ProductionManager');
require_once "../connect_db.php";

// Fetch raw materials with low stock alert
$sql = "SELECT MaterialID, MaterialName, CurrentStock, MinimumStock, ReorderLevel, Unit 
        FROM raw_material 
        ORDER BY CurrentStock ASC";
$materials = $conn->query($sql);
?>
<?php include "../includes/layout.php"; ?>

<div class="page-header">
    <h1>Raw Materials Inventory</h1>
    <p>Monitor material availability for production planning</p>
</div>

<div class="card">
    <div class="card-header">
        <h3><span class="material-icons">inventory_2</span> Current Stock Levels</h3>
    </div>
    <div class="table-container">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Material Name</th>
                    <th>Current Stock</th>
                    <th>Minimum Stock</th>
                    <th>Reorder Level</th>
                    <th>Status</th>
                    <th>Unit</th>
                </tr>
            </thead>
            <tbody>
                <?php while ($material = $materials->fetch_assoc()): 
                    $status = 'healthy';
                    $status_text = 'Healthy';
                    if ($material['CurrentStock'] <= $material['ReorderLevel']) {
                        $status = 'critical';
                        $status_text = 'Critical - Reorder Now';
                    } elseif ($material['CurrentStock'] <= $material['MinimumStock'] * 1.5) {
                        $status = 'warning';
                        $status_text = 'Warning - Low Stock';
                    }
                ?>
                <tr class="<?php echo $status; ?>-row">
                    <td><?php echo h($material['MaterialName']); ?></td>
                    <td class="<?php echo $status; ?>-value"><?php echo number_format($material['CurrentStock']); ?></td>
                    <td><?php echo number_format($material['MinimumStock']); ?></td>
                    <td><?php echo number_format($material['ReorderLevel']); ?></td>
                    <td><span class="badge badge-<?php echo $status; ?>"><?php echo $status_text; ?></span></td>
                    <td><?php echo h($material['Unit']); ?></td>
                </tr>
                <?php endwhile; ?>
            </tbody>
        </table>
    </div>
</div>

<style>
.critical-row { background-color: var(--error-bg); }
.warning-row { background-color: var(--warning-bg); }
.critical-value { color: var(--error); font-weight: 700; }
.badge-critical { background: var(--error-bg); color: var(--error); }
.badge-warning { background: var(--warning-bg); color: var(--warning); }
.badge-healthy { background: var(--success-bg); color: var(--success); }
</style>

<?php include "../includes/footer.php"; ?>