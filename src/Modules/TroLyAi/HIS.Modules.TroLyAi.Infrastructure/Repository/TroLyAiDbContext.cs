using HIS.Modules.TroLyAi.Application;
using HIS.Modules.TroLyAi.Domain;
using HIS.SharedKernel.Infrastructure.Persistence;
using Microsoft.EntityFrameworkCore;

namespace HIS.Modules.TroLyAi.Infrastructure;

/// <summary>DbContext của module TroLyAi: bảng tiền tố tl_, bảng lịch sử migration __ef_lich_su_tl.</summary>
public sealed class TroLyAiDbContext(DbContextOptions<TroLyAiDbContext> options) : ModuleDbContext(options), ITroLyAiDbContext
{
    public DbSet<HoiThoai> HoiThoai => Set<HoiThoai>();
    public DbSet<TinNhan> TinNhan => Set<TinNhan>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<HoiThoai>(e =>
        {
            e.ToTable("tl_hoi_thoai");
            e.HasIndex(x => x.TaiKhoanId);
            e.HasMany(x => x.TinNhan).WithOne().HasForeignKey(x => x.HoiThoaiId);
        });
        modelBuilder.Entity<TinNhan>(e =>
        {
            e.ToTable("tl_tin_nhan");
            e.Property(x => x.NoiDung).HasColumnType("text");
            e.HasIndex(x => new { x.HoiThoaiId, x.SoThuTu }).IsUnique();
        });
        base.OnModelCreating(modelBuilder);
    }
}
