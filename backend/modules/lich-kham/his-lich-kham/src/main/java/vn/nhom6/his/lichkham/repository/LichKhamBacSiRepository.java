package vn.nhom6.his.lichkham.repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.nhom6.his.lichkham.constant.Buoi;
import vn.nhom6.his.lichkham.entity.LichKhamBacSi;

public interface LichKhamBacSiRepository extends JpaRepository<LichKhamBacSi, Long> {

    Optional<LichKhamBacSi> findByIdAndDaXoaFalse(Long id);

    boolean existsByBacSiIdAndDaXoaFalseAndNgayGreaterThanEqual(Long bacSiId, LocalDate ngay);

    boolean existsByPhongIdAndNgayAndBuoiAndDaXoaFalseAndIdNot(Integer phongId, LocalDate ngay, Buoi buoi, Long id);

    boolean existsByBacSiIdAndNgayAndBuoiAndDaXoaFalseAndIdNot(Long bacSiId, LocalDate ngay, Buoi buoi, Long id);

    /** Khóa dòng khi "Gọi tiếp" (BR-15). */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from LichKhamBacSi l where l.id = :id")
    Optional<LichKhamBacSi> khoaGhi(@Param("id") Long id);

    @Query("""
            select l from LichKhamBacSi l where l.daXoa = false and l.ngay between :tu and :den
              and (:phongId is null or l.phongId = :phongId) and (:bacSiId is null or l.bacSiId = :bacSiId)
            order by l.ngay, l.buoi, l.phongId
            """)
    List<LichKhamBacSi> timKiem(@Param("tu") LocalDate tu, @Param("den") LocalDate den, @Param("phongId") Integer phongId,
                                @Param("bacSiId") Long bacSiId);

    /** Lịch của một khoa (theo danh sách phòng của khoa, danh sách không rỗng). */
    @Query("""
            select l from LichKhamBacSi l where l.daXoa = false and l.ngay between :tu and :den and l.phongId in :phongIds
              and (:phongId is null or l.phongId = :phongId) and (:bacSiId is null or l.bacSiId = :bacSiId)
            order by l.ngay, l.buoi, l.phongId
            """)
    List<LichKhamBacSi> timKiemTrongPhong(@Param("tu") LocalDate tu, @Param("den") LocalDate den,
                                          @Param("phongIds") Collection<Integer> phongIds, @Param("phongId") Integer phongId,
                                          @Param("bacSiId") Long bacSiId);

    List<LichKhamBacSi> findByBacSiIdAndNgayAndDaXoaFalseOrderByBuoiAsc(Long bacSiId, LocalDate ngay);

    List<LichKhamBacSi> findByPhongIdInAndNgayAndDaXoaFalseOrderByBuoiAsc(Collection<Integer> phongIds, LocalDate ngay);

    List<LichKhamBacSi> findByBacSiIdInAndNgayAndDaXoaFalse(Collection<Long> bacSiIds, LocalDate ngay);
}
