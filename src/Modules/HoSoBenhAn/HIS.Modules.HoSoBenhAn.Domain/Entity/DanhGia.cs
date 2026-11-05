using HIS.SharedKernel.Domain;

namespace HIS.Modules.HoSoBenhAn.Domain;

/// <summary>ba_danh_gia (Rating): mỗi phiếu một lần, không sửa, không xóa (BR-20).</summary>
public sealed class DanhGia : Entity<long>, IXoaMem, ICoNgayTao
{
    public string? NhanXet { get; set; }
    public byte SoDiem { get; set; }
    public long BenhNhanId { get; set; }
    public long BacSiId { get; set; }
    public long PhieuDatKhamId { get; set; }
    public bool DaXoa { get; set; }
    public DateTime NgayTao { get; set; }
}
