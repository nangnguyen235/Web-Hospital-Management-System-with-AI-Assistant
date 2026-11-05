using HIS.SharedKernel.Domain;

namespace HIS.Modules.DanhMuc.Domain;

/// <summary>dm_khoa (Department). Lát mẫu: thực thể có phương thức nghiệp vụ, Admin thêm/sửa/xóa mềm/khôi phục (FR-AD-04).</summary>
public sealed class Khoa : Entity<int>, IXoaMem, ICoNgayTao, ICoNgayCapNhat
{
    private Khoa() { }

    public string TenKhoa { get; private set; } = "";
    public string? MoTa { get; private set; }
    /// <summary>Thêm cho AI 1: từ khóa triệu chứng để gợi ý khoa.</summary>
    public string? TuKhoaTrieuChung { get; private set; }
    public bool DaXoa { get; set; }
    public DateTime NgayTao { get; set; }
    public DateTime NgayCapNhat { get; set; }

    public static Khoa Tao(string tenKhoa, string? moTa, string? tuKhoaTrieuChung)
    {
        var khoa = new Khoa();
        khoa.CapNhat(tenKhoa, moTa, tuKhoaTrieuChung);
        return khoa;
    }

    public void CapNhat(string tenKhoa, string? moTa, string? tuKhoaTrieuChung)
    {
        TenKhoa = tenKhoa.Trim();
        MoTa = string.IsNullOrWhiteSpace(moTa) ? null : moTa.Trim();
        TuKhoaTrieuChung = string.IsNullOrWhiteSpace(tuKhoaTrieuChung) ? null : tuKhoaTrieuChung.Trim();
    }

    public void Xoa() => DaXoa = true;

    public void KhoiPhuc() => DaXoa = false;
}
