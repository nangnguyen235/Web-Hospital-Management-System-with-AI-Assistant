using HIS.Modules.NguoiDung.Application;

namespace HIS.Modules.NguoiDung.Infrastructure;

/// <summary>Mật khẩu băm BCrypt (NFR-02).</summary>
internal sealed class BCryptMatKhauHasher : IMatKhauHasher
{
    public string Hash(string matKhau) => BCrypt.Net.BCrypt.HashPassword(matKhau);

    public bool Verify(string matKhau, string hash) => BCrypt.Net.BCrypt.Verify(matKhau, hash);
}
