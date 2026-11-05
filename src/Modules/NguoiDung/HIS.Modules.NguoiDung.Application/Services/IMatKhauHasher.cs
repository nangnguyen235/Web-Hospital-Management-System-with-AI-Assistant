namespace HIS.Modules.NguoiDung.Application;

public interface IMatKhauHasher
{
    string Hash(string matKhau);
    bool Verify(string matKhau, string hash);
}
