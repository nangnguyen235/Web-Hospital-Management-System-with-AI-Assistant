using HIS.Modules.LichKham.Contracts;
using Microsoft.Extensions.DependencyInjection;

namespace HIS.Modules.LichKham.Application;

public static class DependencyInjection
{
    public static IServiceCollection AddLichKhamApplication(this IServiceCollection services)
    {
        services.AddScoped<SoDangGoiService>();
        services.AddScoped<ILichKhamThongKe, LichKhamThongKe>();
        return services;
    }
}
