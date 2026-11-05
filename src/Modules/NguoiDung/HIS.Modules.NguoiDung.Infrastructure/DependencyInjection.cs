using HIS.Modules.NguoiDung.Application;
using HIS.SharedKernel.Infrastructure.Persistence;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;

namespace HIS.Modules.NguoiDung.Infrastructure;

public static class DependencyInjection
{
    /// <summary>Đăng ký toàn bộ module NguoiDung (HIS.Api gọi).</summary>
    public static IServiceCollection AddNguoiDungModule(this IServiceCollection services, IConfiguration configuration)
    {
        services.AddModuleDbContext<NguoiDungDbContext>(configuration, "tk");
        services.AddScoped<INguoiDungDbContext>(sp => sp.GetRequiredService<NguoiDungDbContext>());
        services.AddNguoiDungApplication();
        services.AddSingleton<IMatKhauHasher, BCryptMatKhauHasher>();
        return services;
    }
}
