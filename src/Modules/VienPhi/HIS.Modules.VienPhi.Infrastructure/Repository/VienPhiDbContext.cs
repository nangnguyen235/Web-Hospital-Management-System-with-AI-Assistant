using HIS.Modules.VienPhi.Application;
using HIS.Modules.VienPhi.Domain;
using HIS.SharedKernel.Infrastructure.Persistence;
using Microsoft.EntityFrameworkCore;

namespace HIS.Modules.VienPhi.Infrastructure;

/// <summary>DbContext của module VienPhi: bảng tiền tố vp_, bảng lịch sử migration __ef_lich_su_vp.</summary>
public sealed class VienPhiDbContext(DbContextOptions<VienPhiDbContext> options) : ModuleDbContext(options), IVienPhiDbContext
{
    public DbSet<GiaoDich> GiaoDich => Set<GiaoDich>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<GiaoDich>(e =>
        {
            e.ToTable("vp_giao_dich");
            e.Property(x => x.MaNganHang).HasMaxLength(20);
            e.Property(x => x.PhuongThuc).HasMaxLength(20);
            e.HasIndex(x => x.TaiKhoanId);
        });
        base.OnModelCreating(modelBuilder);
    }
}
