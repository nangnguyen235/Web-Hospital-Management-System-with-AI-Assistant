package vn.nhom6.his.contracts.danhmuc;

/** Thông tin phòng cho module khác. */
public record PhongInfo(int id, String tenPhong, int khoaId, String tenKhoa, LoaiPhong loaiPhong, Integer dichVuId,
                        boolean daXoa) {
}
