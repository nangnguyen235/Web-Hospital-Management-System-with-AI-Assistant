package vn.nhom6.his.nguoidung.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.nhom6.his.nguoidung.dto.HoSoDto;
import vn.nhom6.his.nguoidung.dto.QuanTriDto;
import vn.nhom6.his.nguoidung.entity.BenhNhan;
import vn.nhom6.his.nguoidung.services.BenhNhanService;

/** Điều dưỡng: tra CCCD (FR-DD-04), tra/sửa hồ sơ (FR-DD-06), hồ sơ khách quầy (FR-DD-08). Chỉ thông tin hành chính. */
@RestController
@RequestMapping("/api/nguoi-dung/dieu-duong")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DIEU_DUONG')")
public class DieuDuongHoSoController {

    private final BenhNhanService benhNhanService;

    @GetMapping("/ho-so")
    public List<HoSoDto> traHoSo(@RequestParam(required = false) String hoTen, @RequestParam(required = false) String soCccd) {
        return benhNhanService.traHoSo(hoTen, soCccd);
    }

    @GetMapping("/benh-nhan")
    public QuanTriDto.BenhNhan traCccd(@RequestParam String soCccd) {
        BenhNhan b = benhNhanService.traCccd(soCccd);
        return new QuanTriDto.BenhNhan(b.getId(), b.getMaBenhNhan(), b.getHoTen(), b.getSoDienThoai(), b.getSoCccd(),
                b.getNgaySinh(), b.getGioiTinh(), b.isDaXoa());
    }

    @PostMapping("/ho-so")
    public HoSoDto taoHoSoQuay(@Valid @RequestBody HoSoDto.Luu r) {
        return benhNhanService.taoHoSoQuay(r);
    }

    @PutMapping("/ho-so/{id}")
    public HoSoDto suaHoSo(@PathVariable long id, @Valid @RequestBody HoSoDto.Luu r) {
        return benhNhanService.suaHoSo(id, r);
    }

    /** Liên kết hồ sơ với bệnh nhân bằng CCCD (dùng ở quầy trước khi tạo phiếu). */
    @PostMapping("/ho-so/{id}/lien-ket")
    public HoSoDto lienKet(@PathVariable long id, @RequestBody LienKet r) {
        return benhNhanService.lienKetVaTra(id, r.soCccd());
    }

    public record LienKet(String soCccd) {
    }
}
