using HIS.Modules.NguoiDung.Application;
using HIS.Modules.NguoiDung.Domain;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;

namespace HIS.Modules.NguoiDung.Infrastructure;

/// <summary>Admin gốc từ biến Seed__* (BR-02), chỉ tạo khi chưa có Admin nào. Mật khẩu không hardcode.</summary>
public static class NguoiDungSeeder
{
    public static async Task TaoAdminGocAsync(IServiceProvider services, IConfiguration configuration, CancellationToken ct = default)
    {
        var db = services.GetRequiredService<NguoiDungDbContext>();
        var logger = services.GetRequiredService<ILoggerFactory>().CreateLogger(typeof(NguoiDungSeeder));
        if (await db.NhanVien.OfType<QuanTri>().AnyAsync(ct)) return;

        var email = configuration["Seed:AdminEmail"];
        var soDienThoai = configuration["Seed:AdminSoDienThoai"];
        var matKhau = configuration["Seed:AdminMatKhau"];
        if (string.IsNullOrWhiteSpace(email) || string.IsNullOrWhiteSpace(soDienThoai) || string.IsNullOrWhiteSpace(matKhau))
        {
            logger.LogWarning("Chưa có Admin và thiếu Seed__AdminEmail/Seed__AdminSoDienThoai/Seed__AdminMatKhau — bỏ qua tạo Admin gốc.");
            return;
        }

        var hoTen = configuration["Seed:AdminHoTen"] ?? "Quản trị viên";
        var hasher = services.GetRequiredService<IMatKhauHasher>();
        var admin = new QuanTri { HoTen = hoTen, SoDienThoai = soDienThoai, GioiTinh = GioiTinh.NAM };
        db.NhanVien.Add(admin);
        db.TaiKhoan.Add(new TaiKhoan
        {
            HoTen = hoTen,
            Email = email,
            SoDienThoai = soDienThoai,
            MatKhauHash = hasher.Hash(matKhau),
            NhanVien = admin,
        });
        await db.SaveChangesAsync(ct);
        logger.LogInformation("Đã tạo Admin gốc.");
    }
}
