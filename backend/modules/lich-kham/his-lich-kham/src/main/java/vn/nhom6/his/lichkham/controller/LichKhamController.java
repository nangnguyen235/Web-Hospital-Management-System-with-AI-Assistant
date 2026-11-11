package vn.nhom6.his.lichkham.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
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
import vn.nhom6.his.lichkham.constant.Buoi;
import vn.nhom6.his.lichkham.dto.LichKhamDto;
import vn.nhom6.his.lichkham.services.DatKhamService;
import vn.nhom6.his.lichkham.services.DieuDuongService;
import vn.nhom6.his.lichkham.services.LichKhamService;
import vn.nhom6.his.shared.kernel.CongKhai;

/** Lịch khám, đặt khám, phiếu, gọi số, quầy, phân công điều dưỡng (module LichKham). */
@RestController
@RequestMapping("/api/lich-kham")
@RequiredArgsConstructor
public class LichKhamController {

    private static final String TRUONG_KHOA = "hasRole('BAC_SI') and hasAuthority('TRUONG_KHOA')";
    private static final String DIEU_DUONG_TRUONG = "hasRole('DIEU_DUONG') and hasAuthority('TRUONG_KHOA')";

    private final LichKhamService lichKhamService;
    private final DatKhamService datKhamService;
    private final DieuDuongService dieuDuongService;

    @Value("${his.frontend-url:https://nangnguyen.me}")
    private String frontendUrl;

    // ---------- Công khai (FR-BN-07, FR-HT-01) ----------
    @GetMapping("/bac-si/{bacSiId}/lich")
    @CongKhai
    public List<LichKhamDto.Lich> lichCuaBacSi(@PathVariable long bacSiId,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tu,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate den) {
        return lichKhamService.lichCongKhaiCuaBacSi(bacSiId, tu, den);
    }

    @GetMapping("/lich/{id}")
    @CongKhai
    public LichKhamDto.Lich chiTietLich(@PathVariable long id) {
        return lichKhamService.chiTiet(id);
    }

    // ---------- Trưởng khoa (FR-BS-11…14) ----------
    @PostMapping("/lich")
    @PreAuthorize(TRUONG_KHOA)
    public List<LichKhamDto.Lich> taoLich(@Valid @RequestBody LichKhamDto.TaoLich r) {
        return lichKhamService.tao(r);
    }

    @PutMapping("/lich/{id}")
    @PreAuthorize(TRUONG_KHOA)
    public LichKhamDto.Lich suaLich(@PathVariable long id, @Valid @RequestBody LichKhamDto.SuaLich r) {
        return lichKhamService.sua(id, r);
    }

