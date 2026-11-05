using HIS.Modules.DanhMuc.Contracts;
using Microsoft.EntityFrameworkCore;

namespace HIS.Modules.DanhMuc.Application;

/// <summary>Cài đặt interface public của DanhMuc cho module khác (JOIN trong cùng module được phép).</summary>
internal sealed class DanhMucTraCuu(IDanhMucDbContext db) : IDanhMucTraCuu
{
    public Task<PhongDto?> LayPhongAsync(int phongId, CancellationToken cancellationToken = default) =>
        (from p in db.Phong.AsNoTracking()
         join k in db.Khoa.AsNoTracking() on p.KhoaId equals k.Id
         where p.Id == phongId
         select new PhongDto(p.Id, p.TenPhong, k.Id, k.TenKhoa, p.LoaiPhong.ToString()))
        .FirstOrDefaultAsync(cancellationToken);

    public Task<int> DemKhoaAsync(CancellationToken cancellationToken = default) =>
        db.Khoa.CountAsync(x => !x.DaXoa, cancellationToken);
}
