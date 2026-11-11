package vn.nhom6.his.lichkham.dto;

import java.time.LocalDate;
import java.util.List;
import vn.nhom6.his.lichkham.constant.Buoi;

/** Màn hình số đang gọi: chỉ số + phòng + khoa, KHÔNG có họ tên (NFR-03). */
public record SoDangGoiDto(long lichKhamId, int soDangGoi, List<Integer> cacSoVuaGoi, String tenPhong, String tenKhoa,
                           LocalDate ngay, Buoi buoi) {
}
