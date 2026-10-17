namespace QAClothingFactory.API.DTOs.Production
{
    public class ProductionSupervisorResponse
    {
        public int EmployeeID { get; set; }

        public string FullName { get; set; } = string.Empty;

        public string EmailAddress { get; set; } = string.Empty;
    }
}