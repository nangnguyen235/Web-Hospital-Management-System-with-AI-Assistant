using HIS.SharedKernel.Domain;

namespace HIS.Modules.NguoiDung.Domain;

/// <summary>tk_ho_so_benh_nhan (PatientProfile): hồ sơ khai trong tài khoản (≤ 10); TaiKhoanId NULL = hồ sơ tạo tại quầy.</summary>
public sealed class HoSoBenhNhan : Entity<long>, IXoaMem, ICoNgayTao, ICoNgayCapNhat
{
    public long? TaiKhoanId { get; set; }
    public long? BenhNhanId { get; set; }
    public string HoTen { get; set; } = "";
    public string? SoDienThoai { get; set; }
    public string? Email { get; set; }
    public GioiTinh GioiTinh { get; set; }
    public string? SoCccd { get; set; }
    public string? DiaChi { get; set; }
    public DateOnly NgaySinh { get; set; }
    /// <summary>RL → dm_dan_toc (module DanhMuc).</summary>
    public int? DanTocId { get; set; }
    public string? Otp { get; set; }
    public int SoLanNhapOtp { get; set; }
    public DateTime? OtpTaoLuc { get; set; }
    public DateTime? OtpHetHan { get; set; }
    public bool DaXoa { get; set; }
    public DateTime NgayTao { get; set; }
    public DateTime NgayCapNhat { get; set; }
}
