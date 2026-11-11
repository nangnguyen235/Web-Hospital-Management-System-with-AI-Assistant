package vn.nhom6.his.nguoidung.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.nhom6.his.shared.kernel.BaseEntity;

/**
 * {@code tk_tai_khoan} (Account). {@code nhan_vien_id} NULL ⇒ bệnh nhân, ngược lại vai trò = loại nhân viên (BR-01).
 * OTP lưu băm SHA-256 (BR-02).
 */
@Entity
@Table(name = "tk_tai_khoan")
@Getter
@Setter
@NoArgsConstructor
public class TaiKhoan extends BaseEntity implements CoOtp {

    public static final int SO_HO_SO_TOI_DA = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String hoTen;

    @Column(length = 150)
    private String email;

    @Column(nullable = false)
    private String matKhauHash;

    @Column(nullable = false, length = 20)
    private String soDienThoai;

    private String anhDaiDienKey;

    @Column(nullable = false)
    private int soLuongHoSo;

    @Column(length = 64)
    private String otp;

    @Column(nullable = false)
    private int soLanNhapOtp;

    private LocalDateTime otpTaoLuc;

    private LocalDateTime otpHetHan;

    private Long nhanVienId;

    public boolean laBenhNhan() {
        return nhanVienId == null;
    }
}
