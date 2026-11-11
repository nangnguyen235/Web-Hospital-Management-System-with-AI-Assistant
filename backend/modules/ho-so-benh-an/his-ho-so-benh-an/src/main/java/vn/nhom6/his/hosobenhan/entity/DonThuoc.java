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

/** {@code ba_don_thuoc} (Prescription) — một đơn/bệnh án, tạo khi kê thuốc đầu tiên (BR-18). */
@Entity
@Table(name = "ba_don_thuoc")
@Getter
@Setter
@NoArgsConstructor
public class DonThuoc extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "char(12)")
    private String maDonThuoc;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal tongTien = BigDecimal.ZERO;

    @Column(nullable = false)
    private Long benhAnId;
}
