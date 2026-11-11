package vn.nhom6.his.danhmuc.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.nhom6.his.danhmuc.entity.Khoa;

public interface KhoaRepository extends JpaRepository<Khoa, Integer> {

    List<Khoa> findByDaXoaFalseOrderByTenKhoaAsc();

    Page<Khoa> findByTenKhoaContaining(String tuKhoa, Pageable pageable);

    boolean existsByTenKhoaAndDaXoaFalseAndIdNot(String tenKhoa, Integer id);

    boolean existsByTenKhoaAndDaXoaFalse(String tenKhoa);

    long countByDaXoaFalse();
}
