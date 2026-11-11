package vn.nhom6.his.nguoidung.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.nhom6.his.nguoidung.constant.GioiTinh;
import vn.nhom6.his.nguoidung.dto.HoSoDto;
import vn.nhom6.his.nguoidung.dto.QuanTriDto;
import vn.nhom6.his.nguoidung.services.HoSoService;
import vn.nhom6.his.nguoidung.services.ThongBaoService;
import vn.nhom6.his.shared.kernel.PageResponse;

/** Bệnh nhân: hồ sơ (FR-BN-05), tìm lại hồ sơ (FR-BN-06), thông báo (FR-BN-16). */
@RestController
@RequestMapping("/api/nguoi-dung")
@RequiredArgsConstructor
@PreAuthorize("hasRole('BENH_NHAN')")
public class HoSoController {

    private final HoSoService hoSoService;
    private final ThongBaoService thongBaoService;

    @GetMapping("/ho-so")
    public List<HoSoDto> danhSach() {
        return hoSoService.cuaToi();
    }

    @GetMapping("/ho-so/{id}")
    public HoSoDto chiTiet(@PathVariable long id) {
        return hoSoService.chiTiet(id);
    }

    @PostMapping("/ho-so")
    public HoSoDto tao(@Valid @RequestBody HoSoDto.Luu r) {
        return hoSoService.tao(r);
    }

    @PutMapping("/ho-so/{id}")
    public HoSoDto sua(@PathVariable long id, @Valid @RequestBody HoSoDto.Luu r) {
        return hoSoService.capNhat(id, r);
    }

    @DeleteMapping("/ho-so/{id}")
    public ResponseEntity<Void> xoa(@PathVariable long id) {
        hoSoService.xoa(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ho-so/tim-lai")
    public List<HoSoDto.TimLai> timLai(@RequestParam(required = false) String maBenhNhan,
                                       @RequestParam(required = false) String hoTen,
                                       @RequestParam(required = false) Integer namSinh,
                                       @RequestParam(required = false) GioiTinh gioiTinh) {
        return hoSoService.timLai(maBenhNhan, hoTen, namSinh, gioiTinh);
    }

    @PostMapping("/ho-so/tim-lai/{id}/gui-otp")
    public ResponseEntity<Void> guiOtpTimLai(@PathVariable long id) {
        hoSoService.guiOtpTimLai(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/ho-so/tim-lai/{id}")
    public HoSoDto xacNhanTimLai(@PathVariable long id, @Valid @RequestBody HoSoDto.XacNhanOtp r) {
        return hoSoService.xacNhanTimLai(id, r.otp());
    }

    @GetMapping("/thong-bao")
    public PageResponse<QuanTriDto.ThongBao> thongBao(@RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(defaultValue = "20") int pageSize) {
        return thongBaoService.cuaToi(page, pageSize);
    }

    @GetMapping("/thong-bao/so-chua-xem")
    public long soChuaXem() {
        return thongBaoService.soChuaXem();
    }

    @PostMapping("/thong-bao/{id}/da-xem")
    public ResponseEntity<Void> daXem(@PathVariable long id) {
        thongBaoService.daXem(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/thong-bao/{id}")
    public ResponseEntity<Void> xoaThongBao(@PathVariable long id) {
        thongBaoService.xoa(id);
        return ResponseEntity.noContent().build();
    }
}
