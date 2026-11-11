package vn.nhom6.his.nguoidung.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Request đăng ký, đăng nhập OTP, quên mật khẩu (FR-BN-01, 03, 04 — BR-02). */
public final class XacThucDto {

    private XacThucDto() {
    }

    static final String SDT = "^0\\d{9,10}$";

    public record GuiOtpDangKy(
            @NotBlank(message = "Vui lòng nhập email.") @Email(message = "Email không hợp lệ.") @Size(max = 150) String email,
            @NotBlank(message = "Vui lòng nhập số điện thoại.") @Pattern(regexp = SDT, message = "Số điện thoại không hợp lệ.") String soDienThoai) {
    }

    public record DangKy(
            @NotBlank(message = "Vui lòng nhập email.") @Email(message = "Email không hợp lệ.") @Size(max = 150) String email,
            @NotBlank(message = "Vui lòng nhập số điện thoại.") @Pattern(regexp = SDT, message = "Số điện thoại không hợp lệ.") String soDienThoai,
            @NotBlank(message = "Vui lòng nhập họ tên.") @Size(max = 100, message = "Họ tên tối đa 100 ký tự.") String hoTen,
            @NotBlank(message = "Vui lòng nhập mật khẩu.") @Size(min = 6, max = 100, message = "Mật khẩu từ 6 đến 100 ký tự.") String matKhau,
            @NotBlank(message = "Vui lòng nhập mã OTP.") String otp) {
    }

    public record GuiOtpEmail(
            @NotBlank(message = "Vui lòng nhập email.") @Email(message = "Email không hợp lệ.") String email) {
    }

    public record DangNhapOtp(
            @NotBlank(message = "Vui lòng nhập email.") @Email(message = "Email không hợp lệ.") String email,
            @NotBlank(message = "Vui lòng nhập mã OTP.") String otp) {
    }

    public record DatLaiMatKhau(
            @NotBlank(message = "Vui lòng nhập email.") @Email(message = "Email không hợp lệ.") String email,
            @NotBlank(message = "Vui lòng nhập mã OTP.") String otp,
            @NotBlank(message = "Vui lòng nhập mật khẩu mới.") @Size(min = 6, max = 100, message = "Mật khẩu từ 6 đến 100 ký tự.") String matKhauMoi,
            @NotBlank(message = "Vui lòng nhập lại mật khẩu.") String nhapLaiMatKhau) {
    }

    public record DoiMatKhau(
            @NotBlank(message = "Vui lòng nhập mật khẩu hiện tại.") String matKhauCu,
            @NotBlank(message = "Vui lòng nhập mật khẩu mới.") @Size(min = 6, max = 100, message = "Mật khẩu từ 6 đến 100 ký tự.") String matKhauMoi) {
    }
}
