using HIS.SharedKernel.Domain;

namespace HIS.Modules.DanhMuc.Domain;

/// <summary>dm_thuoc (Medicine): chỉ kê, không bán — không trừ số lượng khi kê (BR-18).</summary>
public sealed class Thuoc : Entity<int>, IXoaMem, ICoNgayTao, ICoNgayCapNhat
{
    public string TenThuoc { get; set; } = "";
    public decimal Gia { get; set; }
    public int SoLuong { get; set; }
    public DonViThuoc DonVi { get; set; }
    public bool DaXoa { get; set; }
    public DateTime NgayTao { get; set; }
    public DateTime NgayCapNhat { get; set; }
}
