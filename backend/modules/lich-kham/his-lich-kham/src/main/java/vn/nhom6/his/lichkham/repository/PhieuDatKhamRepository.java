package vn.nhom6.his.lichkham.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.nhom6.his.lichkham.entity.PhieuDatKham;

public interface PhieuDatKhamRepository extends JpaRepository<PhieuDatKham, Long> {

    /** Lượt đặt đã thanh toán (BR-25). */
    long countByDaXoaFalse();

    List<PhieuDatKham> findByGiaoDichId(Long giaoDichId);

    /**
     * Handler GiaoDichDaXuLy: khóa phiếu của giao dịch trước khi kiểm cờ — IPN gọi lại làm 2 handler chạy song song,
     * người sau chờ rồi thấy phiếu đã xác nhận (chống xử lý lại thật sự, không chỉ kiểm cờ).
     */
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PhieuDatKham p where p.giaoDichId = :gd order by p.id")
    List<PhieuDatKham> khoaTheoGiaoDich(@Param("gd") Long giaoDichId);

    Optional<PhieuDatKham> findByMaPhieu(String maPhieu);

    boolean existsByMaPhieu(String maPhieu);

    List<PhieuDatKham> findByHoSoIdOrderByIdDesc(Long hoSoId);

    List<PhieuDatKham> findByKhungGioIdIn(Collection<Long> khungGioIds);

    /** Phiếu chờ thanh toán còn hạn 15 phút đang giữ chỗ của khung (BR-10). */
    @Query("""
            select count(p) from PhieuDatKham p where p.khungGioId = :khung and p.daXoa = true and p.daHuy = false
              and p.ngayTao > :tu
            """)
    long demDangGiuCho(@Param("khung") Long khungGioId, @Param("tu") LocalDateTime tu);

    /** Hồ sơ đã có phiếu còn hiệu lực ở lịch (đã thanh toán, hoặc đang giữ chỗ còn hạn) — BR-09, BR-14. */
    @Query("""
            select p from PhieuDatKham p, KhungGio k where p.khungGioId = k.id and k.lichKhamId = :lich and p.hoSoId = :hoSo
              and p.daHuy = false and (p.daXoa = false or p.ngayTao > :tu)
            """)
    List<PhieuDatKham> phieuHieuLuc(@Param("hoSo") Long hoSoId, @Param("lich") Long lichKhamId, @Param("tu") LocalDateTime tu);

    /** Số thứ tự lớn nhất đã cấp của lịch (BR-15). */
    @Query("select coalesce(max(p.soThuTu), 0) from PhieuDatKham p, KhungGio k where p.khungGioId = k.id and k.lichKhamId = :lich and p.daXoa = false")
    int soLonNhat(@Param("lich") Long lichKhamId);

    /** Phiếu đã xác nhận có số trong (tu, den] của lịch — để thông báo đến lượt. */
    @Query("""
            select p from PhieuDatKham p, KhungGio k where p.khungGioId = k.id and k.lichKhamId = :lich and p.daXoa = false
              and p.soThuTu > :tu and p.soThuTu <= :den order by p.soThuTu
            """)
    List<PhieuDatKham> trongKhoangSo(@Param("lich") Long lichKhamId, @Param("tu") int tu, @Param("den") int den);

    /** Lịch còn phiếu đã thanh toán hoặc đang giữ chỗ (BR-07 — không xóa lịch). */
    @Query("""
            select count(p) > 0 from PhieuDatKham p, KhungGio k where p.khungGioId = k.id and k.lichKhamId = :lich
              and p.daHuy = false and (p.daXoa = false or p.ngayTao > :tu)
            """)
    boolean lichConPhieu(@Param("lich") Long lichKhamId, @Param("tu") LocalDateTime tu);

    @Query("select count(p) > 0 from PhieuDatKham p, KhungGio k where p.khungGioId = k.id and k.lichKhamId = :lich")
    boolean lichDaCoPhieu(@Param("lich") Long lichKhamId);

    /** Thống kê lượt đặt đã thanh toán theo ngày khám (BR-25). */
    @Query("select count(p) from PhieuDatKham p, KhungGio k where p.khungGioId = k.id and p.daXoa = false and k.ngay between :tu and :den")
    long demLuotDat(@Param("tu") LocalDate tu, @Param("den") LocalDate den);

    @Query("select p from PhieuDatKham p, KhungGio k where p.khungGioId = k.id and p.daXoa = false and k.ngay between :tu and :den")
    List<PhieuDatKham> daThanhToanTrongKhoang(@Param("tu") LocalDate tu, @Param("den") LocalDate den);
}
