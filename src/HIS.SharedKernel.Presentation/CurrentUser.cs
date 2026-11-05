using HIS.SharedKernel.Abstractions;
using HIS.SharedKernel.Auth;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.DependencyInjection;

namespace HIS.SharedKernel.Presentation;

/// <summary>Đọc người dùng hiện tại từ claim JWT, không phải hỏi CSDL mỗi request.</summary>
public sealed class CurrentUser(IHttpContextAccessor accessor) : ICurrentUser
{
    private string? Claim(string ten) => accessor.HttpContext?.User.FindFirst(ten)?.Value;

    public bool DaDangNhap => accessor.HttpContext?.User.Identity?.IsAuthenticated == true;
    public long? TaiKhoanId => long.TryParse(Claim(ClaimTen.TaiKhoanId), out var id) ? id : null;
    public VaiTro? VaiTro => Enum.TryParse<VaiTro>(Claim(ClaimTen.VaiTro), out var vt) ? vt : null;
    public long? NhanVienId => long.TryParse(Claim(ClaimTen.NhanVienId), out var id) ? id : null;
    public int? KhoaId => int.TryParse(Claim(ClaimTen.KhoaId), out var id) ? id : null;
    public bool LaTruongKhoa => Claim(ClaimTen.LaTruongKhoa) == "true";
}

public static class DependencyInjection
{
    public static IServiceCollection AddSharedKernelPresentation(this IServiceCollection services)
    {
        services.AddHttpContextAccessor();
        services.AddScoped<ICurrentUser, CurrentUser>();
        return services;
    }
}
