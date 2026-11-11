package vn.nhom6.his.nguoidung.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.nhom6.his.nguoidung.dto.TaiKhoanDto.DangNhapRequest;
import vn.nhom6.his.nguoidung.dto.TaiKhoanDto.DangNhapResponse;
import vn.nhom6.his.nguoidung.dto.TaiKhoanDto.ThongTinTaiKhoan;
import vn.nhom6.his.nguoidung.services.TaiKhoanService;
import vn.nhom6.his.shared.kernel.CongKhai;

@RestController
@RequestMapping("/api/nguoi-dung")
@RequiredArgsConstructor
public class TaiKhoanController {

    private final TaiKhoanService taiKhoanService;

    /** Đăng nhập bằng email/SĐT + mật khẩu, nhận JWT 60 phút (FR-BN-02, FR-BS-01, FR-DD-01). */
    @PostMapping("/dang-nhap")
    @CongKhai
    public DangNhapResponse dangNhap(@Valid @RequestBody DangNhapRequest request) {
        return taiKhoanService.dangNhap(request);
    }

    /** Thông tin tài khoản đang đăng nhập. */
    @GetMapping("/toi")
    @PreAuthorize("isAuthenticated()")
    public ThongTinTaiKhoan toi() {
        return taiKhoanService.toi();
    }

    /** Đổi ảnh đại diện (≤ 2 MB, jpg/png/webp) — tải lên R2, trả Presigned URL 15 phút. */
    @PutMapping(value = "/toi/anh-dai-dien", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public String anhDaiDien(@RequestPart("file") MultipartFile file) {
        return taiKhoanService.capNhatAnhDaiDien(file);
    }
}
