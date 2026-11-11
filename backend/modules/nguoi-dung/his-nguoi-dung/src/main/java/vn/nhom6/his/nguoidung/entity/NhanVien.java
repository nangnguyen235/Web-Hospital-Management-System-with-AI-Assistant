package vn.nhom6.his.nguoidung.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.nhom6.his.nguoidung.constant.GioiTinh;
import vn.nhom6.his.nguoidung.constant.LoaiNhanVien;
import vn.nhom6.his.shared.kernel.BaseEntity;
import vn.nhom6.his.shared.kernel.VaiTro;

/**
 * {@code tk_nhan_vien} (Staff) — một bảng cho mọi loại nhân viên, y hệt TheDuck: SINGLE_TABLE với cột phân loại
 * {@code loai_nhan_vien} thay {@code dtype}; cột riêng của bác sĩ để NULL ở loại khác (mo-hinh-du-lieu.md mục 2.1).
 */
@Entity
@Table(name = "tk_nhan_vien")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "loai_nhan_vien", discriminatorType = DiscriminatorType.STRING, columnDefinition = "varchar(30)")
@Getter
@Setter
@NoArgsConstructor
public abstract class NhanVien extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String hoTen;

    @Column(nullable = false, length = 20)
    private String soDienThoai;

    @Column(length = 12)
    private String soCccd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(30)")
    private GioiTinh gioiTinh;

    private LocalDate ngaySinh;

    private String anhDaiDienKey;

    /** RL → dm_khoa. */
    private Integer khoaId;

    /** Bác sĩ = trưởng khoa, điều dưỡng = điều dưỡng trưởng. */
    @Column(nullable = false)
    private boolean laTruongKhoa;

    /** Cột phân loại, chỉ đọc (Hibernate tự ghi theo lớp con) — để lọc theo loại trong truy vấn. */
    @Enumerated(EnumType.STRING)
    @Column(name = "loai_nhan_vien", insertable = false, updatable = false, columnDefinition = "varchar(30)")
    @Setter(lombok.AccessLevel.NONE)
    private LoaiNhanVien loaiNhanVien;

    /** Vai trò suy ra từ loại nhân viên (BR-01). */
    public abstract VaiTro vaiTro();
}
