package vn.nhom6.his.lichkham.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.nhom6.his.lichkham.constant.KenhDat;
import vn.nhom6.his.shared.kernel.BaseEntity;

/**
 * {@code lk_phieu_dat_kham} (Booking) — TRỤC của hệ thống. Chưa thanh toán: {@code da_xoa = true}, {@code so_thu_tu = −1}
 * (đúng cách TheDuck); trạng thái suy ra từ cờ (logic-nghiep-vu.md mục 3.3).
 */
@Entity
@Table(name = "lk_phieu_dat_kham")
@Getter
@Setter
@NoArgsConstructor
public class PhieuDatKham extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "char(12)")
    private String maPhieu;

    @Column(nullable = false)
    private int soThuTu = -1;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal giaDichVu;

    @Column(nullable = false)
    private boolean daDanhGia;

    /** RL → tk_ho_so_benh_nhan. */
    @Column(nullable = false)
    private Long hoSoId;

    @Column(nullable = false)
    private Long khungGioId;

    /** RL → ba_benh_an, gán khi tiếp nhận (BenhAnDaTao). */
    private Long benhAnId;

    /** RL → vp_giao_dich. */
    @Column(nullable = false)
    private Long giaoDichId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(30)")
    private KenhDat kenhDat;

    /** Giữ chỗ hết hạn, thanh toán thất bại, vắng mặt — bệnh nhân không tự hủy. */
    @Column(nullable = false)
    private boolean daHuy;
}
