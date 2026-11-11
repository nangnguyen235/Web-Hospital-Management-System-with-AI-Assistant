package vn.nhom6.his.nguoidung.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import vn.nhom6.his.nguoidung.constant.GioiTinh;
import vn.nhom6.his.nguoidung.constant.TrangThaiThongBao;
import vn.nhom6.his.shared.kernel.VaiTro;

/** DTO cho Admin (FR-AD-02, 08) và thông báo (FR-BN-16). */
public final class QuanTriDto {

    private QuanTriDto() {
    }

    public record TaiKhoan(long id, String hoTen, String email, String soDienThoai, VaiTro vaiTro, int soLuongHoSo,
                           boolean daXoa, LocalDateTime ngayTao) {
    }

    public record TaiKhoanChiTiet(TaiKhoan taiKhoan, List<HoSoDto> hoSo) {
    }

    public record BenhNhan(long id, String maBenhNhan, String hoTen, String soDienThoai, String soCccd,
                           LocalDate ngaySinh, GioiTinh gioiTinh, boolean daXoa) {
    }

    public record BenhNhanChiTiet(BenhNhan benhNhan, List<HoSoDto> hoSo) {
    }

    public record ThongBao(long id, String tieuDe, String noiDung, String duLieu, TrangThaiThongBao trangThai,
                           LocalDateTime ngayTao) {
    }
}
