namespace HIS.Modules.DanhMuc.Contracts;

public sealed record PhongDto(int Id, string TenPhong, int KhoaId, string TenKhoa, string LoaiPhong);

/// <summary>Module khác hỏi DanhMuc qua interface này (modular-monolith.md mục 3.2).</summary>
public interface IDanhMucTraCuu
{
    Task<PhongDto?> LayPhongAsync(int phongId, CancellationToken cancellationToken = default);
    Task<int> DemKhoaAsync(CancellationToken cancellationToken = default);
}
