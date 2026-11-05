using HIS.Modules.VienPhi.Application;
using HIS.SharedKernel.Infrastructure.Persistence;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;

namespace HIS.Modules.VienPhi.Infrastructure;

public static class DependencyInjection
{
    /// <summary>Đăng ký toàn bộ module VienPhi (HIS.Api gọi).</summary>
    public static IServiceCollection AddVienPhiModule(this IServiceCollection services, IConfiguration configuration)
    {
        services.AddModuleDbContext<VienPhiDbContext>(configuration, "vp");
        services.AddScoped<IVienPhiDbContext>(sp => sp.GetRequiredService<VienPhiDbContext>());
        services.AddVienPhiApplication();
        return services;
    }
}
