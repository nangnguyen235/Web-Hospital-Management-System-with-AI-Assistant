package vn.nhom6.his.vienphi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import vn.nhom6.his.contracts.vienphi.LoaiThanhToan;
import vn.nhom6.his.vienphi.constant.TrangThaiGiaoDich;
import vn.nhom6.his.vienphi.entity.GiaoDich;

/** Giao dịch (FR-AD-09) và kết quả thanh toán trả cho trang kết quả của frontend (FR-HT-02). */
public record GiaoDichDto(long id, BigDecimal soTien, BigDecimal phi, BigDecimal tongTien, TrangThaiGiaoDich trangThai,
                          String maNganHang, String phuongThuc, LoaiThanhToan loaiThanhToan, Long taiKhoanId,
                          LocalDateTime ngayTao, LocalDateTime ngayCapNhat) {

    public static GiaoDichDto tu(GiaoDich g) {
        return new GiaoDichDto(g.getId(), g.getSoTien(), g.getPhi(), g.getSoTien().add(g.getPhi()), g.getTrangThai(),
                g.getMaNganHang(), g.getPhuongThuc(), g.getLoaiThanhToan(), g.getTaiKhoanId(), g.getNgayTao(),
                g.getNgayCapNhat());
    }

    public record KetQua(Long giaoDichId, boolean thanhCong, TrangThaiGiaoDich trangThai, LoaiThanhToan loaiThanhToan,
                         BigDecimal tongTien, String thongBao) {
    }
}
