using HIS.Modules.HoSoBenhAn.Application;
using HIS.Modules.HoSoBenhAn.Domain;
using HIS.SharedKernel.Infrastructure.Persistence;
using Microsoft.EntityFrameworkCore;

namespace HIS.Modules.HoSoBenhAn.Infrastructure;

/// <summary>DbContext của module HoSoBenhAn: bảng tiền tố ba_, bảng lịch sử migration __ef_lich_su_ba.</summary>
public sealed class HoSoBenhAnDbContext(DbContextOptions<HoSoBenhAnDbContext> options) : ModuleDbContext(options), IHoSoBenhAnDbContext
{
    public DbSet<BenhAn> BenhAn => Set<BenhAn>();
    public DbSet<XetNghiem> XetNghiem => Set<XetNghiem>();
    public DbSet<DonThuoc> DonThuoc => Set<DonThuoc>();
    public DbSet<ChiTietDonThuoc> ChiTietDonThuoc => Set<ChiTietDonThuoc>();
    public DbSet<DanhGia> DanhGia => Set<DanhGia>();
    public DbSet<NhacThuoc> NhacThuoc => Set<NhacThuoc>();
    public DbSet<ChiTietNhacThuoc> ChiTietNhacThuoc => Set<ChiTietNhacThuoc>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<BenhAn>(e =>
        {
            e.ToTable("ba_benh_an");
            e.Property(x => x.TrieuChung).HasColumnType("text");
            e.Property(x => x.ChanDoan).HasColumnType("text");
            e.HasIndex(x => x.PhieuDatKhamId).IsUnique();
            e.HasIndex(x => new { x.LichKhamId, x.TrangThai });
            e.HasIndex(x => x.BenhNhanId);
            e.HasIndex(x => x.HoSoId);
            e.HasMany(x => x.XetNghiem).WithOne().HasForeignKey(x => x.BenhAnId);
            e.HasOne(x => x.DonThuoc).WithOne().HasForeignKey<DonThuoc>(x => x.BenhAnId);
        });
        modelBuilder.Entity<XetNghiem>(e =>
        {
            e.ToTable("ba_xet_nghiem");
            e.Property(x => x.MaXetNghiem).HasMaxLength(14);
            e.Property(x => x.GhiChu).HasColumnType("text");
            e.Property(x => x.KetQua).HasColumnType("text");
            e.HasIndex(x => x.MaXetNghiem).IsUnique();
            e.HasIndex(x => x.GiaoDichId).IsUnique();
            e.HasIndex(x => new { x.PhongId, x.Ngay });
            e.HasIndex(x => x.DichVuId);
            e.HasIndex(x => x.DieuDuongId);
        });
        modelBuilder.Entity<DonThuoc>(e =>
        {
            e.ToTable("ba_don_thuoc");
            e.Property(x => x.MaDonThuoc).HasMaxLength(12).IsFixedLength();
            e.HasIndex(x => x.MaDonThuoc).IsUnique();
            e.HasMany(x => x.ChiTiet).WithOne().HasForeignKey(x => x.DonThuocId);
        });
        modelBuilder.Entity<ChiTietDonThuoc>(e =>
        {
            e.ToTable("ba_chi_tiet_don_thuoc");
            e.Property(x => x.MaDonThuoc).HasMaxLength(12).IsFixedLength();
            e.Property(x => x.SoLuongMoiLan).HasPrecision(5, 2);
            e.HasIndex(x => x.ThuocId);
        });
        modelBuilder.Entity<DanhGia>(e =>
        {
            e.ToTable("ba_danh_gia");
            e.Property(x => x.NhanXet).HasColumnType("text");
            e.HasIndex(x => x.PhieuDatKhamId).IsUnique();
            e.HasIndex(x => x.BacSiId);
            e.HasIndex(x => x.BenhNhanId);
        });
        modelBuilder.Entity<NhacThuoc>(e =>
        {
            e.ToTable("ba_nhac_thuoc");
            e.Property(x => x.TongSoLuong).HasPrecision(6, 2);
            e.Property(x => x.SoLuongConLai).HasPrecision(6, 2);
            e.HasOne<ChiTietDonThuoc>().WithMany().HasForeignKey(x => x.ChiTietDonThuocId);
            e.HasMany(x => x.ChiTiet).WithOne().HasForeignKey(x => x.NhacThuocId);
            e.HasIndex(x => x.HoSoId);
        });
        modelBuilder.Entity<ChiTietNhacThuoc>(e =>
        {
            e.ToTable("ba_chi_tiet_nhac_thuoc");
            e.Property(x => x.MaXacNhan).HasMaxLength(36).IsFixedLength();
            e.Property(x => x.TenThuoc).HasMaxLength(150);
            e.Property(x => x.DonVi).HasMaxLength(30);
            e.Property(x => x.HoTen).HasMaxLength(100);
            e.Property(x => x.SoLuong).HasPrecision(5, 2);
            e.HasIndex(x => new { x.TaiKhoanId, x.ThoiDiemNhac });
        });
        base.OnModelCreating(modelBuilder);
    }
}
