using HIS.Modules.DanhMuc.Application;
using HIS.SharedKernel.Auth;
using HIS.SharedKernel.Presentation;
using HIS.SharedKernel.Results;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace HIS.Modules.DanhMuc.Presentation;

/// <summary>LÁT MẪU: quản lý khoa (FR-AD-04) + danh sách công khai (FR-BN-07).</summary>
[Route("api/danh-muc/khoa")]
public sealed class KhoaController(KhoaService service) : ApiControllerBase
{
    /// <summary>Danh sách khoa đang hoạt động (công khai).</summary>
    [HttpGet]
    [AllowAnonymous]
    public async Task<IReadOnlyList<KhoaDto>> DanhSach(CancellationToken ct) =>
        await service.DanhSachCongKhaiAsync(ct);

    /// <summary>Admin: tìm khoa (gồm khoa đã xóa), phân trang.</summary>
    [HttpGet("quan-ly")]
    [Authorize(Roles = Quyen.QuanTri)]
    public async Task<PagedResult<KhoaDto>> TimKiem(
        [FromQuery] string? tuKhoa, [FromQuery] int page = 1, [FromQuery] int pageSize = 20, CancellationToken ct = default) =>
        await service.TimKiemAsync(tuKhoa, page, pageSize, ct);

    [HttpPost]
    [Authorize(Roles = Quyen.QuanTri)]
    [ProducesResponseType<KhoaDto>(StatusCodes.Status200OK)]
    public async Task<IActionResult> Tao(LuuKhoaRequest request, CancellationToken ct) =>
        FromResult(await service.TaoAsync(request, ct));

    [HttpPut("{id:int}")]
    [Authorize(Roles = Quyen.QuanTri)]
    [ProducesResponseType<KhoaDto>(StatusCodes.Status200OK)]
    public async Task<IActionResult> CapNhat(int id, LuuKhoaRequest request, CancellationToken ct) =>
        FromResult(await service.CapNhatAsync(id, request, ct));

    /// <summary>Xóa mềm.</summary>
    [HttpDelete("{id:int}")]
    [Authorize(Roles = Quyen.QuanTri)]
    public async Task<IActionResult> Xoa(int id, CancellationToken ct) =>
        FromResult(await service.XoaAsync(id, ct));

    [HttpPost("{id:int}/khoi-phuc")]
    [Authorize(Roles = Quyen.QuanTri)]
    public async Task<IActionResult> KhoiPhuc(int id, CancellationToken ct) =>
        FromResult(await service.KhoiPhucAsync(id, ct));
}
