using System.Security.Cryptography;

namespace QAClothingFactory.API.Services
{
    public sealed class CustomerTrackingPinService
    {
        private const int PinLength = 12;

        public string GeneratePin()
        {
            var digits = new char[PinLength];
            for (var i = 0; i < digits.Length; i++)
                digits[i] = (char)('0' + RandomNumberGenerator.GetInt32(10));
            return new string(digits);
        }

        public string HashPin(string pin)
        {
            if (!IsValidPin(pin))
                throw new ArgumentException("PIN must contain exactly 12 digits.", nameof(pin));
            return BCrypt.Net.BCrypt.HashPassword(pin, workFactor: 12);
        }

        public bool VerifyPin(string pin, string? hash)
        {
            if (!IsValidPin(pin) || string.IsNullOrWhiteSpace(hash))
                return false;
            try { return BCrypt.Net.BCrypt.Verify(pin, hash); }
            catch (BCrypt.Net.SaltParseException) { return false; }
        }

        public bool IsValidPin(string? pin) =>
            pin is { Length: PinLength } &&
            pin.All(c => c >= '0' && c <= '9');
    }
}
