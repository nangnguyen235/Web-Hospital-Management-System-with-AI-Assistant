package vn.nhom6.his.nguoidung.repository;

import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.nhom6.his.nguoidung.constant.TrangThaiThongBao;
import vn.nhom6.his.nguoidung.entity.ThongBao;

public interface ThongBaoRepository extends JpaRepository<ThongBao, Long> {

    Page<ThongBao> findByTaiKhoanIdAndTrangThaiNot(Long taiKhoanId, TrangThaiThongBao trangThai, Pageable pageable);

    long countByTaiKhoanIdAndTrangThaiIn(Long taiKhoanId, Collection<TrangThaiThongBao> trangThai);

    /** App lấy về → CHUA_NHAN chuyển DA_NHAN (BR-22). */
    @Modifying
    @Query("update ThongBao t set t.trangThai = 'DA_NHAN' where t.taiKhoanId = :tk and t.trangThai = 'CHUA_NHAN'")
    int danhDauDaNhan(@Param("tk") Long taiKhoanId);
}
