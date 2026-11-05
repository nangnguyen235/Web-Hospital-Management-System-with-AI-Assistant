namespace HIS.SharedKernel.Domain;

/// <summary>Lớp gốc của mọi thực thể. Khóa chính BIGINT (long) hoặc INT (danh mục).</summary>
public abstract class Entity<TId> where TId : struct
{
    public TId Id { get; set; }
}

/// <summary>Bảng có cột <c>da_xoa</c> (xóa mềm như TheDuck).</summary>
public interface IXoaMem
{
    bool DaXoa { get; set; }
}

/// <summary>Bảng có cột <c>ngay_tao</c>; interceptor tự gán khi thêm mới.</summary>
public interface ICoNgayTao
{
    DateTime NgayTao { get; set; }
}

/// <summary>Bảng có cột <c>ngay_cap_nhat</c>; interceptor tự gán khi thêm/sửa.</summary>
public interface ICoNgayCapNhat
{
    DateTime NgayCapNhat { get; set; }
}

/// <summary>Bảng có bộ đếm, dùng <c>phien_ban</c> làm concurrency token (tự tăng khi sửa).</summary>
public interface ICoPhienBan
{
    int PhienBan { get; set; }
}
