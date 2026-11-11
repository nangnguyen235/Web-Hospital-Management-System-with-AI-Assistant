package vn.nhom6.his.lichkham.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.nhom6.his.shared.kernel.BaseEntity;

/**
 * {@code lk_khung_gio} (TimeSlot) — 4 khung/buổi; {@code so_bat_dau} = (chi_so_khung % 4) × so_luong_toi_da + 1 (BR-06);
 * {@code phien_ban} khóa lạc quan khi giữ chỗ / xác nhận (BR-10, BR-11).
 */
@Entity
@Table(name = "lk_khung_gio")
@Getter
@Setter
@NoArgsConstructor
public class KhungGio extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long lichKhamId;

    @Column(nullable = false)
    private int soLuongToiDa;

    /** Số phiếu đã thanh toán — chỉ tăng. */
    @Column(nullable = false)
    private int soLuongDaDat;

    @Column(nullable = false)
    private int soBatDau;

    /** 0–3 sáng (07:00–11:00), 4–7 chiều (13:00–17:00). */
    @Column(nullable = false, columnDefinition = "tinyint unsigned")
    private int chiSoKhung;

    @Column(nullable = false)
    private LocalDate ngay;

    @Version
    @Column(nullable = false)
    private int phienBan;
}
