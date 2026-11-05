using HIS.SharedKernel.Domain;

namespace HIS.Modules.VienPhi.Domain;

/// <summary>vp_giao_dich (Transaction): id dùng làm vnp_TxnRef; chỉ rời DANG_XU_LY một lần (BR-10).</summary>
public sealed class GiaoDich : Entity<long>, IXoaMem, ICoNgayTao, ICoNgayCapNhat
{
    public decimal SoTien { get; set; }
    /// <summary>Phí cổng: 15.000 đặt khám, 1.500 xét nghiệm.</summary>
    public decimal Phi { get; set; }
    public TrangThaiGiaoDich TrangThai { get; set; } = TrangThaiGiaoDich.DANG_XU_LY;
    public string? MaNganHang { get; set; }
    public string PhuongThuc { get; set; } = "VNPAY";
    /// <summary>Origin của frontend để chuyển về trang kết quả.</summary>
    public string? Nguon { get; set; }
    public LoaiThanhToan LoaiThanhToan { get; set; }
    /// <summary>RL → tk_tai_khoan; NULL khi khách quầy chưa có tài khoản.</summary>
    public long? TaiKhoanId { get; set; }
    public bool DaXoa { get; set; }
    public DateTime NgayTao { get; set; }
    public DateTime NgayCapNhat { get; set; }
}
