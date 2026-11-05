using HIS.SharedKernel.Domain;

namespace HIS.Modules.LichKham.Domain;

/// <summary>lk_khung_gio (TimeSlot): 4 khung 1 giờ mỗi buổi; so_bat_dau = (chi_so_khung % 4) × so_luong_toi_da + 1 (BR-06).</summary>
public sealed class KhungGio : Entity<long>, ICoPhienBan, IXoaMem, ICoNgayTao, ICoNgayCapNhat
{
    public long LichKhamId { get; set; }
    public int SoLuongToiDa { get; set; }
    /// <summary>Số phiếu đã thanh toán; chỉ tăng.</summary>
    public int SoLuongDaDat { get; set; }
    public int SoBatDau { get; set; }
    /// <summary>0–3 sáng 07:00–11:00, 4–7 chiều 13:00–17:00.</summary>
    public byte ChiSoKhung { get; set; }
    public DateOnly Ngay { get; set; }
    public int PhienBan { get; set; }
    public bool DaXoa { get; set; }
    public DateTime NgayTao { get; set; }
    public DateTime NgayCapNhat { get; set; }
}
