package vn.nhom6.his.nguoidung.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.nhom6.his.nguoidung.entity.BenhNhan;

public interface BenhNhanRepository extends JpaRepository<BenhNhan, Long> {

    Optional<BenhNhan> findFirstBySoCccd(String soCccd);

    Optional<BenhNhan> findFirstByMaBenhNhan(String maBenhNhan);

    boolean existsByMaBenhNhan(String maBenhNhan);

    long countByDaXoaFalse();

    @Query("""
            select b from BenhNhan b where (:tuKhoa is null or b.hoTen like %:tuKhoa% or b.maBenhNhan like %:tuKhoa%
              or b.soCccd like %:tuKhoa% or b.soDienThoai like %:tuKhoa%)
            """)
    Page<BenhNhan> timKiem(@Param("tuKhoa") String tuKhoa, Pageable pageable);
}
