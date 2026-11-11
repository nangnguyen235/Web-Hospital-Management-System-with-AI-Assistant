package vn.nhom6.his.nguoidung.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.nhom6.his.nguoidung.constant.HocHam;
import vn.nhom6.his.nguoidung.constant.LoaiNhanVien;
import vn.nhom6.his.nguoidung.entity.BacSi;
import vn.nhom6.his.nguoidung.entity.NhanVien;

public interface NhanVienRepository extends JpaRepository<NhanVien, Long> {

    @Query("select count(q) > 0 from QuanTri q")
    boolean coQuanTri();

    @Query("select count(b) from BacSi b where b.daXoa = false")
    long demBacSi();

    /** Admin lọc nhân viên (FR-AD-03). */
    @Query("""
            select n from NhanVien n where (:tuKhoa is null or n.hoTen like %:tuKhoa% or n.soDienThoai like %:tuKhoa%)
              and (:loai is null or n.loaiNhanVien = :loai) and (:khoaId is null or n.khoaId = :khoaId)
              and (:daXoa is null or n.daXoa = :daXoa)
            """)
    Page<NhanVien> timKiem(@Param("tuKhoa") String tuKhoa, @Param("loai") LoaiNhanVien loai,
                           @Param("khoaId") Integer khoaId, @Param("daXoa") Boolean daXoa, Pageable pageable);

    /** Tìm bác sĩ công khai theo tên, khoa, học hàm (FR-BN-07). */
    @Query("""
            select b from BacSi b where b.daXoa = false and b.khoaId is not null
              and (:tuKhoa is null or b.hoTen like %:tuKhoa%) and (:khoaId is null or b.khoaId = :khoaId)
              and (:hocHam is null or b.hocHam = :hocHam)
            """)
    Page<BacSi> timBacSi(@Param("tuKhoa") String tuKhoa, @Param("khoaId") Integer khoaId, @Param("hocHam") HocHam hocHam,
                         Pageable pageable);

    List<NhanVien> findByKhoaIdAndLoaiNhanVienAndDaXoaFalseOrderByHoTenAsc(Integer khoaId, LoaiNhanVien loai);

    List<NhanVien> findByKhoaIdAndLoaiNhanVienAndLaTruongKhoaTrue(Integer khoaId, LoaiNhanVien loai);
}
