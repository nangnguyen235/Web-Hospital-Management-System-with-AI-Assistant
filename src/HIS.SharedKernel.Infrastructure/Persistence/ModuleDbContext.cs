using HIS.SharedKernel.Domain;
using Microsoft.EntityFrameworkCore;

namespace HIS.SharedKernel.Infrastructure.Persistence;

/// <summary>
/// Lớp gốc cho DbContext của từng module: enum lưu chuỗi, tiền DECIMAL(12,2), chuỗi mặc định VARCHAR(255),
/// <c>phien_ban</c> là concurrency token. Tên snake_case đặt ở <see cref="ModuleDbContextExtensions"/>.
/// </summary>
public abstract class ModuleDbContext(DbContextOptions options) : DbContext(options)
{
    protected override void ConfigureConventions(ModelConfigurationBuilder configurationBuilder)
    {
        configurationBuilder.Properties<Enum>().HaveConversion<string>().HaveMaxLength(30);
        configurationBuilder.Properties<decimal>().HavePrecision(12, 2);
        configurationBuilder.Properties<string>().HaveMaxLength(255);
    }

    /// <summary>Lớp con gọi base.OnModelCreating(modelBuilder) SAU khi khai báo bảng.</summary>
    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        foreach (var entityType in modelBuilder.Model.GetEntityTypes())
        {
            if (entityType.BaseType is null && typeof(ICoPhienBan).IsAssignableFrom(entityType.ClrType))
            {
                modelBuilder.Entity(entityType.ClrType).Property(nameof(ICoPhienBan.PhienBan)).IsConcurrencyToken();
            }

            // Dữ liệu chỉ xóa mềm (da_xoa) → không cho xóa cứng dây chuyền.
            foreach (var foreignKey in entityType.GetForeignKeys())
            {
                foreignKey.DeleteBehavior = DeleteBehavior.Restrict;
            }
        }
    }
}
