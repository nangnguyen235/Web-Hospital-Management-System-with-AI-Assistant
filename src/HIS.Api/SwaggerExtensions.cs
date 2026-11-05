using Microsoft.OpenApi;

namespace HIS.Api;

internal static class SwaggerExtensions
{
    /// <summary>Swagger có nút Authorize (Bearer JWT) để kiểm thử API (tiêu chí Exemplary 2).</summary>
    public static IServiceCollection AddSwaggerVoiJwt(this IServiceCollection services)
    {
        services.AddEndpointsApiExplorer();
        services.AddSwaggerGen(c =>
        {
            c.SwaggerDoc("v1", new OpenApiInfo { Title = "HIS API — TheHos", Version = "v1" });
            c.CustomSchemaIds(t => t.FullName?.Replace('+', '.'));
            c.AddSecurityDefinition("Bearer", new OpenApiSecurityScheme
            {
                Type = SecuritySchemeType.Http,
                Scheme = "bearer",
                BearerFormat = "JWT",
                Description = "Dán access token lấy từ POST /api/nguoi-dung/dang-nhap",
            });
            c.AddSecurityRequirement(document => new OpenApiSecurityRequirement
            {
                [new OpenApiSecuritySchemeReference("Bearer", document)] = [],
            });
        });
        return services;
    }
}
