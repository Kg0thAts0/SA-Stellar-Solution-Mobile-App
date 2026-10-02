<!-- Main Footer -->
<footer class="main-footer">
    <div class="footer-content">
        <p>&copy; <?php echo date('Y'); ?> QA Clothing Factory System. All rights reserved.</p>
        <p class="footer-version">Version 2.0 | South African Stellar Solutions</p>
    </div>
</footer>

<script>
// Close flash messages after 5 seconds
setTimeout(function() {
    const alerts = document.querySelectorAll('.alert-success, .alert-error, .alert-info');
    alerts.forEach(alert => {
        alert.style.opacity = '0';
        setTimeout(() => alert.remove(), 300);
    });
}, 5000);
</script>
</body>
</html>
<?php
if (isset($conn) && $conn) {
    $conn->close();
}
?>