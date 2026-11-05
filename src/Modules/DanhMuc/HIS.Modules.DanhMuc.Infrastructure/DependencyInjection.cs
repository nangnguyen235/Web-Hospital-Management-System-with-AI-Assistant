using HIS.Modules.DanhMuc.Application;
using HIS.SharedKernel.Infrastructure.Persistence;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;

namespace HIS.Modules.DanhMuc.Infrastructure;

public static class DependencyInjection
{
    /// <summary>Đăng ký toàn bộ module DanhMuc (HIS.Api gọi).</summary>
    public static IServiceCollection AddDanhMucModule(this IServiceCollection services, IConfiguration configuration)
    {
        services.AddModuleDbContext<DanhMucDbContext>(configuration, "dm");
        services.AddScoped<IDanhMucDbContext>(sp => sp.GetRequiredService<DanhMucDbContext>());
        services.AddDanhMucApplication();
        return services;
    }
}
