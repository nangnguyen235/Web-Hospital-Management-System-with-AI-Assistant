package vn.nhom6.his.lichkham.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom6.his.contracts.danhmuc.DanhMucContracts;
import vn.nhom6.his.contracts.danhmuc.DichVuInfo;
import vn.nhom6.his.contracts.danhmuc.LoaiDichVu;
import vn.nhom6.his.contracts.danhmuc.LoaiPhong;
import vn.nhom6.his.contracts.danhmuc.PhongInfo;
import vn.nhom6.his.contracts.nguoidung.NguoiDungContracts;
import vn.nhom6.his.contracts.nguoidung.NguoiDungContracts.NhanVienInfo;
import vn.nhom6.his.lichkham.constant.Buoi;
import vn.nhom6.his.lichkham.dto.LichKhamDto;
import vn.nhom6.his.lichkham.entity.KhungGio;
import vn.nhom6.his.lichkham.entity.LichKhamBacSi;
import vn.nhom6.his.lichkham.repository.KhungGioRepository;
import vn.nhom6.his.lichkham.repository.LichKhamBacSiRepository;
import vn.nhom6.his.lichkham.repository.PhieuDatKhamRepository;
import vn.nhom6.his.shared.kernel.CurrentUser;
import vn.nhom6.his.shared.kernel.Loi;

