using HIS.SharedKernel.Domain;

namespace HIS.Modules.LichKham.Domain;

/// <summary>lk_lich_kham_bac_si (DoctorSchedule): trưởng khoa tạo; điều dưỡng "Gọi tiếp" tăng so_dang_goi.</summary>
public sealed class LichKhamBacSi : Entity<long>, ICoPhienBan, IXoaMem, ICoNgayTao, ICoNgayCapNhat
{
    /// <summary>RL → tk_nhan_vien.</summary>
    public long BacSiId { get; set; }
    /// <summary>RL → dm_phong (PHONG_KHAM).</summary>
    public int PhongId { get; set; }
    /// <summary>RL → dm_dich_vu (KHAM_BENH).</summary>
    public int DichVuId { get; set; }
    public int SoLuot { get; set; }
    public int SoDangGoi { get; set; }
    public byte ThuTrongTuan { get; set; }
    public DateOnly Ngay { get; set; }
    public Buoi Buoi { get; set; }
    public int PhienBan { get; set; }
    public List<KhungGio> KhungGio { get; set; } = [];
    public bool DaXoa { get; set; }
    public DateTime NgayTao { get; set; }
    public DateTime NgayCapNhat { get; set; }
}
