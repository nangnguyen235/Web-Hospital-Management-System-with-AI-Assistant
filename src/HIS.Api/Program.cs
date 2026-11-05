using System.Text.Json.Serialization;
using HIS.Api;
using HIS.Modules.DanhMuc.Infrastructure;
using HIS.Modules.HoSoBenhAn.Infrastructure;
using HIS.Modules.LichKham.Infrastructure;
using HIS.Modules.NguoiDung.Infrastructure;
using HIS.Modules.TroLyAi.Infrastructure;
using HIS.Modules.VienPhi.Infrastructure;
using HIS.SharedKernel.Infrastructure;
using HIS.SharedKernel.Presentation;
using Microsoft.AspNetCore.HttpOverrides;

var builder = WebApplication.CreateBuilder(args);
var config = builder.Configuration;

// Dùng chung + 6 module (mỗi module tự đăng ký DbContext và service — modular-monolith.md mục 4)
builder.Services
    .AddSharedKernelInfrastructure(config, ModuleCatalog.ApplicationAssemblies)
    .AddSharedKernelPresentation()
    .AddXacThucJwt(config)
    .AddNguoiDungModule(config)
    .AddDanhMucModule(config)
    .AddLichKhamModule(config)
    .AddHoSoBenhAnModule(config)
    .AddVienPhiModule(config)
    .AddTroLyAiModule(config);

var mvc = builder.Services.AddControllers()
    .AddJsonOptions(o => o.JsonSerializerOptions.Converters.Add(new JsonStringEnumConverter()));
foreach (var assembly in ModuleCatalog.PresentationAssemblies)
{
    mvc.AddApplicationPart(assembly);
}

builder.Services.AddProblemDetails();
builder.Services.AddExceptionHandler<GlobalExceptionHandler>();
builder.Services.AddHealthChecks();
builder.Services.AddSwaggerVoiJwt();
builder.Services.AddCors(o => o.AddDefaultPolicy(p => p
    .WithOrigins((config["Cors:AllowedOrigins"] ?? "").Split(',', StringSplitOptions.RemoveEmptyEntries | StringSplitOptions.TrimEntries))
    .AllowAnyHeader()
    .AllowAnyMethod()));
builder.Services.Configure<ForwardedHeadersOptions>(o =>
{
    // NGINX trong Compose đứng trước backend (5-trien-khai-cicd/vps-cloudflare-cicd.md mục 1)
    o.ForwardedHeaders = ForwardedHeaders.XForwardedFor | ForwardedHeaders.XForwardedProto;
    o.KnownIPNetworks.Clear();
    o.KnownProxies.Clear();
});

var app = builder.Build();

app.UseForwardedHeaders();
app.UseExceptionHandler();
if (app.Environment.IsDevelopment() || config.GetValue<bool>("Swagger:Enabled"))
{
    app.UseSwagger();
    app.UseSwaggerUI(o => o.DocumentTitle = "HIS API — TheHos");
}
app.UseCors();
app.UseAuthentication();
app.UseAuthorization();
app.MapControllers();
app.MapHealthChecks("/health");

await app.KhoiTaoCoSoDuLieuAsync();
await app.RunAsync();

/// <summary>Để test tích hợp (WebApplicationFactory) truy cập được.</summary>
public partial class Program;
