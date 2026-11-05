using HIS.Modules.DanhMuc.Application;
using HIS.SharedKernel.Presentation;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace HIS.Modules.DanhMuc.Presentation;

[Route("api/danh-muc/dan-toc")]
public sealed class DanTocController(DanTocService service) : ApiControllerBase
{
    [HttpGet]
    [AllowAnonymous]
    public async Task<IReadOnlyList<DanTocDto>> DanhSach(CancellationToken ct) => await service.DanhSachAsync(ct);
}
