package vn.nhom6.his.contracts.vienphi;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/** Lệnh/hỏi VienPhi từ module khác (modular-monolith.md mục 3.2). Cài đặt: VienPhiContractsService. */
public interface VienPhiContracts {

    /** Tạo giao dịch DANG_XU_LY + URL VNPAY hết hạn sau 15 phút (FR-HT-02, BR-10). */
    GiaoDichMoi taoGiaoDich(TaoGiaoDich yeuCau);

    /** URL VNPAY mới cho giao dịch còn DANG_XU_LY (quầy hiện lại QR). */
    String urlThanhToan(long giaoDichId, String ipKhachHang);

    Optional<GiaoDichInfo> layGiaoDich(long giaoDichId);

    /** Doanh thu = tổng so_tien giao dịch THANH_CONG trong khoảng ngày (BR-25, không tính phí cổng). */
    BigDecimal doanhThu(LocalDate tu, LocalDate den);

    record TaoGiaoDich(LoaiThanhToan loai, BigDecimal soTien, BigDecimal phi, Long taiKhoanId, String nguon,
                       String ipKhachHang) {
    }

    record GiaoDichMoi(long giaoDichId, String urlThanhToan, LocalDateTime hetHan) {
    }

    record GiaoDichInfo(long id, LoaiThanhToan loai, String trangThai, BigDecimal soTien, BigDecimal phi, Long taiKhoanId,
                        LocalDateTime ngayTao) {
    }
}
