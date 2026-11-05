using HIS.SharedKernel.Domain;

namespace HIS.Modules.NguoiDung.Domain;

/// <summary>tk_benh_nhan (Patient): người bệnh thật, định danh bằng CCCD; chỉ điều dưỡng tạo khi tiếp nhận.</summary>
public sealed class BenhNhan : Entity<long>, IXoaMem, ICoNgayTao, ICoNgayCapNhat
{
    public string MaBenhNhan { get; set; } = "";
    public string HoTen { get; set; } = "";
    public string? SoDienThoai { get; set; }
    public string? SoCccd { get; set; }
    public DateOnly NgaySinh { get; set; }
    public GioiTinh GioiTinh { get; set; }
    public bool DaXoa { get; set; }
    public DateTime NgayTao { get; set; }
    public DateTime NgayCapNhat { get; set; }
}
