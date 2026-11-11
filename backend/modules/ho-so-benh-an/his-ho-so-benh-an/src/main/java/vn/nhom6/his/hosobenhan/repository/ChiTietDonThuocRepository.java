package vn.nhom6.his.hosobenhan.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.nhom6.his.hosobenhan.entity.ChiTietDonThuoc;

public interface ChiTietDonThuocRepository extends JpaRepository<ChiTietDonThuoc, Long> {

    List<ChiTietDonThuoc> findByDonThuocIdAndDaXoaFalseOrderByIdAsc(Long donThuocId);
}
