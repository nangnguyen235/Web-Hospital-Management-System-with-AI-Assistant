using HIS.SharedKernel.Domain;

namespace HIS.Modules.NguoiDung.Domain;

/// <summary>
/// tk_nhan_vien (Staff): một bảng cho mọi loại nhân viên, y hệt TheDuck (SINGLE_TABLE + dtype) —
/// EF Core TPH, cột phân loại là <see cref="LoaiNhanVien"/>.
/// </summary>
public abstract class NhanVien : Entity<long>, IXoaMem, ICoNgayTao, ICoNgayCapNhat
{
    public string HoTen { get; set; } = "";
    public string SoDienThoai { get; set; } = "";
    /// <summary>NULL được: Admin gốc tạo từ seed không có CCCD, ngày sinh.</summary>
    public string? SoCccd { get; set; }
    public GioiTinh GioiTinh { get; set; }
    public DateOnly? NgaySinh { get; set; }
    public string? AnhDaiDienKey { get; set; }
    public LoaiNhanVien LoaiNhanVien { get; set; }
    /// <summary>RL → dm_khoa (module DanhMuc).</summary>
    public int? KhoaId { get; set; }
    /// <summary>Bác sĩ: trưởng khoa; điều dưỡng: điều dưỡng trưởng.</summary>
    public bool LaTruongKhoa { get; set; }
    public TaiKhoan? TaiKhoan { get; set; }
    public bool DaXoa { get; set; }
    public DateTime NgayTao { get; set; }
    public DateTime NgayCapNhat { get; set; }
}

public sealed class BacSi : NhanVien
{
    public HocHam? HocHam { get; set; }
    public decimal DiemDanhGia { get; set; }
}

/// <summary>Một loại điều dưỡng: làm phòng khám, phòng xét nghiệm, quầy.</summary>
public sealed class DieuDuong : NhanVien;

public sealed class QuanTri : NhanVien;
