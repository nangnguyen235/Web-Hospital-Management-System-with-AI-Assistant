package vn.nhom6.his.nguoidung.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.nhom6.his.nguoidung.dto.TaiKhoanDto.DangNhapRequest;
import vn.nhom6.his.nguoidung.dto.TaiKhoanDto.DangNhapResponse;
import vn.nhom6.his.nguoidung.dto.TaiKhoanDto.ThongTinTaiKhoan;
import vn.nhom6.his.nguoidung.entity.NhanVien;
import vn.nhom6.his.nguoidung.entity.TaiKhoan;
import vn.nhom6.his.nguoidung.repository.NhanVienRepository;
import vn.nhom6.his.nguoidung.repository.TaiKhoanRepository;
import vn.nhom6.his.shared.kernel.CurrentUser;
import vn.nhom6.his.shared.kernel.IStorageService;
import vn.nhom6.his.shared.kernel.ITokenService;
import vn.nhom6.his.shared.kernel.ITokenService.ThongTinToken;
import vn.nhom6.his.shared.kernel.ITokenService.TokenDaTao;
import vn.nhom6.his.shared.kernel.Loi;
import vn.nhom6.his.shared.kernel.VaiTro;

/** Đăng nhập bằng mật khẩu (FR-BN-02, FR-BS-01, FR-DD-01), thông tin tài khoản, ảnh đại diện lên R2. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaiKhoanService {

    private static final Logger LOG = LoggerFactory.getLogger(TaiKhoanService.class);
    private static final Loi SAI_DANG_NHAP = new Loi("DANG_NHAP_THAT_BAI", "Sai tên đăng nhập hoặc mật khẩu.");
    private static final Map<String, String> DINH_DANG_ANH = Map.of("image/jpeg", "jpg", "image/png", "png", "image/webp", "webp");
    private static final long ANH_TOI_DA = 2L * 1024 * 1024; // NFR-04

    private final TaiKhoanRepository taiKhoanRepository;
    private final NhanVienRepository nhanVienRepository;
    private final PasswordEncoder passwordEncoder;
    private final ITokenService tokenService;
    private final IStorageService storage;
    private final CurrentUser currentUser;

    public DangNhapResponse dangNhap(DangNhapRequest request) {
        String ten = request.tenDangNhap().trim();
        if (ten.contains("@")) {
            ten = XacThucService.chuan(ten);
        }
        TaiKhoan taiKhoan = taiKhoanRepository.findFirstByEmailOrSoDienThoai(ten, ten)
                .orElseThrow(SAI_DANG_NHAP::ngoaiLe);
        NhanVien nhanVien = layNhanVien(taiKhoan);
        if (taiKhoan.isDaXoa() || (nhanVien != null && nhanVien.isDaXoa())
                || !passwordEncoder.matches(request.matKhau(), taiKhoan.getMatKhauHash())) {
            throw SAI_DANG_NHAP.ngoaiLe();
        }
        return taoPhien(taiKhoan, nhanVien);
    }

    /** Cấp JWT cho tài khoản đã xác thực (mật khẩu hoặc OTP). */
    public DangNhapResponse taoPhien(TaiKhoan taiKhoan, NhanVien nhanVien) {
        VaiTro vaiTro = nhanVien == null ? VaiTro.BENH_NHAN : nhanVien.vaiTro();
        TokenDaTao token = tokenService.taoToken(new ThongTinToken(taiKhoan.getId(), vaiTro,
                taiKhoan.getNhanVienId(), nhanVien == null ? null : nhanVien.getKhoaId(),
                nhanVien != null && nhanVien.isLaTruongKhoa()));
        return new DangNhapResponse(token.accessToken(), token.hetHan(), vaiTro, taiKhoan.getHoTen());
    }

    public ThongTinTaiKhoan toi() {
        TaiKhoan taiKhoan = taiKhoanHienTai();
        NhanVien nhanVien = layNhanVien(taiKhoan);
        String anhUrl = taiKhoan.getAnhDaiDienKey() == null ? null : storage.presignedUrl(taiKhoan.getAnhDaiDienKey());
        return new ThongTinTaiKhoan(taiKhoan.getId(), taiKhoan.getHoTen(), taiKhoan.getEmail(), taiKhoan.getSoDienThoai(),
                nhanVien == null ? VaiTro.BENH_NHAN : nhanVien.vaiTro(), taiKhoan.getNhanVienId(),
                nhanVien == null ? null : nhanVien.getKhoaId(), nhanVien != null && nhanVien.isLaTruongKhoa(), anhUrl);
    }

    /** Ảnh ≤ 2 MB, jpg/png/webp; key {@code tai-khoan/{id}/{uuid}.ext} (cloudflare-r2.md mục 2). Trả Presigned URL. */
    @Transactional
    public String capNhatAnhDaiDien(MultipartFile file) {
        String duoi = file == null ? null : DINH_DANG_ANH.get(file.getContentType());
        if (duoi == null) {
            throw new Loi("SAI_DINH_DANG", "Ảnh phải là jpg, png hoặc webp.").ngoaiLe();
        }
        if (file.getSize() <= 0 || file.getSize() > ANH_TOI_DA) {
            throw new Loi("QUA_KICH_THUOC", "Ảnh tối đa 2 MB.").ngoaiLe();
        }
        TaiKhoan taiKhoan = taiKhoanHienTai();
        String keyCu = taiKhoan.getAnhDaiDienKey();
        String keyMoi = "tai-khoan/" + taiKhoan.getId() + "/" + UUID.randomUUID().toString().replace("-", "") + "." + duoi;
        try (InputStream noiDung = file.getInputStream()) {
            storage.upload(noiDung, file.getSize(), keyMoi, file.getContentType());
        } catch (IOException e) {
            throw new Loi("TAI_LEN_THAT_BAI", "Không đọc được tệp tải lên.").ngoaiLe();
        }
        taiKhoan.setAnhDaiDienKey(keyMoi);
        if (keyCu != null) {
            try {
                storage.delete(keyCu);
            } catch (RuntimeException e) {
                LOG.warn("Không xóa được ảnh cũ {} trên R2: {}", keyCu, e.getMessage());
            }
        }
        return storage.presignedUrl(keyMoi);
    }

    private TaiKhoan taiKhoanHienTai() {
        return taiKhoanRepository.findByIdAndDaXoaFalse(currentUser.taiKhoanId())
                .orElseThrow(() -> Loi.khongTimThay("tài khoản").ngoaiLe());
    }

    private NhanVien layNhanVien(TaiKhoan taiKhoan) {
        return taiKhoan.getNhanVienId() == null ? null : nhanVienRepository.findById(taiKhoan.getNhanVienId()).orElse(null);
    }
}
