namespace QAClothingFactory.API.Models
{
    public class LoginAttempt
    {
        public int AttemptID { get; set; }

        public string EmailAddress { get; set; } = string.Empty;

        public string IPAddress { get; set; } = string.Empty;

        public DateTime? AttemptTime { get; set; }

        public bool? Success { get; set; }
    }
}