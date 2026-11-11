package vn.nhom6.his.contracts.danhmuc;

import java.math.BigDecimal;

/** Thông tin dịch vụ cho module khác (giá chốt lúc đặt/chỉ định). */
public record DichVuInfo(int id, String tenDichVu, int khoaId, LoaiDichVu loaiDichVu, BigDecimal gia, boolean daXoa) {
}
