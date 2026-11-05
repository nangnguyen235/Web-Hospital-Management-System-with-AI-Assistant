using HIS.SharedKernel.Domain;

namespace HIS.Modules.HoSoBenhAn.Domain;

/// <summary>ba_benh_an (MedicalExaminationRecord).</summary>
public sealed class BenhAn : Entity<long>, IXoaMem, ICoNgayTao, ICoNgayCapNhat
{
    /// <summary>RL → lk_phieu_dat_kham; UNIQUE để chống tạo trùng.</summary>
    public long PhieuDatKhamId { get; set; }
    public long LichKhamId { get; set; }
    public long BenhNhanId { get; set; }
    public long HoSoId { get; set; }
    public string? TrieuChung { get; set; }
    public string? ChanDoan { get; set; }
    public DateOnly? NgayTaiKham { get; set; }
    /// <summary>Lịch cũ khi bác sĩ khác cùng khoa nhận khám thay.</summary>
    public long? LichKhamTruocId { get; set; }
    public TrangThaiBenhAn TrangThai { get; set; } = TrangThaiBenhAn.CHO_KHAM;
    /// <summary>Thêm cho AI 2: ghi âm trên R2.</summary>
    public string? FileGhiAmKey { get; set; }
    public List<XetNghiem> XetNghiem { get; set; } = [];
    public DonThuoc? DonThuoc { get; set; }
    public bool DaXoa { get; set; }
    public DateTime NgayTao { get; set; }
    public DateTime NgayCapNhat { get; set; }
}
