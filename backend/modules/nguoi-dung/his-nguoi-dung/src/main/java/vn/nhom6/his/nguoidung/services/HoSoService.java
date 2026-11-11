package vn.nhom6.his.nguoidung.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom6.his.contracts.danhmuc.DanhMucContracts;
import vn.nhom6.his.nguoidung.constant.GioiTinh;
import vn.nhom6.his.nguoidung.dto.HoSoDto;
import vn.nhom6.his.nguoidung.entity.BenhNhan;
import vn.nhom6.his.nguoidung.entity.HoSoBenhNhan;
import vn.nhom6.his.nguoidung.entity.TaiKhoan;
import vn.nhom6.his.nguoidung.repository.BenhNhanRepository;
import vn.nhom6.his.nguoidung.repository.HoSoBenhNhanRepository;
import vn.nhom6.his.nguoidung.repository.TaiKhoanRepository;
import vn.nhom6.his.shared.kernel.CurrentUser;
import vn.nhom6.his.shared.kernel.Loi;
import vn.nhom6.his.shared.kernel.LoiNghiepVuException;

/** Hồ sơ bệnh nhân của tài khoản (FR-BN-05) và tìm lại hồ sơ đã mất (FR-BN-06) — BR-03. */
@Service
@RequiredArgsConstructor
@Transactional(noRollbackFor = LoiNghiepVuException.class)
public class HoSoService {

    private final HoSoBenhNhanRepository hoSoRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final BenhNhanRepository benhNhanRepository;
    private final DanhMucContracts danhMuc;
    private final OtpService otpService;
    private final CurrentUser currentUser;

    @Transactional(readOnly = true)
    public List<HoSoDto> cuaToi() {
        return hoSoRepository.findByTaiKhoanIdAndDaXoaFalseOrderByIdAsc(currentUser.taiKhoanId()).stream()
                .map(this::dto).toList();
    }

    @Transactional(readOnly = true)
    public HoSoDto chiTiet(long id) {
        return dto(cuaToi(id));
    }

    /** Tối đa 10 hồ sơ chưa xóa mỗi tài khoản. */
    public HoSoDto tao(HoSoDto.Luu r) {
        TaiKhoan tk = taiKhoanHienTai();
        if (hoSoRepository.countByTaiKhoanIdAndDaXoaFalse(tk.getId()) >= TaiKhoan.SO_HO_SO_TOI_DA) {
            throw new Loi("QUA_SO_HO_SO", "Mỗi tài khoản tối đa " + TaiKhoan.SO_HO_SO_TOI_DA + " hồ sơ.").ngoaiLe();
        }
        HoSoBenhNhan h = new HoSoBenhNhan();
        h.setTaiKhoanId(tk.getId());
        ganThongTin(h, r);
        hoSoRepository.save(h);
        tk.setSoLuongHoSo(tk.getSoLuongHoSo() + 1);
        return dto(h);
    }

    public HoSoDto capNhat(long id, HoSoDto.Luu r) {
        HoSoBenhNhan h = cuaToi(id);
        ganThongTin(h, r);
        return dto(h);
    }

    public void xoa(long id) {
        HoSoBenhNhan h = cuaToi(id);
        h.setDaXoa(true);
        TaiKhoan tk = taiKhoanHienTai();
        tk.setSoLuongHoSo(Math.max(0, tk.getSoLuongHoSo() - 1));
    }

    /** Theo mã bệnh nhân (các hồ sơ đã liên kết) hoặc họ tên + năm sinh + giới tính. */
    @Transactional(readOnly = true)
    public List<HoSoDto.TimLai> timLai(String maBenhNhan, String hoTen, Integer namSinh, GioiTinh gioiTinh) {
        List<HoSoBenhNhan> kq;
        if (maBenhNhan != null && !maBenhNhan.isBlank()) {
            kq = benhNhanRepository.findFirstByMaBenhNhan(maBenhNhan.trim().toUpperCase())
                    .map(b -> hoSoRepository.findByBenhNhanIdAndDaXoaFalse(b.getId())).orElse(List.of());
        } else if (hoTen != null && !hoTen.isBlank() && namSinh != null && gioiTinh != null) {
            kq = hoSoRepository.timLai(hoTen.trim(), LocalDate.of(namSinh, 1, 1), LocalDate.of(namSinh, 12, 31), gioiTinh);
        } else {
            throw new Loi("THIEU_DIEU_KIEN", "Nhập mã bệnh nhân, hoặc họ tên + năm sinh + giới tính.").ngoaiLe();
        }
        long toi = currentUser.taiKhoanId();
        List<HoSoDto.TimLai> ds = new ArrayList<>();
        for (HoSoBenhNhan h : kq) {
            if (!Long.valueOf(toi).equals(h.getTaiKhoanId())) {
                ds.add(new HoSoDto.TimLai(h.getId(), h.getHoTen(), h.getNgaySinh(), h.getGioiTinh(), cheEmail(h.getEmail()),
                        h.getEmail() != null && !h.getEmail().isBlank()));
            }
        }
        return ds;
    }

