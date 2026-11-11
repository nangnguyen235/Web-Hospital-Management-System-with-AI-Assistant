package vn.nhom6.his.trolyai.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.nhom6.his.shared.kernel.ThoiGian;
import vn.nhom6.his.trolyai.constant.TrangThaiHoiThoai;

/** {@code tl_hoi_thoai} (Conversation) — AI 1, giai đoạn AI (chưa có nghiệp vụ). Bảng không có {@code da_xoa}. */
@Entity
@Table(name = "tl_hoi_thoai")
@Getter
@Setter
@NoArgsConstructor
public class HoiThoai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long taiKhoanId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(30)")
    private TrangThaiHoiThoai trangThai = TrangThaiHoiThoai.DANG_MO;

    @Column(nullable = false)
    private int soThuTuLonNhat;

    @Column(nullable = false, updatable = false)
    private LocalDateTime ngayTao;

    @Column(nullable = false)
    private LocalDateTime ngayCapNhat;

    @PrePersist
    void truocKhiThem() {
        ngayTao = ThoiGian.bayGio();
        ngayCapNhat = ngayTao;
    }

    @PreUpdate
    void truocKhiSua() {
        ngayCapNhat = ThoiGian.bayGio();
    }
}
