using System.Reflection;

namespace HIS.Api;

/// <summary>Danh sách assembly của 6 module: Application (handler sự kiện MediatR) và Presentation (controller).</summary>
internal static class ModuleCatalog
{
    public static readonly Assembly[] ApplicationAssemblies =
    [
        typeof(HIS.Modules.NguoiDung.Application.INguoiDungDbContext).Assembly,
        typeof(HIS.Modules.DanhMuc.Application.IDanhMucDbContext).Assembly,
        typeof(HIS.Modules.LichKham.Application.ILichKhamDbContext).Assembly,
        typeof(HIS.Modules.HoSoBenhAn.Application.IHoSoBenhAnDbContext).Assembly,
        typeof(HIS.Modules.VienPhi.Application.IVienPhiDbContext).Assembly,
        typeof(HIS.Modules.TroLyAi.Application.ITroLyAiDbContext).Assembly,
    ];

    public static readonly Assembly[] PresentationAssemblies =
    [
        typeof(HIS.Modules.NguoiDung.Presentation.PresentationAssembly).Assembly,
        typeof(HIS.Modules.DanhMuc.Presentation.PresentationAssembly).Assembly,
        typeof(HIS.Modules.LichKham.Presentation.PresentationAssembly).Assembly,
        typeof(HIS.Modules.HoSoBenhAn.Presentation.PresentationAssembly).Assembly,
        typeof(HIS.Modules.VienPhi.Presentation.PresentationAssembly).Assembly,
        typeof(HIS.Modules.TroLyAi.Presentation.PresentationAssembly).Assembly,
    ];
}
