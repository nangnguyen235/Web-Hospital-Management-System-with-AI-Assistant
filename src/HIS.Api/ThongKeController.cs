using HIS.Modules.DanhMuc.Contracts;
using HIS.Modules.LichKham.Contracts;
using HIS.Modules.NguoiDung.Contracts;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace HIS.Api;

public sealed record ThongKeTrangChuDto(int SoBacSi, int SoKhoa, int SoLuotKham);

/// <summary>Thống kê ghép số liệu nhiều module qua *.Contracts (BR-25) — nằm ở HIS.Api, không thêm module.</summary>
[ApiController]
[Route("api/thong-ke")]
public sealed class ThongKeController(
    INguoiDungThongKe nguoiDung, IDanhMucTraCuu danhMuc, ILichKhamThongKe lichKham) : ControllerBase
{
    /// <summary>Số liệu công khai cho trang chủ.</summary>
    [HttpGet("trang-chu")]
    [AllowAnonymous]
    public async Task<ThongKeTrangChuDto> TrangChu(CancellationToken ct) => new(
        await nguoiDung.DemBacSiAsync(ct),
        await danhMuc.DemKhoaAsync(ct),
        await lichKham.DemLuotKhamAsync(ct));
}
