package vn.nhom6.his.contracts.hosobenhan;

import java.time.LocalDate;
import java.util.List;

/** Lệnh/hỏi HoSoBenhAn từ module khác (modular-monolith.md mục 3.2). Cài đặt: HoSoBenhAnContractsService. */
public interface HoSoBenhAnContracts {

    /**
     * Tạo bệnh án CHO_KHAM cho phiếu (phiếu QUAY vừa thanh toán — BR-14). Phiếu đã có bệnh án thì trả bệnh án cũ.
     * Phát {@link BenhAnDaTaoIntegrationEvent}.
     */
    long taoBenhAnChoPhieu(long phieuDatKhamId, long lichKhamId, long benhNhanId, long hoSoId);

    /** Lịch sử khám của bệnh nhân (Admin xem chi tiết bệnh nhân — FR-AD-08). */
    List<LichSuKham> lichSuCuaBenhNhan(long benhNhanId);

    /** Doanh thu xét nghiệm đã thanh toán theo khoa (dịch vụ → khoa), theo ngày chỉ định (BR-25). */
    java.util.Map<Integer, java.math.BigDecimal> doanhThuXetNghiemTheoKhoa(LocalDate tu, LocalDate den);

    record LichSuKham(long benhAnId, long lichKhamId, String trangThai, String chanDoan, LocalDate ngayTaiKham,
                      java.time.LocalDateTime ngayTao) {
    }
}
