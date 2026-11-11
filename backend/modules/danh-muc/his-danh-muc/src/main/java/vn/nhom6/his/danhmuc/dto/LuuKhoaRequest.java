package vn.nhom6.his.danhmuc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LuuKhoaRequest(
        @NotBlank(message = "Tên khoa là bắt buộc.") @Size(max = 100, message = "Tên khoa tối đa 100 ký tự.") String tenKhoa,
        @Size(max = 2000, message = "Mô tả tối đa 2000 ký tự.") String moTa,
        @Size(max = 2000, message = "Từ khóa triệu chứng tối đa 2000 ký tự.") String tuKhoaTrieuChung) {
}
