using HIS.SharedKernel.Domain;

namespace HIS.Modules.HoSoBenhAn.Domain;

/// <summary>ba_don_thuoc (Prescription): một đơn mỗi bệnh án, tạo khi kê thuốc đầu tiên.</summary>
public sealed class DonThuoc : Entity<long>, IXoaMem, ICoNgayTao, ICoNgayCapNhat
{
    public string MaDonThuoc { get; set; } = "";
    public decimal TongTien { get; set; }
    public long BenhAnId { get; set; }
    public List<ChiTietDonThuoc> ChiTiet { get; set; } = [];
    public bool DaXoa { get; set; }
    public DateTime NgayTao { get; set; }
    public DateTime NgayCapNhat { get; set; }
}

/// <summary>ba_chi_tiet_don_thuoc (PrescriptionItem).</summary>
public sealed class ChiTietDonThuoc : Entity<long>, IXoaMem, ICoNgayTao, ICoNgayCapNhat
{
    public long DonThuocId { get; set; }
    public string MaDonThuoc { get; set; } = "";
    /// <summary>RL → dm_thuoc.</summary>
    public int ThuocId { get; set; }
    public int SoLuong { get; set; }
    public decimal Gia { get; set; }
    public decimal ThanhTien { get; set; }
    public int SoLanMoiNgay { get; set; }
    public int SoNgay { get; set; }
    public decimal SoLuongMoiLan { get; set; }
    public bool Sang { get; set; }
    public bool Trua { get; set; }
    public bool Chieu { get; set; }
    public bool Toi { get; set; }
    public string? HuongDan { get; set; }
    public bool DaXoa { get; set; }
    public DateTime NgayTao { get; set; }
    public DateTime NgayCapNhat { get; set; }
}
