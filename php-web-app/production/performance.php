<?php
require_once "../includes/auth.php";
require_role('ProductionManager');
require_once "../connect_db.php";

// Monthly performance data
$performance_data = [];
for ($i = 5; $i >= 0; $i--) {
    $month = date('Y-m', strtotime("-$i months"));
    $month_name = date('M Y', strtotime("-$i months"));
    
    $sql = "SELECT SUM(QuantityProduced) as total, SUM(QuantityDefective) as defective, COUNT(*) as batches 
            FROM production_batch 
            WHERE DATE_FORMAT(ProductionDate, '%Y-%m') = '$month'";
    $result = $conn->query($sql);
    $data = $result->fetch_assoc();
    
    $total = $data['total'] ?? 0;
    $defective = $data['defective'] ?? 0;
    $efficiency = $total > 0 ? round((($total - $defective) / $total) * 100, 1) : 0;
    
    $performance_data[] = ['month' => $month_name, 'total' => $total, 'efficiency' => $efficiency];
}

// Shift performance
$shift_data = $conn->query("SELECT Shift, AVG(QuantityProduced) as avg_output, SUM(QuantityDefective) as defective, COUNT(*) as batches 
                            FROM production_batch WHERE ProductionDate >= DATE_SUB(NOW(), INTERVAL 30 DAY) 
                            GROUP BY Shift");
?>
<?php include "../includes/layout.php"; ?>

<div class="page-header">
    <h1>Production Performance</h1>
    <p>Analyze production efficiency and quality metrics</p>
</div>

<div class="dashboard-grid">
    <div class="stat-card">
        <div class="stat-icon"><span class="material-icons">trending_up</span></div>
        <div class="stat-info">
            <h3><?php echo $performance_data[count($performance_data)-1]['efficiency']; ?>%</h3>
            <p>Current Efficiency</p>
        </div>
    </div>
    <div class="stat-card">
        <div class="stat-icon"><span class="material-icons">speed</span></div>
        <div class="stat-info">
            <h3><?php echo number_format($performance_data[0]['total'] ?? 0); ?></h3>
            <p>Total Output (<?php echo $performance_data[0]['month']; ?>)</p>
        </div>
    </div>
</div>

<div class="card mt-2">
    <div class="card-header"><h3><span class="material-icons">show_chart</span> Monthly Performance Trend</h3></div>
    <div class="card-body"><canvas id="performanceChart" height="300"></canvas></div>
</div>

<div class="card mt-2">
    <div class="card-header"><h3><span class="material-icons">schedule</span> Shift Performance (Last 30 Days)</h3></div>
    <div class="table-container">
        <table class="data-table">
            <thead><tr><th>Shift</th><th>Avg Output/Batch</th><th>Defective</th><th>Batches</th></tr></thead>
            <tbody>
                <?php while ($shift = $shift_data->fetch_assoc()): ?>
                <tr><td><?php echo h($shift['Shift']); ?></td><td><?php echo number_format($shift['avg_output']); ?></td><td><?php echo number_format($shift['defective']); ?></td><td><?php echo $shift['batches']; ?></td></tr>
                <?php endwhile; ?>
            </tbody>
        </table>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
const ctx = document.getElementById('performanceChart').getContext('2d');
const data = <?php 
    $months = array_column($performance_data, 'month');
    $efficiencies = array_column($performance_data, 'efficiency');
    echo json_encode(['months' => $months, 'efficiencies' => $efficiencies]);
?>;
new Chart(ctx, {
    type: 'line',
    data: { labels: data.months, datasets: [{ label: 'Efficiency (%)', data: data.efficiencies, borderColor: '#c9a03d', tension: 0.3, fill: true }] },
    options: { responsive: true, scales: { y: { min: 0, max: 100 } } }
});
</script>

<?php include "../includes/footer.php"; ?>