using HIS.Modules.DanhMuc.Infrastructure;
using HIS.Modules.HoSoBenhAn.Infrastructure;
using HIS.Modules.LichKham.Infrastructure;
using HIS.Modules.NguoiDung.Infrastructure;
using HIS.Modules.TroLyAi.Infrastructure;
using HIS.Modules.VienPhi.Infrastructure;
using Microsoft.EntityFrameworkCore;

namespace HIS.Api;

internal static class KhoiTaoCoSoDuLieu
{
    /// <summary>
    /// Database__AutoMigrate=true: chờ MySQL sẵn sàng, chạy migration của 6 DbContext rồi tạo Admin gốc
    /// (modular-monolith.md mục 4 — không chạy migration riêng trong pipeline).
    /// </summary>
    public static async Task KhoiTaoCoSoDuLieuAsync(this WebApplication app)
    {
        if (!app.Configuration.GetValue<bool>("Database:AutoMigrate")) return;

        using var scope = app.Services.CreateScope();
        var sp = scope.ServiceProvider;
        var logger = sp.GetRequiredService<ILoggerFactory>().CreateLogger(nameof(KhoiTaoCoSoDuLieu));
        DbContext[] contexts =
        [
            sp.GetRequiredService<NguoiDungDbContext>(),
            sp.GetRequiredService<DanhMucDbContext>(),
            sp.GetRequiredService<LichKhamDbContext>(),
            sp.GetRequiredService<HoSoBenhAnDbContext>(),
            sp.GetRequiredService<VienPhiDbContext>(),
            sp.GetRequiredService<TroLyAiDbContext>(),
        ];

        for (var lan = 1; lan <= 15; lan++)
        {
            if (await contexts[0].Database.CanConnectAsync()) break;
            logger.LogInformation("Chờ MySQL sẵn sàng (lần {Lan}/15)...", lan);
            await Task.Delay(TimeSpan.FromSeconds(3));
        }

        foreach (var context in contexts)
        {
            await context.Database.MigrateAsync();
        }
        logger.LogInformation("Đã chạy migration cho {SoContext} DbContext.", contexts.Length);

        await NguoiDungSeeder.TaoAdminGocAsync(sp, app.Configuration);
    }
}
