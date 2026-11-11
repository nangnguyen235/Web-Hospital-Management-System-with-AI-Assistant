package vn.nhom6.his.nguoidung.services;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import vn.nhom6.his.contracts.hosobenhan.DanhGiaDaTaoIntegrationEvent;
import vn.nhom6.his.contracts.lichkham.LuotKhamDaGoiIntegrationEvent;
import vn.nhom6.his.nguoidung.entity.BacSi;
import vn.nhom6.his.nguoidung.entity.HoSoBenhNhan;
import vn.nhom6.his.nguoidung.repository.HoSoBenhNhanRepository;
import vn.nhom6.his.nguoidung.repository.NhanVienRepository;

/** NguoiDung nghe sự kiện của module khác (modular-monolith.md mục 3.1). Chạy sau commit, transaction mới. */
@Service
@RequiredArgsConstructor
public class NguoiDungSuKien {

    private final HoSoBenhNhanRepository hoSoRepository;
    private final NhanVienRepository nhanVienRepository;
    private final ThongBaoService thongBaoService;

    /** BR-15: thông báo "Đến lượt khám bệnh của bạn" cho các số vừa gọi mà hồ sơ có tài khoản. */
    @ApplicationModuleListener
    void luotKhamDaGoi(LuotKhamDaGoiIntegrationEvent e) {
        Map<Long, HoSoBenhNhan> hoSo = hoSoRepository.findAllById(
                        e.danhSach().stream().map(LuotKhamDaGoiIntegrationEvent.SoDaGoi::hoSoId).toList())
                .stream().collect(Collectors.toMap(HoSoBenhNhan::getId, h -> h));
        for (var so : e.danhSach()) {
            HoSoBenhNhan h = hoSo.get(so.hoSoId());
            if (h != null && h.getTaiKhoanId() != null) {
                thongBaoService.tao(h.getTaiKhoanId(), "Đến lượt khám bệnh của bạn",
                        "Số " + so.soThuTu() + " — " + h.getHoTen() + ", mời đến " + e.tenPhong() + ".",
                        "{\"hanhDong\":\"DEN_LUOT_KHAM\",\"lichKhamId\":" + e.lichKhamId() + ",\"hoSoId\":" + h.getId() + "}");
            }
        }
    }

    /** BR-20: điểm trung bình mới của bác sĩ do HoSoBenhAn tính. Nhận trùng thì ghi lại cùng giá trị (an toàn). */
    @ApplicationModuleListener
    void danhGiaDaTao(DanhGiaDaTaoIntegrationEvent e) {
        nhanVienRepository.findById(e.bacSiId()).ifPresent(nv -> {
            if (nv instanceof BacSi b) {
                b.setDiemDanhGia(e.diemTrungBinh() == null ? BigDecimal.ZERO : e.diemTrungBinh());
            }
        });
    }
}
