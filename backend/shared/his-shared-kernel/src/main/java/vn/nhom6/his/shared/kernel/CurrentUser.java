package vn.nhom6.his.shared.kernel;

/** Người dùng của request hiện tại, đọc từ claim JWT (không hỏi CSDL mỗi request). */
public interface CurrentUser {

    boolean daDangNhap();

    /** Ném 401 nếu chưa đăng nhập. */
    long taiKhoanId();

    VaiTro vaiTro();

    Long nhanVienId();

    Integer khoaId();

    /** Bác sĩ = trưởng khoa, điều dưỡng = điều dưỡng trưởng. */
    boolean laTruongKhoa();
}
