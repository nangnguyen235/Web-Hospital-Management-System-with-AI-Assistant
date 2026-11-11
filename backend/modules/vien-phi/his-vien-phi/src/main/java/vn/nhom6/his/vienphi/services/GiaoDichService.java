package vn.nhom6.his.vienphi.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom6.his.contracts.vienphi.GiaoDichDaXuLyIntegrationEvent;
import vn.nhom6.his.contracts.vienphi.LoaiThanhToan;
import vn.nhom6.his.shared.kernel.CurrentUser;
import vn.nhom6.his.shared.kernel.Loi;
import vn.nhom6.his.shared.kernel.PageResponse;
import vn.nhom6.his.shared.kernel.PhanTrang;
import vn.nhom6.his.shared.kernel.VaiTro;
import vn.nhom6.his.vienphi.constant.TrangThaiGiaoDich;
import vn.nhom6.his.vienphi.dto.GiaoDichDto;
import vn.nhom6.his.vienphi.entity.GiaoDich;
import vn.nhom6.his.vienphi.repository.GiaoDichRepository;

/**
 * Xử lý kết quả VNPAY (FR-HT-02, BR-10): return URL và IPN gọi CHUNG {@link #xuLy} — kiểm chữ ký, số tiền, khóa dòng
 * giao dịch; chỉ rời DANG_XU_LY một lần; gặp giao dịch đã xử lý thì phát lại sự kiện (handler chống xử lý lại).
 */
@Service
@RequiredArgsConstructor
public class GiaoDichService {

    private static final Logger LOG = LoggerFactory.getLogger(GiaoDichService.class);

    private final GiaoDichRepository giaoDichRepository;
    private final VnpayService vnpay;
    private final ApplicationEventPublisher events;
    private final CurrentUser currentUser;

    /** Kết quả xử lý: mã IPN theo tài liệu VNPAY + kết quả cho trang return. */
    public record XuLy(String rspCode, String message, GiaoDichDto.KetQua ketQua) {
    }

    @Transactional
    public XuLy xuLy(Map<String, String> thamSo) {
        if (!vnpay.dungChuKy(thamSo)) {
            LOG.warn("VNPAY: sai chữ ký (TxnRef={})", thamSo.get("vnp_TxnRef"));
            return new XuLy("97", "Invalid Checksum", new GiaoDichDto.KetQua(null, false, null, null, null, "Chữ ký không hợp lệ."));
        }
        long id;
        try {
            id = Long.parseLong(thamSo.getOrDefault("vnp_TxnRef", ""));
        } catch (NumberFormatException e) {
            return new XuLy("01", "Order not found", new GiaoDichDto.KetQua(null, false, null, null, null, "Không tìm thấy giao dịch."));
        }
        GiaoDich g = giaoDichRepository.khoaTheoId(id).orElse(null);
        if (g == null) {
            return new XuLy("01", "Order not found", new GiaoDichDto.KetQua(id, false, null, null, null, "Không tìm thấy giao dịch."));
        }
        if (!String.valueOf(VnpayService.soTienVnpay(g)).equals(thamSo.get("vnp_Amount"))) {
            LOG.warn("VNPAY: sai số tiền giao dịch {}", id);
            return new XuLy("04", "Invalid amount", ketQua(g, "Số tiền không khớp."));
        }
        if (g.getTrangThai() != TrangThaiGiaoDich.DANG_XU_LY) {
            // Cổng gọi lại: không ghi nhận lại, nhưng phát lại sự kiện để phiếu không bị kẹt (modular-monolith.md mục 3.1)
            phatSuKien(g);
            return new XuLy("02", "Order already confirmed", ketQua(g, null));
        }
        boolean thanhCong = "00".equals(thamSo.get("vnp_ResponseCode"))
                && "00".equals(thamSo.getOrDefault("vnp_TransactionStatus", "00"));
        g.setTrangThai(thanhCong ? TrangThaiGiaoDich.THANH_CONG : TrangThaiGiaoDich.THAT_BAI);
        g.setMaNganHang(cat(thamSo.get("vnp_BankCode"), 20));
        phatSuKien(g);
        LOG.info("VNPAY: giao dịch {} → {}", id, g.getTrangThai());
        return new XuLy("00", "Confirm Success", ketQua(g, null));
    }

    @Transactional(readOnly = true)
    public GiaoDichDto chiTiet(long id) {
        GiaoDich g = giaoDichRepository.findById(id).orElseThrow(() -> Loi.khongTimThay("giao dịch").ngoaiLe());
        boolean laNhanVien = currentUser.vaiTro() != VaiTro.BENH_NHAN;
        if (!laNhanVien && (g.getTaiKhoanId() == null || g.getTaiKhoanId() != currentUser.taiKhoanId())) {
            throw Loi.khongTimThay("giao dịch").ngoaiLe();
        }
        return GiaoDichDto.tu(g);
    }

    /** Admin xem giao dịch (FR-AD-09); bệnh nhân xem giao dịch của mình. */
    @Transactional(readOnly = true)
    public PageResponse<GiaoDichDto> timKiem(LocalDate tu, LocalDate den, TrangThaiGiaoDich trangThai, LoaiThanhToan loai,
                                            boolean cuaToi, Integer page, Integer pageSize) {
        PhanTrang pt = PhanTrang.cua(page, pageSize);
        Page<GiaoDich> kq = giaoDichRepository.timKiem(tu == null ? null : tu.atStartOfDay(),
                den == null ? null : den.plusDays(1).atStartOfDay(), trangThai, loai,
                cuaToi ? currentUser.taiKhoanId() : null,
                PageRequest.of(pt.chiSoTrang(), pt.pageSize(), Sort.by(Sort.Direction.DESC, "id")));
        return new PageResponse<>(kq.map(GiaoDichDto::tu).getContent(), kq.getTotalElements(), pt.page(), pt.pageSize());
    }

    @Transactional(readOnly = true)
    public BigDecimal doanhThu(LocalDate tu, LocalDate den) {
        return giaoDichRepository.doanhThu(tu == null ? null : tu.atStartOfDay(), den == null ? null : den.plusDays(1).atStartOfDay());
    }

    private void phatSuKien(GiaoDich g) {
        events.publishEvent(new GiaoDichDaXuLyIntegrationEvent(g.getId(), g.getLoaiThanhToan(),
                g.getTrangThai() == TrangThaiGiaoDich.THANH_CONG));
    }

    private static GiaoDichDto.KetQua ketQua(GiaoDich g, String thongBao) {
        boolean ok = g.getTrangThai() == TrangThaiGiaoDich.THANH_CONG;
        return new GiaoDichDto.KetQua(g.getId(), ok, g.getTrangThai(), g.getLoaiThanhToan(), g.getSoTien().add(g.getPhi()),
                thongBao != null ? thongBao : ok ? "Thanh toán thành công." : "Thanh toán không thành công.");
    }

    private static String cat(String s, int n) {
        return s == null ? null : s.length() <= n ? s : s.substring(0, n);
    }
}
