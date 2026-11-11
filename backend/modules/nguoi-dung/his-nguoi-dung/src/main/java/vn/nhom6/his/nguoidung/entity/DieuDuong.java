package vn.nhom6.his.nguoidung.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import vn.nhom6.his.shared.kernel.VaiTro;

/** Điều dưỡng — một loại, làm phòng khám, phòng xét nghiệm, quầy (BR-01). */
@Entity
@DiscriminatorValue("DIEU_DUONG")
@NoArgsConstructor
public class DieuDuong extends NhanVien {

    @Override
    public VaiTro vaiTro() {
        return VaiTro.DIEU_DUONG;
    }
}
