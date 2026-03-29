using Amazon.S3;
using Amazon.S3.Model;
using Backend.API.Configuration;
using Backend.API.Data;
using Backend.API.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Options;
using Microsoft.EntityFrameworkCore;

[ApiController]
[Route("api/[controller]")]
public class UploadController : ControllerBase
{
    private readonly IAmazonS3 _s3Client;
    private readonly AppDbContext _context;
    private readonly AwsOptions _awsOptions;

    public UploadController(IAmazonS3 s3Client, AppDbContext context, IOptions<AwsOptions> awsOptions)
    {
        _s3Client = s3Client;
        _context = context;
        _awsOptions = awsOptions.Value;
    }

    [HttpPost]
    [Consumes("multipart/form-data")]
    public async Task<IActionResult> Upload(IFormFile file)
    {
        // 1. Upload to S3 (LocalStack)
        var key = $"{Guid.NewGuid()}-{file.FileName}";
        using var stream = file.OpenReadStream();

        var putRequest = new PutObjectRequest
        {
            BucketName = _awsOptions.BucketName,
            Key = key,
            InputStream = stream,
            ContentType = file.ContentType
        };

        await _s3Client.PutObjectAsync(putRequest);

        // 2. Save Metadata to Postgres
        var metadata = new ImageMetadata
        {
            FileName = file.FileName,
            S3Url = $"{_awsOptions.ServiceUrl}/{_awsOptions.BucketName}/{key}",
            FileSize = file.Length,
            ContentType = file.ContentType
        };

        _context.Images.Add(metadata);
        await _context.SaveChangesAsync();

        return Ok(new { metadata.Id, metadata.S3Url });
    }
    // [HttpGet]
    // public async Task<IActionResult> GetAll()
    // {
    //     // Fetch all records from Postgres, ordered by most recent
    //     var images = await _context.Images
    //         .OrderByDescending(i => i.Id)
    //         .ToListAsync();

    //     return Ok(images);
    // }

    [HttpGet]
    public async Task<IActionResult> GetAll()
    {
        var images = await _context.Images.ToListAsync();

        // Transform the static S3Url into a temporary secure URL
        var response = images.Select(img => new
        {
            img.Id,
            img.FileName,
            SecureUrl = GeneratePreSignedUrl(Path.GetFileName(img.S3Url)), // Extract key from stored URL
            img.CreatedAt
        });

        return Ok(response);
    }

    [HttpDelete("{id:guid}")]
    public async Task<IActionResult> Delete(Guid id)
    {
        // 1. Find the record in Postgres
        var image = await _context.Images.FindAsync(id);
        if (image == null) return NotFound();

        // 2. Extract the S3 Key (filename) from the stored URL
        var s3Key = Path.GetFileName(image.S3Url);

        try
        {
            // 3. Delete from S3 (LocalStack)
            await _s3Client.DeleteObjectAsync(_awsOptions.BucketName, s3Key);

            // 4. Delete from Postgres
            _context.Images.Remove(image);
            await _context.SaveChangesAsync();

            return NoContent(); // 204 Success
        }
        catch (Exception ex)
        {
            return StatusCode(500, $"Error deleting file: {ex.Message}");
        }
    }

    [HttpGet("{id:guid}")]
    public async Task<IActionResult> GetById(Guid id)
    {
        // Find the specific record by its Primary Key
        var image = await _context.Images.FindAsync(id);

        if (image == null)
        {
            return NotFound(new { message = $"Image with ID {id} not found." });
        }

        return Ok(image);
    }

    private string GeneratePreSignedUrl(string objectKey)
    {
        var request = new GetPreSignedUrlRequest
        {
            BucketName = _awsOptions.BucketName,
            Key = objectKey,
            Expires = DateTime.UtcNow.AddMinutes(10) // URL expires in 10 minutes
        };

        return _s3Client.GetPreSignedURL(request);
    }
}