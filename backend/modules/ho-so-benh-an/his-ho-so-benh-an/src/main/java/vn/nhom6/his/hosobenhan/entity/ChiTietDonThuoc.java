package vn.nhom6.his.hosobenhan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.nhom6.his.shared.kernel.BaseEntity;

/** {@code ba_chi_tiet_don_thuoc} (PrescriptionItem) — {@code thanh_tien} = gia × so_luong (bất biến 10). */
@Entity
@Table(name = "ba_chi_tiet_don_thuoc")
@Getter
@Setter
@NoArgsConstructor
public class ChiTietDonThuoc extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long donThuocId;

    @Column(nullable = false, columnDefinition = "char(12)")
    private String maDonThuoc;

    /** RL → dm_thuoc. */
    @Column(nullable = false)
    private Integer thuocId;

    @Column(nullable = false)
    private int soLuong;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal gia;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal thanhTien;

    @Column(nullable = false)
    private int soLanMoiNgay;

    @Column(nullable = false)
    private int soNgay;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal soLuongMoiLan;

    @Column(nullable = false)
    private boolean sang;

    @Column(nullable = false)
    private boolean trua;

    @Column(nullable = false)
    private boolean chieu;

    @Column(nullable = false)
    private boolean toi;

    private String huongDan;
}
