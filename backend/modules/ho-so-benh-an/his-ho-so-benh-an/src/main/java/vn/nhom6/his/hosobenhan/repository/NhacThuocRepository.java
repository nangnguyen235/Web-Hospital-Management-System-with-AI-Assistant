package vn.nhom6.his.hosobenhan.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.nhom6.his.hosobenhan.entity.NhacThuoc;

public interface NhacThuocRepository extends JpaRepository<NhacThuoc, Long> {

    Optional<NhacThuoc> findFirstByChiTietDonThuocIdAndHoSoIdAndDaXoaFalse(Long chiTietDonThuocId, Long hoSoId);

    List<NhacThuoc> findByHoSoIdAndDaXoaFalseOrderByIdDesc(Long hoSoId);
}
