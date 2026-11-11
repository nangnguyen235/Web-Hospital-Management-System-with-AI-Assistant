package vn.nhom6.his.contracts.danhmuc;

import java.util.Map;
import java.util.Optional;

/** Hỏi DanhMuc từ module khác (modular-monolith.md mục 3.2). Cài đặt: DanhMucContractsService. */
public interface DanhMucContracts {

    /** Phòng kèm tên khoa (gồm cả phòng đã xóa để dữ liệu cũ vẫn hiện đúng tên). */
    Optional<PhongInfo> layPhong(int phongId);

    /** Dịch vụ (gồm cả dịch vụ đã xóa). */
    Optional<DichVuInfo> layDichVu(int dichVuId);

    /** Số khoa đang hoạt động (thống kê trang chủ, BR-25). */
    long demKhoa();

    /** Khoa tồn tại và chưa xóa. */
    boolean khoaTonTai(int khoaId);

    /** Tên mọi khoa (kể cả đã xóa) theo id — để hiển thị. */
    Map<Integer, String> tenCacKhoa();

    boolean danTocTonTai(int danTocId);

    /** Id mọi phòng của khoa (kể cả đã xóa) — lọc lịch khám theo khoa. */
    java.util.List<Integer> phongCuaKhoa(int khoaId);

    /**
     * Cấp số xét nghiệm (BR-17): chọn phòng PHONG_XET_NGHIEM chưa xóa của dịch vụ có {@code so_xn_lon_nhat} nhỏ nhất,
     * đặt lại bộ đếm khi sang ngày (lười), tăng bộ đếm (khóa phien_ban). Trả phòng + số thứ tự.
     */
    SoXetNghiem capSoXetNghiem(int dichVuId);

    /** Gọi tiếp 5 số của phòng xét nghiệm (BR-17): {@code so_xn_dang_goi = min(+5, so_xn_lon_nhat)}. */
    SoXetNghiem goiTiepXetNghiem(int phongId);

    /** Số đang gọi / lớn nhất của phòng xét nghiệm hôm nay. */
    SoXetNghiem soXetNghiem(int phongId);

    record SoXetNghiem(int phongId, String tenPhong, int soThuTu, int soDangGoi, int soLonNhat) {
    }

    /** Thuốc (kể cả đã xóa — đơn cũ vẫn hiện đúng tên). */
    Map<Integer, ThuocInfo> layCacThuoc(java.util.Collection<Integer> ids);

    /** Dịch vụ theo id (kể cả đã xóa). */
    Map<Integer, DichVuInfo> layCacDichVu(java.util.Collection<Integer> ids);

    record ThuocInfo(int id, String tenThuoc, java.math.BigDecimal gia, String donVi, boolean daXoa) {
    }
}
