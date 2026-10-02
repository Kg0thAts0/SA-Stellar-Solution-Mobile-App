<?php
// QA Clothing Factory - Contact Form Handler
// WAMP Compatible | No Database Required

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    
    // Honeypot spam protection (hidden field)
    if (!empty($_POST['website'])) {
        header("Location: ../thank-you.html");
        exit();
    }
    
    // Sanitize and validate input
    $firstname = filter_var(trim($_POST['firstname'] ?? ''), FILTER_SANITIZE_STRING);
    $lastname = filter_var(trim($_POST['lastname'] ?? ''), FILTER_SANITIZE_STRING);
    $phone = filter_var(trim($_POST['phone'] ?? ''), FILTER_SANITIZE_STRING);
    $email = filter_var(trim($_POST['email'] ?? ''), FILTER_SANITIZE_EMAIL);
    $inquiry_type = filter_var(trim($_POST['inquiry_type'] ?? ''), FILTER_SANITIZE_STRING);
    $message = filter_var(trim($_POST['message'] ?? ''), FILTER_SANITIZE_STRING);
    
    // Validation
    $errors = [];
    
    if (empty($firstname)) $errors[] = "First name is required";
    if (empty($lastname)) $errors[] = "Last name is required";
    if (empty($phone)) $errors[] = "Phone number is required";
    if (empty($email) || !filter_var($email, FILTER_VALIDATE_EMAIL)) $errors[] = "Valid email is required";
    if (empty($inquiry_type)) $errors[] = "Inquiry type is required";
    if (empty($message)) $errors[] = "Message is required";
    
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
    $subject = "New Contact Inquiry: $inquiry_type";
    
    $email_body = "CONTACT FORM SUBMISSION\n\n";
    $email_body .= "Name: $firstname $lastname\n";
    $email_body .= "Phone: $phone\n";
    $email_body .= "Email: $email\n";
    $email_body .= "Inquiry Type: $inquiry_type\n\n";
    $email_body .= "Message:\n$message\n";
    
    $headers = "From: $email\r\n";
    $headers .= "Reply-To: $email\r\n";
    $headers .= "X-Mailer: PHP/" . phpversion();
    
    // Auto-responder for customer
    $auto_subject = "Thank you for contacting QA Clothing Factory";
    $auto_message = "Dear $firstname,\n\n";
    $auto_message .= "Thank you for contacting QA Clothing Factory. We have received your inquiry regarding $inquiry_type.\n\n";
    $auto_message .= "A member of our team will get back to you within 24 business hours.\n\n";
    $auto_message .= "For urgent matters, please call us at 078 117 0652 or WhatsApp us.\n\n";
    $auto_message .= "Regards,\n";
    $auto_message .= "QA Clothing Factory Team\n";
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
        echo "Sorry, there was an error sending your message. Please try again or call us directly.";
    }
    
} else {
    // Not a POST request
    header("Location: ../contact.html");
    exit();
}
?>