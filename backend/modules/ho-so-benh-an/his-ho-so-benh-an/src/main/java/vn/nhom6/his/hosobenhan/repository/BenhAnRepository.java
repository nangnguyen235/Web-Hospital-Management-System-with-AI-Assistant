package vn.nhom6.his.hosobenhan.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.nhom6.his.hosobenhan.constant.TrangThaiBenhAn;
import vn.nhom6.his.hosobenhan.entity.BenhAn;

public interface BenhAnRepository extends JpaRepository<BenhAn, Long> {

    Optional<BenhAn> findByPhieuDatKhamId(Long phieuDatKhamId);

    List<BenhAn> findByLichKhamIdAndDaXoaFalse(Long lichKhamId);

    List<BenhAn> findByBenhNhanIdAndDaXoaFalseOrderByIdDesc(Long benhNhanId);

    List<BenhAn> findByHoSoIdAndDaXoaFalseOrderByIdDesc(Long hoSoId);

    List<BenhAn> findByLichKhamIdInAndTrangThaiAndDaXoaFalse(Collection<Long> lichKhamIds, TrangThaiBenhAn trangThai);
}
