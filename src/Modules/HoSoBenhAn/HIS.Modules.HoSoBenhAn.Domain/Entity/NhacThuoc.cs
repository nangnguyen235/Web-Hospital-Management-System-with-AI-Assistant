using HIS.SharedKernel.Domain;

namespace HIS.Modules.HoSoBenhAn.Domain;

/// <summary>ba_nhac_thuoc (MedicineReminder): không job nền, không push (BR-21).</summary>
public sealed class NhacThuoc : Entity<long>, IXoaMem, ICoNgayTao, ICoNgayCapNhat
{
    public long ChiTietDonThuocId { get; set; }
    public long HoSoId { get; set; }
    public DateOnly NgayBatDau { get; set; }
    public DateTime NgayKetThuc { get; set; }
    public decimal TongSoLuong { get; set; }
    public decimal SoLuongConLai { get; set; }
    public List<ChiTietNhacThuoc> ChiTiet { get; set; } = [];
    public bool DaXoa { get; set; }
    public DateTime NgayTao { get; set; }
    public DateTime NgayCapNhat { get; set; }
}

/// <summary>ba_chi_tiet_nhac_thuoc (MedicineReminderDetail): ba bản sao tên thuốc, đơn vị, họ tên như TheDuck.</summary>
public sealed class ChiTietNhacThuoc : Entity<long>, IXoaMem, ICoNgayTao, ICoNgayCapNhat
{
    public string MaXacNhan { get; set; } = "";
    public long NhacThuocId { get; set; }
    public DateTime ThoiDiemNhac { get; set; }
    public int ThuTuNhac { get; set; }
    public string TenThuoc { get; set; } = "";
    public string DonVi { get; set; } = "";
    public string HoTen { get; set; } = "";
    public decimal SoLuong { get; set; }
    public bool DaUong { get; set; }
    public bool BoQua { get; set; }
    public bool DaNhan { get; set; }
    public long TaiKhoanId { get; set; }
    public bool DaXoa { get; set; }
    public DateTime NgayTao { get; set; }
    public DateTime NgayCapNhat { get; set; }
}
