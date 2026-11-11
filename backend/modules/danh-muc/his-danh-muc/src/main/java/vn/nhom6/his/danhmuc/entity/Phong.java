package vn.nhom6.his.danhmuc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.nhom6.his.contracts.danhmuc.LoaiPhong;
import vn.nhom6.his.shared.kernel.BaseEntity;

/**
 * {@code dm_phong} (Room). Phòng xét nghiệm giữ bộ đếm hàng trong ngày ({@code so_xn_*}), đặt lại lười theo
 * {@code ngay_dem_xn} (BR-17); {@code phien_ban} chống tranh chấp khi cấp số / gọi tiếp.
 */
@Entity
@Table(name = "dm_phong")
@Getter
@Setter
@NoArgsConstructor
public class Phong extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String tenPhong;

    @Column(length = 255)
    private String moTa;

    @Column(nullable = false)
    private Integer khoaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(30)")
    private LoaiPhong loaiPhong;

    /** Phòng xét nghiệm làm dịch vụ nào. */
    private Integer dichVuId;

    @Column(nullable = false)
    private int soXnDangGoi;

    @Column(nullable = false)
    private int soXnLonNhat;

    private LocalDate ngayDemXn;

    @Version
    @Column(nullable = false)
    private int phienBan;
}
