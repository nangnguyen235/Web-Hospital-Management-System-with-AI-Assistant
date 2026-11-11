package vn.nhom6.his.danhmuc.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.nhom6.his.contracts.danhmuc.LoaiDichVu;
import vn.nhom6.his.danhmuc.entity.DichVu;

public interface DichVuRepository extends JpaRepository<DichVu, Integer> {

    @Query("""
            select d from DichVu d where d.daXoa = false and (:khoaId is null or d.khoaId = :khoaId)
              and (:loai is null or d.loaiDichVu = :loai) order by d.tenDichVu
            """)
    List<DichVu> danhSach(@Param("khoaId") Integer khoaId, @Param("loai") LoaiDichVu loai);

    @Query("""
            select d from DichVu d where (:tuKhoa is null or d.tenDichVu like %:tuKhoa%)
              and (:khoaId is null or d.khoaId = :khoaId)
            """)
    Page<DichVu> timKiem(@Param("tuKhoa") String tuKhoa, @Param("khoaId") Integer khoaId, Pageable pageable);
}
