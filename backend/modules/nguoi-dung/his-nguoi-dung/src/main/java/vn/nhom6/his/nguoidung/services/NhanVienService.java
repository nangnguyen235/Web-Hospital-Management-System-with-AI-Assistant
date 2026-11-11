package vn.nhom6.his.nguoidung.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.nhom6.his.contracts.danhmuc.DanhMucContracts;
import vn.nhom6.his.contracts.lichkham.LichKhamContracts;
import vn.nhom6.his.nguoidung.constant.HocHam;
import vn.nhom6.his.nguoidung.constant.LoaiNhanVien;
import vn.nhom6.his.nguoidung.dto.NhanVienDto;
import vn.nhom6.his.nguoidung.entity.BacSi;
import vn.nhom6.his.nguoidung.entity.DieuDuong;
import vn.nhom6.his.nguoidung.entity.NhanVien;
import vn.nhom6.his.nguoidung.entity.QuanTri;
import vn.nhom6.his.nguoidung.entity.TaiKhoan;
import vn.nhom6.his.nguoidung.repository.NhanVienRepository;
import vn.nhom6.his.nguoidung.repository.TaiKhoanRepository;
import vn.nhom6.his.shared.kernel.CurrentUser;
import vn.nhom6.his.shared.kernel.IStorageService;
import vn.nhom6.his.shared.kernel.Loi;
import vn.nhom6.his.shared.kernel.PageResponse;
import vn.nhom6.his.shared.kernel.PhanTrang;

