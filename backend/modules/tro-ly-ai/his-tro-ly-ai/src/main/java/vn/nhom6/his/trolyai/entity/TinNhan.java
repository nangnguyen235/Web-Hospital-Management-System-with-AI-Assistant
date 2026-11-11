package vn.nhom6.his.trolyai.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

/** {@code tl_tin_nhan} (Message) — AI 1, giai đoạn AI. */
@Entity
@Table(name = "tl_tin_nhan")
@Getter
@Setter
@NoArgsConstructor
public class TinNhan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long hoiThoaiId;

    @Column(nullable = false, columnDefinition = "text")
    private String noiDung;

    @Column(nullable = false)
    private int soThuTu;

    /** TheDuck {@code isSupportAgent}. */
    @Column(nullable = false)
    private boolean laTroLy;

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
