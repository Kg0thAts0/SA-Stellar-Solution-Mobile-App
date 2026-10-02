<?php
require_once "../includes/auth.php";
require_role('QualityController');
require_once "../connect_db.php";

// Fetch inspection history
$sql = "SELECT i.*, b.BatchNumber, p.ProductName, e.FullName as InspectorName
        FROM quality_inspection i
        JOIN production_batch b ON i.BatchID = b.BatchID
        LEFT JOIN finished_good p ON b.ProductID = p.ProductID
        LEFT JOIN employee e ON i.InspectorID = e.EmployeeID
        ORDER BY i.InspectionDate DESC";
$history = $conn->query($sql);

// Get counts for summary
$total_count = $history->num_rows;

// Count results
$result_counts = [];
$result_sql = "SELECT Result, COUNT(*) as count FROM quality_inspection GROUP BY Result";
$result_result = $conn->query($result_sql);
while ($row = $result_result->fetch_assoc()) {
    $result_counts[$row['Result']] = $row['count'];
}
?>
<?php include "../includes/layout.php"; ?>

<div class="page-header">
    <h1>Inspection History</h1>
    <p>View all past quality inspection records</p>
</div>

<!-- Summary Cards -->
<div class="summary-cards">
    <div class="summary-card">
        <span class="summary-number"><?php echo $total_count; ?></span>
        <span class="summary-label">Total Inspections</span>
    </div>
    <div class="summary-card success">
        <span class="summary-number"><?php echo $result_counts['Pass'] ?? 0; ?></span>
        <span class="summary-label">Passed</span>
    </div>
    <div class="summary-card danger">
        <span class="summary-number"><?php echo $result_counts['Fail'] ?? 0; ?></span>
        <span class="summary-label">Failed</span>
    </div>
    <div class="summary-card warning">
        <span class="summary-number"><?php echo $result_counts['Conditional'] ?? 0; ?></span>
        <span class="summary-label">Conditional</span>
    </div>
</div>

<div class="card">
    <div class="card-header">
        <h3><span class="material-icons">history</span> All Inspection Records</h3>
        <button class="btn btn-outline" onclick="exportToCSV()">
            <span class="material-icons">download</span>
            Export CSV
        </button>
    </div>
    <div class="table-container">
        <table class="data-table" id="historyTable">
            <thead>
                <tr>
                    <th>Date</th>
                    <th>Batch</th>
                    <th>Product</th>
                    <th>Result</th>
                    <th>Defect Type</th>
                    <th>Defect Qty</th>
                    <th>Inspector</th>
                    <th>Remarks</th>
                </tr>
            </thead>
            <tbody>
                <?php if ($history->num_rows > 0): ?>
                    <?php while ($row = $history->fetch_assoc()): ?>
                    <tr>
                        <td><?php echo date('d M Y', strtotime($row['InspectionDate'])); ?></td>
                        <td><?php echo h($row['BatchNumber']); ?></td>
                        <td><?php echo h($row['ProductName'] ?? 'N/A'); ?></td>
                        <td>
                            <span class="badge badge-<?php echo strtolower($row['Result']); ?>">
                                <?php echo $row['Result']; ?>
                            </span>
                        </td>
                        <td><?php echo h($row['DefectType'] ?? '-'); ?></td>
                        <td><?php echo $row['DefectQuantity'] > 0 ? number_format($row['DefectQuantity']) : '-'; ?></td>
                        <td><?php echo h($row['InspectorName']); ?></td>
                        <td><?php echo h($row['Remarks'] ?? '-'); ?></td>
                    </tr>
                    <?php endwhile; ?>
                <?php else: ?>
                    <tr>
                        <td colspan="8" class="text-center">No inspection records found.</td>
                    </tr>
                <?php endif; ?>
            </tbody>
        </table>
    </div>
</div>

<script>
function exportToCSV() {
    const table = document.getElementById('historyTable');
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
    a.download = 'inspection_history_export.csv';
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
    border-top: 4px solid #1a2634;
}
.summary-card.success {
    border-top-color: #2e7d32;
}
.summary-card.danger {
    border-top-color: #c62828;
}
.summary-card.warning {
    border-top-color: #ed6c02;
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
    font-size: 13px;
    transition: all 0.3s ease;
}
.btn-outline:hover {
    background: #c9a03d;
    color: white;
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
.text-center {
    text-align: center;
    padding: 20px;
}
</style>

<?php include "../includes/footer.php"; ?>