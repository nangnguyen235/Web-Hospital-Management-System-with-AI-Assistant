using Microsoft.EntityFrameworkCore;

namespace HIS.Modules.DanhMuc.Application;

public sealed class DanTocService(IDanhMucDbContext db)
{
    public async Task<IReadOnlyList<DanTocDto>> DanhSachAsync(CancellationToken ct) =>
        await db.DanToc.AsNoTracking().OrderBy(x => x.Id).Select(x => new DanTocDto(x.Id, x.TenDanToc)).ToListAsync(ct);
}
