package vn.nhom6.his.nguoidung.entity;

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
import vn.nhom6.his.nguoidung.constant.TrangThaiThongBao;
import vn.nhom6.his.shared.kernel.BaseEntity;

/** {@code tk_thong_bao} (Notification) — in-app, không push (BR-22). */
@Entity
@Table(name = "tk_thong_bao")
@Getter
@Setter
@NoArgsConstructor
public class ThongBao extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long taiKhoanId;

    @Column(nullable = false, length = 200)
    private String tieuDe;

    @Column(nullable = false, columnDefinition = "text")
    private String noiDung;

    /** JSON: hành động + id liên quan (TheDuck {@code data}). */
    @Column(length = 1000)
    private String duLieu;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(30)")
    private TrangThaiThongBao trangThai = TrangThaiThongBao.CHUA_NHAN;
}