/**
 * Admin quản lý nhân viên kèm tài khoản (FR-AD-03), xếp nhân viên vào khoa, chọn trưởng khoa / điều dưỡng trưởng
 * (FR-AD-04, BR-04); tìm bác sĩ công khai (FR-BN-07); danh sách nhân viên khoa mình cho trưởng khoa.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NhanVienService {

    private static final Map<String, String> DINH_DANG_ANH = Map.of("image/jpeg", "jpg", "image/png", "png", "image/webp", "webp");

    private final NhanVienRepository nhanVienRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final PasswordEncoder passwordEncoder;
    private final DanhMucContracts danhMuc;
    private final LichKhamContracts lichKham;
    private final IStorageService storage;
    private final CurrentUser currentUser;

    public PageResponse<NhanVienDto> timKiem(String tuKhoa, LoaiNhanVien loai, Integer khoaId, Boolean daXoa,
                                             Integer page, Integer pageSize) {
        PhanTrang pt = PhanTrang.cua(page, pageSize);
        Page<NhanVien> kq = nhanVienRepository.timKiem(HoSoService.trong(tuKhoa), loai, khoaId, daXoa,
                PageRequest.of(pt.chiSoTrang(), pt.pageSize(), Sort.by("daXoa", "hoTen")));
        Map<Integer, String> khoa = danhMuc.tenCacKhoa();
        return new PageResponse<>(kq.getContent().stream().map(n -> dto(n, khoa)).toList(), kq.getTotalElements(),
                pt.page(), pt.pageSize());
    }

    public NhanVienDto chiTiet(long id) {
        return dto(tim(id), danhMuc.tenCacKhoa());
    }

    @Transactional
    public NhanVienDto tao(NhanVienDto.Luu r) {
        if (r.matKhau() == null || r.matKhau().isBlank()) {
            throw new Loi("THIEU_MAT_KHAU", "Vui lòng nhập mật khẩu cho tài khoản nhân viên.").ngoaiLe();
        }
        String email = XacThucService.chuan(r.email());
        if (taiKhoanRepository.existsByEmail(email)) {
            throw Loi.xungDot("EMAIL_DA_DUNG", "Email đã được dùng cho tài khoản khác.").ngoaiLe();
        }
        if (taiKhoanRepository.existsBySoDienThoai(r.soDienThoai().trim())) {
            throw Loi.xungDot("SDT_DA_DUNG", "Số điện thoại đã được dùng cho tài khoản khác.").ngoaiLe();
        }
        NhanVien nv = switch (r.loaiNhanVien()) {
            case BAC_SI -> new BacSi();
            case DIEU_DUONG -> new DieuDuong();
            case QUAN_TRI -> new QuanTri();
        };
        gan(nv, r);
        nhanVienRepository.save(nv);

        TaiKhoan tk = new TaiKhoan();
        tk.setHoTen(nv.getHoTen());
        tk.setEmail(email);
        tk.setSoDienThoai(nv.getSoDienThoai());
        tk.setMatKhauHash(passwordEncoder.encode(r.matKhau()));
        tk.setNhanVienId(nv.getId());
        taiKhoanRepository.save(tk);
        return dto(nv, danhMuc.tenCacKhoa());
    }

    @Transactional
    public NhanVienDto capNhat(long id, NhanVienDto.Luu r) {
        NhanVien nv = tim(id);
        TaiKhoan tk = taiKhoanRepository.findByNhanVienId(id).orElseThrow(() -> Loi.khongTimThay("tài khoản").ngoaiLe());
        String email = XacThucService.chuan(r.email());
        if (taiKhoanRepository.existsByEmailAndIdNot(email, tk.getId())) {
            throw Loi.xungDot("EMAIL_DA_DUNG", "Email đã được dùng cho tài khoản khác.").ngoaiLe();
        }
        if (taiKhoanRepository.existsBySoDienThoaiAndIdNot(r.soDienThoai().trim(), tk.getId())) {
            throw Loi.xungDot("SDT_DA_DUNG", "Số điện thoại đã được dùng cho tài khoản khác.").ngoaiLe();
        }
        Integer khoaCu = nv.getKhoaId();
        gan(nv, r);
        if (!java.util.Objects.equals(khoaCu, nv.getKhoaId())) {
            kiemTraDoiKhoa(nv, khoaCu);
            nv.setLaTruongKhoa(false);
        }
        tk.setHoTen(nv.getHoTen());
        tk.setEmail(email);
        tk.setSoDienThoai(nv.getSoDienThoai());
        if (r.matKhau() != null && !r.matKhau().isBlank()) {
            tk.setMatKhauHash(passwordEncoder.encode(r.matKhau()));
        }
        return dto(nv, danhMuc.tenCacKhoa());
    }

    /** Thêm/bớt nhân viên khỏi khoa (FR-AD-04). Không bớt bác sĩ còn lịch chưa diễn ra (BR-04). */
    @Transactional
    public NhanVienDto doiKhoa(long id, Integer khoaId) {
        NhanVien nv = tim(id);
        if (nv instanceof QuanTri) {
            throw new Loi("KHONG_HOP_LE", "Quản trị viên không thuộc khoa.").ngoaiLe();
        }
        if (khoaId != null && !danhMuc.khoaTonTai(khoaId)) {
            throw new Loi("KHOA_KHONG_HOP_LE", "Khoa không tồn tại hoặc đã xóa.").ngoaiLe();
        }
        Integer khoaCu = nv.getKhoaId();
        if (!java.util.Objects.equals(khoaCu, khoaId)) {
            nv.setKhoaId(khoaId);
            kiemTraDoiKhoa(nv, khoaCu);
            nv.setLaTruongKhoa(false);
        }
        return dto(nv, danhMuc.tenCacKhoa());
    }

    /** Mỗi khoa tối đa 1 trưởng khoa (bác sĩ) và 1 điều dưỡng trưởng; đổi người thì bỏ cờ người cũ (BR-04). */
    @Transactional
    public NhanVienDto datTruongKhoa(long id, boolean la) {
        NhanVien nv = tim(id);
        if (nv instanceof QuanTri || nv.getKhoaId() == null || nv.isDaXoa()) {
            throw new Loi("KHONG_HOP_LE", "Chỉ bác sĩ/điều dưỡng đang làm việc và đã thuộc một khoa.").ngoaiLe();
        }
        if (la) {
            nhanVienRepository.findByKhoaIdAndLoaiNhanVienAndLaTruongKhoaTrue(nv.getKhoaId(), nv.getLoaiNhanVien())
                    .forEach(cu -> cu.setLaTruongKhoa(false));
        }
        nv.setLaTruongKhoa(la);
        return dto(nv, danhMuc.tenCacKhoa());
    }

    /** Xóa/khôi phục nhân viên — tài khoản đi theo (đăng nhập bị chặn khi nhân viên đã xóa). */
    @Transactional
    public void doiTrangThai(long id, boolean daXoa) {
        NhanVien nv = tim(id);
        if (daXoa && currentUser.nhanVienId() != null && currentUser.nhanVienId() == id) {
            throw new Loi("KHONG_HOP_LE", "Không thể tự xóa tài khoản của mình.").ngoaiLe();
        }
        if (daXoa && nv instanceof BacSi && lichKham.bacSiConLichSapToi(id)) {
            throw new Loi("CON_LICH_KHAM", "Bác sĩ còn lịch khám chưa diễn ra, không xóa được.").ngoaiLe();
        }
        nv.setDaXoa(daXoa);
        if (daXoa) {
            nv.setLaTruongKhoa(false);
        }
        taiKhoanRepository.findByNhanVienId(id).ifPresent(tk -> tk.setDaXoa(daXoa));
    }

    @Transactional
    public String capNhatAnh(long id, MultipartFile file) {
        NhanVien nv = tim(id);
        String duoi = file == null ? null : DINH_DANG_ANH.get(file.getContentType());
        if (duoi == null) {
            throw new Loi("SAI_DINH_DANG", "Ảnh phải là jpg, png hoặc webp.").ngoaiLe();
        }
        if (file.getSize() <= 0 || file.getSize() > 2L * 1024 * 1024) {
            throw new Loi("QUA_KICH_THUOC", "Ảnh tối đa 2 MB.").ngoaiLe();
        }
        String key = "nhan-vien/" + id + "/" + UUID.randomUUID().toString().replace("-", "") + "." + duoi;
        try (InputStream in = file.getInputStream()) {
            storage.upload(in, file.getSize(), key, file.getContentType());
        } catch (IOException e) {
            throw new Loi("TAI_LEN_THAT_BAI", "Không đọc được tệp tải lên.").ngoaiLe();
        }
        nv.setAnhDaiDienKey(key);
        return storage.presignedUrl(key);
    }

    /** FR-BN-07: bác sĩ đang làm việc, đã thuộc khoa. */
    public PageResponse<NhanVienDto.BacSiCongKhai> timBacSi(String tuKhoa, Integer khoaId, HocHam hocHam,
                                                            Integer page, Integer pageSize) {
        PhanTrang pt = PhanTrang.cua(page, pageSize);
        Page<BacSi> kq = nhanVienRepository.timBacSi(HoSoService.trong(tuKhoa), khoaId, hocHam,
                PageRequest.of(pt.chiSoTrang(), pt.pageSize(), Sort.by(Sort.Direction.DESC, "diemDanhGia").and(Sort.by("hoTen"))));
        Map<Integer, String> khoa = danhMuc.tenCacKhoa();
        return new PageResponse<>(kq.getContent().stream().map(b -> congKhai(b, khoa)).toList(), kq.getTotalElements(),
                pt.page(), pt.pageSize());
    }

    public NhanVienDto.BacSiCongKhai bacSi(long id) {
        NhanVien nv = tim(id);
        if (!(nv instanceof BacSi b) || b.isDaXoa()) {
            throw Loi.khongTimThay("bác sĩ").ngoaiLe();
        }
        return congKhai(b, danhMuc.tenCacKhoa());
    }

    /** Bác sĩ / điều dưỡng của khoa mình (trưởng khoa, điều dưỡng trưởng). */
    public List<NhanVienDto.Gon> nhanVienKhoaToi(LoaiNhanVien loai) {
        Integer khoaId = currentUser.khoaId();
        if (khoaId == null) {
            return List.of();
        }
        return nhanVienRepository.findByKhoaIdAndLoaiNhanVienAndDaXoaFalseOrderByHoTenAsc(khoaId, loai).stream()
                .map(n -> new NhanVienDto.Gon(n.getId(), n.getHoTen(), n instanceof BacSi b ? b.getHocHam() : null,
                        n.isLaTruongKhoa()))
                .toList();
    }

    private void kiemTraDoiKhoa(NhanVien nv, Integer khoaCu) {
        if (nv instanceof BacSi && khoaCu != null && lichKham.bacSiConLichSapToi(nv.getId())) {
            throw new Loi("CON_LICH_KHAM", "Bác sĩ còn lịch khám chưa diễn ra, không bớt khỏi khoa được.").ngoaiLe();
        }
    }

    private void gan(NhanVien nv, NhanVienDto.Luu r) {
        if (r.khoaId() != null && !danhMuc.khoaTonTai(r.khoaId())) {
            throw new Loi("KHOA_KHONG_HOP_LE", "Khoa không tồn tại hoặc đã xóa.").ngoaiLe();
        }
        nv.setHoTen(r.hoTen().trim());
        nv.setSoDienThoai(r.soDienThoai().trim());
        nv.setGioiTinh(r.gioiTinh());
        nv.setNgaySinh(r.ngaySinh());
        nv.setSoCccd(HoSoService.trong(r.soCccd()));
        if (!(nv instanceof QuanTri)) {
            nv.setKhoaId(r.khoaId());
        }
        if (nv instanceof BacSi b) {
            b.setHocHam(r.hocHam() == null ? HocHam.BS : r.hocHam());
        }
    }

    private NhanVien tim(long id) {
        return nhanVienRepository.findById(id).orElseThrow(() -> Loi.khongTimThay("nhân viên").ngoaiLe());
    }

    private String anh(NhanVien n) {
        return n.getAnhDaiDienKey() == null ? null : storage.presignedUrl(n.getAnhDaiDienKey());
    }

    private NhanVienDto dto(NhanVien n, Map<Integer, String> khoa) {
        TaiKhoan tk = taiKhoanRepository.findByNhanVienId(n.getId()).orElse(null);
        BacSi b = n instanceof BacSi x ? x : null;
        return new NhanVienDto(n.getId(), n.getHoTen(), tk == null ? null : tk.getEmail(), n.getSoDienThoai(), n.getSoCccd(),
                n.getGioiTinh(), n.getNgaySinh(), LoaiNhanVien.valueOf(n.vaiTro().name()), b == null ? null : b.getHocHam(),
                n.getKhoaId(), n.getKhoaId() == null ? null : khoa.get(n.getKhoaId()), n.isLaTruongKhoa(),
                b == null ? null : b.getDiemDanhGia(), anh(n), tk == null ? null : tk.getId(), n.isDaXoa());
    }

    private NhanVienDto.BacSiCongKhai congKhai(BacSi b, Map<Integer, String> khoa) {
        return new NhanVienDto.BacSiCongKhai(b.getId(), b.getHoTen(), b.getGioiTinh(), b.getHocHam(), b.getKhoaId(),
                khoa.get(b.getKhoaId()), b.getDiemDanhGia(), anh(b));
    }
}
