package vn.nhom6.his.contracts.lichkham;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Hỏi/lệnh LichKham từ module khác (modular-monolith.md mục 3.2). Cài đặt: LichKhamContractsService. */
public interface LichKhamContracts {

    /** Số lượt đặt đã thanh toán (thống kê trang chủ, BR-25). */
    long demLuotKham();

    /** Bác sĩ còn lịch khám chưa diễn ra (từ hôm nay) — không được bớt khỏi khoa (BR-04). */
    boolean bacSiConLichSapToi(long bacSiId);

    Optional<LichInfo> layLich(long lichKhamId);

    Map<Long, LichInfo> layCacLich(Collection<Long> lichKhamIds);

    /** Lịch của bác sĩ trong một ngày (nhận khám thay — BR-16). */
    List<LichInfo> lichCuaBacSi(long bacSiId, LocalDate ngay);

    /**
     * Kiểm tra phiếu khi tiếp nhận (BR-13): tồn tại, đã thanh toán, chưa hủy, đúng phòng (sai → báo phòng đúng),
     * ngày khám là hôm nay. Ném lỗi nghiệp vụ nếu không hợp lệ.
     */
    PhieuInfo kiemTraPhieuTiepNhan(String maPhieu, int phongId);

    Optional<PhieuInfo> layPhieu(long phieuId);

    Map<Long, PhieuInfo> layCacPhieu(Collection<Long> phieuIds);

    /** Thống kê (BR-25): lượt đặt đã thanh toán và doanh thu khám theo khoa (dịch vụ → khoa) trong khoảng ngày khám. */
    ThongKe thongKe(LocalDate tu, LocalDate den);

    record LichInfo(long id, long bacSiId, int phongId, int dichVuId, int khoaId, LocalDate ngay, String buoi,
                    int soDangGoi) {
    }

    record PhieuInfo(long id, String maPhieu, long hoSoId, long lichKhamId, int soThuTu, Long benhAnId, boolean daDanhGia,
                     boolean daThanhToan) {
    }

    record ThongKe(long luotDat, Map<Integer, Long> luotTheoKhoa, Map<Integer, BigDecimal> doanhThuTheoKhoa,
                   Map<LocalDate, Long> luotTheoNgay) {
    }
}
