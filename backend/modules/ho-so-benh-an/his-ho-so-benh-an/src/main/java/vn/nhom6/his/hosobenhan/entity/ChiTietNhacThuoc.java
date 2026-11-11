package vn.nhom6.his.hosobenhan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.nhom6.his.shared.kernel.BaseEntity;

/**
 * {@code ba_chi_tiet_nhac_thuoc} (MedicineReminderDetail) — mỗi lần nhắc; chép tên thuốc, đơn vị, họ tên như TheDuck.
 * Không job nền: app hiện các lần có {@code thoi_diem_nhac} ≤ hiện tại (BR-21).
 */
@Entity
@Table(name = "ba_chi_tiet_nhac_thuoc")
@Getter
@Setter
@NoArgsConstructor
public class ChiTietNhacThuoc extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "char(36)")
    private String maXacNhan;

    @Column(nullable = false)
    private Long nhacThuocId;

    @Column(nullable = false)
    private LocalDateTime thoiDiemNhac;

    @Column(nullable = false)
    private int thuTuNhac;

    @Column(nullable = false, length = 150)
    private String tenThuoc;

    /** Bản sao đơn vị thuốc (TUYP/CHAI/HOP/GOI/CAI/VIEN). */
    @Column(nullable = false, length = 30)
    private String donVi;

    @Column(nullable = false, length = 100)
    private String hoTen;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal soLuong;

    @Column(nullable = false)
    private boolean daUong;

    @Column(nullable = false)
    private boolean boQua;

    @Column(nullable = false)
    private boolean daNhan;

    @Column(nullable = false)
    private Long taiKhoanId;
}
