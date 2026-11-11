package vn.nhom6.his.nguoidung.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.nhom6.his.nguoidung.constant.HocHam;
import vn.nhom6.his.shared.kernel.VaiTro;

@Entity
@DiscriminatorValue("BAC_SI")
@Getter
@Setter
@NoArgsConstructor
public class BacSi extends NhanVien {

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(30)")
    private HocHam hocHam;

    /** Điểm đánh giá trung bình, cập nhật qua DanhGiaDaTao (BR-20). */
    @Column(precision = 2, scale = 1)
    private BigDecimal diemDanhGia = BigDecimal.ZERO;

    @Override
    public VaiTro vaiTro() {
        return VaiTro.BAC_SI;
    }
}
