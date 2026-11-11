package vn.nhom6.his.lichkham.entity;

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
import vn.nhom6.his.lichkham.constant.Buoi;
import vn.nhom6.his.shared.kernel.BaseEntity;

/** {@code lk_lich_kham_bac_si} (DoctorSchedule) — {@code phien_ban} chống tranh chấp khi "Gọi tiếp" (BR-15). */
@Entity
@Table(name = "lk_lich_kham_bac_si")
@Getter
@Setter
@NoArgsConstructor
public class LichKhamBacSi extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** RL → tk_nhan_vien (bác sĩ). */
    @Column(nullable = false)
    private Long bacSiId;

    /** RL → dm_phong (PHONG_KHAM). */
    @Column(nullable = false)
    private Integer phongId;

    /** RL → dm_dich_vu (KHAM_BENH). */
    @Column(nullable = false)
    private Integer dichVuId;

    /** = số lượt mỗi khung × 4. */
    @Column(nullable = false)
    private int soLuot;

    @Column(nullable = false)
    private int soDangGoi;

    /** 1 = Chủ nhật … 7 = Thứ 7 (như TheDuck). */
    @Column(nullable = false, columnDefinition = "tinyint unsigned")
    private int thuTrongTuan;

    @Column(nullable = false)
    private LocalDate ngay;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(30)")
    private Buoi buoi;

    @Version
    @Column(nullable = false)
    private int phienBan;
}
