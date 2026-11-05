namespace HIS.SharedKernel.Auth;

/// <summary>4 tác nhân (BR-01). Bệnh nhân = tài khoản không gắn nhân viên.</summary>
public enum VaiTro
{
    BENH_NHAN,
    BAC_SI,
    DIEU_DUONG,
    QUAN_TRI,
}

/// <summary>Tên claim trong JWT (modular-monolith.md mục 3).</summary>
public static class ClaimTen
{
    public const string TaiKhoanId = "tai_khoan_id";
    public const string VaiTro = "vai_tro";
    public const string NhanVienId = "nhan_vien_id";
    public const string KhoaId = "khoa_id";
    public const string LaTruongKhoa = "la_truong_khoa";
}

/// <summary>Tên vai trò/chính sách dùng trong [Authorize].</summary>
public static class Quyen
{
    public const string BenhNhan = nameof(VaiTro.BENH_NHAN);
    public const string BacSi = nameof(VaiTro.BAC_SI);
    public const string DieuDuong = nameof(VaiTro.DIEU_DUONG);
    public const string QuanTri = nameof(VaiTro.QUAN_TRI);

    /// <summary>Bác sĩ có cờ la_truong_khoa.</summary>
    public const string TruongKhoa = "TruongKhoa";

    /// <summary>Điều dưỡng có cờ la_truong_khoa.</summary>
    public const string DieuDuongTruong = "DieuDuongTruong";
}

public sealed record ThongTinToken(long TaiKhoanId, VaiTro VaiTro, long? NhanVienId, int? KhoaId, bool LaTruongKhoa);

public sealed record TokenDaTao(string AccessToken, DateTimeOffset HetHan);

/// <summary>Tạo JWT access token 60 phút (BR-02).</summary>
public interface IJwtTokenService
{
    TokenDaTao TaoToken(ThongTinToken thongTin);
}