    /** OTP gửi email của hồ sơ; hồ sơ không có email thì không tìm lại được (nhờ điều dưỡng bổ sung). */
    public void guiOtpTimLai(long hoSoId) {
        HoSoBenhNhan h = hoSoRepository.findByIdAndDaXoaFalse(hoSoId).orElseThrow(() -> Loi.khongTimThay("hồ sơ").ngoaiLe());
        if (h.getEmail() == null || h.getEmail().isBlank()) {
            throw new Loi("HO_SO_KHONG_CO_EMAIL",
                    "Hồ sơ chưa có email. Vui lòng nhờ điều dưỡng bổ sung email khi đến viện.").ngoaiLe();
        }
        otpService.taoVaGui(h, h.getEmail(), "tìm lại hồ sơ");
    }

    /** Đúng OTP → gắn hồ sơ vào tài khoản (kể cả đang thuộc tài khoản khác, như TheDuck). */
    public HoSoDto xacNhanTimLai(long hoSoId, String otp) {
        HoSoBenhNhan h = hoSoRepository.findByIdAndDaXoaFalse(hoSoId).orElseThrow(() -> Loi.khongTimThay("hồ sơ").ngoaiLe());
        TaiKhoan tk = taiKhoanHienTai();
        if (tk.getId().equals(h.getTaiKhoanId())) {
            return dto(h);
        }
        if (hoSoRepository.countByTaiKhoanIdAndDaXoaFalse(tk.getId()) >= TaiKhoan.SO_HO_SO_TOI_DA) {
            throw new Loi("QUA_SO_HO_SO", "Mỗi tài khoản tối đa " + TaiKhoan.SO_HO_SO_TOI_DA + " hồ sơ.").ngoaiLe();
        }
        otpService.kiemTra(h, otp);
        if (h.getTaiKhoanId() != null) {
            taiKhoanRepository.findById(h.getTaiKhoanId())
                    .ifPresent(cu -> cu.setSoLuongHoSo(Math.max(0, cu.getSoLuongHoSo() - 1)));
        }
        h.setTaiKhoanId(tk.getId());
        tk.setSoLuongHoSo(tk.getSoLuongHoSo() + 1);
        return dto(h);
    }

    void ganThongTin(HoSoBenhNhan h, HoSoDto.Luu r) {
        if (r.danTocId() != null && !danhMuc.danTocTonTai(r.danTocId())) {
            throw new Loi("DAN_TOC_KHONG_HOP_LE", "Dân tộc không hợp lệ.").ngoaiLe();
        }
        h.setHoTen(r.hoTen().trim());
        h.setSoDienThoai(trong(r.soDienThoai()));
        h.setEmail(r.email() == null || r.email().isBlank() ? null : XacThucService.chuan(r.email()));
        h.setGioiTinh(r.gioiTinh());
        // CCCD đã đối chiếu khi tiếp nhận (đã liên kết bệnh nhân) thì không cho sửa
        if (h.getBenhNhanId() == null) {
            h.setSoCccd(trong(r.soCccd()));
        }
        h.setDiaChi(trong(r.diaChi()));
        h.setNgaySinh(r.ngaySinh());
        h.setDanTocId(r.danTocId());
    }

    HoSoDto dto(HoSoBenhNhan h) {
        String ma = h.getBenhNhanId() == null ? null
                : benhNhanRepository.findById(h.getBenhNhanId()).map(BenhNhan::getMaBenhNhan).orElse(null);
        return HoSoDto.tu(h, ma);
    }

    private HoSoBenhNhan cuaToi(long id) {
        HoSoBenhNhan h = hoSoRepository.findByIdAndDaXoaFalse(id).orElseThrow(() -> Loi.khongTimThay("hồ sơ").ngoaiLe());
        if (!Long.valueOf(currentUser.taiKhoanId()).equals(h.getTaiKhoanId())) {
            throw Loi.khongTimThay("hồ sơ").ngoaiLe(); // không lộ hồ sơ của tài khoản khác
        }
        return h;
    }

    private TaiKhoan taiKhoanHienTai() {
        return taiKhoanRepository.findByIdAndDaXoaFalse(currentUser.taiKhoanId())
                .orElseThrow(() -> Loi.khongTimThay("tài khoản").ngoaiLe());
    }

    static String trong(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    static String cheEmail(String email) {
        if (email == null || !email.contains("@")) {
            return null;
        }
        int at = email.indexOf('@');
        return email.charAt(0) + "***" + email.substring(at);
    }
}
