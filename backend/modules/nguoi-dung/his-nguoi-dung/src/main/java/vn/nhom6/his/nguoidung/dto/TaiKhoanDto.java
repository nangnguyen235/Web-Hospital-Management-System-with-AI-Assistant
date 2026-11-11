package vn.nhom6.his.nguoidung.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import vn.nhom6.his.shared.kernel.VaiTro;

/** DTO đăng nhập và thông tin tài khoản — giữ đúng JSON bản trước (frontend services/nguoiDung.ts). */
public final class TaiKhoanDto {

    private TaiKhoanDto() {
    }

    /** {@code tenDangNhap} = email hoặc số điện thoại. */
    public record DangNhapRequest(
            @NotBlank(message = "Vui lòng nhập email hoặc số điện thoại.") @Size(max = 150) String tenDangNhap,
            @NotBlank(message = "Vui lòng nhập mật khẩu.") @Size(max = 100) String matKhau) {
    }

    public record DangNhapResponse(String accessToken, OffsetDateTime hetHan, VaiTro vaiTro, String hoTen) {
    }

    public record ThongTinTaiKhoan(long id, String hoTen, String email, String soDienThoai, VaiTro vaiTro,
                                   Long nhanVienId, Integer khoaId, boolean laTruongKhoa, String anhDaiDienUrl) {
    }
}
