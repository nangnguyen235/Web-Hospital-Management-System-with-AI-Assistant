using HIS.Modules.LichKham.Domain;
using Microsoft.EntityFrameworkCore;

namespace HIS.Modules.LichKham.Application;

/// <summary>Application dùng thẳng DbSet (không repository — modular-monolith.md mục 4).</summary>
public interface ILichKhamDbContext
{
    DbSet<LichKhamBacSi> LichKhamBacSi { get; }
    DbSet<KhungGio> KhungGio { get; }
    DbSet<LichDieuDuong> LichDieuDuong { get; }
    DbSet<PhieuDatKham> PhieuDatKham { get; }
    Task<int> SaveChangesAsync(CancellationToken cancellationToken = default);
}
