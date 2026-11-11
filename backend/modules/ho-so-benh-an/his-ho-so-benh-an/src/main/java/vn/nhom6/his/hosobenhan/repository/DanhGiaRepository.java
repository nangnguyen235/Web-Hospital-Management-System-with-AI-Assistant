package vn.nhom6.his.hosobenhan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.nhom6.his.hosobenhan.entity.DanhGia;

public interface DanhGiaRepository extends JpaRepository<DanhGia, Long> {

    boolean existsByPhieuDatKhamId(Long phieuDatKhamId);

    /** Điểm trung bình của bác sĩ (BR-20: trung bình cộng mọi lần đánh giá). */
    @Query("select avg(d.soDiem) from DanhGia d where d.bacSiId = :bacSi and d.daXoa = false")
    Double diemTrungBinh(@Param("bacSi") Long bacSiId);
}
