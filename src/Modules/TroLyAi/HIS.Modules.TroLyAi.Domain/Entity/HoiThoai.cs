using HIS.SharedKernel.Domain;

namespace HIS.Modules.TroLyAi.Domain;

/// <summary>tl_hoi_thoai (Conversation của TheDuck — AI 1 thay tư vấn viên).</summary>
public sealed class HoiThoai : Entity<long>, ICoNgayTao, ICoNgayCapNhat
{
    public long TaiKhoanId { get; set; }
    public TrangThaiHoiThoai TrangThai { get; set; } = TrangThaiHoiThoai.DANG_MO;
    public int SoThuTuLonNhat { get; set; }
    public List<TinNhan> TinNhan { get; set; } = [];
    public DateTime NgayTao { get; set; }
    public DateTime NgayCapNhat { get; set; }
}

/// <summary>tl_tin_nhan (Message).</summary>
public sealed class TinNhan : Entity<long>, ICoNgayTao, ICoNgayCapNhat
{
    public long HoiThoaiId { get; set; }
    public string NoiDung { get; set; } = "";
    public int SoThuTu { get; set; }
    /// <summary>TheDuck isSupportAgent: tin của trợ lý AI.</summary>
    public bool LaTroLy { get; set; }
    public DateTime NgayTao { get; set; }
    public DateTime NgayCapNhat { get; set; }
}
