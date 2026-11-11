package vn.nhom6.his.hosobenhan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.nhom6.his.hosobenhan.constant.TrangThaiXetNghiem;
import vn.nhom6.his.shared.kernel.ThoiGian;

/** {@code ba_xet_nghiem} (MedicalTest) — bảng không có {@code ngay_cap_nhat} (mo-hinh-du-lieu.md mục 2.4). */
@Entity
@Table(name = "ba_xet_nghiem")
@Getter
@Setter
@NoArgsConstructor
public class XetNghiem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 14)
    private String maXetNghiem;

    /** Theo phòng xét nghiệm trong ngày. */
    @Column(nullable = false)
    private int soThuTu;

    @Column(columnDefinition = "text")
    private String ghiChu;

    @Column(columnDefinition = "text")
    private String ketQua;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal gia;

    private String fileKetQuaKey;

    @Column(nullable = false)
    private Integer dichVuId;

    @Column(nullable = false)
    private Long benhAnId;

    @Column(nullable = false)
    private Integer phongId;

    private Long dieuDuongId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(30)")
    private TrangThaiXetNghiem trangThai = TrangThaiXetNghiem.CHO_THUC_HIEN;

    @Column(nullable = false)
    private LocalDate ngay;

    private Long giaoDichId;

    /** Bản sao trạng thái giao dịch (không JOIN sang VienPhi). */
    @Column(nullable = false)
    private boolean daThanhToan;

    @Column(nullable = false)
    private boolean daXoa;

    @Column(nullable = false, updatable = false)
    private LocalDateTime ngayTao;

    @PrePersist
    void truocKhiThem() {
        if (ngayTao == null) {
            ngayTao = ThoiGian.bayGio();
        }
    }
}
