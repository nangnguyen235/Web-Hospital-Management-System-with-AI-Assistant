package vn.nhom6.his.nguoidung.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.nhom6.his.nguoidung.constant.GioiTinh;
import vn.nhom6.his.nguoidung.entity.HoSoBenhNhan;

public interface HoSoBenhNhanRepository extends JpaRepository<HoSoBenhNhan, Long> {

    List<HoSoBenhNhan> findByTaiKhoanIdAndDaXoaFalseOrderByIdAsc(Long taiKhoanId);

    List<HoSoBenhNhan> findByTaiKhoanIdOrderByIdAsc(Long taiKhoanId);

    long countByTaiKhoanIdAndDaXoaFalse(Long taiKhoanId);

    Optional<HoSoBenhNhan> findByIdAndDaXoaFalse(Long id);

    List<HoSoBenhNhan> findByBenhNhanIdAndDaXoaFalse(Long benhNhanId);

    List<HoSoBenhNhan> findByBenhNhanId(Long benhNhanId);

    /** Tìm lại hồ sơ theo họ tên + năm sinh + giới tính (BR-03). */
    @Query("""
            select h from HoSoBenhNhan h where h.daXoa = false and lower(h.hoTen) = lower(:hoTen)
              and h.ngaySinh between :tu and :den and h.gioiTinh = :gioiTinh order by h.id
            """)
    List<HoSoBenhNhan> timLai(@Param("hoTen") String hoTen, @Param("tu") LocalDate tu, @Param("den") LocalDate den,
                              @Param("gioiTinh") GioiTinh gioiTinh);

    /** Điều dưỡng / quầy: tra theo tên và/hoặc CCCD, mọi hồ sơ kể cả không tài khoản (BR-13, BR-14). */
    @Query("""
            select h from HoSoBenhNhan h where h.daXoa = false
              and (:hoTen is null or lower(h.hoTen) like lower(concat('%', :hoTen, '%')))
              and (:soCccd is null or h.soCccd = :soCccd)
            order by h.hoTen
            """)
    Page<HoSoBenhNhan> traCuu(@Param("hoTen") String hoTen, @Param("soCccd") String soCccd, Pageable pageable);

    /** Admin: mọi hồ sơ (FR-AD-08). */
    @Query("""
            select h from HoSoBenhNhan h where (:tuKhoa is null or h.hoTen like %:tuKhoa% or h.soCccd like %:tuKhoa%
              or h.soDienThoai like %:tuKhoa% or h.email like %:tuKhoa%)
            """)
    Page<HoSoBenhNhan> timKiem(@Param("tuKhoa") String tuKhoa, Pageable pageable);
}
