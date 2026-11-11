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

/** {@code tk_nguoi_dung_tam} (TemporaryUser) — giữ OTP đăng ký tới khi xác thực xong (email thay SĐT). */
@Entity
@Table(name = "tk_nguoi_dung_tam")
@Getter
@Setter
@NoArgsConstructor
public class NguoiDungTam extends BaseEntity implements CoOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 64)
    private String otp;

    @Column(nullable = false)
    private int soLanNhapOtp;

    private LocalDateTime otpTaoLuc;

    private LocalDateTime otpHetHan;
}
