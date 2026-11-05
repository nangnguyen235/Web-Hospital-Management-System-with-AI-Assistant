using HIS.Modules.DanhMuc.Contracts;
using HIS.Modules.LichKham.Domain;
using HIS.SharedKernel.Results;
using Microsoft.EntityFrameworkCore;

namespace HIS.Modules.LichKham.Application;

/// <summary>Màn hình số đang gọi công khai (FR-BN-10, BR-15). Hỏi DanhMuc tên phòng/khoa qua Contracts.</summary>
public sealed class SoDangGoiService(ILichKhamDbContext db, IDanhMucTraCuu danhMuc)
{
    private const int SoMoiLanGoi = 5;

    public async Task<Result<SoDangGoiDto>> LayAsync(long lichKhamId, CancellationToken ct)
    {
        var lich = await db.LichKhamBacSi.AsNoTracking().FirstOrDefaultAsync(x => x.Id == lichKhamId && !x.DaXoa, ct);
        if (lich is null) return Error.KhongTimThay("lịch khám");

        var phong = await danhMuc.LayPhongAsync(lich.PhongId, ct);
        var tu = Math.Max(1, lich.SoDangGoi - SoMoiLanGoi + 1);
        var cacSo = lich.SoDangGoi <= 0 ? [] : Enumerable.Range(tu, lich.SoDangGoi - tu + 1).ToList();
        return new SoDangGoiDto(
            lich.Id, lich.SoDangGoi, cacSo, phong?.TenPhong ?? "", phong?.TenKhoa ?? "", lich.Ngay, lich.Buoi);
    }
}
