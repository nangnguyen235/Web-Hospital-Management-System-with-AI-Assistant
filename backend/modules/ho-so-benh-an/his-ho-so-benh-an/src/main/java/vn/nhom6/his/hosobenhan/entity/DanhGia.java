package vn.nhom6.his.hosobenhan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.nhom6.his.shared.kernel.ThoiGian;

/** {@code ba_danh_gia} (Rating) — mỗi phiếu một lần, không sửa, không xóa (BR-20). Không có {@code ngay_cap_nhat}. */
@Entity
@Table(name = "ba_danh_gia")
@Getter
@Setter
@NoArgsConstructor
public class DanhGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "text")
    private String nhanXet;

    @Column(nullable = false, columnDefinition = "tinyint unsigned")
    private int soDiem;

    @Column(nullable = false)
    private Long benhNhanId;

    @Column(nullable = false)
    private Long bacSiId;

    @Column(nullable = false)
    private Long phieuDatKhamId;

    @Column(nullable = false)
    private boolean daXoa;

    @Column(nullable = false, updatable = false)
    private LocalDateTime ngayTao;

    @PrePersist
    void truocKhiThem() {
        if (ngayTao == null) {
            ngayTao = ThoiGian.bayGio();
        }
    }
}
