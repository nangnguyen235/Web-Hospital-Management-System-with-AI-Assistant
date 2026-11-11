package vn.nhom6.his.lichkham.repository;

import jakarta.persistence.LockModeType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.nhom6.his.lichkham.entity.KhungGio;

public interface KhungGioRepository extends JpaRepository<KhungGio, Long> {

    List<KhungGio> findByLichKhamIdOrderByChiSoKhungAsc(Long lichKhamId);

    List<KhungGio> findByLichKhamIdInOrderByChiSoKhungAsc(Collection<Long> lichKhamIds);

    Optional<KhungGio> findByLichKhamIdAndChiSoKhung(Long lichKhamId, int chiSoKhung);

    /**
     * Giữ chỗ (BR-10): khóa ghi dòng khung (và tăng phien_ban khi commit) — hai người lấy chỗ cuối thì người sau chờ,
     * đếm lại thấy hết chỗ. Khóa ngay từ đầu tránh deadlock "FK khóa chia sẻ rồi cùng nâng lên khóa ghi" của InnoDB.
     */
    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    @Query("select k from KhungGio k where k.id = :id")
    Optional<KhungGio> giuCho(@Param("id") Long id);

    /** Xác nhận, cấp số (BR-11): khóa dòng để handler sự kiện không tranh nhau. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select k from KhungGio k where k.id = :id")
    Optional<KhungGio> khoaGhi(@Param("id") Long id);
}
