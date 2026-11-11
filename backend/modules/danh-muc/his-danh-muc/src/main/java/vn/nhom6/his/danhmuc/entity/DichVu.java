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
import vn.nhom6.his.contracts.danhmuc.LoaiDichVu;
import vn.nhom6.his.shared.kernel.BaseEntity;

/** {@code dm_dich_vu} (MedicalService). Giá khám nằm ở dịch vụ, không ở bác sĩ. */
@Entity
@Table(name = "dm_dich_vu")
@Getter
@Setter
@NoArgsConstructor
public class DichVu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String tenDichVu;

    @Column(columnDefinition = "text")
    private String moTa;

    @Column(nullable = false)
    private Integer khoaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(30)")
    private LoaiDichVu loaiDichVu;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal gia;
}
