using HIS.Modules.NguoiDung.Contracts;
using HIS.Modules.NguoiDung.Domain;
using Microsoft.EntityFrameworkCore;

namespace HIS.Modules.NguoiDung.Application;

internal sealed class NguoiDungThongKe(INguoiDungDbContext db) : INguoiDungThongKe
{
    public Task<int> DemBacSiAsync(CancellationToken cancellationToken = default) =>
        db.NhanVien.OfType<BacSi>().CountAsync(x => !x.DaXoa, cancellationToken);

    public Task<int> DemBenhNhanAsync(CancellationToken cancellationToken = default) =>
        db.BenhNhan.CountAsync(x => !x.DaXoa, cancellationToken);
}
