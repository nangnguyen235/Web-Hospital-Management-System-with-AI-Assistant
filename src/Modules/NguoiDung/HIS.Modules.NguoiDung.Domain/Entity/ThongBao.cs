using HIS.SharedKernel.Domain;

namespace HIS.Modules.NguoiDung.Domain;

/// <summary>tk_thong_bao (Notification): in-app, không push (BR-22).</summary>
public sealed class ThongBao : Entity<long>, IXoaMem, ICoNgayTao, ICoNgayCapNhat
{
    public long TaiKhoanId { get; set; }
    public string TieuDe { get; set; } = "";
    public string NoiDung { get; set; } = "";
    /// <summary>JSON: hành động + id liên quan (như TheDuck data).</summary>
    public string? DuLieu { get; set; }
    public TrangThaiThongBao TrangThai { get; set; } = TrangThaiThongBao.CHUA_NHAN;
    public bool DaXoa { get; set; }
    public DateTime NgayTao { get; set; }
    public DateTime NgayCapNhat { get; set; }
}
