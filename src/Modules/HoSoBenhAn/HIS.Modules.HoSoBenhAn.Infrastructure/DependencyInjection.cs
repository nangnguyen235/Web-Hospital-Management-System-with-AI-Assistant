using HIS.Modules.HoSoBenhAn.Application;
using HIS.SharedKernel.Infrastructure.Persistence;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;

namespace HIS.Modules.HoSoBenhAn.Infrastructure;

public static class DependencyInjection
{
    /// <summary>Đăng ký toàn bộ module HoSoBenhAn (HIS.Api gọi).</summary>
    public static IServiceCollection AddHoSoBenhAnModule(this IServiceCollection services, IConfiguration configuration)
    {
        services.AddModuleDbContext<HoSoBenhAnDbContext>(configuration, "ba");
        services.AddScoped<IHoSoBenhAnDbContext>(sp => sp.GetRequiredService<HoSoBenhAnDbContext>());
        services.AddHoSoBenhAnApplication();
        return services;
    }
}
