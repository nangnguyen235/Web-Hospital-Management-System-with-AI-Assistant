package vn.nhom6.his.hosobenhan.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
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
import vn.nhom6.his.hosobenhan.constant.TrangThaiBenhAn;
import vn.nhom6.his.hosobenhan.constant.TrangThaiXetNghiem;
import vn.nhom6.his.hosobenhan.dto.BenhAnDto;
import vn.nhom6.his.hosobenhan.services.BenhNhanKhamService;
import vn.nhom6.his.hosobenhan.services.KhamService;
import vn.nhom6.his.hosobenhan.services.NhacThuocService;
import vn.nhom6.his.hosobenhan.services.TiepNhanService;
import vn.nhom6.his.hosobenhan.services.XetNghiemService;

/** Tiếp nhận (điều dưỡng), khám (bác sĩ), xét nghiệm, bệnh án + đánh giá + nhắc thuốc (bệnh nhân). */
@RestController
@RequestMapping("/api/ho-so-benh-an")
@RequiredArgsConstructor
public class HoSoBenhAnController {

    private static final String BAC_SI = "hasRole('BAC_SI')";
    private static final String DIEU_DUONG = "hasRole('DIEU_DUONG')";
    private static final String BENH_NHAN = "hasRole('BENH_NHAN')";

    private final TiepNhanService tiepNhanService;
    private final KhamService khamService;
    private final XetNghiemService xetNghiemService;
    private final BenhNhanKhamService benhNhanKhamService;
    private final NhacThuocService nhacThuocService;

    @Value("${his.frontend-url:https://nangnguyen.me}")
    private String frontendUrl;

    // ---------- Điều dưỡng: tiếp nhận (FR-DD-03…05) ----------
    @GetMapping("/tiep-nhan/kiem-tra")
    @PreAuthorize(DIEU_DUONG)
    public BenhAnDto.PhieuTiepNhan kiemTraPhieu(@RequestParam String maPhieu, @RequestParam int phongId) {
        return tiepNhanService.kiemTra(maPhieu, phongId);
    }

    @PostMapping("/tiep-nhan")
    @PreAuthorize(DIEU_DUONG)
    public BenhAnDto.KetQuaTiepNhan tiepNhan(@Valid @RequestBody BenhAnDto.TiepNhan r) {
        return tiepNhanService.tiepNhan(r);
    }

    // ---------- Bác sĩ (FR-BS-03…05, 07…10) ----------
    @GetMapping("/lich/{lichKhamId}")
    @PreAuthorize(BAC_SI)
    public List<BenhAnDto.BenhAnGon> benhAnCuaLich(@PathVariable long lichKhamId,
                                                   @RequestParam(required = false) TrangThaiBenhAn trangThai) {
        return khamService.cuaLich(lichKhamId, trangThai);
    }

    @GetMapping("/benh-an/{id}")
    @PreAuthorize(BAC_SI)
    public BenhAnDto.ChiTiet chiTietBacSi(@PathVariable long id) {
        return khamService.chiTiet(id);
    }

    @PostMapping("/benh-an/{id}/nhan-kham")
    @PreAuthorize(BAC_SI)
    public BenhAnDto.ChiTiet nhanKham(@PathVariable long id) {
        return khamService.nhanKham(id);
    }

    @PutMapping("/benh-an/{id}")
    @PreAuthorize(BAC_SI)
    public BenhAnDto.ChiTiet ghi(@PathVariable long id, @Valid @RequestBody BenhAnDto.GhiBenhAn r) {
        return khamService.ghi(id, r);
    }

    @PostMapping("/benh-an/{id}/hoan-tat")
    @PreAuthorize(BAC_SI)
    public BenhAnDto.ChiTiet hoanTat(@PathVariable long id) {
        return khamService.hoanTat(id);
    }

    @GetMapping("/benh-an/{id}/lich-su")
    @PreAuthorize(BAC_SI)
    public List<BenhAnDto.LichSu> lichSuTrongKhoa(@PathVariable long id) {
        return khamService.lichSu(id);
    }

    @PostMapping("/benh-an/{id}/xet-nghiem")
    @PreAuthorize(BAC_SI)
    public BenhAnDto.XetNghiem chiDinh(@PathVariable long id, @Valid @RequestBody BenhAnDto.ChiDinh r) {
        return khamService.chiDinh(id, r);
    }

