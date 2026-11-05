using HIS.Modules.LichKham.Application;
using HIS.Modules.LichKham.Domain;
using HIS.SharedKernel.Infrastructure.Persistence;
using Microsoft.EntityFrameworkCore;

namespace HIS.Modules.LichKham.Infrastructure;

/// <summary>DbContext của module LichKham: bảng tiền tố lk_, bảng lịch sử migration __ef_lich_su_lk.</summary>
public sealed class LichKhamDbContext(DbContextOptions<LichKhamDbContext> options) : ModuleDbContext(options), ILichKhamDbContext
{
    public DbSet<LichKhamBacSi> LichKhamBacSi => Set<LichKhamBacSi>();
    public DbSet<KhungGio> KhungGio => Set<KhungGio>();
    public DbSet<LichDieuDuong> LichDieuDuong => Set<LichDieuDuong>();
    public DbSet<PhieuDatKham> PhieuDatKham => Set<PhieuDatKham>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<LichKhamBacSi>(e =>
        {
            e.ToTable("lk_lich_kham_bac_si");
            e.HasIndex(x => x.BacSiId);
            e.HasIndex(x => x.PhongId);
            e.HasIndex(x => x.DichVuId);
            e.HasIndex(x => new { x.Ngay, x.Buoi });
            e.HasMany(x => x.KhungGio).WithOne().HasForeignKey(x => x.LichKhamId);
        });
        modelBuilder.Entity<KhungGio>(e =>
        {
            e.ToTable("lk_khung_gio");
            e.HasIndex(x => new { x.LichKhamId, x.ChiSoKhung }).IsUnique();
        });
        modelBuilder.Entity<LichDieuDuong>(e =>
        {
            e.ToTable("lk_lich_dieu_duong");
            e.Property(x => x.TenDieuDuong).HasMaxLength(100);
            e.Property(x => x.TenPhong).HasMaxLength(100);
            e.HasIndex(x => x.DieuDuongId);
            e.HasIndex(x => x.PhongId);
        });
        modelBuilder.Entity<PhieuDatKham>(e =>
        {
            e.ToTable("lk_phieu_dat_kham");
            e.Property(x => x.MaPhieu).HasMaxLength(12).IsFixedLength();
            e.HasIndex(x => x.MaPhieu).IsUnique();
            e.HasOne(x => x.KhungGio).WithMany().HasForeignKey(x => x.KhungGioId);
            e.HasIndex(x => x.HoSoId);
            e.HasIndex(x => x.GiaoDichId);
            e.HasIndex(x => x.BenhAnId);
            e.HasIndex(x => new { x.KhungGioId, x.DaXoa, x.DaHuy });
        });
        base.OnModelCreating(modelBuilder);
    }
}
