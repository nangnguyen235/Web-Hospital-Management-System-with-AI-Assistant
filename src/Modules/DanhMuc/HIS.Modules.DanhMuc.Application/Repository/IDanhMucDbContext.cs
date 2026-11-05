using HIS.Modules.DanhMuc.Domain;
using Microsoft.EntityFrameworkCore;

namespace HIS.Modules.DanhMuc.Application;

/// <summary>Application dùng thẳng DbSet (không repository — modular-monolith.md mục 4).</summary>
public interface IDanhMucDbContext
{
    DbSet<Khoa> Khoa { get; }
    DbSet<Phong> Phong { get; }
    DbSet<DichVu> DichVu { get; }
    DbSet<Thuoc> Thuoc { get; }
    DbSet<DanToc> DanToc { get; }
    Task<int> SaveChangesAsync(CancellationToken cancellationToken = default);
}
