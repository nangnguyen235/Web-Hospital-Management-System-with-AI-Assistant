using HIS.Modules.DanhMuc.Contracts;
using Microsoft.Extensions.DependencyInjection;

namespace HIS.Modules.DanhMuc.Application;

public static class DependencyInjection
{
    public static IServiceCollection AddDanhMucApplication(this IServiceCollection services)
    {
        services.AddScoped<KhoaService>();
        services.AddScoped<DanTocService>();
        services.AddScoped<IDanhMucTraCuu, DanhMucTraCuu>();
        return services;
    }
}