/** Lịch khám bác sĩ: trưởng khoa tạo/sửa/xóa/xem (FR-BS-11…14), bác sĩ xem (FR-BS-02), công khai, Admin (FR-AD-10). */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LichKhamService {

    private final LichKhamBacSiRepository lichRepository;
    private final KhungGioRepository khungRepository;
    private final PhieuDatKhamRepository phieuRepository;
    private final DanhMucContracts danhMuc;
    private final NguoiDungContracts nguoiDung;
    private final LichKhamTraCuu traCuu;
    private final CurrentUser currentUser;

    /** BR-05, BR-06: chỉ trưởng khoa, cho bác sĩ/dịch vụ KHAM_BENH/phòng PHONG_KHAM của khoa mình; tự sinh 4 khung/buổi. */
    @Transactional
    public List<LichKhamDto.Lich> tao(LichKhamDto.TaoLich r) {
        int khoaId = khoaCuaToi();
        kiemTraBacSi(r.bacSiId(), khoaId);
        DichVuInfo dv = danhMuc.layDichVu(r.dichVuId()).filter(d -> !d.daXoa() && d.khoaId() == khoaId
                && d.loaiDichVu() == LoaiDichVu.KHAM_BENH)
                .orElseThrow(() -> new Loi("DICH_VU_KHONG_HOP_LE", "Dịch vụ phải là dịch vụ khám của khoa.").ngoaiLe());
        PhongInfo phong = danhMuc.layPhong(r.phongId()).filter(p -> !p.daXoa() && p.khoaId() == khoaId
                && p.loaiPhong() == LoaiPhong.PHONG_KHAM)
                .orElseThrow(() -> new Loi("PHONG_KHONG_HOP_LE", "Phòng phải là phòng khám của khoa.").ngoaiLe());
        List<Object[]> cacBuoi = new ArrayList<>();
        (r.ngaySang() == null ? List.<LocalDate>of() : r.ngaySang()).forEach(n -> cacBuoi.add(new Object[]{n, Buoi.SANG}));
        (r.ngayChieu() == null ? List.<LocalDate>of() : r.ngayChieu()).forEach(n -> cacBuoi.add(new Object[]{n, Buoi.CHIEU}));
        if (cacBuoi.isEmpty()) {
            throw new Loi("THIEU_NGAY", "Vui lòng chọn ít nhất một ngày buổi sáng hoặc buổi chiều.").ngoaiLe();
        }
        Set<String> daChon = new HashSet<>();
        List<LichKhamBacSi> moi = new ArrayList<>();
        for (Object[] b : cacBuoi) {
            LocalDate ngay = (LocalDate) b[0];
            Buoi buoi = (Buoi) b[1];
            if (!daChon.add(ngay + "-" + buoi)) {
                continue;
            }
            String nhan = ngay + " buổi " + (buoi == Buoi.SANG ? "sáng" : "chiều");
            if (!ngay.atTime(LichKhamTraCuu.ketThucBuoi(buoi)).isAfter(traCuu.bayGio())) {
                throw new Loi("BUOI_DA_QUA", "Không tạo lịch cho buổi đã qua (" + nhan + ").").ngoaiLe();
            }
            if (lichRepository.existsByPhongIdAndNgayAndBuoiAndDaXoaFalseAndIdNot(phong.id(), ngay, buoi, -1L)) {
                throw Loi.xungDot("TRUNG_LICH_PHONG", "Phòng " + phong.tenPhong() + " đã có lịch " + nhan + ".").ngoaiLe();
            }
            if (lichRepository.existsByBacSiIdAndNgayAndBuoiAndDaXoaFalseAndIdNot(r.bacSiId(), ngay, buoi, -1L)) {
                throw Loi.xungDot("TRUNG_LICH_BAC_SI", "Bác sĩ đã có lịch " + nhan + ".").ngoaiLe();
            }
            LichKhamBacSi l = new LichKhamBacSi();
            l.setBacSiId(r.bacSiId());
            l.setPhongId(phong.id());
            l.setDichVuId(dv.id());
            l.setSoLuot(r.soLuotMoiKhung() * 4);
            l.setThuTrongTuan(LichKhamTraCuu.thu(ngay));
            l.setNgay(ngay);
            l.setBuoi(buoi);
            lichRepository.save(l);
            sinhKhung(l, r.soLuotMoiKhung());
            moi.add(l);
        }
        return traCuu.lich(moi);
    }

    /** FR-HT-06, BR-06: 4 khung 1 giờ; so_bat_dau = (chi_so_khung % 4) × so_luong_toi_da + 1. */
    private void sinhKhung(LichKhamBacSi l, int soLuotMoiKhung) {
        int dau = l.getBuoi() == Buoi.SANG ? 0 : 4;
        for (int chi = dau; chi < dau + 4; chi++) {
            KhungGio k = new KhungGio();
            k.setLichKhamId(l.getId());
            k.setChiSoKhung(chi);
            k.setNgay(l.getNgay());
            k.setSoLuongToiDa(soLuotMoiKhung);
            k.setSoBatDau((chi % 4) * soLuotMoiKhung + 1);
            khungRepository.save(k);
        }
    }

    /** BR-07: sửa khi ngày khám sau hôm nay; đổi bác sĩ cùng khoa, không trùng; số lượt chỉ khi lịch chưa có phiếu. */
    @Transactional
    public LichKhamDto.Lich sua(long id, LichKhamDto.SuaLich r) {
        int khoaId = khoaCuaToi();
        LichKhamBacSi l = lichCuaKhoa(id, khoaId);
        if (!l.getNgay().isAfter(traCuu.homNay())) {
            throw new Loi("KHONG_SUA_DUOC", "Chỉ sửa được lịch của các ngày sau hôm nay.").ngoaiLe();
        }
        if (!r.bacSiId().equals(l.getBacSiId())) {
            kiemTraBacSi(r.bacSiId(), khoaId);
            if (lichRepository.existsByBacSiIdAndNgayAndBuoiAndDaXoaFalseAndIdNot(r.bacSiId(), l.getNgay(), l.getBuoi(), l.getId())) {
                throw Loi.xungDot("TRUNG_LICH_BAC_SI", "Bác sĩ đã có lịch trong buổi này.").ngoaiLe();
            }
            l.setBacSiId(r.bacSiId());
        }
        if (r.soLuotMoiKhung() * 4 != l.getSoLuot()) {
            if (phieuRepository.lichDaCoPhieu(l.getId())) {
                throw new Loi("LICH_DA_CO_PHIEU", "Lịch đã có phiếu đặt, không đổi được số lượt.").ngoaiLe();
            }
            l.setSoLuot(r.soLuotMoiKhung() * 4);
            for (KhungGio k : khungRepository.findByLichKhamIdOrderByChiSoKhungAsc(l.getId())) {
                k.setSoLuongToiDa(r.soLuotMoiKhung());
                k.setSoBatDau((k.getChiSoKhung() % 4) * r.soLuotMoiKhung() + 1);
            }
        }
        return traCuu.lich(List.of(l)).getFirst();
    }

    /** BR-07: chỉ xóa lịch không có phiếu đã thanh toán hoặc đang giữ chỗ. */
    @Transactional
    public void xoa(long id) {
        LichKhamBacSi l = lichCuaKhoa(id, khoaCuaToi());
        if (phieuRepository.lichConPhieu(l.getId(), traCuu.mocGiuCho())) {
            throw new Loi("LICH_CON_PHIEU", "Lịch đã có người đặt, không xóa được.").ngoaiLe();
        }
        l.setDaXoa(true);
    }

    /** FR-BS-14: lịch của khoa theo phòng/bác sĩ/khoảng ngày. */
    public List<LichKhamDto.Lich> lichKhoa(LocalDate tu, LocalDate den, Integer phongId, Long bacSiId) {
        return lichTheoKhoa(khoaCuaToi(), tu, den, phongId, bacSiId);
    }

    /** FR-AD-10: Admin xem mọi lịch (lọc khoa tùy chọn). */
    public List<LichKhamDto.Lich> lichAdmin(Integer khoaId, LocalDate tu, LocalDate den, Integer phongId, Long bacSiId) {
        if (khoaId != null) {
            return lichTheoKhoa(khoaId, tu, den, phongId, bacSiId);
        }
        LocalDate t = tu == null ? traCuu.homNay() : tu;
        return traCuu.lich(lichRepository.timKiem(t, den == null ? t.plusDays(30) : den, phongId, bacSiId));
    }

    /** FR-BS-14: bác sĩ của khoa chưa có lịch trong ngày – buổi. */
    public List<LichKhamDto.BacSiTrong> bacSiChuaCoLich(LocalDate ngay, Buoi buoi) {
        int khoaId = khoaCuaToi();
        List<NhanVienInfo> bacSi = nguoiDung.bacSiCuaKhoa(khoaId);
        Set<Long> co = lichRepository.findByBacSiIdInAndNgayAndDaXoaFalse(bacSi.stream().map(NhanVienInfo::id).toList(), ngay)
                .stream().filter(l -> buoi == null || l.getBuoi() == buoi).map(LichKhamBacSi::getBacSiId).collect(Collectors.toSet());
        return bacSi.stream().filter(b -> !co.contains(b.id())).map(b -> new LichKhamDto.BacSiTrong(b.id(), b.hoTen())).toList();
    }

    /** FR-BS-02: lịch hôm nay và thời khóa biểu của bác sĩ đăng nhập. */
    public List<LichKhamDto.Lich> lichCuaToi(LocalDate tu, LocalDate den) {
        Long bacSiId = currentUser.nhanVienId();
        LocalDate t = tu == null ? traCuu.homNay() : tu;
        return traCuu.lich(lichRepository.timKiem(t, den == null ? t : den, null, bacSiId));
    }

    /** Công khai: lịch sắp tới của một bác sĩ kèm khung còn chỗ (FR-BN-07, FR-HT-01). */
    public List<LichKhamDto.Lich> lichCongKhaiCuaBacSi(long bacSiId, LocalDate tu, LocalDate den) {
        LocalDate t = tu == null || tu.isBefore(traCuu.homNay()) ? traCuu.homNay() : tu;
        return traCuu.lich(lichRepository.timKiem(t, den == null ? t.plusDays(30) : den, null, bacSiId)).stream()
                .filter(l -> l.khung().stream().anyMatch(k -> !k.daQua())).toList();
    }

    public LichKhamDto.Lich chiTiet(long id) {
        return traCuu.lich(List.of(lichRepository.findByIdAndDaXoaFalse(id)
                .orElseThrow(() -> Loi.khongTimThay("lịch khám").ngoaiLe()))).getFirst();
    }

    private List<LichKhamDto.Lich> lichTheoKhoa(int khoaId, LocalDate tu, LocalDate den, Integer phongId, Long bacSiId) {
        List<Integer> phong = danhMuc.phongCuaKhoa(khoaId);
        if (phong.isEmpty()) {
            return List.of();
        }
        LocalDate t = tu == null ? traCuu.homNay() : tu;
        return traCuu.lich(lichRepository.timKiemTrongPhong(t, den == null ? t.plusDays(30) : den, phong, phongId, bacSiId));
    }

    private void kiemTraBacSi(long bacSiId, int khoaId) {
        nguoiDung.layNhanVien(bacSiId)
                .filter(n -> !n.daXoa() && "BAC_SI".equals(n.vaiTro()) && Integer.valueOf(khoaId).equals(n.khoaId()))
                .orElseThrow(() -> new Loi("BAC_SI_KHONG_HOP_LE", "Bác sĩ phải đang làm việc ở khoa của bạn.").ngoaiLe());
    }

    private LichKhamBacSi lichCuaKhoa(long id, int khoaId) {
        LichKhamBacSi l = lichRepository.findByIdAndDaXoaFalse(id).orElseThrow(() -> Loi.khongTimThay("lịch khám").ngoaiLe());
        if (!danhMuc.phongCuaKhoa(khoaId).contains(l.getPhongId())) {
            throw Loi.khongCoQuyen("Lịch không thuộc khoa của bạn.").ngoaiLe();
        }
        return l;
    }

    private int khoaCuaToi() {
        Integer khoaId = currentUser.khoaId();
        if (khoaId == null || !currentUser.laTruongKhoa()) {
            throw Loi.khongCoQuyen("Chỉ trưởng khoa mới quản lý lịch khám của khoa.").ngoaiLe();
        }
        return khoaId;
    }
}
