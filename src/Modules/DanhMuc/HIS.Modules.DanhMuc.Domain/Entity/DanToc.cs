using HIS.SharedKernel.Domain;

namespace HIS.Modules.DanhMuc.Domain;

/// <summary>dm_dan_toc (Nation): seed 54 dân tộc, không có ngay_tao.</summary>
public sealed class DanToc : Entity<int>
{
    public string TenDanToc { get; set; } = "";
}
