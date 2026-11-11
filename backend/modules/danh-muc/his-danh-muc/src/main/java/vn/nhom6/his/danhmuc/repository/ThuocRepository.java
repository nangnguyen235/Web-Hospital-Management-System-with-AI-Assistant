package vn.nhom6.his.danhmuc.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.nhom6.his.danhmuc.entity.Thuoc;

public interface ThuocRepository extends JpaRepository<Thuoc, Integer> {

    @Query("select t from Thuoc t where (:tuKhoa is null or t.tenThuoc like %:tuKhoa%)")
    Page<Thuoc> timKiem(@Param("tuKhoa") String tuKhoa, Pageable pageable);

    @Query("select t from Thuoc t where t.daXoa = false and (:tuKhoa is null or t.tenThuoc like %:tuKhoa%) order by t.tenThuoc")
    List<Thuoc> chon(@Param("tuKhoa") String tuKhoa, Pageable pageable);

    boolean existsByTenThuocAndDaXoaFalseAndIdNot(String tenThuoc, Integer id);
}
