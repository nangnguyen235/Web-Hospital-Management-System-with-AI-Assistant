package vn.nhom6.his.danhmuc.entity;

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
import vn.nhom6.his.danhmuc.constant.DonViThuoc;
import vn.nhom6.his.shared.kernel.BaseEntity;

/** {@code dm_thuoc} (Medicine). Chỉ kê, không bán: {@code so_luong} không bị trừ khi kê (BR-18). */
@Entity
@Table(name = "dm_thuoc")
@Getter
@Setter
@NoArgsConstructor
public class Thuoc extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String tenThuoc;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal gia;

    @Column(nullable = false)
    private int soLuong;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(30)")
    private DonViThuoc donVi;
}
