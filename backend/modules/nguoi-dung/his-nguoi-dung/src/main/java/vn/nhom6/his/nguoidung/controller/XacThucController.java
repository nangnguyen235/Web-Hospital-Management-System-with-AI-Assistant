package vn.nhom6.his.nguoidung.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.nhom6.his.nguoidung.dto.TaiKhoanDto.DangNhapResponse;
import vn.nhom6.his.nguoidung.dto.XacThucDto;
import vn.nhom6.his.nguoidung.services.XacThucService;
import vn.nhom6.his.shared.kernel.CongKhai;

/** Đăng ký OTP (FR-BN-01), đăng nhập OTP (FR-BN-03), quên mật khẩu (FR-BN-04), đổi mật khẩu. */
@RestController
@RequestMapping("/api/nguoi-dung")
@RequiredArgsConstructor
public class XacThucController {

    private final XacThucService xacThucService;

    @PostMapping("/dang-ky/gui-otp")
    @CongKhai
    public ResponseEntity<Void> guiOtpDangKy(@Valid @RequestBody XacThucDto.GuiOtpDangKy r) {
        xacThucService.guiOtpDangKy(r);
        return ResponseEntity.noContent().build();
    }

    /** Đúng OTP → tạo tài khoản và đăng nhập luôn. */
    @PostMapping("/dang-ky")
    @CongKhai
    public DangNhapResponse dangKy(@Valid @RequestBody XacThucDto.DangKy r) {
        return xacThucService.dangKy(r);
    }

    @PostMapping("/dang-nhap-otp/gui-otp")
    @CongKhai
    public ResponseEntity<Void> guiOtpDangNhap(@Valid @RequestBody XacThucDto.GuiOtpEmail r) {
        xacThucService.guiOtpDangNhap(r);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/dang-nhap-otp")
    @CongKhai
    public DangNhapResponse dangNhapOtp(@Valid @RequestBody XacThucDto.DangNhapOtp r) {
        return xacThucService.dangNhapOtp(r);
    }

    @PostMapping("/quen-mat-khau/gui-otp")
    @CongKhai
    public ResponseEntity<Void> guiOtpQuenMatKhau(@Valid @RequestBody XacThucDto.GuiOtpEmail r) {
        xacThucService.guiOtpQuenMatKhau(r);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/quen-mat-khau")
    @CongKhai
    public ResponseEntity<Void> datLaiMatKhau(@Valid @RequestBody XacThucDto.DatLaiMatKhau r) {
        xacThucService.datLaiMatKhau(r);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/toi/doi-mat-khau")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> doiMatKhau(@Valid @RequestBody XacThucDto.DoiMatKhau r) {
        xacThucService.doiMatKhau(r);
        return ResponseEntity.noContent().build();
    }
}
