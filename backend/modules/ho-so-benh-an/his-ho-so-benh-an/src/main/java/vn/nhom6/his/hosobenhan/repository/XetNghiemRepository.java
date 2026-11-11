package vn.nhom6.his.hosobenhan.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.nhom6.his.hosobenhan.entity.XetNghiem;

public interface XetNghiemRepository extends JpaRepository<XetNghiem, Long> {

    List<XetNghiem> findByBenhAnIdAndDaXoaFalseOrderByIdAsc(Long benhAnId);

    List<XetNghiem> findByPhongIdAndNgayAndDaXoaFalseOrderBySoThuTuAsc(Integer phongId, LocalDate ngay);

    Optional<XetNghiem> findByMaXetNghiemAndDaXoaFalse(String maXetNghiem);

    Optional<XetNghiem> findByGiaoDichId(Long giaoDichId);

    boolean existsByMaXetNghiem(String ma);

    List<XetNghiem> findByDaThanhToanTrueAndNgayBetween(LocalDate tu, LocalDate den);
}
