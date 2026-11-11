package vn.nhom6.his.danhmuc.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import vn.nhom6.his.contracts.danhmuc.LoaiDichVu;
import vn.nhom6.his.contracts.danhmuc.LoaiPhong;
import vn.nhom6.his.danhmuc.constant.DonViThuoc;

/** DTO phòng, dịch vụ, thuốc (FR-AD-05…07). */
public final class DanhMucDto {

    private DanhMucDto() {
    }

    public record Phong(int id, String tenPhong, String moTa, int khoaId, String tenKhoa, LoaiPhong loaiPhong,
                        Integer dichVuId, String tenDichVu, boolean daXoa) {
    }

    public record LuuPhong(
            @NotBlank(message = "Vui lòng nhập tên phòng.") @Size(max = 100, message = "Tên phòng tối đa 100 ký tự.") String tenPhong,
            @Size(max = 255, message = "Mô tả tối đa 255 ký tự.") String moTa,
            @NotNull(message = "Vui lòng chọn khoa.") Integer khoaId,
            @NotNull(message = "Vui lòng chọn loại phòng.") LoaiPhong loaiPhong,
            Integer dichVuId) {
    }

    public record DichVu(int id, String tenDichVu, String moTa, int khoaId, String tenKhoa, LoaiDichVu loaiDichVu,
                         BigDecimal gia, boolean daXoa) {
    }

    public record LuuDichVu(
            @NotBlank(message = "Vui lòng nhập tên dịch vụ.") @Size(max = 150, message = "Tên dịch vụ tối đa 150 ký tự.") String tenDichVu,
            @Size(max = 2000) String moTa,
            @NotNull(message = "Vui lòng chọn khoa.") Integer khoaId,
            @NotNull(message = "Vui lòng chọn loại dịch vụ.") LoaiDichVu loaiDichVu,
            @NotNull(message = "Vui lòng nhập giá.") @DecimalMin(value = "0", message = "Giá không được âm.") BigDecimal gia) {
    }

    public record Thuoc(int id, String tenThuoc, BigDecimal gia, int soLuong, DonViThuoc donVi, boolean daXoa) {
    }

    public record LuuThuoc(
            @NotBlank(message = "Vui lòng nhập tên thuốc.") @Size(max = 150, message = "Tên thuốc tối đa 150 ký tự.") String tenThuoc,
            @NotNull(message = "Vui lòng nhập giá.") @DecimalMin(value = "0", message = "Giá không được âm.") BigDecimal gia,
            @Min(value = 0, message = "Số lượng không được âm.") int soLuong,
            @NotNull(message = "Vui lòng chọn đơn vị.") DonViThuoc donVi) {
    }
}
