using System.Reflection;
using HIS.SharedKernel.Abstractions;
using HIS.SharedKernel.Auth;
using HIS.SharedKernel.Events;
using HIS.SharedKernel.Infrastructure.Auth;
using HIS.SharedKernel.Infrastructure.Email;
using HIS.SharedKernel.Infrastructure.Events;
using HIS.SharedKernel.Infrastructure.Persistence;
using HIS.SharedKernel.Infrastructure.Storage;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.IdentityModel.Tokens;

namespace HIS.SharedKernel.Infrastructure;

public static class DependencyInjection
{
    /// <summary>Đăng ký dịch vụ dùng chung; <paramref name="moduleAssemblies"/> là các assembly Application có handler sự kiện.</summary>
    public static IServiceCollection AddSharedKernelInfrastructure(
        this IServiceCollection services, IConfiguration configuration, params Assembly[] moduleAssemblies)
    {
        services.AddSingleton(TimeProvider.System);
        services.AddSingleton<ThoiGianInterceptor>();

        services.AddMediatR(cfg => cfg.RegisterServicesFromAssemblies(moduleAssemblies));
        services.AddScoped<IEventBus, MediatrEventBus>();

        services.Configure<R2Options>(configuration.GetSection("R2"));
        services.AddSingleton<IStorageService, S3StorageWrapper>();

        services.Configure<SmtpOptions>(configuration.GetSection("Smtp"));
        services.AddScoped<IEmailService, SmtpEmailService>();

        services.Configure<JwtOptions>(configuration.GetSection("Jwt"));
        services.AddSingleton<IJwtTokenService, JwtTokenService>();
        return services;
    }

    /// <summary>Xác thực JWT + phân quyền theo vai trò và cờ trưởng khoa/điều dưỡng trưởng (NFR-02).</summary>
    public static IServiceCollection AddXacThucJwt(this IServiceCollection services, IConfiguration configuration)
    {
        var jwt = configuration.GetSection("Jwt").Get<JwtOptions>() ?? new JwtOptions();
        services
            .AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
            .AddJwtBearer(options =>
            {
                options.MapInboundClaims = false;
                options.TokenValidationParameters = new TokenValidationParameters
                {
                    ValidateIssuer = true,
                    ValidIssuer = jwt.Issuer,
                    ValidateAudience = true,
                    ValidAudience = jwt.Audience,
                    ValidateIssuerSigningKey = true,
                    IssuerSigningKey = JwtTokenService.TaoKhoa(jwt.Key),
                    ValidateLifetime = true,
                    ClockSkew = TimeSpan.FromMinutes(1),
                    RoleClaimType = ClaimTen.VaiTro,
                    NameClaimType = ClaimTen.TaiKhoanId,
                };
            });

        services.AddAuthorizationBuilder()
            .AddPolicy(Quyen.TruongKhoa, p => p.RequireRole(Quyen.BacSi).RequireClaim(ClaimTen.LaTruongKhoa, "true"))
            .AddPolicy(Quyen.DieuDuongTruong, p => p.RequireRole(Quyen.DieuDuong).RequireClaim(ClaimTen.LaTruongKhoa, "true"));
        return services;
    }
}
