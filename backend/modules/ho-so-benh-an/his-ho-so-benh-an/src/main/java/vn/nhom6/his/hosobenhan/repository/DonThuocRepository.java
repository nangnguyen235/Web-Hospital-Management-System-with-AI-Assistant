package vn.nhom6.his.hosobenhan.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.nhom6.his.hosobenhan.entity.DonThuoc;

public interface DonThuocRepository extends JpaRepository<DonThuoc, Long> {

    Optional<DonThuoc> findByBenhAnId(Long benhAnId);

    List<DonThuoc> findByBenhAnIdIn(Collection<Long> benhAnIds);

    boolean existsByMaDonThuoc(String ma);
}
