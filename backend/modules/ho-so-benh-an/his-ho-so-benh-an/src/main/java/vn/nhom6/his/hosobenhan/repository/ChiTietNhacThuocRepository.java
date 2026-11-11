package vn.nhom6.his.hosobenhan.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.nhom6.his.hosobenhan.entity.ChiTietNhacThuoc;

public interface ChiTietNhacThuocRepository extends JpaRepository<ChiTietNhacThuoc, Long> {

    List<ChiTietNhacThuoc> findByNhacThuocIdAndDaXoaFalseOrderByThoiDiemNhacAsc(Long nhacThuocId);

    List<ChiTietNhacThuoc> findByTaiKhoanIdAndDaXoaFalseAndThoiDiemNhacBetweenOrderByThoiDiemNhacAsc(
            Long taiKhoanId, LocalDateTime tu, LocalDateTime den);
}
