<?php
require_once "../includes/auth.php";
require_role('Supervisor');
require_once "../connect_db.php";

// Get filter parameters
$days = isset($_GET['days']) ? intval($_GET['days']) : 30;

// Fetch rejected batches
$sql = "SELECT b.BatchNumber, p.ProductName, i.DefectType, i.DefectQuantity, i.Remarks, i.InspectionDate, e.FullName as InspectorName
        FROM production_batch b
        JOIN quality_inspection i ON b.BatchID = i.BatchID
        LEFT JOIN finished_good p ON b.ProductID = p.ProductID
        LEFT JOIN employee e ON i.InspectorID = e.EmployeeID
        WHERE i.Result = 'Fail'
        AND i.InspectionDate >= DATE_SUB(NOW(), INTERVAL ? DAY)
        ORDER BY i.InspectionDate DESC";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $days);
$stmt->execute();
$rejected_batches = $stmt->get_result();

// Summary statistics
$total_rejected = $rejected_batches->num_rows;

// Defect type breakdown
$defect_breakdown = [];
$sql_defect = "SELECT DefectType, COUNT(*) as count FROM quality_inspection WHERE Result = 'Fail' GROUP BY DefectType ORDER BY count DESC";
$result_defect = $conn->query($sql_defect);
while ($row = $result_defect->fetch_assoc()) {
    $defect_breakdown[$row['DefectType']] = $row['count'];
}
?>
<?php include "../includes/layout.php"; ?>

<div class="page-header">
    <h1>Quality Control Issues</h1>
    <p>Monitor rejected batches and defect patterns</p>
</div>

<!-- Summary -->
<div class="summary-cards">
    <div class="summary-card">
        <span class="summary-number"><?php echo $total_rejected; ?></span>
        <span class="summary-label">Total Rejected Batches</span>
    </div>
    <div class="summary-card">
        <span class="summary-number"><?php echo count($defect_breakdown); ?></span>
        <span class="summary-label">Defect Types</span>
    </div>
    <div class="summary-card">
        <span class="summary-number"><?php echo $days; ?></span>
        <span class="summary-label">Days Analyzed</span>
    </div>
</div>

<!-- Defect Type Breakdown -->
<?php if (!empty($defect_breakdown)): ?>
<div class="defect-breakdown">
    <h3>Defect Type Distribution</h3>
    <div class="defect-bars">
        <?php foreach ($defect_breakdown as $type => $count): 
            $percentage = round(($count / $total_rejected) * 100);
        ?>
        <div class="defect-bar">
            <span class="defect-label"><?php echo h($type ?: 'Unknown'); ?></span>
            <div class="bar-track">
                <div class="bar-fill" style="width: <?php echo $percentage; ?>%; background: <?php 
                    if ($type == 'Fabric Defect') echo '#c62828';
                    elseif ($type == 'Stitching Issue') echo '#ed6c02';
                    elseif ($type == 'Size Mismatch') echo '#0288d1';
                    elseif ($type == 'Color Variation') echo '#7b1fa2';
                    else echo '#6c757d';
                ?>;"></div>
                <span class="bar-count"><?php echo $count; ?></span>
            </div>
        </div>
        <?php endforeach; ?>
    </div>
</div>
<?php endif; ?>

<!-- Filter -->
<div class="filter-bar">
    <div class="filter-group">
        <label>Period</label>
        <select id="daysFilter" onchange="window.location.href='?days='+this.value">
            <option value="7" <?php echo $days == 7 ? 'selected' : ''; ?>>Last 7 Days</option>
            <option value="14" <?php echo $days == 14 ? 'selected' : ''; ?>>Last 14 Days</option>
            <option value="30" <?php echo $days == 30 ? 'selected' : ''; ?>>Last 30 Days</option>
            <option value="90" <?php echo $days == 90 ? 'selected' : ''; ?>>Last 90 Days</option>
        </select>
    </div>
    <button class="btn btn-outline" onclick="exportToCSV()">
        <span class="material-icons">download</span>
        Export CSV
    </button>
</div>

<div class="card">
    <div class="card-header">
        <h3><span class="material-icons">report_problem</span> Rejected Batches</h3>
        <span class="badge badge-danger"><?php echo $total_rejected; ?> rejected</span>
    </div>
    <div class="table-container">
        <table class="data-table" id="issuesTable">
            <thead>
                <tr>
                    <th>Date</th>
                    <th>Batch</th>
                    <th>Product</th>
                    <th>Defect Type</th>
                    <th>Quantity Affected</th>
                    <th>Inspector</th>
                    <th>Remarks</th>
                </tr>
            </thead>
            <tbody>
                <?php if ($rejected_batches->num_rows > 0): ?>
                    <?php while ($row = $rejected_batches->fetch_assoc()): ?>
                    <tr class="warning-row">
                        <td><?php echo date('d M Y', strtotime($row['InspectionDate'])); ?></td>
                        <td><?php echo h($row['BatchNumber']); ?></td>
                        <td><?php echo h($row['ProductName'] ?? 'N/A'); ?></td>
                        <td><?php echo h($row['DefectType'] ?? 'N/A'); ?></td>
                        <td class="critical-value"><?php echo number_format($row['DefectQuantity']); ?></td>
                        <td><?php echo h($row['InspectorName']); ?></td>
                        <td><?php echo h($row['Remarks'] ?? '-'); ?></td>
                    </tr>
                    <?php endwhile; ?>
                <?php else: ?>
                    <tr>
                        <td colspan="7" class="text-center">No rejected batches found.</td>
                    </tr>
                <?php endif; ?>
            </tbody>
        </table>
    </div>
</div>

<script>
function exportToCSV() {
    const table = document.getElementById('issuesTable');
    const rows = table.querySelectorAll('tr');
    let csv = [];
    
    rows.forEach(row => {
        const cells = row.querySelectorAll('th, td');
        const rowData = Array.from(cells).map(cell => {
            let text = cell.innerText;
            return `"${text.replace(/"/g, '""')}"`;
        });
        if (rowData.length > 0) {
            csv.push(rowData.join(','));
        }
    });
    
    const blob = new Blob([csv.join('\n')], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'qc_issues_export.csv';
    a.click();
    URL.revokeObjectURL(url);
}
</script>

<style>
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
.defect-breakdown {
    background: white;
    border-radius: 12px;
    padding: 20px;
    margin-bottom: 24px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}
.defect-breakdown h3 {
    font-size: 16px;
    margin-bottom: 16px;
}
.defect-bars {
    display: flex;
    flex-direction: column;
    gap: 10px;
}
.defect-bar {
    display: flex;
    align-items: center;
    gap: 12px;
}
.defect-label {
    min-width: 120px;
    font-size: 13px;
    font-weight: 500;
}
.bar-track {
    flex: 1;
    height: 24px;
    background: #f8f9fa;
    border-radius: 12px;
    position: relative;
    overflow: hidden;
}
.bar-fill {
    height: 100%;
    border-radius: 12px;
    transition: width 0.5s ease;
}
.bar-count {
    position: absolute;
    right: 8px;
    top: 50%;
    transform: translateY(-50%);
    font-size: 12px;
    font-weight: 600;
    color: #1a2634;
}
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
.warning-row {
    background: #fff4e5;
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
.badge-danger {
    background: #ffebee;
    color: #c62828;
}
.text-center {
    text-align: center;
    padding: 20px;
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