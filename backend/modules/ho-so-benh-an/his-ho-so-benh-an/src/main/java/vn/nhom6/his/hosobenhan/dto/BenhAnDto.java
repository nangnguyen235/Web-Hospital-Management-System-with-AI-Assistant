package vn.nhom6.his.hosobenhan.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import vn.nhom6.his.hosobenhan.constant.TrangThaiBenhAn;
import vn.nhom6.his.hosobenhan.constant.TrangThaiXetNghiem;

/** DTO bệnh án, xét nghiệm, đơn thuốc, đánh giá, nhắc thuốc (module HoSoBenhAn). */
public final class BenhAnDto {

    private BenhAnDto() {
    }

    // ---- Tiếp nhận (FR-DD-03, 05) — chỉ thông tin hành chính, điều dưỡng không xem nội dung bệnh án ----
    public record TiepNhan(@NotBlank(message = "Vui lòng nhập mã phiếu.") String maPhieu,
                           @NotNull(message = "Vui lòng chọn phòng.") Integer phongId,
                           String soCccd) {
    }

    public record PhieuTiepNhan(long phieuId, String maPhieu, int soThuTu, long hoSoId, String hoTen, LocalDate ngaySinh,
                                String gioiTinh, String soCccd, String soDienThoai, boolean daLienKet, String maBenhNhan,
                                Long benhAnId) {
    }

    public record KetQuaTiepNhan(long benhAnId, String maBenhNhan, int soThuTu, String hoTen) {
    }

    // ---- Bệnh án (FR-BS-03…05, 09, 10; FR-BN-11) ----
    public record BenhAnGon(long id, long phieuDatKhamId, int soThuTu, long hoSoId, String hoTen, LocalDate ngaySinh,
                            String gioiTinh, String maBenhNhan, TrangThaiBenhAn trangThai, long lichKhamId,
                            LocalDateTime ngayTao) {
    }

    public record ChiTiet(long id, long phieuDatKhamId, long lichKhamId, Long lichKhamTruocId, long benhNhanId,
                          String maBenhNhan, long hoSoId, String hoTen, LocalDate ngaySinh, String gioiTinh,
                          TrangThaiBenhAn trangThai, String trieuChung, String chanDoan, LocalDate ngayTaiKham,
                          LocalDate ngayKham, String tenBacSi, String tenKhoa, String tenPhong, boolean daDanhGia,
                          List<XetNghiem> xetNghiem, DonThuoc donThuoc, LocalDateTime ngayTao) {
    }

    public record GhiBenhAn(@Size(max = 5000) String trieuChung, @Size(max = 5000) String chanDoan, LocalDate ngayTaiKham) {
    }

    // ---- Xét nghiệm (FR-BS-07, FR-DD-09…11, FR-BN-12, 13) ----
    public record XetNghiem(long id, String maXetNghiem, int soThuTu, long dichVuId, String tenDichVu, int phongId,
                            String tenPhong, BigDecimal gia, String ghiChu, String ketQua, boolean coFileKetQua,
                            TrangThaiXetNghiem trangThai, boolean daThanhToan, Long giaoDichId, LocalDate ngay,
                            Long benhAnId, String hoTen) {
    }

    public record ChiDinh(@NotNull(message = "Vui lòng chọn dịch vụ xét nghiệm.") Integer dichVuId,
                          @Size(max = 2000) String ghiChu) {
    }

    public record ThanhToan(long xetNghiemId, long giaoDichId, String urlThanhToan, BigDecimal tongTien) {
    }

    public record SoPhongXetNghiem(int phongId, String tenPhong, int soDangGoi, int soLonNhat) {
    }

    // ---- Đơn thuốc (FR-BS-08, BR-18, BR-26) ----
    public record DonThuoc(long id, String maDonThuoc, BigDecimal tongTien, List<DongThuoc> thuoc, LocalDateTime ngayTao) {
    }

    public record DongThuoc(long id, int thuocId, String tenThuoc, String donVi, int soLuong, BigDecimal gia,
                            BigDecimal thanhTien, int soLanMoiNgay, int soNgay, BigDecimal soLuongMoiLan, boolean sang,
                            boolean trua, boolean chieu, boolean toi, String huongDan) {
    }

    public record KeThuoc(@NotNull(message = "Vui lòng chọn thuốc.") Integer thuocId,
                          @Min(value = 1, message = "Số lượng phải lớn hơn 0.") int soLuong,
                          @Min(value = 1, message = "Số lần mỗi ngày tối thiểu 1.") int soLanMoiNgay,
                          @Min(value = 1, message = "Số ngày tối thiểu 1.") int soNgay,
                          @NotNull @DecimalMin(value = "0.25", message = "Số lượng mỗi lần tối thiểu 0,25.") BigDecimal soLuongMoiLan,
                          boolean sang, boolean trua, boolean chieu, boolean toi,
                          @Size(max = 255) String huongDan) {
    }

    /** BR-26 (2): chỉ cảnh báo thuốc trùng, vẫn lưu. */
    public record KetQuaKeThuoc(DonThuoc donThuoc, String canhBao) {
    }

    // ---- Lịch sử khám, đánh giá (FR-BN-11, 14; FR-BS-09) ----
    public record LichSu(long benhAnId, long phieuDatKhamId, LocalDate ngayKham, String tenBacSi, String tenKhoa,
                         String chanDoan, TrangThaiBenhAn trangThai, LocalDate ngayTaiKham, boolean daDanhGia,
                         String hoTen) {
    }

    public record DanhGia(@Min(value = 1, message = "Điểm từ 1 đến 5.") @Max(value = 5, message = "Điểm từ 1 đến 5.") int soDiem,
                          @Size(max = 2000) String nhanXet) {
    }

    // ---- Nhắc uống thuốc (FR-BN-15, BR-21) ----
    public record LanUong(@NotBlank @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Giờ dạng HH:mm.") String gio,
                          @NotNull @DecimalMin(value = "0.25", message = "Số lượng mỗi lần tối thiểu 0,25.") BigDecimal soLuong) {
    }

    public record TaoNhacThuoc(@NotNull Long hoSoId, @NotNull Long chiTietDonThuocId,
                               @NotNull(message = "Vui lòng chọn ngày bắt đầu.") LocalDate ngayBatDau,
                               @NotNull @DecimalMin(value = "0.25") BigDecimal tongSoLuong,
                               @NotEmpty(message = "Vui lòng nhập giờ uống.") List<@Valid LanUong> cacLan) {
    }

    public record NhacThuoc(long id, long chiTietDonThuocId, long hoSoId, String tenThuoc, LocalDate ngayBatDau,
                            LocalDateTime ngayKetThuc, BigDecimal tongSoLuong, BigDecimal soLuongConLai) {
    }

    public record LanNhac(long id, long nhacThuocId, String maXacNhan, LocalDateTime thoiDiemNhac, int thuTuNhac,
                          String tenThuoc, String donVi, String hoTen, BigDecimal soLuong, boolean daNhan, boolean daUong,
                          boolean boQua) {
    }

    public enum HanhDongNhac { DA_NHAN, DA_UONG, BO_QUA }
}
