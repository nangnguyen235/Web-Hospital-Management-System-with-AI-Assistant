package vn.nhom6.his.lichkham.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import vn.nhom6.his.lichkham.constant.Buoi;
import vn.nhom6.his.lichkham.constant.KenhDat;

/** DTO lịch khám, khung giờ, phiếu, phân công (LichKham). */
public final class LichKhamDto {

    private LichKhamDto() {
    }

    // ---- Lịch khám bác sĩ (FR-BS-11…14, FR-AD-10, FR-BS-02) ----
    public record Lich(long id, long bacSiId, String tenBacSi, int phongId, String tenPhong, int dichVuId, String tenDichVu,
                       BigDecimal gia, LocalDate ngay, Buoi buoi, int thuTrongTuan, int soLuot, int soLuotMoiKhung,
                       int soDangGoi, int soDaDat, List<Khung> khung) {
    }

    public record Khung(long id, int chiSoKhung, String gioBatDau, String gioKetThuc, int soLuongToiDa, int soLuongDaDat,
                        int conCho, boolean daQua) {
    }

    /** Một lần tạo: nhiều ngày buổi sáng + nhiều ngày buổi chiều (BR-05). */
    public record TaoLich(
            @NotNull(message = "Vui lòng chọn bác sĩ.") Long bacSiId,
            @NotNull(message = "Vui lòng chọn dịch vụ khám.") Integer dichVuId,
            @NotNull(message = "Vui lòng chọn phòng khám.") Integer phongId,
            List<LocalDate> ngaySang,
            List<LocalDate> ngayChieu,
            @Min(value = 1, message = "Số lượt mỗi khung tối thiểu 1.") @Max(value = 50, message = "Số lượt mỗi khung tối đa 50.") int soLuotMoiKhung) {
    }

    public record SuaLich(@NotNull(message = "Vui lòng chọn bác sĩ.") Long bacSiId,
                          @Min(value = 1, message = "Số lượt mỗi khung tối thiểu 1.") @Max(50) int soLuotMoiKhung) {
    }

    public record BacSiTrong(long id, String hoTen) {
    }

    // ---- Đặt khám (FR-BN-08) ----
    public record DatKham(
            @NotNull(message = "Vui lòng chọn hồ sơ.") Long hoSoId,
            @NotEmpty(message = "Vui lòng chọn khung giờ.") @Size(max = 3, message = "Tối đa 3 khung giờ một lần.") List<Long> khungGioIds) {
    }

    public record KetQuaDatKham(long giaoDichId, String urlThanhToan, LocalDateTime hetHan, BigDecimal soTien,
                                BigDecimal phi, List<Phieu> phieu) {
    }

    // ---- Phiếu (FR-BN-09, 10) ----
    public enum TrangThaiPhieu { CHO_THANH_TOAN, HET_HAN, DA_XAC_NHAN, DA_TIEP_NHAN, VANG_MAT }

    public record Phieu(long id, String maPhieu, int soThuTu, TrangThaiPhieu trangThai, long hoSoId, String hoTen,
                        long lichKhamId, LocalDate ngay, Buoi buoi, String gioBatDau, String gioKetThuc, int phongId,
                        String tenPhong, String tenKhoa, String tenBacSi, String tenDichVu, BigDecimal giaDichVu,
                        KenhDat kenhDat, long giaoDichId, Long benhAnId, boolean daDanhGia, int soDangGoi,
                        LocalDateTime ngayTao) {
    }

    // ---- Quầy (FR-DD-08) ----
    public record TaoPhieuQuay(@NotNull(message = "Vui lòng chọn hồ sơ.") Long hoSoId,
                               @NotNull(message = "Vui lòng chọn lịch khám.") Long lichKhamId) {
    }

    public record KetQuaQuay(Phieu phieu, String urlThanhToan, boolean daThanhToan) {
    }

    // ---- Gọi tiếp (FR-DD-07) ----
    public record GoiTiep(long lichKhamId, int soDangGoi, int soLonNhat, List<Integer> cacSoVuaGoi) {
    }

    // ---- Phân công điều dưỡng (FR-DD-12, FR-DD-02) ----
    public record Ca(@Min(1) @Max(7) int thuTrongTuan, @NotNull Buoi buoi) {
    }

    public record PhanCong(
            @NotNull(message = "Vui lòng chọn điều dưỡng.") Long dieuDuongId,
            @NotNull(message = "Vui lòng chọn phòng.") Integer phongId,
            @NotEmpty(message = "Vui lòng chọn thứ và buổi.") List<@Valid Ca> cacCa) {
    }

    public record LichDieuDuong(long id, long dieuDuongId, String tenDieuDuong, int phongId, String tenPhong,
                                int thuTrongTuan, Buoi buoi) {
    }

    /** Lịch khám hôm nay của các phòng điều dưỡng được phân công (FR-DD-02). */
    public record PhongHomNay(int phongId, String tenPhong, List<Lich> lich) {
    }
}
