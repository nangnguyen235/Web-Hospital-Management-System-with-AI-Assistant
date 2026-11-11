package vn.nhom6.his.nguoidung.services;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom6.his.nguoidung.dto.TaiKhoanDto.DangNhapResponse;
import vn.nhom6.his.nguoidung.dto.XacThucDto;
import vn.nhom6.his.nguoidung.entity.NguoiDungTam;
import vn.nhom6.his.nguoidung.entity.NhanVien;
import vn.nhom6.his.nguoidung.entity.TaiKhoan;
import vn.nhom6.his.nguoidung.repository.NguoiDungTamRepository;
import vn.nhom6.his.nguoidung.repository.NhanVienRepository;
import vn.nhom6.his.nguoidung.repository.TaiKhoanRepository;
import vn.nhom6.his.shared.kernel.CurrentUser;
import vn.nhom6.his.shared.kernel.Loi;
import vn.nhom6.his.shared.kernel.LoiNghiepVuException;

/** Đăng ký (FR-BN-01), đăng nhập OTP (FR-BN-03), quên mật khẩu (FR-BN-04), đổi mật khẩu — BR-02. */
@Service
@RequiredArgsConstructor
@Transactional(noRollbackFor = LoiNghiepVuException.class)
public class XacThucService {

    private final TaiKhoanRepository taiKhoanRepository;
    private final NguoiDungTamRepository tamRepository;
    private final NhanVienRepository nhanVienRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final TaiKhoanService taiKhoanService;
    private final CurrentUser currentUser;

    /** Email và SĐT chưa dùng → OTP lưu ở tk_nguoi_dung_tam (xin lại thì thay bản cũ). */
    public void guiOtpDangKy(XacThucDto.GuiOtpDangKy r) {
        String email = chuan(r.email());
        kiemTraChuaDung(email, r.soDienThoai());
        NguoiDungTam tam = tamRepository.findFirstByEmailAndDaXoaFalseOrderByIdDesc(email).orElseGet(NguoiDungTam::new);
        tam.setEmail(email);
        otpService.taoVaGui(tam, email, "đăng ký tài khoản");
        tamRepository.save(tam);
    }

    public DangNhapResponse dangKy(XacThucDto.DangKy r) {
        String email = chuan(r.email());
        NguoiDungTam tam = tamRepository.findFirstByEmailAndDaXoaFalseOrderByIdDesc(email)
                .orElseThrow(() -> new Loi("OTP_HET_HIEU_LUC", "Vui lòng xin mã OTP trước khi đăng ký.").ngoaiLe());
        otpService.kiemTra(tam, r.otp());
        kiemTraChuaDung(email, r.soDienThoai());
        tam.setDaXoa(true);

        TaiKhoan tk = new TaiKhoan();
        tk.setHoTen(r.hoTen().trim());
        tk.setEmail(email);
        tk.setSoDienThoai(r.soDienThoai().trim());
        tk.setMatKhauHash(passwordEncoder.encode(r.matKhau()));
        taiKhoanRepository.save(tk);
        return taiKhoanService.taoPhien(tk, null);
    }

    public void guiOtpDangNhap(XacThucDto.GuiOtpEmail r) {
        TaiKhoan tk = timTheoEmail(r.email());
        otpService.taoVaGui(tk, tk.getEmail(), "đăng nhập");
    }

    public DangNhapResponse dangNhapOtp(XacThucDto.DangNhapOtp r) {
        TaiKhoan tk = timTheoEmail(r.email());
        otpService.kiemTra(tk, r.otp());
        NhanVien nv = tk.getNhanVienId() == null ? null : nhanVienRepository.findById(tk.getNhanVienId()).orElse(null);
        if (nv != null && nv.isDaXoa()) {
            throw new Loi("TAI_KHOAN_BI_KHOA", "Tài khoản đã bị khóa.").ngoaiLe();
        }
        return taiKhoanService.taoPhien(tk, nv);
    }

    public void guiOtpQuenMatKhau(XacThucDto.GuiOtpEmail r) {
        TaiKhoan tk = timTheoEmail(r.email());
        otpService.taoVaGui(tk, tk.getEmail(), "đặt lại mật khẩu");
    }

    public void datLaiMatKhau(XacThucDto.DatLaiMatKhau r) {
        if (!r.matKhauMoi().equals(r.nhapLaiMatKhau())) {
            throw new Loi("MAT_KHAU_KHONG_KHOP", "Mật khẩu nhập lại không khớp.").ngoaiLe();
        }
        TaiKhoan tk = timTheoEmail(r.email());
        otpService.kiemTra(tk, r.otp());
        tk.setMatKhauHash(passwordEncoder.encode(r.matKhauMoi()));
    }

    public void doiMatKhau(XacThucDto.DoiMatKhau r) {
        TaiKhoan tk = taiKhoanRepository.findByIdAndDaXoaFalse(currentUser.taiKhoanId())
                .orElseThrow(() -> Loi.khongTimThay("tài khoản").ngoaiLe());
        if (!passwordEncoder.matches(r.matKhauCu(), tk.getMatKhauHash())) {
            throw new Loi("SAI_MAT_KHAU", "Mật khẩu hiện tại không đúng.").ngoaiLe();
        }
        tk.setMatKhauHash(passwordEncoder.encode(r.matKhauMoi()));
    }

    private TaiKhoan timTheoEmail(String email) {
        return taiKhoanRepository.findFirstByEmailAndDaXoaFalse(chuan(email))
                .orElseThrow(() -> new Loi("KHONG_TIM_THAY", "Không có tài khoản nào dùng email này.",
                        vn.nhom6.his.shared.kernel.LoaiLoi.KHONG_TIM_THAY).ngoaiLe());
    }

    private void kiemTraChuaDung(String email, String soDienThoai) {
        if (taiKhoanRepository.existsByEmail(email)) {
            throw Loi.xungDot("EMAIL_DA_DUNG", "Email đã được dùng cho tài khoản khác.").ngoaiLe();
        }
        if (taiKhoanRepository.existsBySoDienThoai(soDienThoai.trim())) {
            throw Loi.xungDot("SDT_DA_DUNG", "Số điện thoại đã được dùng cho tài khoản khác.").ngoaiLe();
        }
    }

    static String chuan(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
