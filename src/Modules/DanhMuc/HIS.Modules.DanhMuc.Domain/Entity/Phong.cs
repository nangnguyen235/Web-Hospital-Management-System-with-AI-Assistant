using HIS.SharedKernel.Domain;

namespace HIS.Modules.DanhMuc.Domain;

/// <summary>dm_phong (Room): phòng khám hoặc phòng xét nghiệm; bộ đếm hàng xét nghiệm trong ngày (đặt lại lười).</summary>
public sealed class Phong : Entity<int>, ICoPhienBan, IXoaMem, ICoNgayTao, ICoNgayCapNhat
{
    public string TenPhong { get; set; } = "";
    /// <summary>Vị trí.</summary>
    public string? MoTa { get; set; }
    public int KhoaId { get; set; }
    public LoaiPhong LoaiPhong { get; set; }
    /// <summary>Phòng xét nghiệm làm dịch vụ nào.</summary>
    public int? DichVuId { get; set; }
    public int SoXnDangGoi { get; set; }
    public int SoXnLonNhat { get; set; }
    public DateOnly? NgayDemXn { get; set; }
    public int PhienBan { get; set; }
    public bool DaXoa { get; set; }
    public DateTime NgayTao { get; set; }
    public DateTime NgayCapNhat { get; set; }
}
