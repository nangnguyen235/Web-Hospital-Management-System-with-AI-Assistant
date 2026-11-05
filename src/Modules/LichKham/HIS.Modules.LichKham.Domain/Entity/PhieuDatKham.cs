using HIS.SharedKernel.Domain;

namespace HIS.Modules.LichKham.Domain;

/// <summary>
/// lk_phieu_dat_kham (Booking) — TRỤC của hệ thống. Như TheDuck: da_xoa = true và so_thu_tu = −1 khi chưa thanh toán;
/// da_huy = giữ chỗ hết hạn, thanh toán thất bại hoặc vắng mặt.
/// </summary>
public sealed class PhieuDatKham : Entity<long>, IXoaMem, ICoNgayTao, ICoNgayCapNhat
{
    public string MaPhieu { get; set; } = "";
    public int SoThuTu { get; set; } = -1;
    public decimal GiaDichVu { get; set; }
    public bool DaDanhGia { get; set; }
    /// <summary>RL → tk_ho_so_benh_nhan.</summary>
    public long HoSoId { get; set; }
    public long KhungGioId { get; set; }
    public KhungGio? KhungGio { get; set; }
    /// <summary>RL → ba_benh_an, gán khi tiếp nhận.</summary>
    public long? BenhAnId { get; set; }
    /// <summary>RL → vp_giao_dich.</summary>
    public long GiaoDichId { get; set; }
    public KenhDat KenhDat { get; set; }
    public bool DaHuy { get; set; }
    public bool DaXoa { get; set; }
    public DateTime NgayTao { get; set; }
    public DateTime NgayCapNhat { get; set; }
}
