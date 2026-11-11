package vn.nhom6.his.lichkham.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom6.his.contracts.danhmuc.DanhMucContracts;
import vn.nhom6.his.contracts.danhmuc.PhongInfo;
import vn.nhom6.his.contracts.lichkham.LuotKhamDaGoiIntegrationEvent;
import vn.nhom6.his.contracts.nguoidung.NguoiDungContracts;
import vn.nhom6.his.contracts.nguoidung.NguoiDungContracts.NhanVienInfo;
import vn.nhom6.his.lichkham.dto.LichKhamDto;
import vn.nhom6.his.lichkham.entity.LichDieuDuong;
import vn.nhom6.his.lichkham.entity.LichKhamBacSi;
import vn.nhom6.his.lichkham.entity.PhieuDatKham;
import vn.nhom6.his.lichkham.repository.LichDieuDuongRepository;
import vn.nhom6.his.lichkham.repository.LichKhamBacSiRepository;
import vn.nhom6.his.lichkham.repository.PhieuDatKhamRepository;
import vn.nhom6.his.shared.kernel.CurrentUser;
import vn.nhom6.his.shared.kernel.Loi;

/** Điều dưỡng: gọi tiếp (FR-DD-07, BR-15), lịch phân công + phòng hôm nay (FR-DD-02), phân công (FR-DD-12, BR-08). */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DieuDuongService {

    static final int SO_MOI_LAN_GOI = 5;

    private final LichKhamBacSiRepository lichRepository;
    private final PhieuDatKhamRepository phieuRepository;
    private final LichDieuDuongRepository phanCongRepository;
    private final DanhMucContracts danhMuc;
    private final NguoiDungContracts nguoiDung;
    private final LichKhamTraCuu traCuu;
    private final ApplicationEventPublisher events;
    private final CurrentUser currentUser;

    /** BR-15: so_dang_goi = min(+5, số lớn nhất đã cấp); khóa dòng lịch; phát LuotKhamDaGoi để thông báo. */
    @Transactional
    public LichKhamDto.GoiTiep goiTiep(long lichKhamId) {
        LichKhamBacSi l = lichRepository.khoaGhi(lichKhamId).filter(x -> !x.isDaXoa())
                .orElseThrow(() -> Loi.khongTimThay("lịch khám").ngoaiLe());
        if (!l.getNgay().equals(traCuu.homNay())) {
            throw new Loi("KHONG_PHAI_HOM_NAY", "Chỉ gọi số cho lịch khám hôm nay.").ngoaiLe();
        }
        int lonNhat = phieuRepository.soLonNhat(l.getId());
        int cu = l.getSoDangGoi();
        if (cu >= lonNhat) {
            throw new Loi("HET_LUOT", "Đã gọi tới số cuối cùng của lịch khám.").ngoaiLe();
        }
        int moi = Math.min(cu + SO_MOI_LAN_GOI, lonNhat);
        l.setSoDangGoi(moi);
        List<PhieuDatKham> vuaGoi = phieuRepository.trongKhoangSo(l.getId(), cu, moi);
        String tenPhong = danhMuc.layPhong(l.getPhongId()).map(PhongInfo::tenPhong).orElse("");
        events.publishEvent(new LuotKhamDaGoiIntegrationEvent(l.getId(), tenPhong, vuaGoi.stream()
                .filter(p -> !p.isDaHuy())
                .map(p -> new LuotKhamDaGoiIntegrationEvent.SoDaGoi(p.getHoSoId(), p.getSoThuTu())).toList()));
        return new LichKhamDto.GoiTiep(l.getId(), moi, lonNhat, SoDangGoiService.cacSoVuaGoi(moi));
    }

    /** FR-DD-02: phòng được phân công hôm nay (theo thứ) kèm lịch khám hôm nay của phòng. */
    public List<LichKhamDto.PhongHomNay> phongHomNay() {
        int thu = LichKhamTraCuu.thu(traCuu.homNay());
        Map<Integer, String> phong = new LinkedHashMap<>();
        phanCongRepository.findByDieuDuongIdAndThuTrongTuanAndDaXoaFalse(currentUser.nhanVienId(), thu)
                .forEach(pc -> phong.putIfAbsent(pc.getPhongId(), pc.getTenPhong()));
        if (phong.isEmpty()) {
            return List.of();
        }
        List<LichKhamDto.Lich> lich = traCuu.lich(lichRepository.findByPhongIdInAndNgayAndDaXoaFalseOrderByBuoiAsc(phong.keySet(), traCuu.homNay()));
        return phong.entrySet().stream().map(e -> new LichKhamDto.PhongHomNay(e.getKey(), e.getValue(),
                lich.stream().filter(x -> x.phongId() == e.getKey()).toList())).toList();
    }

    /** Lịch khám hôm nay của một phòng bất kỳ — không chặn phòng ngoài phân công (BR-08). */
    public List<LichKhamDto.Lich> lichHomNayCuaPhong(int phongId) {
        return traCuu.lich(lichRepository.findByPhongIdInAndNgayAndDaXoaFalseOrderByBuoiAsc(List.of(phongId), traCuu.homNay()));
    }

    /** FR-DD-02: lịch phân công của điều dưỡng đăng nhập (theo tuần, frontend trải ra tháng). */
    public List<LichKhamDto.LichDieuDuong> phanCongCuaToi() {
        return phanCongRepository.findByDieuDuongIdAndDaXoaFalseOrderByThuTrongTuanAscBuoiAsc(currentUser.nhanVienId())
                .stream().map(DieuDuongService::dto).toList();
    }

    /** FR-DD-12: phân công của khoa (điều dưỡng trưởng). */
    public List<LichKhamDto.LichDieuDuong> phanCongCuaKhoa() {
        List<Integer> phong = danhMuc.phongCuaKhoa(khoaDieuDuongTruong());
        return phong.isEmpty() ? List.of()
                : phanCongRepository.findByPhongIdInAndDaXoaFalseOrderByThuTrongTuanAscBuoiAsc(phong).stream()
                .map(DieuDuongService::dto).toList();
    }

    /**
     * BR-08: chỉ điều dưỡng trưởng, cho điều dưỡng của khoa mình vào phòng của khoa mình. Điều dưỡng đã có phòng ở
     * thứ – buổi đó → báo tên phòng; phòng đã có điều dưỡng → từ chối.
     */
    @Transactional
    public List<LichKhamDto.LichDieuDuong> phanCong(LichKhamDto.PhanCong r) {
        int khoaId = khoaDieuDuongTruong();
        NhanVienInfo dd = nguoiDung.layNhanVien(r.dieuDuongId())
                .filter(n -> !n.daXoa() && "DIEU_DUONG".equals(n.vaiTro()) && Integer.valueOf(khoaId).equals(n.khoaId()))
                .orElseThrow(() -> new Loi("DIEU_DUONG_KHONG_HOP_LE", "Điều dưỡng phải đang làm việc ở khoa của bạn.").ngoaiLe());
        PhongInfo phong = danhMuc.layPhong(r.phongId()).filter(p -> !p.daXoa() && p.khoaId() == khoaId)
                .orElseThrow(() -> new Loi("PHONG_KHONG_HOP_LE", "Phòng phải thuộc khoa của bạn.").ngoaiLe());
        List<LichDieuDuong> moi = new ArrayList<>();
        for (LichKhamDto.Ca ca : r.cacCa()) {
            String nhan = (ca.thuTrongTuan() == 1 ? "Chủ nhật" : "Thứ " + ca.thuTrongTuan()) + " " + ca.buoi();
            phanCongRepository.findFirstByDieuDuongIdAndThuTrongTuanAndBuoiAndDaXoaFalse(dd.id(), ca.thuTrongTuan(), ca.buoi())
                    .ifPresent(x -> {
                        throw Loi.xungDot("DIEU_DUONG_DA_CO_PHONG", dd.hoTen() + " đã làm " + x.getTenPhong() + " (" + nhan + ").").ngoaiLe();
                    });
            phanCongRepository.findFirstByPhongIdAndThuTrongTuanAndBuoiAndDaXoaFalse(phong.id(), ca.thuTrongTuan(), ca.buoi())
                    .ifPresent(x -> {
                        throw Loi.xungDot("PHONG_DA_CO_DIEU_DUONG", phong.tenPhong() + " đã có " + x.getTenDieuDuong() + " (" + nhan + ").").ngoaiLe();
                    });
            LichDieuDuong pc = new LichDieuDuong();
            pc.setDieuDuongId(dd.id());
            pc.setTenDieuDuong(dd.hoTen());
            pc.setPhongId(phong.id());
            pc.setTenPhong(phong.tenPhong());
            pc.setThuTrongTuan(ca.thuTrongTuan());
            pc.setBuoi(ca.buoi());
            moi.add(phanCongRepository.save(pc));
        }
        return moi.stream().map(DieuDuongService::dto).toList();
    }

    /** FR-DD-12: xóa một hoặc nhiều phân công của khoa. */
    @Transactional
    public void xoaPhanCong(List<Long> ids) {
        List<Integer> phong = danhMuc.phongCuaKhoa(khoaDieuDuongTruong());
        for (LichDieuDuong pc : phanCongRepository.findAllById(ids)) {
            if (!phong.contains(pc.getPhongId())) {
                throw Loi.khongCoQuyen("Phân công không thuộc khoa của bạn.").ngoaiLe();
            }
            pc.setDaXoa(true);
        }
    }

    private int khoaDieuDuongTruong() {
        if (currentUser.khoaId() == null || !currentUser.laTruongKhoa()) {
            throw Loi.khongCoQuyen("Chỉ điều dưỡng trưởng mới phân công.").ngoaiLe();
        }
        return currentUser.khoaId();
    }

    static LichKhamDto.LichDieuDuong dto(LichDieuDuong x) {
        return new LichKhamDto.LichDieuDuong(x.getId(), x.getDieuDuongId(), x.getTenDieuDuong(), x.getPhongId(), x.getTenPhong(),
                x.getThuTrongTuan(), x.getBuoi());
    }
}
