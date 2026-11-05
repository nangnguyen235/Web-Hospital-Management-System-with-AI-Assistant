using System.Security.Claims;
using System.Text;
using HIS.SharedKernel.Auth;
using Microsoft.Extensions.Options;
using Microsoft.IdentityModel.JsonWebTokens;
using Microsoft.IdentityModel.Tokens;

namespace HIS.SharedKernel.Infrastructure.Auth;

public sealed class JwtOptions
{
    public string Key { get; set; } = "";
    public string Issuer { get; set; } = "his-api";
    public string Audience { get; set; } = "his-frontend";
    public int SoPhutHetHan { get; set; } = 60; // BR-02
}

public sealed class JwtTokenService(IOptions<JwtOptions> options, TimeProvider thoiGian) : IJwtTokenService
{
    public TokenDaTao TaoToken(ThongTinToken thongTin)
    {
        var o = options.Value;
        var hetHan = thoiGian.GetUtcNow().AddMinutes(o.SoPhutHetHan);
        var claims = new List<Claim>
        {
            new(ClaimTen.TaiKhoanId, thongTin.TaiKhoanId.ToString()),
            new(ClaimTen.VaiTro, thongTin.VaiTro.ToString()),
            new(ClaimTen.LaTruongKhoa, thongTin.LaTruongKhoa ? "true" : "false"),
        };
        if (thongTin.NhanVienId is { } nhanVienId) claims.Add(new(ClaimTen.NhanVienId, nhanVienId.ToString()));
        if (thongTin.KhoaId is { } khoaId) claims.Add(new(ClaimTen.KhoaId, khoaId.ToString()));

        var token = new JsonWebTokenHandler().CreateToken(new SecurityTokenDescriptor
        {
            Subject = new ClaimsIdentity(claims),
            Issuer = o.Issuer,
            Audience = o.Audience,
            Expires = hetHan.UtcDateTime,
            SigningCredentials = new SigningCredentials(TaoKhoa(o.Key), SecurityAlgorithms.HmacSha256),
        });
        return new TokenDaTao(token, hetHan);
    }

    internal static SymmetricSecurityKey TaoKhoa(string key)
    {
        if (string.IsNullOrWhiteSpace(key) || key.Length < 32)
        {
            throw new InvalidOperationException("Jwt__Key phải có ít nhất 32 ký tự ngẫu nhiên.");
        }
        return new SymmetricSecurityKey(Encoding.UTF8.GetBytes(key));
    }
}
