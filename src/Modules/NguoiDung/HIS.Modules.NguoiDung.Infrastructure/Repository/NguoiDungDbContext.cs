using HIS.Modules.NguoiDung.Application;
using HIS.Modules.NguoiDung.Domain;
using HIS.SharedKernel.Infrastructure.Persistence;
using Microsoft.EntityFrameworkCore;

namespace HIS.Modules.NguoiDung.Infrastructure;

/// <summary>DbContext của module NguoiDung: bảng tiền tố tk_, bảng lịch sử migration __ef_lich_su_tk.</summary>
public sealed class NguoiDungDbContext(DbContextOptions<NguoiDungDbContext> options) : ModuleDbContext(options), INguoiDungDbContext
{
    public DbSet<TaiKhoan> TaiKhoan => Set<TaiKhoan>();
    public DbSet<NguoiDungTam> NguoiDungTam => Set<NguoiDungTam>();
    public DbSet<NhanVien> NhanVien => Set<NhanVien>();
    public DbSet<BenhNhan> BenhNhan => Set<BenhNhan>();
    public DbSet<HoSoBenhNhan> HoSoBenhNhan => Set<HoSoBenhNhan>();
    public DbSet<ThongBao> ThongBao => Set<ThongBao>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<TaiKhoan>(e =>
        {
            e.ToTable("tk_tai_khoan");
            e.Property(x => x.HoTen).HasMaxLength(100);
            e.Property(x => x.Email).HasMaxLength(150);
            e.Property(x => x.SoDienThoai).HasMaxLength(20);
            e.Property(x => x.Otp).HasMaxLength(64);
            e.HasIndex(x => x.Email).IsUnique();
            e.HasIndex(x => x.SoDienThoai).IsUnique();
            e.HasOne(x => x.NhanVien).WithOne(x => x.TaiKhoan).HasForeignKey<TaiKhoan>(x => x.NhanVienId);
        });
        modelBuilder.Entity<NguoiDungTam>(e =>
        {
            e.ToTable("tk_nguoi_dung_tam");
            e.Property(x => x.Email).HasMaxLength(150);
            e.Property(x => x.Otp).HasMaxLength(64);
            e.HasIndex(x => x.Email);
        });
        modelBuilder.Entity<NhanVien>(e =>
        {
            e.ToTable("tk_nhan_vien");
            e.HasDiscriminator(x => x.LoaiNhanVien)
                .HasValue<BacSi>(LoaiNhanVien.BAC_SI)
                .HasValue<DieuDuong>(LoaiNhanVien.DIEU_DUONG)
                .HasValue<QuanTri>(LoaiNhanVien.QUAN_TRI);
            e.Property(x => x.HoTen).HasMaxLength(100);
            e.Property(x => x.SoDienThoai).HasMaxLength(20);
            e.Property(x => x.SoCccd).HasMaxLength(12);
            e.HasIndex(x => x.KhoaId);
        });
        modelBuilder.Entity<BacSi>().Property(x => x.DiemDanhGia).HasPrecision(2, 1);
        modelBuilder.Entity<BenhNhan>(e =>
        {
            e.ToTable("tk_benh_nhan");
            e.Property(x => x.MaBenhNhan).HasMaxLength(10);
            e.Property(x => x.HoTen).HasMaxLength(100);
            e.Property(x => x.SoDienThoai).HasMaxLength(20);
            e.Property(x => x.SoCccd).HasMaxLength(12);
            e.HasIndex(x => x.MaBenhNhan).IsUnique();
            e.HasIndex(x => x.SoCccd).IsUnique();
        });
        modelBuilder.Entity<HoSoBenhNhan>(e =>
        {
            e.ToTable("tk_ho_so_benh_nhan");
            e.Property(x => x.HoTen).HasMaxLength(100);
            e.Property(x => x.SoDienThoai).HasMaxLength(20);
            e.Property(x => x.Email).HasMaxLength(150);
            e.Property(x => x.SoCccd).HasMaxLength(12);
            e.Property(x => x.Otp).HasMaxLength(64);
            e.HasOne<TaiKhoan>().WithMany().HasForeignKey(x => x.TaiKhoanId);
            e.HasOne<BenhNhan>().WithMany().HasForeignKey(x => x.BenhNhanId);
            e.HasIndex(x => x.DanTocId);
        });
        modelBuilder.Entity<ThongBao>(e =>
        {
            e.ToTable("tk_thong_bao");
            e.Property(x => x.TieuDe).HasMaxLength(200);
            e.Property(x => x.NoiDung).HasColumnType("text");
            e.Property(x => x.DuLieu).HasMaxLength(1000);
            e.HasOne<TaiKhoan>().WithMany().HasForeignKey(x => x.TaiKhoanId);
        });
        base.OnModelCreating(modelBuilder);
    }
}
