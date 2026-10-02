<?php
require_once "../includes/auth.php";
require_role('Supervisor');
require_once "../connect_db.php";

// Get date range filter
$days = isset($_GET['days']) ? intval($_GET['days']) : 7;
$start_date = date('Y-m-d', strtotime("-$days days"));

// Production by shift
$sql = "SELECT 
            DATE(ProductionDate) as prod_date,
            Shift,
            SUM(QuantityProduced) as total_output,
            SUM(QuantityDefective) as total_defective,
            COUNT(*) as batch_count
        FROM production_batch
        WHERE ProductionDate >= '$start_date'
        GROUP BY DATE(ProductionDate), Shift
        ORDER BY prod_date DESC, Shift";
$production = $conn->query($sql);

// Organize data by date
$daily_data = [];
$shift_totals = ['Morning' => 0, 'Afternoon' => 0, 'Night' => 0];
$date_labels = [];

while ($row = $production->fetch_assoc()) {
    $date = $row['prod_date'];
    $shift = $row['Shift'];
    $daily_data[$date][$shift] = $row['total_output'];
    $daily_data[$date]['defective_' . $shift] = $row['total_defective'];
    $daily_data[$date]['batches_' . $shift] = $row['batch_count'];
    $shift_totals[$shift] += $row['total_output'];
    if (!in_array($date, $date_labels)) {
        $date_labels[] = $date;
    }
}

// Sort dates descending
rsort($date_labels);

// Overall totals
$sql = "SELECT 
            SUM(QuantityProduced) as total_produced,
            SUM(QuantityDefective) as total_defective,
            COUNT(*) as total_batches
        FROM production_batch
        WHERE ProductionDate >= '$start_date'";
$result = $conn->query($sql);
$totals = $result->fetch_assoc();
$efficiency = $totals['total_produced'] > 0 ? round((($totals['total_produced'] - $totals['total_defective']) / $totals['total_produced']) * 100, 1) : 0;
?>
<?php include "../includes/layout.php"; ?>

<div class="page-header">
    <h1>Production Overview</h1>
    <p>Monitor daily output and line performance</p>
</div>

<!-- Filter Bar -->
<div class="filter-bar">
    <div class="filter-group">
        <label>Period</label>
        <select id="daysFilter" onchange="window.location.href='?days='+this.value">
            <option value="3" <?php echo $days == 3 ? 'selected' : ''; ?>>Last 3 Days</option>
            <option value="7" <?php echo $days == 7 ? 'selected' : ''; ?>>Last 7 Days</option>
            <option value="14" <?php echo $days == 14 ? 'selected' : ''; ?>>Last 14 Days</option>
            <option value="30" <?php echo $days == 30 ? 'selected' : ''; ?>>Last 30 Days</option>
        </select>
    </div>
    <div class="filter-actions">
        <button class="btn btn-outline" onclick="window.print()">
            <span class="material-icons">print</span>
            Print
        </button>
    </div>
</div>

<!-- Summary Cards -->
<div class="summary-cards">
    <div class="summary-card">
        <span class="summary-number"><?php echo number_format($totals['total_produced']); ?></span>
        <span class="summary-label">Total Units Produced</span>
    </div>
    <div class="summary-card">
        <span class="summary-number"><?php echo $totals['total_batches']; ?></span>
        <span class="summary-label">Total Batches</span>
    </div>
    <div class="summary-card">
        <span class="summary-number"><?php echo $efficiency; ?>%</span>
        <span class="summary-label">Overall Efficiency</span>
    </div>
    <div class="summary-card">
        <span class="summary-number"><?php echo number_format($totals['total_defective']); ?></span>
        <span class="summary-label">Total Defective Units</span>
    </div>
</div>

<!-- Shift Performance Summary -->
<div class="shift-summary">
    <h3>Shift Performance</h3>
    <div class="shift-cards">
        <div class="shift-card">
            <span class="shift-name">Morning</span>
            <span class="shift-value"><?php echo number_format($shift_totals['Morning']); ?></span>
            <span class="shift-label">units</span>
        </div>
        <div class="shift-card">
            <span class="shift-name">Afternoon</span>
            <span class="shift-value"><?php echo number_format($shift_totals['Afternoon']); ?></span>
            <span class="shift-label">units</span>
        </div>
        <div class="shift-card">
            <span class="shift-name">Night</span>
            <span class="shift-value"><?php echo number_format($shift_totals['Night']); ?></span>
            <span class="shift-label">units</span>
        </div>
    </div>
</div>

