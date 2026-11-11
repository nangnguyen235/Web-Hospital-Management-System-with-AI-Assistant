package vn.nhom6.his.lichkham.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.nhom6.his.lichkham.constant.Buoi;
import vn.nhom6.his.shared.kernel.BaseEntity;

/** {@code lk_lich_dieu_duong} (NurseSchedule) — phân công theo thứ + buổi; chỉ để hiện, không chặn (BR-08). */
@Entity
@Table(name = "lk_lich_dieu_duong")
@Getter
@Setter
@NoArgsConstructor
public class LichDieuDuong extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long dieuDuongId;

    /** Bản sao tên như TheDuck để hiển thị không phải hỏi module khác. */
    @Column(nullable = false, length = 100)
    private String tenDieuDuong;

    @Column(nullable = false)
    private Integer phongId;

    @Column(nullable = false, length = 100)
    private String tenPhong;

    @Column(nullable = false, columnDefinition = "tinyint unsigned")
    private int thuTrongTuan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(30)")
    private Buoi buoi;
}
