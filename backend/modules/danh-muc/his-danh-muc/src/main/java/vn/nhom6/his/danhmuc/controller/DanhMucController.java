package vn.nhom6.his.danhmuc.controller;

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
import vn.nhom6.his.contracts.danhmuc.LoaiDichVu;
import vn.nhom6.his.contracts.danhmuc.LoaiPhong;
import vn.nhom6.his.danhmuc.dto.DanhMucDto;
import vn.nhom6.his.danhmuc.services.DichVuService;
import vn.nhom6.his.danhmuc.services.PhongService;
import vn.nhom6.his.danhmuc.services.ThuocService;
import vn.nhom6.his.shared.kernel.CongKhai;
import vn.nhom6.his.shared.kernel.PageResponse;

/** Phòng (FR-AD-05), dịch vụ (FR-AD-06), thuốc (FR-AD-07) — cùng mẫu với KhoaController. */
@RestController
@RequestMapping("/api/danh-muc")
@RequiredArgsConstructor
public class DanhMucController {

    private static final String ADMIN = "hasRole('QUAN_TRI')";

    private final PhongService phongService;
    private final DichVuService dichVuService;
    private final ThuocService thuocService;

    // ---- Phòng ----
    @GetMapping("/phong")
    @PreAuthorize("isAuthenticated()")
    public List<DanhMucDto.Phong> danhSachPhong(@RequestParam(required = false) Integer khoaId,
                                                @RequestParam(required = false) LoaiPhong loaiPhong) {
        return phongService.danhSach(khoaId, loaiPhong);
    }

    @GetMapping("/phong/quan-ly")
    @PreAuthorize(ADMIN)
    public PageResponse<DanhMucDto.Phong> timPhong(@RequestParam(required = false) String tuKhoa,
                                                   @RequestParam(required = false) Integer khoaId,
                                                   @RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "20") int pageSize) {
        return phongService.timKiem(tuKhoa, khoaId, page, pageSize);
    }

    @PostMapping("/phong")
    @PreAuthorize(ADMIN)
    public DanhMucDto.Phong taoPhong(@Valid @RequestBody DanhMucDto.LuuPhong r) {
        return phongService.tao(r);
    }

    @PutMapping("/phong/{id}")
    @PreAuthorize(ADMIN)
    public DanhMucDto.Phong suaPhong(@PathVariable int id, @Valid @RequestBody DanhMucDto.LuuPhong r) {
        return phongService.capNhat(id, r);
    }

    @DeleteMapping("/phong/{id}")
    @PreAuthorize(ADMIN)
    public ResponseEntity<Void> xoaPhong(@PathVariable int id) {
        phongService.doiTrangThai(id, true);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/phong/{id}/khoi-phuc")
    @PreAuthorize(ADMIN)
    public ResponseEntity<Void> khoiPhucPhong(@PathVariable int id) {
        phongService.doiTrangThai(id, false);
        return ResponseEntity.noContent().build();
    }

    // ---- Dịch vụ ----
    /** Công khai: luồng đặt khám hiện giá dịch vụ khám của khoa. */
    @GetMapping("/dich-vu")
    @CongKhai
    public List<DanhMucDto.DichVu> danhSachDichVu(@RequestParam(required = false) Integer khoaId,
                                                  @RequestParam(required = false) LoaiDichVu loaiDichVu) {
        return dichVuService.danhSach(khoaId, loaiDichVu);
    }

    @GetMapping("/dich-vu/quan-ly")
    @PreAuthorize(ADMIN)
    public PageResponse<DanhMucDto.DichVu> timDichVu(@RequestParam(required = false) String tuKhoa,
                                                     @RequestParam(required = false) Integer khoaId,
                                                     @RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "20") int pageSize) {
        return dichVuService.timKiem(tuKhoa, khoaId, page, pageSize);
    }

    @PostMapping("/dich-vu")
    @PreAuthorize(ADMIN)
    public DanhMucDto.DichVu taoDichVu(@Valid @RequestBody DanhMucDto.LuuDichVu r) {
        return dichVuService.tao(r);
    }

    @PutMapping("/dich-vu/{id}")
    @PreAuthorize(ADMIN)
    public DanhMucDto.DichVu suaDichVu(@PathVariable int id, @Valid @RequestBody DanhMucDto.LuuDichVu r) {
        return dichVuService.capNhat(id, r);
    }

    @DeleteMapping("/dich-vu/{id}")
    @PreAuthorize(ADMIN)
    public ResponseEntity<Void> xoaDichVu(@PathVariable int id) {
        dichVuService.doiTrangThai(id, true);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/dich-vu/{id}/khoi-phuc")
    @PreAuthorize(ADMIN)
    public ResponseEntity<Void> khoiPhucDichVu(@PathVariable int id) {
        dichVuService.doiTrangThai(id, false);
        return ResponseEntity.noContent().build();
    }

    // ---- Thuốc ----
    /** Bác sĩ chọn thuốc khi kê đơn. */
    @GetMapping("/thuoc")
    @PreAuthorize("hasAnyRole('BAC_SI','QUAN_TRI')")
    public List<DanhMucDto.Thuoc> chonThuoc(@RequestParam(required = false) String tuKhoa) {
        return thuocService.chon(tuKhoa);
    }

    @GetMapping("/thuoc/quan-ly")
    @PreAuthorize(ADMIN)
    public PageResponse<DanhMucDto.Thuoc> timThuoc(@RequestParam(required = false) String tuKhoa,
                                                   @RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "20") int pageSize) {
        return thuocService.timKiem(tuKhoa, page, pageSize);
    }

    @PostMapping("/thuoc")
    @PreAuthorize(ADMIN)
    public DanhMucDto.Thuoc taoThuoc(@Valid @RequestBody DanhMucDto.LuuThuoc r) {
        return thuocService.tao(r);
    }

    @PutMapping("/thuoc/{id}")
    @PreAuthorize(ADMIN)
    public DanhMucDto.Thuoc suaThuoc(@PathVariable int id, @Valid @RequestBody DanhMucDto.LuuThuoc r) {
        return thuocService.capNhat(id, r);
    }

    @DeleteMapping("/thuoc/{id}")
    @PreAuthorize(ADMIN)
    public ResponseEntity<Void> xoaThuoc(@PathVariable int id) {
        thuocService.doiTrangThai(id, true);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/thuoc/{id}/khoi-phuc")
    @PreAuthorize(ADMIN)
    public ResponseEntity<Void> khoiPhucThuoc(@PathVariable int id) {
        thuocService.doiTrangThai(id, false);
        return ResponseEntity.noContent().build();
    }
}
