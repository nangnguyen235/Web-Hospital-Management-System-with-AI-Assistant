package vn.nhom6.his.danhmuc.dto;

import vn.nhom6.his.danhmuc.entity.Khoa;

public record KhoaDto(int id, String tenKhoa, String moTa, String tuKhoaTrieuChung, boolean daXoa) {

    public static KhoaDto tu(Khoa k) {
        return new KhoaDto(k.getId(), k.getTenKhoa(), k.getMoTa(), k.getTuKhoaTrieuChung(), k.isDaXoa());
    }
}
