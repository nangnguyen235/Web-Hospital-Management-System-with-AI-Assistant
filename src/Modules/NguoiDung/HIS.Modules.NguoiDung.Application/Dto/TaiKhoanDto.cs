using System.ComponentModel.DataAnnotations;
using HIS.SharedKernel.Auth;

namespace HIS.Modules.NguoiDung.Application;

public sealed record DangNhapRequest
{
    /// <summary>Email hoặc số điện thoại.</summary>
    [Required, MaxLength(150)] public string TenDangNhap { get; init; } = "";
    [Required, MaxLength(100)] public string MatKhau { get; init; } = "";
}

public sealed record DangNhapResponse(string AccessToken, DateTimeOffset HetHan, VaiTro VaiTro, string HoTen);

public sealed record ThongTinTaiKhoanDto(
    long Id, string HoTen, string? Email, string SoDienThoai, VaiTro VaiTro,
    long? NhanVienId, int? KhoaId, bool LaTruongKhoa, string? AnhDaiDienUrl);
