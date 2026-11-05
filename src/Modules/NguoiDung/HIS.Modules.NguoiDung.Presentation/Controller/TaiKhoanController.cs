using HIS.Modules.NguoiDung.Application;
using HIS.SharedKernel.Presentation;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace HIS.Modules.NguoiDung.Presentation;

[Route("api/nguoi-dung")]
public sealed class TaiKhoanController(TaiKhoanService service) : ApiControllerBase
{
    /// <summary>Đăng nhập bằng email/SĐT + mật khẩu, nhận JWT 60 phút (FR-BN-02, FR-BS-01, FR-DD-01).</summary>
    [HttpPost("dang-nhap")]
    [AllowAnonymous]
    [ProducesResponseType<DangNhapResponse>(StatusCodes.Status200OK)]
    public async Task<IActionResult> DangNhap(DangNhapRequest request, CancellationToken ct) =>
        FromResult(await service.DangNhapAsync(request, ct));

    /// <summary>Thông tin tài khoản đang đăng nhập.</summary>
    [HttpGet("toi")]
    [Authorize]
    [ProducesResponseType<ThongTinTaiKhoanDto>(StatusCodes.Status200OK)]
    public async Task<IActionResult> Toi(CancellationToken ct) =>
        FromResult(await service.LayThongTinToiAsync(ct));

    /// <summary>Đổi ảnh đại diện (≤ 2 MB, jpg/png/webp) — tải lên Cloudflare R2, trả Presigned URL 15 phút.</summary>
    [HttpPut("toi/anh-dai-dien")]
    [Authorize]
    [RequestSizeLimit(3 * 1024 * 1024)]
    [ProducesResponseType<string>(StatusCodes.Status200OK)]
    public async Task<IActionResult> AnhDaiDien(IFormFile file, CancellationToken ct)
    {
        await using var stream = file.OpenReadStream();
        return FromResult(await service.CapNhatAnhDaiDienAsync(stream, file.ContentType, file.Length, ct));
    }
}
