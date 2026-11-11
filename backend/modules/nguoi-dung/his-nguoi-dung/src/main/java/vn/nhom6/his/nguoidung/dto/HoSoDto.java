package vn.nhom6.his.nguoidung.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import vn.nhom6.his.nguoidung.constant.GioiTinh;
import vn.nhom6.his.nguoidung.entity.HoSoBenhNhan;

/** Hồ sơ bệnh nhân (FR-BN-05, 06; BR-03). */
public record HoSoDto(long id, String hoTen, String soDienThoai, String email, GioiTinh gioiTinh, String soCccd,
                      String diaChi, LocalDate ngaySinh, Integer danTocId, Long benhNhanId, String maBenhNhan,
                      Long taiKhoanId, boolean daXoa) {

    public static HoSoDto tu(HoSoBenhNhan h, String maBenhNhan) {
        return new HoSoDto(h.getId(), h.getHoTen(), h.getSoDienThoai(), h.getEmail(), h.getGioiTinh(), h.getSoCccd(),
                h.getDiaChi(), h.getNgaySinh(), h.getDanTocId(), h.getBenhNhanId(), maBenhNhan, h.getTaiKhoanId(),
                h.isDaXoa());
    }

    /** Họ tên, ngày sinh, giới tính bắt buộc; SĐT, email, CCCD, địa chỉ, dân tộc tùy chọn. */
    public record Luu(
            @NotBlank(message = "Vui lòng nhập họ tên.") @Size(max = 100, message = "Họ tên tối đa 100 ký tự.") String hoTen,
            @Pattern(regexp = "^$|^0\\d{9,10}$", message = "Số điện thoại không hợp lệ.") String soDienThoai,
            @Email(message = "Email không hợp lệ.") @Size(max = 150) String email,
            @NotNull(message = "Vui lòng chọn giới tính.") GioiTinh gioiTinh,
            @Pattern(regexp = "^$|^\\d{12}$", message = "CCCD gồm 12 chữ số.") String soCccd,
            @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự.") String diaChi,
            @NotNull(message = "Vui lòng nhập ngày sinh.") @Past(message = "Ngày sinh phải trước hôm nay.") LocalDate ngaySinh,
            Integer danTocId) {
    }

    /** Kết quả tìm lại hồ sơ: che bớt thông tin vì người tìm chưa chứng minh sở hữu. */
    public record TimLai(long id, String hoTen, LocalDate ngaySinh, GioiTinh gioiTinh, String emailChe, boolean coEmail) {
    }

    public record XacNhanOtp(@NotBlank(message = "Vui lòng nhập mã OTP.") String otp) {
    }
}
