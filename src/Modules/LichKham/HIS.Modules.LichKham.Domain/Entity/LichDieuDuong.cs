using HIS.SharedKernel.Domain;

namespace HIS.Modules.LichKham.Domain;

/// <summary>lk_lich_dieu_duong (NurseSchedule): phân công theo thứ + buổi; chỉ để hiện danh sách phòng, không chặn thao tác.</summary>
public sealed class LichDieuDuong : Entity<long>, IXoaMem, ICoNgayTao, ICoNgayCapNhat
{
    public long DieuDuongId { get; set; }
    public string TenDieuDuong { get; set; } = "";
    public int PhongId { get; set; }
    public string TenPhong { get; set; } = "";
    public byte ThuTrongTuan { get; set; }
    public Buoi Buoi { get; set; }
    public bool DaXoa { get; set; }
    public DateTime NgayTao { get; set; }
    public DateTime NgayCapNhat { get; set; }
}
