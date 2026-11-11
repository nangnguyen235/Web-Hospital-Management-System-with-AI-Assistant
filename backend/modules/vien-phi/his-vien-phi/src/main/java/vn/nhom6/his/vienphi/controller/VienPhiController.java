package vn.nhom6.his.vienphi.controller;

import java.time.LocalDate;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.nhom6.his.contracts.vienphi.LoaiThanhToan;
import vn.nhom6.his.shared.kernel.CongKhai;
import vn.nhom6.his.shared.kernel.PageResponse;
import vn.nhom6.his.vienphi.constant.TrangThaiGiaoDich;
import vn.nhom6.his.vienphi.dto.GiaoDichDto;
import vn.nhom6.his.vienphi.services.GiaoDichService;

/** VNPAY return + IPN (FR-HT-02), giao dịch của tôi, Admin xem giao dịch (FR-AD-09). */
@RestController
@RequestMapping("/api/vien-phi")
@RequiredArgsConstructor
public class VienPhiController {

    private final GiaoDichService giaoDichService;

    /** IPN: VNPAY gọi server-to-server. Trả {RspCode, Message} theo tài liệu VNPAY. */
    @GetMapping("/vnpay/ipn")
    @CongKhai
    public Map<String, String> ipn(@RequestParam Map<String, String> thamSo) {
        GiaoDichService.XuLy kq = giaoDichService.xuLy(thamSo);
        return Map.of("RspCode", kq.rspCode(), "Message", kq.message());
    }

    /** Trang kết quả của frontend gửi lại query string VNPAY trả về (return URL) — cùng hàm xử lý với IPN. */
    @GetMapping("/vnpay/ket-qua")
    @CongKhai
    public GiaoDichDto.KetQua ketQua(@RequestParam Map<String, String> thamSo) {
        return giaoDichService.xuLy(thamSo).ketQua();
    }

    /** Quầy/bệnh nhân hỏi trạng thái mỗi 5 giây khi chờ quét QR. */
    @GetMapping("/giao-dich/{id}")
    @PreAuthorize("isAuthenticated()")
    public GiaoDichDto chiTiet(@PathVariable long id) {
        return giaoDichService.chiTiet(id);
    }

    @GetMapping("/giao-dich/cua-toi")
    @PreAuthorize("hasRole('BENH_NHAN')")
    public PageResponse<GiaoDichDto> cuaToi(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "20") int pageSize) {
        return giaoDichService.timKiem(null, null, null, null, true, page, pageSize);
    }

    @GetMapping("/admin/giao-dich")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public PageResponse<GiaoDichDto> timKiem(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tu,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate den,
            @RequestParam(required = false) TrangThaiGiaoDich trangThai,
            @RequestParam(required = false) LoaiThanhToan loai,
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int pageSize) {
        return giaoDichService.timKiem(tu, den, trangThai, loai, false, page, pageSize);
    }
}
