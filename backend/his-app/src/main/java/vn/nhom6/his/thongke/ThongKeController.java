package vn.nhom6.his.thongke;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.nhom6.his.contracts.danhmuc.DanhMucContracts;
import vn.nhom6.his.contracts.hosobenhan.HoSoBenhAnContracts;
import vn.nhom6.his.contracts.lichkham.LichKhamContracts;
import vn.nhom6.his.contracts.nguoidung.NguoiDungContracts;
import vn.nhom6.his.contracts.vienphi.VienPhiContracts;
import vn.nhom6.his.shared.kernel.CongKhai;

/** Thống kê ghép số liệu nhiều module qua *-contracts (BR-25, FR-AD-01, FR-AD-08) — nằm ở his-app, không thêm module. */
@RestController
@RequestMapping("/api/thong-ke")
@RequiredArgsConstructor
public class ThongKeController {

    private final NguoiDungContracts nguoiDung;
    private final DanhMucContracts danhMuc;
    private final LichKhamContracts lichKham;
    private final VienPhiContracts vienPhi;
    private final HoSoBenhAnContracts hoSoBenhAn;
    private final Clock clock;

    public record ThongKeTrangChu(long soBacSi, long soKhoa, long soLuotKham) {
    }

    public record TheoKhoa(int khoaId, String tenKhoa, long luotDat, BigDecimal doanhThu) {
    }

    public record TheoNgay(LocalDate ngay, long luotDat) {
    }

    public record TongQuan(LocalDate tu, LocalDate den, BigDecimal tongDoanhThu, long luotDat, long soBenhNhan, long soKhoa,
                           long soBacSi, List<TheoKhoa> theoKhoa, List<TheoNgay> theoNgay, TheoKhoa khoaNhieuLuotNhat) {
    }

    /** Số liệu công khai cho trang chủ. */
    @GetMapping("/trang-chu")
    @CongKhai
    public ThongKeTrangChu trangChu() {
        return new ThongKeTrangChu(nguoiDung.demBacSi(), danhMuc.demKhoa(), lichKham.demLuotKham());
    }

    /** FR-AD-01: doanh thu (giao dịch THANH_CONG, không tính phí cổng), lượt đặt, theo khoa và theo ngày. */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public TongQuan admin(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tu,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate den) {
        LocalDate d = den == null ? LocalDate.now(clock) : den;
        LocalDate t = tu == null ? d.minusDays(29) : tu;
        LichKhamContracts.ThongKe tk = lichKham.thongKe(t, d);
        Map<Integer, BigDecimal> xetNghiem = hoSoBenhAn.doanhThuXetNghiemTheoKhoa(t, d);
        Map<Integer, String> tenKhoa = danhMuc.tenCacKhoa();
        Set<Integer> khoa = new HashSet<>(tk.luotTheoKhoa().keySet());
        khoa.addAll(xetNghiem.keySet());
        List<TheoKhoa> theoKhoa = new ArrayList<>();
        for (Integer k : khoa) {
            theoKhoa.add(new TheoKhoa(k, tenKhoa.getOrDefault(k, "Khác"), tk.luotTheoKhoa().getOrDefault(k, 0L),
                    tk.doanhThuTheoKhoa().getOrDefault(k, BigDecimal.ZERO).add(xetNghiem.getOrDefault(k, BigDecimal.ZERO))));
        }
        theoKhoa.sort(Comparator.comparingLong(TheoKhoa::luotDat).reversed());
        return new TongQuan(t, d, vienPhi.doanhThu(t, d), tk.luotDat(), nguoiDung.demBenhNhan(), danhMuc.demKhoa(),
                nguoiDung.demBacSi(), theoKhoa,
                tk.luotTheoNgay().entrySet().stream().map(e -> new TheoNgay(e.getKey(), e.getValue())).toList(),
                theoKhoa.isEmpty() ? null : theoKhoa.getFirst());
    }

    /** FR-AD-08: lịch sử khám của một bệnh nhân. */
    @GetMapping("/admin/benh-nhan/{benhNhanId}/lich-su")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public List<HoSoBenhAnContracts.LichSuKham> lichSuBenhNhan(@PathVariable long benhNhanId) {
        return hoSoBenhAn.lichSuCuaBenhNhan(benhNhanId);
    }
}
