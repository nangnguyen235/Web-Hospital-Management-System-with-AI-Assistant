using HIS.SharedKernel.Domain;

namespace HIS.Modules.DanhMuc.Domain;

/// <summary>dm_dich_vu (MedicalService): giá khám nằm ở dịch vụ, không ở bác sĩ.</summary>
public sealed class DichVu : Entity<int>, IXoaMem, ICoNgayTao, ICoNgayCapNhat
{
    public string TenDichVu { get; set; } = "";
    public string? MoTa { get; set; }
    public int KhoaId { get; set; }
    public LoaiDichVu LoaiDichVu { get; set; }
    public decimal Gia { get; set; } = 150_000m;
    public bool DaXoa { get; set; }
    public DateTime NgayTao { get; set; }
    public DateTime NgayCapNhat { get; set; }
}
