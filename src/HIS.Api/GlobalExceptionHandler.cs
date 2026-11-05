using Microsoft.AspNetCore.Diagnostics;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace HIS.Api;

/// <summary>Xử lý ngoại lệ tập trung (tiêu chí Exemplary 2): log lỗi, trả ProblemDetails, không lộ chi tiết.</summary>
internal sealed class GlobalExceptionHandler(ILogger<GlobalExceptionHandler> logger, IProblemDetailsService problemDetails)
    : IExceptionHandler
{
    public async ValueTask<bool> TryHandleAsync(HttpContext httpContext, Exception exception, CancellationToken cancellationToken)
    {
        var (status, ma, moTa) = exception switch
        {
            DbUpdateConcurrencyException => (StatusCodes.Status409Conflict, "DU_LIEU_DA_THAY_DOI", "Dữ liệu vừa bị người khác thay đổi, vui lòng thử lại."),
            BadHttpRequestException => (StatusCodes.Status400BadRequest, "YEU_CAU_KHONG_HOP_LE", "Yêu cầu không hợp lệ."),
            _ => (StatusCodes.Status500InternalServerError, "LOI_HE_THONG", "Có lỗi xảy ra, vui lòng thử lại sau."),
        };

        if (status == StatusCodes.Status500InternalServerError)
        {
            logger.LogError(exception, "Lỗi chưa xử lý tại {Method} {Path}", httpContext.Request.Method, httpContext.Request.Path);
        }
        else
        {
            logger.LogWarning("{Ma} tại {Method} {Path}", ma, httpContext.Request.Method, httpContext.Request.Path);
        }

        httpContext.Response.StatusCode = status;
        return await problemDetails.TryWriteAsync(new ProblemDetailsContext
        {
            HttpContext = httpContext,
            Exception = exception,
            ProblemDetails = new ProblemDetails { Status = status, Title = ma, Detail = moTa },
        });
    }
}
