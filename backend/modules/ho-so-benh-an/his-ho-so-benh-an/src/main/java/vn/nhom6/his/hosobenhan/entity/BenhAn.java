package vn.nhom6.his.hosobenhan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.nhom6.his.hosobenhan.constant.TrangThaiBenhAn;
import vn.nhom6.his.shared.kernel.BaseEntity;

/** {@code ba_benh_an} (MedicalExaminationRecord) — {@code phieu_dat_kham_id} UNIQUE chống tạo trùng (BR-13). */
@Entity
@Table(name = "ba_benh_an")
@Getter
@Setter
@NoArgsConstructor
public class BenhAn extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long phieuDatKhamId;

    @Column(nullable = false)
    private Long lichKhamId;

    @Column(nullable = false)
    private Long benhNhanId;

    @Column(nullable = false)
    private Long hoSoId;

    @Column(columnDefinition = "text")
    private String trieuChung;

    @Column(columnDefinition = "text")
    private String chanDoan;

    private LocalDate ngayTaiKham;

    /** Lịch cũ khi bác sĩ khác cùng khoa nhận khám thay (BR-16). */
    private Long lichKhamTruocId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(30)")
    private TrangThaiBenhAn trangThai = TrangThaiBenhAn.CHO_KHAM;

    /** AI 2 (giai đoạn AI). */
    private String fileGhiAmKey;
}
