using HIS.Modules.TroLyAi.Application;
using HIS.SharedKernel.Infrastructure.Persistence;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;

namespace HIS.Modules.TroLyAi.Infrastructure;

public static class DependencyInjection
{
    /// <summary>Đăng ký toàn bộ module TroLyAi (HIS.Api gọi).</summary>
    public static IServiceCollection AddTroLyAiModule(this IServiceCollection services, IConfiguration configuration)
    {
        services.AddModuleDbContext<TroLyAiDbContext>(configuration, "tl");
        services.AddScoped<ITroLyAiDbContext>(sp => sp.GetRequiredService<TroLyAiDbContext>());
        services.AddTroLyAiApplication();
        return services;
    }
}
