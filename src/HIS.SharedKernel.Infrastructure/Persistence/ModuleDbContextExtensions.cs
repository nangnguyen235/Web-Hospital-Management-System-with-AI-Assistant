using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;

namespace HIS.SharedKernel.Infrastructure.Persistence;

public static class ModuleDbContextExtensions
{
    /// <summary>
    /// Đăng ký DbContext của một module trên CSDL MySQL chung. Mỗi module một bảng lịch sử migration
    /// <c>__ef_lich_su_&lt;tiền tố&gt;</c> để 6 context không giẫm nhau (modular-monolith.md mục 4).
    /// </summary>
    public static IServiceCollection AddModuleDbContext<TContext>(
        this IServiceCollection services, IConfiguration configuration, string tienTo)
        where TContext : DbContext
    {
        var connectionString = configuration.GetConnectionString("MySql")
            ?? throw new InvalidOperationException("Thiếu biến ConnectionStrings__MySql.");

        services.AddDbContext<TContext>((sp, options) => options
            .UseMySQL(connectionString, mysql => mysql.MigrationsHistoryTable($"__ef_lich_su_{tienTo}"))
            .UseSnakeCaseNamingConvention()
            .AddInterceptors(sp.GetRequiredService<ThoiGianInterceptor>()));
        return services;
    }
}
