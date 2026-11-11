package vn.nhom6.his.nguoidung.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.nhom6.his.nguoidung.entity.TaiKhoan;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Long> {

    Optional<TaiKhoan> findFirstByEmailOrSoDienThoai(String email, String soDienThoai);

    Optional<TaiKhoan> findByIdAndDaXoaFalse(Long id);

    Optional<TaiKhoan> findFirstByEmailAndDaXoaFalse(String email);

    Optional<TaiKhoan> findByNhanVienId(Long nhanVienId);

    boolean existsByEmail(String email);

    boolean existsBySoDienThoai(String soDienThoai);

    boolean existsBySoDienThoaiAndIdNot(String soDienThoai, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    /** Admin lọc tài khoản (FR-AD-02): {@code loai} = null (tất cả) | BENH_NHAN | NHAN_VIEN. */
    @Query("""
            select t from TaiKhoan t
            where (:tuKhoa is null or t.hoTen like %:tuKhoa% or t.email like %:tuKhoa% or t.soDienThoai like %:tuKhoa%)
              and (:daXoa is null or t.daXoa = :daXoa)
              and (:loai is null or (:loai = 'BENH_NHAN' and t.nhanVienId is null) or (:loai = 'NHAN_VIEN' and t.nhanVienId is not null))
            """)
    Page<TaiKhoan> timKiem(@Param("tuKhoa") String tuKhoa, @Param("loai") String loai, @Param("daXoa") Boolean daXoa,
                           Pageable pageable);
}
