package vn.nhom6.his.danhmuc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.nhom6.his.shared.kernel.BaseEntity;

/** {@code dm_khoa} (Department). Trưởng khoa, điều dưỡng trưởng là cờ ở {@code tk_nhan_vien}. */
@Entity
@Table(name = "dm_khoa")
@Getter
@Setter
@NoArgsConstructor
public class Khoa extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String tenKhoa;

    @Column(columnDefinition = "text")
    private String moTa;

    /** Thêm cho AI 1 (gợi ý khoa theo triệu chứng). */
    @Column(columnDefinition = "text")
    private String tuKhoaTrieuChung;

    public Khoa(String tenKhoa, String moTa, String tuKhoaTrieuChung) {
        capNhat(tenKhoa, moTa, tuKhoaTrieuChung);
    }

    public void capNhat(String tenKhoa, String moTa, String tuKhoaTrieuChung) {
        this.tenKhoa = tenKhoa.trim();
        this.moTa = moTa;
        this.tuKhoaTrieuChung = tuKhoaTrieuChung;
    }
}
