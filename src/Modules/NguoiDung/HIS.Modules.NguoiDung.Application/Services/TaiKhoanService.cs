using System.ComponentModel.DataAnnotations;
using HIS.Modules.NguoiDung.Domain;
using HIS.SharedKernel.Abstractions;
using HIS.SharedKernel.Auth;
using HIS.SharedKernel.Results;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Logging;

namespace HIS.Modules.NguoiDung.Application;

/// <summary>Đăng nhập bằng mật khẩu (FR-BN-02, FR-BS-01, FR-DD-01), thông tin tài khoản, ảnh đại diện lên R2.</summary>
public sealed class TaiKhoanService(
    INguoiDungDbContext db,
    IMatKhauHasher hasher,
    IJwtTokenService jwt,
    IStorageService storage,
    ICurrentUser currentUser,
    ILogger<TaiKhoanService> logger)
{
    private static readonly Error SaiDangNhap = new("DANG_NHAP_THAT_BAI", "Sai tên đăng nhập hoặc mật khẩu.");
    private static readonly Dictionary<string, string> DinhDangAnh = new()
    {
        ["image/jpeg"] = "jpg",
        ["image/png"] = "png",
        ["image/webp"] = "webp",
    };
    private const long AnhToiDa = 2 * 1024 * 1024; // NFR-04

    public async Task<Result<DangNhapResponse>> DangNhapAsync(DangNhapRequest request, CancellationToken ct)
    {
        var ten = request.TenDangNhap.Trim();
        var taiKhoan = await db.TaiKhoan.Include(x => x.NhanVien)
            .FirstOrDefaultAsync(x => x.Email == ten || x.SoDienThoai == ten, ct);

        if (taiKhoan is null || taiKhoan.DaXoa || taiKhoan.NhanVien?.DaXoa == true
            || !hasher.Verify(request.MatKhau, taiKhoan.MatKhauHash))
        {
            return SaiDangNhap;
        }

        var vaiTro = taiKhoan.LayVaiTro();
        var token = jwt.TaoToken(new ThongTinToken(
            taiKhoan.Id, vaiTro, taiKhoan.NhanVienId, taiKhoan.NhanVien?.KhoaId, taiKhoan.NhanVien?.LaTruongKhoa ?? false));
        return new DangNhapResponse(token.AccessToken, token.HetHan, vaiTro, taiKhoan.HoTen);
    }

    public async Task<Result<ThongTinTaiKhoanDto>> LayThongTinToiAsync(CancellationToken ct)
    {
        var taiKhoan = await TimTaiKhoanHienTaiAsync(ct);
        if (taiKhoan is null) return Error.KhongTimThay("tài khoản");

        var anhUrl = taiKhoan.AnhDaiDienKey is null ? null : await storage.GetPresignedUrlAsync(taiKhoan.AnhDaiDienKey);
        return new ThongTinTaiKhoanDto(
            taiKhoan.Id, taiKhoan.HoTen, taiKhoan.Email, taiKhoan.SoDienThoai, taiKhoan.LayVaiTro(),
            taiKhoan.NhanVienId, taiKhoan.NhanVien?.KhoaId, taiKhoan.NhanVien?.LaTruongKhoa ?? false, anhUrl);
    }

    /// <summary>Ảnh đại diện ≤ 2 MB, jpg/png/webp; key tai-khoan/{id}/{uuid}.ext (cloudflare-r2.md mục 2).</summary>
    public async Task<Result<string>> CapNhatAnhDaiDienAsync(Stream noiDung, string contentType, long kichThuoc, CancellationToken ct)
    {
        if (!DinhDangAnh.TryGetValue(contentType, out var duoi))
            return new Error("SAI_DINH_DANG", "Ảnh phải là jpg, png hoặc webp.");
        if (kichThuoc <= 0 || kichThuoc > AnhToiDa)
            return new Error("QUA_KICH_THUOC", "Ảnh tối đa 2 MB.");

        var taiKhoan = await TimTaiKhoanHienTaiAsync(ct);
        if (taiKhoan is null) return Error.KhongTimThay("tài khoản");

        var keyCu = taiKhoan.AnhDaiDienKey;
        var keyMoi = $"tai-khoan/{taiKhoan.Id}/{Guid.NewGuid():N}.{duoi}";
        await storage.UploadAsync(noiDung, keyMoi, contentType, ct);
        taiKhoan.AnhDaiDienKey = keyMoi;
        await db.SaveChangesAsync(ct);

        if (keyCu is not null)
        {
            try
            {
                await storage.DeleteAsync(keyCu, ct);
            }
            catch (Exception ex)
            {
                logger.LogWarning(ex, "Không xóa được ảnh cũ {Key} trên R2", keyCu);
            }
        }
        return await storage.GetPresignedUrlAsync(keyMoi);
    }

    private Task<TaiKhoan?> TimTaiKhoanHienTaiAsync(CancellationToken ct) =>
        db.TaiKhoan.Include(x => x.NhanVien)
            .FirstOrDefaultAsync(x => x.Id == currentUser.TaiKhoanId && !x.DaXoa, ct);
}
