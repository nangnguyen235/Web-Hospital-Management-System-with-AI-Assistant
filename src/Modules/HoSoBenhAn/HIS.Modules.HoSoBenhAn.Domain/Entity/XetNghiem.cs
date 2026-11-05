using HIS.SharedKernel.Domain;

namespace HIS.Modules.HoSoBenhAn.Domain;

/// <summary>ba_xet_nghiem (MedicalTest): chỉ làm khi đã thanh toán (BR-17).</summary>
public sealed class XetNghiem : Entity<long>, IXoaMem, ICoNgayTao
{
    public string MaXetNghiem { get; set; } = "";
    public int SoThuTu { get; set; }
    public string? GhiChu { get; set; }
    public string? KetQua { get; set; }
    public decimal Gia { get; set; }
    public string? FileKetQuaKey { get; set; }
    public int DichVuId { get; set; }
    public long BenhAnId { get; set; }
    public int PhongId { get; set; }
    public long? DieuDuongId { get; set; }
    public TrangThaiXetNghiem TrangThai { get; set; } = TrangThaiXetNghiem.CHO_THUC_HIEN;
    public DateOnly Ngay { get; set; }
    public long? GiaoDichId { get; set; }
    /// <summary>Bản sao trạng thái giao dịch (không JOIN sang VienPhi).</summary>
    public bool DaThanhToan { get; set; }
    public bool DaXoa { get; set; }
    public DateTime NgayTao { get; set; }
}
