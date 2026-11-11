package vn.nhom6.his.lichkham.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.nhom6.his.lichkham.constant.Buoi;
import vn.nhom6.his.lichkham.entity.LichDieuDuong;

public interface LichDieuDuongRepository extends JpaRepository<LichDieuDuong, Long> {

    Optional<LichDieuDuong> findFirstByDieuDuongIdAndThuTrongTuanAndBuoiAndDaXoaFalse(Long dieuDuongId, int thu, Buoi buoi);

    Optional<LichDieuDuong> findFirstByPhongIdAndThuTrongTuanAndBuoiAndDaXoaFalse(Integer phongId, int thu, Buoi buoi);

    List<LichDieuDuong> findByDieuDuongIdAndDaXoaFalseOrderByThuTrongTuanAscBuoiAsc(Long dieuDuongId);

    List<LichDieuDuong> findByPhongIdInAndDaXoaFalseOrderByThuTrongTuanAscBuoiAsc(Collection<Integer> phongIds);

    List<LichDieuDuong> findByDieuDuongIdAndThuTrongTuanAndDaXoaFalse(Long dieuDuongId, int thu);
}
