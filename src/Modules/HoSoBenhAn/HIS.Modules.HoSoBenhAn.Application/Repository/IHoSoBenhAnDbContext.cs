using HIS.Modules.HoSoBenhAn.Domain;
using Microsoft.EntityFrameworkCore;

namespace HIS.Modules.HoSoBenhAn.Application;

/// <summary>Application dùng thẳng DbSet (không repository — modular-monolith.md mục 4).</summary>
public interface IHoSoBenhAnDbContext
{
    DbSet<BenhAn> BenhAn { get; }
    DbSet<XetNghiem> XetNghiem { get; }
    DbSet<DonThuoc> DonThuoc { get; }
    DbSet<ChiTietDonThuoc> ChiTietDonThuoc { get; }
    DbSet<DanhGia> DanhGia { get; }
    DbSet<NhacThuoc> NhacThuoc { get; }
    DbSet<ChiTietNhacThuoc> ChiTietNhacThuoc { get; }
    Task<int> SaveChangesAsync(CancellationToken cancellationToken = default);
}
