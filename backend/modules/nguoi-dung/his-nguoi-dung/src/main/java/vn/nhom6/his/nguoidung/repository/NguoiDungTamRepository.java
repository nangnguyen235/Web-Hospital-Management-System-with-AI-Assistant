package vn.nhom6.his.nguoidung.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.nhom6.his.nguoidung.entity.NguoiDungTam;

public interface NguoiDungTamRepository extends JpaRepository<NguoiDungTam, Long> {

    Optional<NguoiDungTam> findFirstByEmailAndDaXoaFalseOrderByIdDesc(String email);
}
