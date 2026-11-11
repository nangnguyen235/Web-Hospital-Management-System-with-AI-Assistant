package vn.nhom6.his.nguoidung.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import vn.nhom6.his.nguoidung.constant.GioiTinh;
import vn.nhom6.his.nguoidung.constant.HocHam;
import vn.nhom6.his.nguoidung.constant.LoaiNhanVien;

/** Nhân viên (FR-AD-03, 04) và bác sĩ công khai (FR-BN-07). */
public record NhanVienDto(long id, String hoTen, String email, String soDienThoai, String soCccd, GioiTinh gioiTinh,
                          LocalDate ngaySinh, LoaiNhanVien loaiNhanVien, HocHam hocHam, Integer khoaId, String tenKhoa,
                          boolean laTruongKhoa, BigDecimal diemDanhGia, String anhDaiDienUrl, Long taiKhoanId,
                          boolean daXoa) {

    /** Tạo nhân viên kèm tài khoản; khi sửa: bỏ qua {@code matKhau} (để trống) và {@code loaiNhanVien}. */
    public record Luu(
            @NotBlank(message = "Vui lòng nhập họ tên.") @Size(max = 100) String hoTen,
            @NotBlank(message = "Vui lòng nhập email.") @Email(message = "Email không hợp lệ.") @Size(max = 150) String email,
            @NotBlank(message = "Vui lòng nhập số điện thoại.") @Pattern(regexp = "^0\\d{9,10}$", message = "Số điện thoại không hợp lệ.") String soDienThoai,
            @Size(min = 6, max = 100, message = "Mật khẩu từ 6 đến 100 ký tự.") String matKhau,
            @NotNull(message = "Vui lòng chọn giới tính.") GioiTinh gioiTinh,
            @Past(message = "Ngày sinh phải trước hôm nay.") LocalDate ngaySinh,
            @Pattern(regexp = "^$|^\\d{12}$", message = "CCCD gồm 12 chữ số.") String soCccd,
            @NotNull(message = "Vui lòng chọn loại nhân viên.") LoaiNhanVien loaiNhanVien,
            HocHam hocHam,
            Integer khoaId) {
    }

    public record DoiKhoa(Integer khoaId) {
    }

    public record BacSiCongKhai(long id, String hoTen, GioiTinh gioiTinh, HocHam hocHam, Integer khoaId, String tenKhoa,
                                BigDecimal diemDanhGia, String anhDaiDienUrl) {
    }

    /** Nhân viên của khoa mình (trưởng khoa xếp lịch, điều dưỡng trưởng phân công). */
    public record Gon(long id, String hoTen, HocHam hocHam, boolean laTruongKhoa) {
    }
}
