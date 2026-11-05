using System.Reflection;
using NetArchTest.Rules;

namespace HIS.ArchitectureTests;

/// <summary>Chặn sai chiều tầng và tham chiếu chéo module (modular-monolith.md mục 2.2, NFR-07).</summary>
public sealed class KienTrucTests
{
    private static readonly string[] Modules = ["NguoiDung", "DanhMuc", "LichKham", "HoSoBenhAn", "VienPhi", "TroLyAi"];
    private static readonly string[] TangTrong = ["Domain", "Application", "Infrastructure", "Presentation"];
    private static readonly string[] GoiHaTang =
        ["Microsoft.EntityFrameworkCore", "Microsoft.AspNetCore", "Amazon", "MailKit", "MimeKit", "System.Net.Http"];

    public static TheoryData<string> CacModule => [.. Modules];

    private static Assembly Nap(string ten) => Assembly.Load(ten);

    private static void KiemTra(Assembly assembly, IEnumerable<string> cam)
    {
        var ketQua = Types.InAssembly(assembly).ShouldNot().HaveDependencyOnAny([.. cam]).GetResult();
        Assert.True(ketQua.IsSuccessful,
            $"{assembly.GetName().Name} phụ thuộc sai: {string.Join(", ", ketQua.FailingTypeNames ?? [])}");
    }

    [Theory]
    [MemberData(nameof(CacModule))]
    public void Domain_khong_phu_thuoc_ha_tang_va_tang_tren(string module) =>
        KiemTra(Nap($"HIS.Modules.{module}.Domain"),
            GoiHaTang.Concat([
                $"HIS.Modules.{module}.Application", $"HIS.Modules.{module}.Infrastructure", $"HIS.Modules.{module}.Presentation",
                "HIS.SharedKernel.Infrastructure", "HIS.SharedKernel.Presentation",
            ]));

    [Theory]
    [MemberData(nameof(CacModule))]
    public void Application_khong_phu_thuoc_Infrastructure_Presentation(string module) =>
        KiemTra(Nap($"HIS.Modules.{module}.Application"),
            [$"HIS.Modules.{module}.Infrastructure", $"HIS.Modules.{module}.Presentation",
             "HIS.SharedKernel.Infrastructure", "HIS.SharedKernel.Presentation", "Microsoft.AspNetCore"]);

    [Theory]
    [MemberData(nameof(CacModule))]
    public void Module_chi_dung_Contracts_cua_module_khac(string module)
    {
        var tangCuaModuleKhac = Modules.Where(m => m != module)
            .SelectMany(m => TangTrong.Select(t => $"HIS.Modules.{m}.{t}"))
            .ToArray();
        foreach (var tang in TangTrong.Append("Contracts"))
        {
            KiemTra(Nap($"HIS.Modules.{module}.{tang}"), tangCuaModuleKhac);
        }
    }

    [Theory]
    [MemberData(nameof(CacModule))]
    public void Contracts_khong_phu_thuoc_tang_trong_nao(string module) =>
        KiemTra(Nap($"HIS.Modules.{module}.Contracts"),
            Modules.SelectMany(m => TangTrong.Select(t => $"HIS.Modules.{m}.{t}"))
                .Concat(GoiHaTang)
                .Concat(["HIS.SharedKernel.Infrastructure", "HIS.SharedKernel.Presentation"]));

    [Theory]
    [InlineData("HIS.SharedKernel")]
    [InlineData("HIS.SharedKernel.Infrastructure")]
    [InlineData("HIS.SharedKernel.Presentation")]
    public void SharedKernel_khong_phu_thuoc_module(string assembly) =>
        KiemTra(Nap(assembly), ["HIS.Modules"]);

    [Fact]
    public void SharedKernel_chi_chua_abstraction() =>
        KiemTra(Nap("HIS.SharedKernel"), GoiHaTang.Concat(["HIS.SharedKernel.Infrastructure", "HIS.SharedKernel.Presentation"]));
}
