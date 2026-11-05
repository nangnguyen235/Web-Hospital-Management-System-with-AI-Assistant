using HIS.Modules.NguoiDung.Contracts;
using Microsoft.Extensions.DependencyInjection;

namespace HIS.Modules.NguoiDung.Application;

public static class DependencyInjection
{
    public static IServiceCollection AddNguoiDungApplication(this IServiceCollection services)
    {
        services.AddScoped<TaiKhoanService>();
        services.AddScoped<INguoiDungThongKe, NguoiDungThongKe>();
        return services;
    }
}
