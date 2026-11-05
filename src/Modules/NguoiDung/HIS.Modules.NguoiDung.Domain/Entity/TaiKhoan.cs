using HIS.SharedKernel.Auth;
using HIS.SharedKernel.Domain;

namespace HIS.Modules.NguoiDung.Domain;

using HIS.SharedKernel.Auth;

/// <summary>tk_tai_khoan (Account). Vai trò suy ra như TheDuck: không gắn nhân viên ⇒ bệnh nhân.</summary>
public sealed class TaiKhoan : Entity<long>, IXoaMem, ICoNgayTao, ICoNgayCapNhat
{
    public string HoTen { get; set; } = "";
    public string? Email { get; set; }
    public string MatKhauHash { get; set; } = "";
    public string SoDienThoai { get; set; } = "";
    public string? AnhDaiDienKey { get; set; }
    public int SoLuongHoSo { get; set; }
    public string? Otp { get; set; }
    public int SoLanNhapOtp { get; set; }
    public DateTime? OtpTaoLuc { get; set; }
    public DateTime? OtpHetHan { get; set; }
    public long? NhanVienId { get; set; }
    public NhanVien? NhanVien { get; set; }
    public bool DaXoa { get; set; }
    public DateTime NgayTao { get; set; }
    public DateTime NgayCapNhat { get; set; }

    /// <summary>Cần nạp <see cref="NhanVien"/> trước khi gọi với tài khoản nhân viên.</summary>
    public VaiTro LayVaiTro()
    {
        if (NhanVienId is null) return VaiTro.BENH_NHAN;
        var nhanVien = NhanVien ?? throw new InvalidOperationException("Chưa nạp NhanVien của tài khoản.");
        return nhanVien.LoaiNhanVien switch
        {
            LoaiNhanVien.BAC_SI => VaiTro.BAC_SI,
            LoaiNhanVien.DIEU_DUONG => VaiTro.DIEU_DUONG,
            _ => VaiTro.QUAN_TRI,
        };
    }
}
