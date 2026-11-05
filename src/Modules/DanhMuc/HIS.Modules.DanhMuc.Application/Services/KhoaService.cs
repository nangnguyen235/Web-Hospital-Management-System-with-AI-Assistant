using System.ComponentModel.DataAnnotations;
using HIS.Modules.DanhMuc.Domain;
using HIS.SharedKernel.Results;
using Microsoft.EntityFrameworkCore;

namespace HIS.Modules.DanhMuc.Application;

/// <summary>
/// LÁT MẪU cho cả nhóm chép theo: Admin quản lý khoa (FR-AD-04, BR-04) + danh sách công khai (FR-BN-07).
/// Mẫu: Presentation gọi service → service kiểm nghiệp vụ, trả Result → controller đổi sang HTTP.
/// </summary>
public sealed class KhoaService(IDanhMucDbContext db)
{
    public async Task<IReadOnlyList<KhoaDto>> DanhSachCongKhaiAsync(CancellationToken ct) =>
        await db.Khoa.AsNoTracking()
            .Where(x => !x.DaXoa)
            .OrderBy(x => x.TenKhoa)
            .Select(x => new KhoaDto(x.Id, x.TenKhoa, x.MoTa, x.TuKhoaTrieuChung, x.DaXoa))
            .ToListAsync(ct);

    /// <summary>Admin: tìm theo tên, gồm cả khoa đã xóa (để khôi phục), phân trang (NFR-01).</summary>
    public async Task<PagedResult<KhoaDto>> TimKiemAsync(string? tuKhoa, int page, int pageSize, CancellationToken ct)
    {
        page = Math.Max(page, 1);
        pageSize = Math.Clamp(pageSize, 1, 100);
        var query = db.Khoa.AsNoTracking();
        if (!string.IsNullOrWhiteSpace(tuKhoa))
        {
            query = query.Where(x => x.TenKhoa.Contains(tuKhoa.Trim()));
        }

        var tong = await query.CountAsync(ct);
        var items = await query
            .OrderBy(x => x.DaXoa).ThenBy(x => x.TenKhoa)
            .Skip((page - 1) * pageSize).Take(pageSize)
            .Select(x => new KhoaDto(x.Id, x.TenKhoa, x.MoTa, x.TuKhoaTrieuChung, x.DaXoa))
            .ToListAsync(ct);
        return new PagedResult<KhoaDto>(items, tong, page, pageSize);
    }

    public async Task<Result<KhoaDto>> TaoAsync(LuuKhoaRequest request, CancellationToken ct)
    {
        if (await TrungTenAsync(request.TenKhoa, null, ct)) return TrungTen(request.TenKhoa);

        var khoa = Khoa.Tao(request.TenKhoa, request.MoTa, request.TuKhoaTrieuChung);
        db.Khoa.Add(khoa);
        await db.SaveChangesAsync(ct);
        return ToDto(khoa);
    }

    public async Task<Result<KhoaDto>> CapNhatAsync(int id, LuuKhoaRequest request, CancellationToken ct)
    {
        var khoa = await db.Khoa.FirstOrDefaultAsync(x => x.Id == id, ct);
        if (khoa is null) return Error.KhongTimThay("khoa");
        if (await TrungTenAsync(request.TenKhoa, id, ct)) return TrungTen(request.TenKhoa);

        khoa.CapNhat(request.TenKhoa, request.MoTa, request.TuKhoaTrieuChung);
        await db.SaveChangesAsync(ct);
        return ToDto(khoa);
    }

    /// <summary>Xóa mềm (da_xoa = true): khoa không hiện khi chọn mới, dữ liệu cũ vẫn hiện đúng tên (BR-04).</summary>
    public Task<Result> XoaAsync(int id, CancellationToken ct) => DoiTrangThaiAsync(id, k => k.Xoa(), ct);

    public Task<Result> KhoiPhucAsync(int id, CancellationToken ct) => DoiTrangThaiAsync(id, k => k.KhoiPhuc(), ct);

    private async Task<Result> DoiTrangThaiAsync(int id, Action<Khoa> doi, CancellationToken ct)
    {
        var khoa = await db.Khoa.FirstOrDefaultAsync(x => x.Id == id, ct);
        if (khoa is null) return Error.KhongTimThay("khoa");

        doi(khoa);
        await db.SaveChangesAsync(ct);
        return Result.Success();
    }

    private Task<bool> TrungTenAsync(string tenKhoa, int? boQuaId, CancellationToken ct)
    {
        var ten = tenKhoa.Trim();
        return db.Khoa.AnyAsync(x => x.TenKhoa == ten && !x.DaXoa && x.Id != boQuaId, ct);
    }

    private static Error TrungTen(string tenKhoa) =>
        new("TRUNG_TEN_KHOA", $"Đã có khoa tên \"{tenKhoa.Trim()}\".", LoaiLoi.XungDot);

    private static KhoaDto ToDto(Khoa x) => new(x.Id, x.TenKhoa, x.MoTa, x.TuKhoaTrieuChung, x.DaXoa);
}
