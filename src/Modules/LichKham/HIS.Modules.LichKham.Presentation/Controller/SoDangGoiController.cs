using HIS.Modules.LichKham.Application;
using HIS.SharedKernel.Presentation;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace HIS.Modules.LichKham.Presentation;

[Route("api/lich-kham")]
public sealed class SoDangGoiController(SoDangGoiService service) : ApiControllerBase
{
    /// <summary>Màn hình số đang gọi công khai: chỉ số + phòng + khoa, không họ tên (BR-15, NFR-03).</summary>
    [HttpGet("{lichKhamId:long}/so-dang-goi")]
    [AllowAnonymous]
    [ProducesResponseType<SoDangGoiDto>(StatusCodes.Status200OK)]
    public async Task<IActionResult> SoDangGoi(long lichKhamId, CancellationToken ct) =>
        FromResult(await service.LayAsync(lichKhamId, ct));
}
