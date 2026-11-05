using HIS.Modules.LichKham.Contracts;
using Microsoft.EntityFrameworkCore;

namespace HIS.Modules.LichKham.Application;

internal sealed class LichKhamThongKe(ILichKhamDbContext db) : ILichKhamThongKe
{
    /// <summary>Phiếu đã thanh toán: da_xoa = false (cách TheDuck đánh dấu).</summary>
    public Task<int> DemLuotKhamAsync(CancellationToken cancellationToken = default) =>
        db.PhieuDatKham.CountAsync(x => !x.DaXoa, cancellationToken);
}
