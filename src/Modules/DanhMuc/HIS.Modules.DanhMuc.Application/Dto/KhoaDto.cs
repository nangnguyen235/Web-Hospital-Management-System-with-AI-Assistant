using System.ComponentModel.DataAnnotations;

namespace HIS.Modules.DanhMuc.Application;

public sealed record KhoaDto(int Id, string TenKhoa, string? MoTa, string? TuKhoaTrieuChung, bool DaXoa);

public sealed record LuuKhoaRequest
{
    [Required(ErrorMessage = "Tên khoa là bắt buộc."), MaxLength(100)]
    public string TenKhoa { get; init; } = "";

    [MaxLength(2000)]
    public string? MoTa { get; init; }

    [MaxLength(2000)]
    public string? TuKhoaTrieuChung { get; init; }
}
