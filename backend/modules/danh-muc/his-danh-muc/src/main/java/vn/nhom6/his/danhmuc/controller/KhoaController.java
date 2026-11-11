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
import vn.nhom6.his.danhmuc.dto.KhoaDto;
import vn.nhom6.his.danhmuc.dto.LuuKhoaRequest;
import vn.nhom6.his.danhmuc.services.KhoaService;
import vn.nhom6.his.shared.kernel.CongKhai;
import vn.nhom6.his.shared.kernel.PageResponse;

/** LÁT MẪU: quản lý khoa (FR-AD-04) + danh sách công khai (FR-BN-07). */
@RestController
@RequestMapping("/api/danh-muc/khoa")
@RequiredArgsConstructor
public class KhoaController {

    private final KhoaService khoaService;

    /** Danh sách khoa đang hoạt động (công khai). */
    @GetMapping
    @CongKhai
    public List<KhoaDto> danhSach() {
        return khoaService.danhSachCongKhai();
    }

    /** Admin: tìm khoa (gồm khoa đã xóa), phân trang. */
    @GetMapping("/quan-ly")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public PageResponse<KhoaDto> timKiem(@RequestParam(required = false) String tuKhoa,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "20") int pageSize) {
        return khoaService.timKiem(tuKhoa, page, pageSize);
    }

    @PostMapping
    @PreAuthorize("hasRole('QUAN_TRI')")
    public KhoaDto tao(@Valid @RequestBody LuuKhoaRequest request) {
        return khoaService.tao(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public KhoaDto capNhat(@PathVariable int id, @Valid @RequestBody LuuKhoaRequest request) {
        return khoaService.capNhat(id, request);
    }

    /** Xóa mềm. */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<Void> xoa(@PathVariable int id) {
        khoaService.xoa(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/khoi-phuc")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<Void> khoiPhuc(@PathVariable int id) {
        khoaService.khoiPhuc(id);
        return ResponseEntity.noContent().build();
    }
}
