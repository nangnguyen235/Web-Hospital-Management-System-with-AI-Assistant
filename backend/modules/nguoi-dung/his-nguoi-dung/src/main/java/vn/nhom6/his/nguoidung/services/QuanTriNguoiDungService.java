package vn.nhom6.his.nguoidung.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom6.his.nguoidung.dto.HoSoDto;
import vn.nhom6.his.nguoidung.dto.QuanTriDto;
import vn.nhom6.his.nguoidung.entity.BenhNhan;
import vn.nhom6.his.nguoidung.entity.HoSoBenhNhan;
import vn.nhom6.his.nguoidung.entity.NhanVien;
import vn.nhom6.his.nguoidung.entity.TaiKhoan;
import vn.nhom6.his.nguoidung.repository.BenhNhanRepository;
import vn.nhom6.his.nguoidung.repository.HoSoBenhNhanRepository;
import vn.nhom6.his.nguoidung.repository.NhanVienRepository;
import vn.nhom6.his.nguoidung.repository.TaiKhoanRepository;
import vn.nhom6.his.shared.kernel.CurrentUser;
import vn.nhom6.his.shared.kernel.Loi;
import vn.nhom6.his.shared.kernel.PageResponse;
import vn.nhom6.his.shared.kernel.PhanTrang;
import vn.nhom6.his.shared.kernel.VaiTro;

/** Admin: tài khoản (FR-AD-02 — lọc, chi tiết, xóa/khôi phục) và bệnh nhân, hồ sơ (FR-AD-08). */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuanTriNguoiDungService {

    private final TaiKhoanRepository taiKhoanRepository;
    private final NhanVienRepository nhanVienRepository;
    private final HoSoBenhNhanRepository hoSoRepository;
    private final BenhNhanRepository benhNhanRepository;
    private final HoSoService hoSoService;
    private final CurrentUser currentUser;

    /** {@code loai}: null | BENH_NHAN | NHAN_VIEN. */
    public PageResponse<QuanTriDto.TaiKhoan> timTaiKhoan(String tuKhoa, String loai, Boolean daXoa, Integer page,
                                                         Integer pageSize) {
        PhanTrang pt = PhanTrang.cua(page, pageSize);
        Page<TaiKhoan> kq = taiKhoanRepository.timKiem(HoSoService.trong(tuKhoa), HoSoService.trong(loai), daXoa,
                PageRequest.of(pt.chiSoTrang(), pt.pageSize(), Sort.by(Sort.Direction.DESC, "id")));
        return new PageResponse<>(kq.getContent().stream().map(this::dto).toList(), kq.getTotalElements(), pt.page(),
                pt.pageSize());
    }

    public QuanTriDto.TaiKhoanChiTiet chiTietTaiKhoan(long id) {
        TaiKhoan tk = taiKhoanRepository.findById(id).orElseThrow(() -> Loi.khongTimThay("tài khoản").ngoaiLe());
        return new QuanTriDto.TaiKhoanChiTiet(dto(tk),
                hoSoRepository.findByTaiKhoanIdOrderByIdAsc(id).stream().map(hoSoService::dto).toList());
    }

    /** Tài khoản đã xóa bị từ chối đăng nhập (BR-02). */
    @Transactional
    public void doiTrangThaiTaiKhoan(long id, boolean daXoa) {
        if (daXoa && id == currentUser.taiKhoanId()) {
            throw new Loi("KHONG_HOP_LE", "Không thể tự xóa tài khoản của mình.").ngoaiLe();
        }
        TaiKhoan tk = taiKhoanRepository.findById(id).orElseThrow(() -> Loi.khongTimThay("tài khoản").ngoaiLe());
        tk.setDaXoa(daXoa);
    }

    public PageResponse<QuanTriDto.BenhNhan> timBenhNhan(String tuKhoa, Integer page, Integer pageSize) {
        PhanTrang pt = PhanTrang.cua(page, pageSize);
        Page<BenhNhan> kq = benhNhanRepository.timKiem(HoSoService.trong(tuKhoa),
                PageRequest.of(pt.chiSoTrang(), pt.pageSize(), Sort.by(Sort.Direction.DESC, "id")));
        return new PageResponse<>(kq.getContent().stream().map(QuanTriNguoiDungService::dto).toList(),
                kq.getTotalElements(), pt.page(), pt.pageSize());
    }

    public QuanTriDto.BenhNhanChiTiet chiTietBenhNhan(long id) {
        BenhNhan b = benhNhanRepository.findById(id).orElseThrow(() -> Loi.khongTimThay("bệnh nhân").ngoaiLe());
        return new QuanTriDto.BenhNhanChiTiet(dto(b),
                hoSoRepository.findByBenhNhanId(id).stream().map(hoSoService::dto).toList());
    }

    public PageResponse<HoSoDto> timHoSo(String tuKhoa, Integer page, Integer pageSize) {
        PhanTrang pt = PhanTrang.cua(page, pageSize);
        Page<HoSoBenhNhan> kq = hoSoRepository.timKiem(HoSoService.trong(tuKhoa),
                PageRequest.of(pt.chiSoTrang(), pt.pageSize(), Sort.by(Sort.Direction.DESC, "id")));
        return new PageResponse<>(kq.getContent().stream().map(hoSoService::dto).toList(), kq.getTotalElements(),
                pt.page(), pt.pageSize());
    }

    /** Xóa/khôi phục hồ sơ — cập nhật {@code so_luong_ho_so} của tài khoản sở hữu (bất biến 9). */
    @Transactional
    public void doiTrangThaiHoSo(long id, boolean daXoa) {
        HoSoBenhNhan h = hoSoRepository.findById(id).orElseThrow(() -> Loi.khongTimThay("hồ sơ").ngoaiLe());
        if (h.isDaXoa() == daXoa) {
            return;
        }
        if (h.getTaiKhoanId() != null) {
            TaiKhoan tk = taiKhoanRepository.findById(h.getTaiKhoanId()).orElseThrow();
            if (!daXoa && hoSoRepository.countByTaiKhoanIdAndDaXoaFalse(tk.getId()) >= TaiKhoan.SO_HO_SO_TOI_DA) {
                throw new Loi("QUA_SO_HO_SO", "Tài khoản sở hữu đã đủ 10 hồ sơ.").ngoaiLe();
            }
            tk.setSoLuongHoSo(Math.max(0, tk.getSoLuongHoSo() + (daXoa ? -1 : 1)));
        }
        h.setDaXoa(daXoa);
    }

    private QuanTriDto.TaiKhoan dto(TaiKhoan t) {
        VaiTro vaiTro = t.getNhanVienId() == null ? VaiTro.BENH_NHAN
                : nhanVienRepository.findById(t.getNhanVienId()).map(NhanVien::vaiTro).orElse(VaiTro.BENH_NHAN);
        return new QuanTriDto.TaiKhoan(t.getId(), t.getHoTen(), t.getEmail(), t.getSoDienThoai(), vaiTro, t.getSoLuongHoSo(),
                t.isDaXoa(), t.getNgayTao());
    }

    static QuanTriDto.BenhNhan dto(BenhNhan b) {
        return new QuanTriDto.BenhNhan(b.getId(), b.getMaBenhNhan(), b.getHoTen(), b.getSoDienThoai(), b.getSoCccd(),
                b.getNgaySinh(), b.getGioiTinh(), b.isDaXoa());
    }
}