<!-- Daily Production Table -->
<div class="card">
    <div class="card-header">
        <h3><span class="material-icons">analytics</span> Daily Production by Shift</h3>
    </div>
    <div class="table-container">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Date</th>
                    <th>Morning</th>
                    <th>Afternoon</th>
                    <th>Night</th>
                    <th>Total</th>
                    <th>Defective</th>
                    <th>Batches</th>
                </tr>
            </thead>
            <tbody>
                <?php foreach ($date_labels as $date): ?>
                <tr>
                    <td><?php echo date('d M Y', strtotime($date)); ?></td>
                    <td><?php echo number_format($daily_data[$date]['Morning'] ?? 0); ?></td>
                    <td><?php echo number_format($daily_data[$date]['Afternoon'] ?? 0); ?></td>
                    <td><?php echo number_format($daily_data[$date]['Night'] ?? 0); ?></td>
                    <td><strong><?php 
                        $total = ($daily_data[$date]['Morning'] ?? 0) + ($daily_data[$date]['Afternoon'] ?? 0) + ($daily_data[$date]['Night'] ?? 0);
                        echo number_format($total);
                    ?></strong></td>
                    <td class="defective">
                        <?php 
                        $defective = ($daily_data[$date]['defective_Morning'] ?? 0) + ($daily_data[$date]['defective_Afternoon'] ?? 0) + ($daily_data[$date]['defective_Night'] ?? 0);
                        echo number_format($defective);
                        ?>
                    </td>
                    <td>
                        <?php 
                        $batches = ($daily_data[$date]['batches_Morning'] ?? 0) + ($daily_data[$date]['batches_Afternoon'] ?? 0) + ($daily_data[$date]['batches_Night'] ?? 0);
                        echo $batches;
                        ?>
                    </td>
                </tr>
                <?php endforeach; ?>
            </tbody>
            <tfoot>
                <tr class="total-row">
                    <td><strong>Total</strong></td>
                    <td><strong><?php echo number_format($shift_totals['Morning']); ?></strong></td>
                    <td><strong><?php echo number_format($shift_totals['Afternoon']); ?></strong></td>
                    <td><strong><?php echo number_format($shift_totals['Night']); ?></strong></td>
                    <td><strong><?php echo number_format($totals['total_produced']); ?></strong></td>
                    <td><strong><?php echo number_format($totals['total_defective']); ?></strong></td>
                    <td><strong><?php echo $totals['total_batches']; ?></strong></td>
                </tr>
            </tfoot>
        </table>
    </div>
</div>

<style>
.filter-bar {
    display: flex;
    gap: 16px;
    margin-bottom: 20px;
    align-items: flex-end;
    flex-wrap: wrap;
}
.filter-group label {
    display: block;
    font-size: 12px;
    color: #6c757d;
    margin-bottom: 4px;
}
.filter-group select {
    padding: 8px 12px;
    border: 1px solid #ddd;
    border-radius: 6px;
    background: white;
}
.summary-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
    gap: 16px;
    margin-bottom: 24px;
}
.summary-card {
    background: white;
    border-radius: 12px;
    padding: 20px;
    text-align: center;
    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}
.summary-number {
    display: block;
    font-size: 24px;
    font-weight: 700;
    color: #1a2634;
}
.summary-label {
    display: block;
    font-size: 13px;
    color: #6c757d;
    margin-top: 4px;
}
.shift-summary {
    margin-bottom: 24px;
}
.shift-summary h3 {
    margin-bottom: 12px;
    font-size: 16px;
}
.shift-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 16px;
}
.shift-card {
    background: white;
    border-radius: 12px;
    padding: 16px;
    text-align: center;
    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
    border-top: 3px solid #c9a03d;
}
.shift-name {
    display: block;
    font-size: 13px;
    color: #6c757d;
}
.shift-value {
    display: block;
    font-size: 24px;
    font-weight: 700;
    margin: 4px 0;
}
.shift-label {
    display: block;
    font-size: 11px;
    color: #6c757d;
}
.card {
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
    overflow: hidden;
    margin-top: 20px;
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
.data-table .total-row {
    background: #f8f9fa;
    font-weight: bold;
}
.defective {
    color: #c62828;
}
.btn-outline {
    background: transparent;
    border: 1px solid #c9a03d;
    color: #c9a03d;
    padding: 8px 16px;
    border-radius: 6px;
    cursor: pointer;
    display: inline-flex;
    align-items: center;
    gap: 8px;
}
.btn-outline:hover {
    background: #c9a03d;
    color: white;
}
</style>

<?php include "../includes/footer.php"; ?>