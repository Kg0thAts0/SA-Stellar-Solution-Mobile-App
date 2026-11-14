using MailKit;
using MailKit.Net.Smtp;
using MailKit.Security;
using MimeKit;

namespace QAClothingFactory.API.Services
{
    public class GmailEmailService
    {
        private readonly IConfiguration _configuration;
        private readonly ILogger<GmailEmailService> _logger;

        public GmailEmailService(
            IConfiguration configuration,
            ILogger<GmailEmailService> logger)
        {
            _configuration = configuration;
            _logger = logger;
        }

        public async Task SendEmailAsync(
            string recipientEmail,
            string subject,
            string body,
            CancellationToken cancellationToken = default)
        {
            if (string.IsNullOrWhiteSpace(recipientEmail))
                throw new ArgumentException(
                    "Recipient email is required.",
                    nameof(recipientEmail));

            var host = _configuration["Email:SmtpHost"];
            var portValue = _configuration["Email:SmtpPort"];
            var username = _configuration["Email:Username"];
            var password = _configuration["Email:Password"];
            var senderEmail = _configuration["Email:SenderEmail"];
            var senderName =
                _configuration["Email:SenderName"]
                ?? "QA Clothing Factory";

            if (string.IsNullOrWhiteSpace(host) ||
                !int.TryParse(portValue, out var port) ||
                string.IsNullOrWhiteSpace(username) ||
                string.IsNullOrWhiteSpace(password) ||
                string.IsNullOrWhiteSpace(senderEmail))
            {
                throw new InvalidOperationException(
                    "Gmail SMTP configuration is incomplete.");
            }

            if (port != 587)
            {
                throw new InvalidOperationException(
                    "This Gmail SMTP service requires port 587.");
            }

            var message = new MimeMessage();

            message.From.Add(
                new MailboxAddress(senderName, senderEmail));

            message.To.Add(
                MailboxAddress.Parse(recipientEmail));

            message.Subject = subject;

            message.Body = new TextPart("plain")
            {
                Text = body
            };

            using var client = new SmtpClient();

            try
            {
                await client.ConnectAsync(
                    host,
                    port,
                    SecureSocketOptions.StartTls,
                    cancellationToken);

                await client.AuthenticateAsync(
                    username,
                    password,
                    cancellationToken);

                await client.SendAsync(
                    message,
                    cancellationToken);

                await client.DisconnectAsync(
                    true,
                    cancellationToken);

                _logger.LogInformation(
                    "Email submitted to SMTP for recipient {Recipient}",
                    recipientEmail);
            }
            catch (Exception ex)
            {
                _logger.LogError(
                    ex,
                    "SMTP delivery failed for recipient {Recipient}",
                    recipientEmail);

                throw;
            }
        }
    }
}
