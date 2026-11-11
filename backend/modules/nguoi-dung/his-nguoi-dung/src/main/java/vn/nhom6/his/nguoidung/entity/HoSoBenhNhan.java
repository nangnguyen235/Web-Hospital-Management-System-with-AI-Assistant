package vn.nhom6.his.nguoidung.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.nhom6.his.nguoidung.constant.GioiTinh;
import vn.nhom6.his.shared.kernel.BaseEntity;

/**
 * {@code tk_ho_so_benh_nhan} (PatientProfile) — hồ sơ khai trong tài khoản (≤ 10); {@code tai_khoan_id} NULL = hồ sơ
 * điều dưỡng tạo tại quầy. OTP ở đây dùng cho "tìm lại hồ sơ" (BR-03).
 */
@Entity
@Table(name = "tk_ho_so_benh_nhan")
@Getter
@Setter
@NoArgsConstructor
public class HoSoBenhNhan extends BaseEntity implements CoOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long taiKhoanId;

    private Long benhNhanId;

    @Column(nullable = false, length = 100)
    private String hoTen;

    @Column(length = 20)
    private String soDienThoai;

    @Column(length = 150)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(30)")
    private GioiTinh gioiTinh;

    @Column(length = 12)
    private String soCccd;

    private String diaChi;

    @Column(nullable = false)
    private LocalDate ngaySinh;

    /** RL → dm_dan_toc. */
    private Integer danTocId;

    @Column(length = 64)
    private String otp;

    @Column(nullable = false)
    private int soLanNhapOtp;

    private LocalDateTime otpTaoLuc;

    private LocalDateTime otpHetHan;
}
