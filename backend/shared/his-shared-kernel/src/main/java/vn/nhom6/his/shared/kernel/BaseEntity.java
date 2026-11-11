package vn.nhom6.his.shared.kernel;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;

/**
 * Cột chung của hầu hết bảng (mo-hinh-du-lieu.md mục 1): xóa mềm {@code da_xoa}, {@code ngay_tao}, {@code ngay_cap_nhat}.
 * Mỗi thực thể tự khai báo khóa chính (BIGINT hoặc INT).
 */
@MappedSuperclass
public abstract class BaseEntity {

    @Column(name = "da_xoa", nullable = false)
    private boolean daXoa;

    @Column(name = "ngay_tao", nullable = false, updatable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat", nullable = false)
    private LocalDateTime ngayCapNhat;

    @PrePersist
    protected void truocKhiThem() {
        LocalDateTime bayGio = ThoiGian.bayGio();
        if (ngayTao == null) {
            ngayTao = bayGio;
        }
        ngayCapNhat = bayGio;
    }

    @PreUpdate
    protected void truocKhiSua() {
        ngayCapNhat = ThoiGian.bayGio();
    }

    public boolean isDaXoa() {
        return daXoa;
    }

    public void setDaXoa(boolean daXoa) {
        this.daXoa = daXoa;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public LocalDateTime getNgayCapNhat() {
        return ngayCapNhat;
    }
}
