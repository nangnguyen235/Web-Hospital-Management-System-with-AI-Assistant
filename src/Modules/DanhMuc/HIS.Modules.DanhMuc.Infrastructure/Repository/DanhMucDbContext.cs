using HIS.Modules.DanhMuc.Application;
using HIS.Modules.DanhMuc.Domain;
using HIS.SharedKernel.Infrastructure.Persistence;
using Microsoft.EntityFrameworkCore;

namespace HIS.Modules.DanhMuc.Infrastructure;

/// <summary>DbContext của module DanhMuc: bảng tiền tố dm_, bảng lịch sử migration __ef_lich_su_dm.</summary>
public sealed class DanhMucDbContext(DbContextOptions<DanhMucDbContext> options) : ModuleDbContext(options), IDanhMucDbContext
{
    public DbSet<Khoa> Khoa => Set<Khoa>();
    public DbSet<Phong> Phong => Set<Phong>();
    public DbSet<DichVu> DichVu => Set<DichVu>();
    public DbSet<Thuoc> Thuoc => Set<Thuoc>();
    public DbSet<DanToc> DanToc => Set<DanToc>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Khoa>(e =>
        {
            e.ToTable("dm_khoa");
            e.Property(x => x.TenKhoa).HasMaxLength(100);
            e.Property(x => x.MoTa).HasColumnType("text");
            e.Property(x => x.TuKhoaTrieuChung).HasColumnType("text");
        });
        modelBuilder.Entity<Phong>(e =>
        {
            e.ToTable("dm_phong");
            e.Property(x => x.TenPhong).HasMaxLength(100);
            e.HasOne<Khoa>().WithMany().HasForeignKey(x => x.KhoaId);
            e.HasOne<DichVu>().WithMany().HasForeignKey(x => x.DichVuId);
        });
        modelBuilder.Entity<DichVu>(e =>
        {
            e.ToTable("dm_dich_vu");
            e.Property(x => x.TenDichVu).HasMaxLength(150);
            e.Property(x => x.MoTa).HasColumnType("text");
            e.HasOne<Khoa>().WithMany().HasForeignKey(x => x.KhoaId);
        });
        modelBuilder.Entity<Thuoc>(e =>
        {
            e.ToTable("dm_thuoc");
            e.Property(x => x.TenThuoc).HasMaxLength(150);
        });
        modelBuilder.Entity<DanToc>(e =>
        {
            e.ToTable("dm_dan_toc");
            e.Property(x => x.TenDanToc).HasMaxLength(50);
            e.HasData(DanTocSeed.DanhSach.Select((ten, i) => new DanToc { Id = i + 1, TenDanToc = ten }));
        });
        base.OnModelCreating(modelBuilder);
    }
}
