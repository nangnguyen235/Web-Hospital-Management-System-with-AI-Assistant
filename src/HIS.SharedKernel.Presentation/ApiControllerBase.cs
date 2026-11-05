using HIS.SharedKernel.Results;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace HIS.SharedKernel.Presentation;

/// <summary>Controller gốc: đổi <see cref="Result"/> thành HTTP 2xx hoặc ProblemDetails (title = mã lỗi, detail = mô tả).</summary>
[ApiController]
public abstract class ApiControllerBase : ControllerBase
{
    protected IActionResult FromResult(Result result) =>
        result.IsSuccess ? NoContent() : FromError(result.Error!);

    protected IActionResult FromResult<T>(Result<T> result) =>
        result.IsSuccess ? Ok(result.Value) : FromError(result.Error!);

    protected IActionResult FromError(Error error) => Problem(
        title: error.Ma,
        detail: error.MoTa,
        statusCode: error.Loai switch
        {
            LoaiLoi.KhongTimThay => StatusCodes.Status404NotFound,
            LoaiLoi.XungDot => StatusCodes.Status409Conflict,
            LoaiLoi.KhongCoQuyen => StatusCodes.Status403Forbidden,
            _ => StatusCodes.Status400BadRequest,
        });
}
