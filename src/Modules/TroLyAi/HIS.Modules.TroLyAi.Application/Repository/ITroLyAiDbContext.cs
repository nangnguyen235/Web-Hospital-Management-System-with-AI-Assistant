using HIS.Modules.TroLyAi.Domain;
using Microsoft.EntityFrameworkCore;

namespace HIS.Modules.TroLyAi.Application;

/// <summary>Application dùng thẳng DbSet (không repository — modular-monolith.md mục 4).</summary>
public interface ITroLyAiDbContext
{
    DbSet<HoiThoai> HoiThoai { get; }
    DbSet<TinNhan> TinNhan { get; }
    Task<int> SaveChangesAsync(CancellationToken cancellationToken = default);
}