    @DeleteMapping("/lich/{id}")
    @PreAuthorize(TRUONG_KHOA)
    public ResponseEntity<Void> xoaLich(@PathVariable long id) {
        lichKhamService.xoa(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/lich/khoa")
    @PreAuthorize(TRUONG_KHOA)
    public List<LichKhamDto.Lich> lichKhoa(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tu,
                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate den,
                                           @RequestParam(required = false) Integer phongId,
                                           @RequestParam(required = false) Long bacSiId) {
        return lichKhamService.lichKhoa(tu, den, phongId, bacSiId);
    }

    @GetMapping("/lich/khoa/bac-si-trong")
    @PreAuthorize(TRUONG_KHOA)
    public List<LichKhamDto.BacSiTrong> bacSiTrong(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngay,
                                                   @RequestParam(required = false) Buoi buoi) {
        return lichKhamService.bacSiChuaCoLich(ngay, buoi);
    }

    // ---------- Bác sĩ (FR-BS-02) ----------
    @GetMapping("/lich/cua-toi")
    @PreAuthorize("hasRole('BAC_SI')")
    public List<LichKhamDto.Lich> lichCuaToi(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tu,
                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate den) {
        return lichKhamService.lichCuaToi(tu, den);
    }

    // ---------- Bệnh nhân (FR-BN-08…10) ----------
    @PostMapping("/dat-kham")
    @PreAuthorize("hasRole('BENH_NHAN')")
    public LichKhamDto.KetQuaDatKham datKham(@Valid @RequestBody LichKhamDto.DatKham r, HttpServletRequest request) {
        return datKhamService.datKham(r, nguon(request), request.getRemoteAddr());
    }

    @GetMapping("/phieu")
    @PreAuthorize("hasRole('BENH_NHAN')")
    public List<LichKhamDto.Phieu> phieuCuaHoSo(@RequestParam long hoSoId) {
        return datKhamService.phieuCuaHoSo(hoSoId);
    }

    @GetMapping("/phieu/{id}")
    @PreAuthorize("isAuthenticated()")
    public LichKhamDto.Phieu chiTietPhieu(@PathVariable long id) {
        return datKhamService.chiTiet(id);
    }

    @GetMapping("/phieu/giao-dich/{giaoDichId}")
    @PreAuthorize("isAuthenticated()")
    public List<LichKhamDto.Phieu> phieuCuaGiaoDich(@PathVariable long giaoDichId) {
        return datKhamService.phieuCuaGiaoDich(giaoDichId);
    }

    // ---------- Điều dưỡng (FR-DD-02, 07, 08) ----------
    @PostMapping("/{lichKhamId}/goi-tiep")
    @PreAuthorize("hasRole('DIEU_DUONG')")
    public LichKhamDto.GoiTiep goiTiep(@PathVariable long lichKhamId) {
        return dieuDuongService.goiTiep(lichKhamId);
    }

    @GetMapping("/dieu-duong/phong-hom-nay")
    @PreAuthorize("hasRole('DIEU_DUONG')")
    public List<LichKhamDto.PhongHomNay> phongHomNay() {
        return dieuDuongService.phongHomNay();
    }

    @GetMapping("/phong/{phongId}/hom-nay")
    @PreAuthorize("hasAnyRole('DIEU_DUONG','BAC_SI')")
    public List<LichKhamDto.Lich> lichHomNayCuaPhong(@PathVariable int phongId) {
        return dieuDuongService.lichHomNayCuaPhong(phongId);
    }

    @GetMapping("/phan-cong/cua-toi")
    @PreAuthorize("hasRole('DIEU_DUONG')")
    public List<LichKhamDto.LichDieuDuong> phanCongCuaToi() {
        return dieuDuongService.phanCongCuaToi();
    }

    @PostMapping("/quay/phieu")
    @PreAuthorize("hasRole('DIEU_DUONG')")
    public LichKhamDto.KetQuaQuay taoPhieuQuay(@Valid @RequestBody LichKhamDto.TaoPhieuQuay r, HttpServletRequest request) {
        return datKhamService.taoPhieuQuay(r, nguon(request), request.getRemoteAddr());
    }

    // ---------- Điều dưỡng trưởng (FR-DD-12) ----------
    @GetMapping("/phan-cong")
    @PreAuthorize(DIEU_DUONG_TRUONG)
    public List<LichKhamDto.LichDieuDuong> phanCongKhoa() {
        return dieuDuongService.phanCongCuaKhoa();
    }

    @PostMapping("/phan-cong")
    @PreAuthorize(DIEU_DUONG_TRUONG)
    public List<LichKhamDto.LichDieuDuong> phanCong(@Valid @RequestBody LichKhamDto.PhanCong r) {
        return dieuDuongService.phanCong(r);
    }

    @DeleteMapping("/phan-cong")
    @PreAuthorize(DIEU_DUONG_TRUONG)
    public ResponseEntity<Void> xoaPhanCong(@RequestParam List<Long> ids) {
        dieuDuongService.xoaPhanCong(ids);
        return ResponseEntity.noContent().build();
    }

    // ---------- Admin (FR-AD-10) ----------
    @GetMapping("/admin/lich")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public List<LichKhamDto.Lich> lichAdmin(@RequestParam(required = false) Integer khoaId,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tu,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate den,
                                            @RequestParam(required = false) Integer phongId,
                                            @RequestParam(required = false) Long bacSiId) {
        return lichKhamService.lichAdmin(khoaId, tu, den, phongId, bacSiId);
    }

    /** Origin của frontend để VNPAY quay về đúng nơi (máy dev hoặc nangnguyen.me). */
    private String nguon(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        return origin == null || origin.isBlank() ? frontendUrl : origin;
    }
}
