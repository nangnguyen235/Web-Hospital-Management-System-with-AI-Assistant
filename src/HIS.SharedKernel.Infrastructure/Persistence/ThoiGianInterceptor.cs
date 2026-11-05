using HIS.SharedKernel.Domain;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Diagnostics;

namespace HIS.SharedKernel.Infrastructure.Persistence;

/// <summary>Tự gán ngay_tao, ngay_cap_nhat (giờ Việt Nam) và tăng phien_ban trước khi lưu.</summary>
public sealed class ThoiGianInterceptor(TimeProvider thoiGian) : SaveChangesInterceptor
{
    public override ValueTask<InterceptionResult<int>> SavingChangesAsync(
        DbContextEventData eventData, InterceptionResult<int> result, CancellationToken cancellationToken = default)
    {
        if (eventData.Context is not null)
        {
            CapNhat(eventData.Context);
        }
        return base.SavingChangesAsync(eventData, result, cancellationToken);
    }

    public override InterceptionResult<int> SavingChanges(DbContextEventData eventData, InterceptionResult<int> result)
    {
        if (eventData.Context is not null)
        {
            CapNhat(eventData.Context);
        }
        return base.SavingChanges(eventData, result);
    }

    private void CapNhat(DbContext context)
    {
        var bayGio = thoiGian.HienTai();
        foreach (var entry in context.ChangeTracker.Entries())
        {
            if (entry.State == EntityState.Added)
            {
                if (entry.Entity is ICoNgayTao tao) tao.NgayTao = bayGio;
                if (entry.Entity is ICoNgayCapNhat capNhat) capNhat.NgayCapNhat = bayGio;
            }
            else if (entry.State == EntityState.Modified)
            {
                if (entry.Entity is ICoNgayCapNhat capNhat) capNhat.NgayCapNhat = bayGio;
                if (entry.Entity is ICoPhienBan phienBan) phienBan.PhienBan++;
            }
        }
    }
}
