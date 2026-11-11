package vn.nhom6.his.nguoidung.entity;

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
import vn.nhom6.his.nguoidung.constant.GioiTinh;
import vn.nhom6.his.shared.kernel.BaseEntity;

/** {@code tk_benh_nhan} (Patient) — người bệnh thật, định danh bằng CCCD; chỉ tạo khi tiếp nhận (BR-03, BR-13). */
@Entity
@Table(name = "tk_benh_nhan")
@Getter
@Setter
@NoArgsConstructor
public class BenhNhan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String maBenhNhan;

    @Column(nullable = false, length = 100)
    private String hoTen;

    @Column(length = 20)
    private String soDienThoai;

    @Column(length = 12)
    private String soCccd;

    @Column(nullable = false)
    private LocalDate ngaySinh;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(30)")
    private GioiTinh gioiTinh;
}
