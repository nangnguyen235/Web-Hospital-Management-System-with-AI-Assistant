using HIS.SharedKernel.Domain;

namespace HIS.Modules.NguoiDung.Domain;

/// <summary>tk_nguoi_dung_tam (TemporaryUser): giữ OTP đăng ký tới khi xác thực xong.</summary>
public sealed class NguoiDungTam : Entity<long>, IXoaMem, ICoNgayTao, ICoNgayCapNhat
{
    public string Email { get; set; } = "";
    public string Otp { get; set; } = "";
    public int SoLanNhapOtp { get; set; }
    public DateTime? OtpTaoLuc { get; set; }
    public DateTime? OtpHetHan { get; set; }
    public bool DaXoa { get; set; }
    public DateTime NgayTao { get; set; }
    public DateTime NgayCapNhat { get; set; }
}
