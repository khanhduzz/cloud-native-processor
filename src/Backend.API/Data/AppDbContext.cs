using Microsoft.EntityFrameworkCore;
using Backend.API.Models;

namespace Backend.API.Data;

public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

    public DbSet<ImageMetadata> Images => Set<ImageMetadata>();
}