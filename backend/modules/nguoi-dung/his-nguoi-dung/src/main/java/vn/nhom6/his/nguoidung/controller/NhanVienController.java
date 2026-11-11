package vn.nhom6.his.nguoidung.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.nhom6.his.nguoidung.constant.HocHam;
import vn.nhom6.his.nguoidung.constant.LoaiNhanVien;
import vn.nhom6.his.nguoidung.dto.HoSoDto;
import vn.nhom6.his.nguoidung.dto.NhanVienDto;
import vn.nhom6.his.nguoidung.dto.QuanTriDto;
import vn.nhom6.his.nguoidung.services.NhanVienService;
import vn.nhom6.his.nguoidung.services.QuanTriNguoiDungService;
import vn.nhom6.his.shared.kernel.CongKhai;
import vn.nhom6.his.shared.kernel.PageResponse;

/**
 * Bác sĩ công khai (FR-BN-07); nhân viên khoa mình (trưởng khoa, điều dưỡng trưởng);
 * Admin: nhân viên (FR-AD-03, 04), tài khoản (FR-AD-02), bệnh nhân + hồ sơ (FR-AD-08).
 */
@RestController
@RequestMapping("/api/nguoi-dung")
@RequiredArgsConstructor
public class NhanVienController {

    private static final String ADMIN = "hasRole('QUAN_TRI')";

    private final NhanVienService nhanVienService;
    private final QuanTriNguoiDungService quanTriService;

    // ---- Công khai ----
    @GetMapping("/bac-si")
    @CongKhai
    public PageResponse<NhanVienDto.BacSiCongKhai> timBacSi(@RequestParam(required = false) String tuKhoa,
                                                            @RequestParam(required = false) Integer khoaId,
                                                            @RequestParam(required = false) HocHam hocHam,
                                                            @RequestParam(defaultValue = "1") int page,
                                                            @RequestParam(defaultValue = "20") int pageSize) {
        return nhanVienService.timBacSi(tuKhoa, khoaId, hocHam, page, pageSize);
    }

    @GetMapping("/bac-si/{id}")
    @CongKhai
    public NhanVienDto.BacSiCongKhai bacSi(@PathVariable long id) {
        return nhanVienService.bacSi(id);
    }

    // ---- Trưởng khoa / điều dưỡng trưởng ----
    @GetMapping("/nhan-vien/khoa-toi")
    @PreAuthorize("hasAuthority('TRUONG_KHOA')")
    public List<NhanVienDto.Gon> nhanVienKhoaToi(@RequestParam LoaiNhanVien loai) {
        return nhanVienService.nhanVienKhoaToi(loai);
    }

