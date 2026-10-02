<?php
// QA Clothing Factory - Quote Request Form Handler
// WAMP Compatible | No Database Required

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    
    // Honeypot spam protection
    if (!empty($_POST['website'])) {
        header("Location: ../thank-you.html");
        exit();
    }
    
    // Sanitize input
    $fullname = filter_var(trim($_POST['fullname'] ?? ''), FILTER_SANITIZE_STRING);
    $company = filter_var(trim($_POST['company'] ?? ''), FILTER_SANITIZE_STRING);
    $email = filter_var(trim($_POST['email'] ?? ''), FILTER_SANITIZE_EMAIL);
    $phone = filter_var(trim($_POST['phone'] ?? ''), FILTER_SANITIZE_STRING);
    $category = filter_var(trim($_POST['category'] ?? ''), FILTER_SANITIZE_STRING);
    $quantity = filter_var(trim($_POST['quantity'] ?? ''), FILTER_SANITIZE_STRING);
    $deadline = filter_var(trim($_POST['deadline'] ?? ''), FILTER_SANITIZE_STRING);
    $specifications = filter_var(trim($_POST['specifications'] ?? ''), FILTER_SANITIZE_STRING);
    
    // Validation
    $errors = [];
    
    if (empty($fullname)) $errors[] = "Full name is required";
    if (empty($email) || !filter_var($email, FILTER_VALIDATE_EMAIL)) $errors[] = "Valid email is required";
    if (empty($phone)) $errors[] = "Phone number is required";
    if (empty($category)) $errors[] = "Product category is required";
    if (empty($quantity)) $errors[] = "Quantity is required";
    if (empty($deadline)) $errors[] = "Deadline is required";
    if (empty($specifications)) $errors[] = "Specifications are required";
    
    if (!empty($errors)) {
        echo "<h3>Please fix the following errors:</h3><ul>";
        foreach ($errors as $error) {
            echo "<li>$error</li>";
        }
        echo "</ul><a href='../contact.html'>Go Back</a>";
        exit();
    }
    
    // Email configuration
    $to = "sales@clothingfactory.co";
    $subject = "NEW QUOTE REQUEST: $category from $fullname";
    
    $email_body = "QUOTE REQUEST DETAILS\n\n";
    $email_body .= "Customer Information:\n";
    $email_body .= "-------------------\n";
    $email_body .= "Full Name: $fullname\n";
    $email_body .= "Company: " . ($company ?: "Not provided") . "\n";
    $email_body .= "Email: $email\n";
    $email_body .= "Phone: $phone\n\n";
    
    $email_body .= "Order Requirements:\n";
    $email_body .= "-------------------\n";
    $email_body .= "Product Category: $category\n";
    $email_body .= "Estimated Quantity: $quantity\n";
    $email_body .= "Deadline: $deadline\n\n";
    
    $email_body .= "Specifications:\n";
    $email_body .= "-------------------\n";
    $email_body .= "$specifications\n\n";
    
    $email_body .= "PRIORITY: Please respond within 24 hours.\n";
    
    $headers = "From: $email\r\n";
    $headers .= "Reply-To: $email\r\n";
    $headers .= "Priority: Urgent\r\n";
    $headers .= "X-Mailer: PHP/" . phpversion();
    
    // Auto-responder for customer
    $auto_subject = "Quote Request Received - QA Clothing Factory";
    $auto_message = "Dear $fullname,\n\n";
    $auto_message .= "Thank you for your quote request for $category.\n\n";
    $auto_message .= "We have received your specifications and will prepare a detailed quote for you within 24 business hours.\n\n";
    $auto_message .= "Quote Request Summary:\n";
    $auto_message .= "- Category: $category\n";
    $auto_message .= "- Quantity: $quantity\n";
    $auto_message .= "- Deadline: $deadline\n\n";
    $auto_message .= "A sales representative will contact you shortly to discuss your requirements in detail.\n\n";
    $auto_message .= "For urgent inquiries, please call us at 078 117 0652.\n\n";
    $auto_message .= "Regards,\n";
    $auto_message .= "QA Clothing Factory Sales Team\n";
    $auto_message .= "Quality Assured Since 2014";
    
    $auto_headers = "From: sales@clothingfactory.co\r\n";
    $auto_headers .= "Reply-To: sales@clothingfactory.co\r\n";
    
    // Send emails
    $mail_sent = mail($to, $subject, $email_body, $headers);
    $auto_sent = mail($email, $auto_subject, $auto_message, $auto_headers);
    
    // Redirect on success
    if ($mail_sent) {
        header("Location: ../thank-you.html");
        exit();
    } else {
        echo "Sorry, there was an error submitting your quote request. Please call us directly at 078 117 0652.";
    }
    
} else {
    header("Location: ../contact.html");
    exit();
}
?>