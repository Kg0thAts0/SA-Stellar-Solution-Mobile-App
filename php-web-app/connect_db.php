<?php
/**
 * Database Connection Handler
 * QA Clothing Factory System
 */

// Database configuration for WAMP
$host = "localhost";
$user = "root";
$pass = "";        // Leave empty for WAMP default
$dbname = "qaclothingfactory";

// Create connection
$conn = new mysqli($host, $user, $pass, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Set charset to UTF-8
$conn->set_charset("utf8mb4");

// Connection successful - no errors
// echo "Connected successfully"; // Uncomment for testing
?>