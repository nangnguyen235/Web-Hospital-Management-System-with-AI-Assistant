package vn.nhom6.his.vienphi.repository;

import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.nhom6.his.contracts.vienphi.LoaiThanhToan;
import vn.nhom6.his.vienphi.constant.TrangThaiGiaoDich;
import vn.nhom6.his.vienphi.entity.GiaoDich;

public interface GiaoDichRepository extends JpaRepository<GiaoDich, Long> {

    /** Khóa dòng khi xử lý return/IPN: hai lần gọi đồng thời không cùng đổi trạng thái (BR-10). */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select g from GiaoDich g where g.id = :id")
    Optional<GiaoDich> khoaTheoId(@Param("id") Long id);

    @Query("""
            select g from GiaoDich g where (:tu is null or g.ngayTao >= :tu) and (:den is null or g.ngayTao < :den)
              and (:trangThai is null or g.trangThai = :trangThai) and (:loai is null or g.loaiThanhToan = :loai)
              and (:taiKhoanId is null or g.taiKhoanId = :taiKhoanId)
            """)
    Page<GiaoDich> timKiem(@Param("tu") LocalDateTime tu, @Param("den") LocalDateTime den,
                           @Param("trangThai") TrangThaiGiaoDich trangThai, @Param("loai") LoaiThanhToan loai,
                           @Param("taiKhoanId") Long taiKhoanId, Pageable pageable);

    @Query("""
            select coalesce(sum(g.soTien), 0) from GiaoDich g where g.trangThai = 'THANH_CONG'
              and (:tu is null or g.ngayTao >= :tu) and (:den is null or g.ngayTao < :den)
            """)
    BigDecimal doanhThu(@Param("tu") LocalDateTime tu, @Param("den") LocalDateTime den);
}
