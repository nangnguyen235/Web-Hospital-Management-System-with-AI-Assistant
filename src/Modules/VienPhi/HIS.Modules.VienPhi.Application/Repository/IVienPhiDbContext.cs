using HIS.Modules.VienPhi.Domain;
using Microsoft.EntityFrameworkCore;

namespace HIS.Modules.VienPhi.Application;

/// <summary>Application dùng thẳng DbSet (không repository — modular-monolith.md mục 4).</summary>
public interface IVienPhiDbContext
{
    DbSet<GiaoDich> GiaoDich { get; }
    Task<int> SaveChangesAsync(CancellationToken cancellationToken = default);
}
