using HIS.Modules.LichKham.Application;
using HIS.SharedKernel.Infrastructure.Persistence;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;

namespace HIS.Modules.LichKham.Infrastructure;

public static class DependencyInjection
{
    /// <summary>Đăng ký toàn bộ module LichKham (HIS.Api gọi).</summary>
    public static IServiceCollection AddLichKhamModule(this IServiceCollection services, IConfiguration configuration)
    {
        services.AddModuleDbContext<LichKhamDbContext>(configuration, "lk");
        services.AddScoped<ILichKhamDbContext>(sp => sp.GetRequiredService<LichKhamDbContext>());
        services.AddLichKhamApplication();
        return services;
    }
}
