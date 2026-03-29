using Amazon.S3;
using Backend.API.Configuration;
using Backend.API.Data;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Options;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
// Learn more about configuring OpenAPI at https://aka.ms/aspnet/openapi
builder.Services.AddOpenApi();

DotNetEnv.Env.Load("../../.env");

// Get Connection String from Environment Variables
var connectionString = $"Host={Environment.GetEnvironmentVariable("DB_HOST")};" +
                       $"Port={Environment.GetEnvironmentVariable("DB_PORT")};" +
                       $"Database={Environment.GetEnvironmentVariable("DB_NAME")};" +
                       $"Username={Environment.GetEnvironmentVariable("DB_USER")};" +
                       $"Password={Environment.GetEnvironmentVariable("DB_PASSWORD")}";

builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseNpgsql(connectionString));

builder.Services.Configure<AwsOptions>(options =>
{
    options.ServiceUrl = Environment.GetEnvironmentVariable("AWS_SERVICE_URL") ?? "http://localhost:4566";
    options.BucketName = Environment.GetEnvironmentVariable("S3_BUCKET_NAME") ?? "wedding-uploads";
    options.Region = Environment.GetEnvironmentVariable("AWS_REGION") ?? "us-east-1";
});
builder.Services.AddSingleton<IAmazonS3>(sp =>
{
    var awsOptions = sp.GetRequiredService<IOptions<AwsOptions>>().Value;
    var config = new AmazonS3Config
    {
        ServiceURL = awsOptions.ServiceUrl,
        ForcePathStyle = true
    };
    return new AmazonS3Client("test", "test", config);
});

builder.Services.AddControllers();
var app = builder.Build();

app.MapControllers();
// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

app.UseHttpsRedirection();
app.Run();
