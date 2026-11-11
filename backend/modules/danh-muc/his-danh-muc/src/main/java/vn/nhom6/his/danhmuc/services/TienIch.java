package vn.nhom6.his.danhmuc.services;

import java.util.Map;
import java.util.stream.Collectors;
import vn.nhom6.his.danhmuc.entity.Khoa;
import vn.nhom6.his.danhmuc.repository.KhoaRepository;

final class TienIch {

    private TienIch() {
    }

    static String trong(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    /** Tên mọi khoa (kể cả đã xóa) — vài chục dòng, đọc một lần cho cả danh sách. */
    static Map<Integer, String> tenKhoa(KhoaRepository repo) {
        return repo.findAll().stream().collect(Collectors.toMap(Khoa::getId, Khoa::getTenKhoa));
    }
}