    @DeleteMapping("/xet-nghiem/{id}")
    @PreAuthorize(BAC_SI)
    public ResponseEntity<Void> xoaXetNghiem(@PathVariable long id) {
        khamService.xoaXetNghiem(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/benh-an/{id}/thuoc")
    @PreAuthorize(BAC_SI)
    public BenhAnDto.KetQuaKeThuoc keThuoc(@PathVariable long id, @Valid @RequestBody BenhAnDto.KeThuoc r) {
        return khamService.keThuoc(id, r);
    }

    @DeleteMapping("/thuoc/{chiTietId}")
    @PreAuthorize(BAC_SI)
    public BenhAnDto.DonThuoc xoaThuoc(@PathVariable long chiTietId) {
        return khamService.xoaThuoc(chiTietId);
    }

    // ---------- Điều dưỡng phòng xét nghiệm (FR-DD-09…11) ----------
    @GetMapping("/xet-nghiem/phong/{phongId}")
    @PreAuthorize(DIEU_DUONG)
    public List<BenhAnDto.XetNghiem> xetNghiemCuaPhong(@PathVariable int phongId,
                                                       @RequestParam(required = false) TrangThaiXetNghiem trangThai) {
        return xetNghiemService.homNayCuaPhong(phongId, trangThai);
    }

    @GetMapping("/xet-nghiem/phong/{phongId}/so")
    @PreAuthorize(DIEU_DUONG)
    public BenhAnDto.SoPhongXetNghiem soPhong(@PathVariable int phongId) {
        return xetNghiemService.soCuaPhong(phongId);
    }

    @PostMapping("/xet-nghiem/phong/{phongId}/goi-tiep")
    @PreAuthorize(DIEU_DUONG)
    public BenhAnDto.SoPhongXetNghiem goiTiepXetNghiem(@PathVariable int phongId) {
        return xetNghiemService.goiTiep(phongId);
    }

    @PostMapping("/xet-nghiem/{id}/nhan")
    @PreAuthorize(DIEU_DUONG)
    public BenhAnDto.XetNghiem nhanXetNghiem(@PathVariable long id) {
        return xetNghiemService.nhan(id);
    }

    @PostMapping(value = "/xet-nghiem/{id}/hoan-tat", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize(DIEU_DUONG)
    public BenhAnDto.XetNghiem hoanTatXetNghiem(@PathVariable long id,
                                                @RequestParam(value = "ketQua", required = false) String ketQua,
                                                @RequestPart(value = "file", required = false) MultipartFile file) {
        return xetNghiemService.hoanTat(id, ketQua, file);
    }

    // ---------- Xét nghiệm: tra mã, thanh toán, kết quả (FR-BN-12, 13; quầy) ----------
    @GetMapping("/xet-nghiem/ma/{ma}")
    @PreAuthorize("hasAnyRole('BENH_NHAN','DIEU_DUONG')")
    public BenhAnDto.XetNghiem traMa(@PathVariable String ma) {
        return xetNghiemService.traMa(ma);
    }

    @PostMapping("/xet-nghiem/ma/{ma}/thanh-toan")
    @PreAuthorize("hasAnyRole('BENH_NHAN','DIEU_DUONG')")
    public BenhAnDto.ThanhToan thanhToan(@PathVariable String ma, HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        return xetNghiemService.thanhToan(ma, origin == null || origin.isBlank() ? frontendUrl : origin, request.getRemoteAddr());
    }

    @GetMapping("/xet-nghiem/{id}/ket-qua")
    @PreAuthorize("hasAnyRole('BENH_NHAN','BAC_SI','DIEU_DUONG')")
    public String urlKetQua(@PathVariable long id) {
        return xetNghiemService.urlKetQua(id);
    }

    // ---------- Bệnh nhân (FR-BN-11, 14, 15) ----------
    @GetMapping("/lich-su")
    @PreAuthorize(BENH_NHAN)
    public List<BenhAnDto.LichSu> lichSu(@RequestParam long hoSoId) {
        return benhNhanKhamService.lichSu(hoSoId);
    }

    @GetMapping("/cua-toi/{benhAnId}")
    @PreAuthorize(BENH_NHAN)
    public BenhAnDto.ChiTiet benhAnCuaToi(@PathVariable long benhAnId) {
        return benhNhanKhamService.chiTiet(benhAnId);
    }

    @PostMapping("/cua-toi/{benhAnId}/danh-gia")
    @PreAuthorize(BENH_NHAN)
    public ResponseEntity<Void> danhGia(@PathVariable long benhAnId, @Valid @RequestBody BenhAnDto.DanhGia r) {
        benhNhanKhamService.danhGia(benhAnId, r);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nhac-thuoc")
    @PreAuthorize(BENH_NHAN)
    public List<BenhAnDto.NhacThuoc> nhacThuoc(@RequestParam long hoSoId) {
        return nhacThuocService.cuaHoSo(hoSoId);
    }

    @PostMapping("/nhac-thuoc")
    @PreAuthorize(BENH_NHAN)
    public BenhAnDto.NhacThuoc taoNhacThuoc(@Valid @RequestBody BenhAnDto.TaoNhacThuoc r) {
        return nhacThuocService.tao(r);
    }

    @PutMapping("/nhac-thuoc/{id}")
    @PreAuthorize(BENH_NHAN)
    public BenhAnDto.NhacThuoc suaNhacThuoc(@PathVariable long id, @Valid @RequestBody BenhAnDto.TaoNhacThuoc r) {
        return nhacThuocService.sua(id, r);
    }

    @DeleteMapping("/nhac-thuoc/{id}")
    @PreAuthorize(BENH_NHAN)
    public ResponseEntity<Void> xoaNhacThuoc(@PathVariable long id) {
        nhacThuocService.xoa(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nhac-thuoc/theo-ngay")
    @PreAuthorize(BENH_NHAN)
    public List<BenhAnDto.LanNhac> theoNgay(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngay) {
        return nhacThuocService.theoNgay(ngay);
    }

    @GetMapping("/nhac-thuoc/den-gio")
    @PreAuthorize(BENH_NHAN)
    public List<BenhAnDto.LanNhac> denGio() {
        return nhacThuocService.denGio();
    }

    @PostMapping("/nhac-thuoc/lan/{chiTietId}/{hanhDong}")
    @PreAuthorize(BENH_NHAN)
    public BenhAnDto.LanNhac xacNhan(@PathVariable long chiTietId, @PathVariable BenhAnDto.HanhDongNhac hanhDong) {
        return nhacThuocService.xacNhan(chiTietId, hanhDong);
    }
}
