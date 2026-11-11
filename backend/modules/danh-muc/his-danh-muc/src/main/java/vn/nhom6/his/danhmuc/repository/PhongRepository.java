package vn.nhom6.his.danhmuc.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.nhom6.his.contracts.danhmuc.LoaiPhong;
import vn.nhom6.his.danhmuc.entity.Phong;

public interface PhongRepository extends JpaRepository<Phong, Integer> {

    @Query("""
            select p from Phong p where p.daXoa = false and (:khoaId is null or p.khoaId = :khoaId)
              and (:loai is null or p.loaiPhong = :loai) order by p.tenPhong
            """)
    List<Phong> danhSach(@Param("khoaId") Integer khoaId, @Param("loai") LoaiPhong loai);

    @Query("""
            select p from Phong p where (:tuKhoa is null or p.tenPhong like %:tuKhoa%)
              and (:khoaId is null or p.khoaId = :khoaId)
            """)
    Page<Phong> timKiem(@Param("tuKhoa") String tuKhoa, @Param("khoaId") Integer khoaId, Pageable pageable);

    boolean existsByTenPhongAndDaXoaFalseAndIdNot(String tenPhong, Integer id);

    /** Khóa dòng khi tăng bộ đếm xét nghiệm (BR-17) — hai bác sĩ chỉ định cùng lúc không trùng số. */
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Phong p where p.id = :id")
    java.util.Optional<Phong> khoaGhi(@Param("id") Integer id);

    /** Phòng xét nghiệm của dịch vụ, chưa xóa — chọn phòng ít hàng nhất khi cấp số (BR-17). */
    List<Phong> findByDichVuIdAndLoaiPhongAndDaXoaFalse(Integer dichVuId, LoaiPhong loaiPhong);
}
