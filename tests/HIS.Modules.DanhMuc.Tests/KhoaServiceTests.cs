using HIS.Modules.DanhMuc.Application;
using HIS.Modules.DanhMuc.Domain;
using HIS.Modules.DanhMuc.Infrastructure;
using HIS.SharedKernel.Results;
using Microsoft.EntityFrameworkCore;

namespace HIS.Modules.DanhMuc.Tests;

/// <summary>Test mẫu cho lát Khoa (FR-AD-04, BR-04) — cả nhóm viết test module khác theo kiểu này.</summary>
public sealed class KhoaServiceTests
{
    private static DanhMucDbContext TaoDb() =>
        new(new DbContextOptionsBuilder<DanhMucDbContext>().UseInMemoryDatabase(Guid.NewGuid().ToString()).Options);

    private static LuuKhoaRequest Khoa(string ten) => new() { TenKhoa = ten, MoTa = "Mô tả" };

    [Fact]
    public async Task Tao_khoa_hop_le_thi_luu_va_tra_dto()
    {
        await using var db = TaoDb();
        var ketQua = await new KhoaService(db).TaoAsync(Khoa("  Tim mạch  "), default);

        Assert.True(ketQua.IsSuccess);
        Assert.Equal("Tim mạch", ketQua.Value.TenKhoa);
        Assert.Equal(1, await db.Khoa.CountAsync());
    }

    [Fact]
    public async Task Tao_trung_ten_khoa_chua_xoa_thi_bao_xung_dot()
    {
        await using var db = TaoDb();
        var service = new KhoaService(db);
        await service.TaoAsync(Khoa("Nhi"), default);

        var ketQua = await service.TaoAsync(Khoa("Nhi"), default);

        Assert.False(ketQua.IsSuccess);
        Assert.Equal(LoaiLoi.XungDot, ketQua.Error!.Loai);
    }

    [Fact]
    public async Task Xoa_mem_thi_an_khoi_danh_sach_cong_khai_va_khoi_phuc_duoc()
    {
        await using var db = TaoDb();
        var service = new KhoaService(db);
        var khoa = (await service.TaoAsync(Khoa("Da liễu"), default)).Value;

        Assert.True((await service.XoaAsync(khoa.Id, default)).IsSuccess);
        Assert.Empty(await service.DanhSachCongKhaiAsync(default));
        Assert.Single((await service.TimKiemAsync(null, 1, 20, default)).Items);

        Assert.True((await service.KhoiPhucAsync(khoa.Id, default)).IsSuccess);
        Assert.Single(await service.DanhSachCongKhaiAsync(default));
    }

    [Fact]
    public async Task Cap_nhat_khoa_khong_ton_tai_thi_bao_khong_tim_thay()
    {
        await using var db = TaoDb();
        var ketQua = await new KhoaService(db).CapNhatAsync(999, Khoa("Mắt"), default);

        Assert.Equal(LoaiLoi.KhongTimThay, ketQua.Error!.Loai);
    }

    [Fact]
    public void Thuc_the_Khoa_xoa_va_khoi_phuc_doi_co_da_xoa()
    {
        var khoa = Domain.Khoa.Tao("Răng - hàm - mặt", null, "đau răng, sâu răng");
        khoa.Xoa();
        Assert.True(khoa.DaXoa);
        khoa.KhoiPhuc();
        Assert.False(khoa.DaXoa);
    }

    [Fact]
    public void Seed_du_54_dan_toc_khong_trung()
    {
        Assert.Equal(54, DanTocSeed.DanhSach.Count);
        Assert.Equal(54, DanTocSeed.DanhSach.Distinct().Count());
        Assert.Equal("Kinh", DanTocSeed.DanhSach[0]);
    }
}
