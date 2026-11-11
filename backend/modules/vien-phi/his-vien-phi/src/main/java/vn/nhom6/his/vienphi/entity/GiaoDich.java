package vn.nhom6.his.vienphi.entity;

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
import vn.nhom6.his.contracts.vienphi.LoaiThanhToan;
import vn.nhom6.his.shared.kernel.BaseEntity;
import vn.nhom6.his.vienphi.constant.TrangThaiGiaoDich;

/** {@code vp_giao_dich} (Transaction) — {@code id} dùng làm {@code vnp_TxnRef}; chỉ VNPAY (BR-10). */
@Entity
@Table(name = "vp_giao_dich")
@Getter
@Setter
@NoArgsConstructor
public class GiaoDich extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal soTien;

    /** Phí cổng: 15.000 đặt khám, 1.500 xét nghiệm (như TheDuck). */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal phi;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(30)")
    private TrangThaiGiaoDich trangThai = TrangThaiGiaoDich.DANG_XU_LY;

    @Column(length = 20)
    private String maNganHang;

    @Column(nullable = false, length = 20)
    private String phuongThuc = "VNPAY";

    /** Origin của frontend để chuyển về trang kết quả. */
    private String nguon;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(30)")
    private LoaiThanhToan loaiThanhToan;

    /** NULL khi khách quầy chưa có tài khoản. */
    private Long taiKhoanId;
}
