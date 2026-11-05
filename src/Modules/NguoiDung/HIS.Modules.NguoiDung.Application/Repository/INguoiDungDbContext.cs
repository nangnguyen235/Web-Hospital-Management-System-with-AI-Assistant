using HIS.Modules.NguoiDung.Domain;
using Microsoft.EntityFrameworkCore;

namespace HIS.Modules.NguoiDung.Application;

/// <summary>Application dùng thẳng DbSet (không repository — modular-monolith.md mục 4).</summary>
public interface INguoiDungDbContext
{
    DbSet<TaiKhoan> TaiKhoan { get; }
    DbSet<NguoiDungTam> NguoiDungTam { get; }
    DbSet<NhanVien> NhanVien { get; }
    DbSet<BenhNhan> BenhNhan { get; }
    DbSet<HoSoBenhNhan> HoSoBenhNhan { get; }
    DbSet<ThongBao> ThongBao { get; }
    Task<int> SaveChangesAsync(CancellationToken cancellationToken = default);
}
