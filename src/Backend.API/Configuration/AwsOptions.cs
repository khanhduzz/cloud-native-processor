namespace Backend.API.Configuration;

public class AwsOptions
{
    public const string SectionName = "AwsConfig"; // Matches our .env/appsettings logic

    public string ServiceUrl { get; set; } = string.Empty;
    public string BucketName { get; set; } = string.Empty;
    public string Region { get; set; } = "us-east-1";
}