    // ---- Admin: nhân viên ----
    @GetMapping("/admin/nhan-vien")
    @PreAuthorize(ADMIN)
    public PageResponse<NhanVienDto> timNhanVien(@RequestParam(required = false) String tuKhoa,
                                                 @RequestParam(required = false) LoaiNhanVien loai,
                                                 @RequestParam(required = false) Integer khoaId,
                                                 @RequestParam(required = false) Boolean daXoa,
                                                 @RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "20") int pageSize) {
        return nhanVienService.timKiem(tuKhoa, loai, khoaId, daXoa, page, pageSize);
    }

    @GetMapping("/admin/nhan-vien/{id}")
    @PreAuthorize(ADMIN)
    public NhanVienDto nhanVien(@PathVariable long id) {
        return nhanVienService.chiTiet(id);
    }

    @PostMapping("/admin/nhan-vien")
    @PreAuthorize(ADMIN)
    public NhanVienDto taoNhanVien(@Valid @RequestBody NhanVienDto.Luu r) {
        return nhanVienService.tao(r);
    }

    @PutMapping("/admin/nhan-vien/{id}")
    @PreAuthorize(ADMIN)
    public NhanVienDto suaNhanVien(@PathVariable long id, @Valid @RequestBody NhanVienDto.Luu r) {
        return nhanVienService.capNhat(id, r);
    }

    @PutMapping("/admin/nhan-vien/{id}/khoa")
    @PreAuthorize(ADMIN)
    public NhanVienDto doiKhoa(@PathVariable long id, @RequestBody NhanVienDto.DoiKhoa r) {
        return nhanVienService.doiKhoa(id, r.khoaId());
    }

    @PostMapping("/admin/nhan-vien/{id}/truong-khoa")
    @PreAuthorize(ADMIN)
    public NhanVienDto datTruongKhoa(@PathVariable long id) {
        return nhanVienService.datTruongKhoa(id, true);
    }

    @DeleteMapping("/admin/nhan-vien/{id}/truong-khoa")
    @PreAuthorize(ADMIN)
    public NhanVienDto boTruongKhoa(@PathVariable long id) {
        return nhanVienService.datTruongKhoa(id, false);
    }

    @PutMapping(value = "/admin/nhan-vien/{id}/anh-dai-dien", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize(ADMIN)
    public String anhNhanVien(@PathVariable long id, @RequestPart("file") MultipartFile file) {
        return nhanVienService.capNhatAnh(id, file);
    }

    @DeleteMapping("/admin/nhan-vien/{id}")
    @PreAuthorize(ADMIN)
    public ResponseEntity<Void> xoaNhanVien(@PathVariable long id) {
        nhanVienService.doiTrangThai(id, true);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/admin/nhan-vien/{id}/khoi-phuc")
    @PreAuthorize(ADMIN)
    public ResponseEntity<Void> khoiPhucNhanVien(@PathVariable long id) {
        nhanVienService.doiTrangThai(id, false);
        return ResponseEntity.noContent().build();
    }

    // ---- Admin: tài khoản ----
    @GetMapping("/admin/tai-khoan")
    @PreAuthorize(ADMIN)
    public PageResponse<QuanTriDto.TaiKhoan> timTaiKhoan(@RequestParam(required = false) String tuKhoa,
                                                         @RequestParam(required = false) String loai,
                                                         @RequestParam(required = false) Boolean daXoa,
                                                         @RequestParam(defaultValue = "1") int page,
                                                         @RequestParam(defaultValue = "20") int pageSize) {
        return quanTriService.timTaiKhoan(tuKhoa, loai, daXoa, page, pageSize);
    }

    @GetMapping("/admin/tai-khoan/{id}")
    @PreAuthorize(ADMIN)
    public QuanTriDto.TaiKhoanChiTiet taiKhoan(@PathVariable long id) {
        return quanTriService.chiTietTaiKhoan(id);
    }

    @DeleteMapping("/admin/tai-khoan/{id}")
    @PreAuthorize(ADMIN)
    public ResponseEntity<Void> xoaTaiKhoan(@PathVariable long id) {
        quanTriService.doiTrangThaiTaiKhoan(id, true);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/admin/tai-khoan/{id}/khoi-phuc")
    @PreAuthorize(ADMIN)
    public ResponseEntity<Void> khoiPhucTaiKhoan(@PathVariable long id) {
        quanTriService.doiTrangThaiTaiKhoan(id, false);
        return ResponseEntity.noContent().build();
    }

    // ---- Admin: bệnh nhân, hồ sơ ----
    @GetMapping("/admin/benh-nhan")
    @PreAuthorize(ADMIN)
    public PageResponse<QuanTriDto.BenhNhan> timBenhNhan(@RequestParam(required = false) String tuKhoa,
                                                         @RequestParam(defaultValue = "1") int page,
                                                         @RequestParam(defaultValue = "20") int pageSize) {
        return quanTriService.timBenhNhan(tuKhoa, page, pageSize);
    }

    @GetMapping("/admin/benh-nhan/{id}")
    @PreAuthorize(ADMIN)
    public QuanTriDto.BenhNhanChiTiet benhNhan(@PathVariable long id) {
        return quanTriService.chiTietBenhNhan(id);
    }

    @GetMapping("/admin/ho-so")
    @PreAuthorize(ADMIN)
    public PageResponse<HoSoDto> timHoSo(@RequestParam(required = false) String tuKhoa,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "20") int pageSize) {
        return quanTriService.timHoSo(tuKhoa, page, pageSize);
    }

    @DeleteMapping("/admin/ho-so/{id}")
    @PreAuthorize(ADMIN)
    public ResponseEntity<Void> xoaHoSo(@PathVariable long id) {
        quanTriService.doiTrangThaiHoSo(id, true);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/admin/ho-so/{id}/khoi-phuc")
    @PreAuthorize(ADMIN)
    public ResponseEntity<Void> khoiPhucHoSo(@PathVariable long id) {
        quanTriService.doiTrangThaiHoSo(id, false);
        return ResponseEntity.noContent().build();
    }
}
