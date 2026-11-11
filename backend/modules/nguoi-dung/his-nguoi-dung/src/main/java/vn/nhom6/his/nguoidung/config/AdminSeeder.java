package vn.nhom6.his.nguoidung.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom6.his.nguoidung.constant.GioiTinh;
import vn.nhom6.his.nguoidung.entity.QuanTri;
import vn.nhom6.his.nguoidung.entity.TaiKhoan;
import vn.nhom6.his.nguoidung.repository.NhanVienRepository;
import vn.nhom6.his.nguoidung.repository.TaiKhoanRepository;

/** Admin gốc từ HIS_SEED_ADMIN_* (BR-02), chỉ tạo khi chưa có Admin nào. Mật khẩu không hardcode, không log. */
@Component
public class AdminSeeder implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(AdminSeeder.class);

    private final NhanVienRepository nhanVienRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String soDienThoai;
    private final String matKhau;
    private final String hoTen;

    public AdminSeeder(NhanVienRepository nhanVienRepository, TaiKhoanRepository taiKhoanRepository,
                       PasswordEncoder passwordEncoder,
                       @Value("${his.seed.admin.email:}") String email,
                       @Value("${his.seed.admin.so-dien-thoai:}") String soDienThoai,
                       @Value("${his.seed.admin.mat-khau:}") String matKhau,
                       @Value("${his.seed.admin.ho-ten:Quản trị viên}") String hoTen) {
        this.nhanVienRepository = nhanVienRepository;
        this.taiKhoanRepository = taiKhoanRepository;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.matKhau = matKhau;
        this.hoTen = hoTen;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (nhanVienRepository.coQuanTri()) {
            return;
        }
        if (email.isBlank() || soDienThoai.isBlank() || matKhau.isBlank()) {
            LOG.warn("Chưa có Admin và thiếu HIS_SEED_ADMIN_EMAIL/SO_DIEN_THOAI/MAT_KHAU — bỏ qua tạo Admin gốc.");
            return;
        }
        QuanTri admin = new QuanTri();
        admin.setHoTen(hoTen);
        admin.setSoDienThoai(soDienThoai);
        admin.setGioiTinh(GioiTinh.NAM);
        nhanVienRepository.save(admin);

        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setHoTen(hoTen);
        taiKhoan.setEmail(email);
        taiKhoan.setSoDienThoai(soDienThoai);
        taiKhoan.setMatKhauHash(passwordEncoder.encode(matKhau));
        taiKhoan.setNhanVienId(admin.getId());
        taiKhoanRepository.save(taiKhoan);
        LOG.info("Đã tạo Admin gốc.");
    }
}
