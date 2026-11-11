package vn.nhom6.his.hosobenhan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.nhom6.his.shared.kernel.BaseEntity;

/** {@code ba_nhac_thuoc} (MedicineReminder) — BR-21. */
@Entity
@Table(name = "ba_nhac_thuoc")
@Getter
@Setter
@NoArgsConstructor
public class NhacThuoc extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long chiTietDonThuocId;

    /** RL → tk_ho_so_benh_nhan. */
    @Column(nullable = false)
    private Long hoSoId;

    @Column(nullable = false)
    private LocalDate ngayBatDau;

    @Column(nullable = false)
    private LocalDateTime ngayKetThuc;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal tongSoLuong;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal soLuongConLai;
}
