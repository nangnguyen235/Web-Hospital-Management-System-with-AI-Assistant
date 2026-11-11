package vn.nhom6.his.nguoidung.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import vn.nhom6.his.shared.kernel.VaiTro;

@Entity
@DiscriminatorValue("QUAN_TRI")
@NoArgsConstructor
public class QuanTri extends NhanVien {

    @Override
    public VaiTro vaiTro() {
        return VaiTro.QUAN_TRI;
    }
}
