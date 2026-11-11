package vn.nhom6.his.lichkham.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import vn.nhom6.his.contracts.danhmuc.DanhMucContracts;
import vn.nhom6.his.contracts.danhmuc.DichVuInfo;
import vn.nhom6.his.contracts.nguoidung.NguoiDungContracts;
import vn.nhom6.his.contracts.nguoidung.NguoiDungContracts.HoSoInfo;
import vn.nhom6.his.contracts.vienphi.LoaiThanhToan;
import vn.nhom6.his.contracts.vienphi.VienPhiContracts;
import vn.nhom6.his.lichkham.constant.KenhDat;
import vn.nhom6.his.lichkham.dto.LichKhamDto;
import vn.nhom6.his.lichkham.entity.KhungGio;
import vn.nhom6.his.lichkham.entity.LichKhamBacSi;
import vn.nhom6.his.lichkham.entity.PhieuDatKham;
import vn.nhom6.his.lichkham.repository.KhungGioRepository;
import vn.nhom6.his.lichkham.repository.LichKhamBacSiRepository;
import vn.nhom6.his.lichkham.repository.PhieuDatKhamRepository;
import vn.nhom6.his.shared.kernel.CurrentUser;
import vn.nhom6.his.shared.kernel.Loi;
import vn.nhom6.his.shared.kernel.MaNgauNhien;

/**
 * Đặt khám online (FR-BN-08, FR-HT-01, BR-09, BR-10), phiếu của bệnh nhân (FR-BN-09, 10), khách tại quầy (FR-DD-08, BR-14).
 * Phiếu mới: {@code da_xoa = true}, {@code so_thu_tu = −1} (cách TheDuck); giữ chỗ 15 phút; khóa phien_ban của khung.
 */
@Service
@RequiredArgsConstructor
public class DatKhamService {

    static final BigDecimal PHI_DAT_KHAM = BigDecimal.valueOf(15_000); // như TheDuck

    private final KhungGioRepository khungRepository;
    private final LichKhamBacSiRepository lichRepository;
    private final PhieuDatKhamRepository phieuRepository;
    private final DanhMucContracts danhMuc;
    private final NguoiDungContracts nguoiDung;
    private final VienPhiContracts vienPhi;
    private final LichKhamTraCuu traCuu;
    private final CurrentUser currentUser;
    private final TransactionTemplate tx;

    /** Hai người tranh chỗ cuối: người sau lỗi phien_ban → thử lại một lần, vẫn lỗi thì báo khung đã đầy. */
    public LichKhamDto.KetQuaDatKham datKham(LichKhamDto.DatKham r, String nguon, String ip) {
        long taiKhoanId = currentUser.taiKhoanId();
        for (int lan = 1; ; lan++) {
            try {
                return tx.execute(s -> datKhamMotLan(r, taiKhoanId, nguon, ip));
            } catch (ObjectOptimisticLockingFailureException | org.springframework.dao.PessimisticLockingFailureException e) {
                if (lan >= 2) {
                    throw Loi.xungDot("KHUNG_DA_DAY", "Khung giờ vừa hết chỗ, vui lòng chọn khung khác.").ngoaiLe();
                }
            }
        }
    }

    private LichKhamDto.KetQuaDatKham datKhamMotLan(LichKhamDto.DatKham r, long taiKhoanId, String nguon, String ip) {
        HoSoInfo hoSo = nguoiDung.layHoSo(r.hoSoId())
                .filter(h -> !h.daXoa() && Long.valueOf(taiKhoanId).equals(h.taiKhoanId()))
                .orElseThrow(() -> Loi.khongTimThay("hồ sơ").ngoaiLe());
        Set<Long> lichDaChon = new HashSet<>();
        Set<Integer> khoaDaChon = new HashSet<>();
        List<KhungGio> cacKhung = new ArrayList<>();
        List<BigDecimal> cacGia = new ArrayList<>();
        for (Long khungId : new java.util.TreeSet<>(r.khungGioIds())) { // khóa theo id tăng dần: không khóa chéo nhau
            KhungGio k = khungRepository.giuCho(khungId).orElseThrow(() -> Loi.khongTimThay("khung giờ").ngoaiLe());
            LichKhamBacSi l = lichRepository.findByIdAndDaXoaFalse(k.getLichKhamId())
                    .orElseThrow(() -> Loi.khongTimThay("lịch khám").ngoaiLe());
            DichVuInfo dv = danhMuc.layDichVu(l.getDichVuId()).orElseThrow();
            if (traCuu.khungDaBatDau(k)) {
                throw new Loi("KHUNG_DA_QUA", "Chỉ đặt được khung giờ chưa bắt đầu.").ngoaiLe();
            }
            if (!lichDaChon.add(l.getId()) || !khoaDaChon.add(dv.khoaId())) {
                throw new Loi("TRUNG_KHOA", "Các khung đặt cùng lúc phải thuộc lịch khác nhau và khoa khác nhau.").ngoaiLe();
            }
            if (!phieuRepository.phieuHieuLuc(hoSo.id(), l.getId(), traCuu.mocGiuCho()).isEmpty()) {
                throw Loi.xungDot("DA_CO_PHIEU", "Hồ sơ đã có phiếu ở lịch khám này.").ngoaiLe();
            }
            if (traCuu.conCho(k) <= 0) {
                throw Loi.xungDot("KHUNG_DA_DAY", "Khung giờ đã hết chỗ, vui lòng chọn khung khác.").ngoaiLe();
            }
            cacKhung.add(k);
            cacGia.add(dv.gia());
        }
        BigDecimal tong = cacGia.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        VienPhiContracts.GiaoDichMoi gd = vienPhi.taoGiaoDich(new VienPhiContracts.TaoGiaoDich(
                LoaiThanhToan.DAT_KHAM, tong, PHI_DAT_KHAM, taiKhoanId, nguon, ip));
        List<PhieuDatKham> phieu = new ArrayList<>();
        for (int i = 0; i < cacKhung.size(); i++) {
            phieu.add(taoPhieu(hoSo.id(), cacKhung.get(i).getId(), cacGia.get(i), gd.giaoDichId(), KenhDat.WEB));
        }
        return new LichKhamDto.KetQuaDatKham(gd.giaoDichId(), gd.urlThanhToan(), gd.hetHan(), tong, PHI_DAT_KHAM,
                traCuu.phieu(phieu, false));
    }

    PhieuDatKham taoPhieu(long hoSoId, long khungGioId, BigDecimal gia, long giaoDichId, KenhDat kenh) {
        PhieuDatKham p = new PhieuDatKham();
        String ma;
        do {
            ma = MaNgauNhien.hex(12);
        } while (phieuRepository.existsByMaPhieu(ma));
        p.setMaPhieu(ma);
        p.setHoSoId(hoSoId);
        p.setKhungGioId(khungGioId);
        p.setGiaDichVu(gia);
        p.setGiaoDichId(giaoDichId);
        p.setKenhDat(kenh);
        p.setSoThuTu(-1);
        p.setDaXoa(true); // chưa thanh toán (cách TheDuck)
        return phieuRepository.save(p);
    }

    /** FR-BN-09: phiếu của một hồ sơ thuộc tài khoản; ghi lười hết hạn/vắng mặt. */
    @Transactional
    public List<LichKhamDto.Phieu> phieuCuaHoSo(long hoSoId) {
        nguoiDung.layHoSo(hoSoId).filter(h -> Long.valueOf(currentUser.taiKhoanId()).equals(h.taiKhoanId()))
                .orElseThrow(() -> Loi.khongTimThay("hồ sơ").ngoaiLe());
        return traCuu.phieu(phieuRepository.findByHoSoIdOrderByIdDesc(hoSoId), true);
    }

    /** FR-BN-09, 10: chi tiết phiếu + số đang gọi. Bệnh nhân chỉ xem phiếu của hồ sơ mình; nhân viên xem được. */
    @Transactional
    public LichKhamDto.Phieu chiTiet(long phieuId) {
        PhieuDatKham p = phieuRepository.findById(phieuId).orElseThrow(() -> Loi.khongTimThay("phiếu").ngoaiLe());
        if (currentUser.vaiTro() == vn.nhom6.his.shared.kernel.VaiTro.BENH_NHAN) {
            nguoiDung.layHoSo(p.getHoSoId()).filter(h -> Long.valueOf(currentUser.taiKhoanId()).equals(h.taiKhoanId()))
                    .orElseThrow(() -> Loi.khongTimThay("phiếu").ngoaiLe());
        }
        return traCuu.phieu(List.of(p), true).getFirst();
    }

    /** Phiếu của một giao dịch (trang kết quả thanh toán). */
    @Transactional(readOnly = true)
    public List<LichKhamDto.Phieu> phieuCuaGiaoDich(long giaoDichId) {
        List<PhieuDatKham> ds = phieuRepository.findByGiaoDichId(giaoDichId);
        if (!ds.isEmpty() && currentUser.vaiTro() == vn.nhom6.his.shared.kernel.VaiTro.BENH_NHAN) {
            nguoiDung.layHoSo(ds.getFirst().getHoSoId()).filter(h -> Long.valueOf(currentUser.taiKhoanId()).equals(h.taiKhoanId()))
                    .orElseThrow(() -> Loi.khongTimThay("phiếu").ngoaiLe());
        }
        return traCuu.phieu(ds, false);
    }

    /**
     * BR-14: khách vãng lai — lịch hôm nay; hồ sơ đã có phiếu còn hiệu lực ở lịch thì trả phiếu cũ; lấy khung còn chỗ
     * sớm nhất chưa kết thúc, hết chỗ thì dồn vào khung cuối buổi (được vượt sức chứa). Thanh toán bằng QR VNPAY.
     */
    @Transactional
    public LichKhamDto.KetQuaQuay taoPhieuQuay(LichKhamDto.TaoPhieuQuay r, String nguon, String ip) {
        HoSoInfo hoSo = nguoiDung.layHoSo(r.hoSoId()).filter(h -> !h.daXoa())
                .orElseThrow(() -> Loi.khongTimThay("hồ sơ").ngoaiLe());
        if (hoSo.benhNhanId() == null) {
            throw new Loi("CHUA_LIEN_KET_CCCD", "Hồ sơ chưa đối chiếu CCCD — liên kết bệnh nhân trước khi tạo phiếu.").ngoaiLe();
        }
        LichKhamBacSi l = lichRepository.findByIdAndDaXoaFalse(r.lichKhamId())
                .orElseThrow(() -> Loi.khongTimThay("lịch khám").ngoaiLe());
        if (!l.getNgay().equals(traCuu.homNay())) {
            throw new Loi("KHONG_PHAI_HOM_NAY", "Quầy chỉ nhận lịch khám hôm nay.").ngoaiLe();
        }
        List<PhieuDatKham> cu = phieuRepository.phieuHieuLuc(hoSo.id(), l.getId(), traCuu.mocGiuCho());
        if (!cu.isEmpty()) {
            PhieuDatKham p = cu.getFirst();
            String url = p.isDaXoa() ? vienPhi.urlThanhToan(p.getGiaoDichId(), ip) : null;
            return new LichKhamDto.KetQuaQuay(traCuu.phieu(List.of(p), false).getFirst(), url, !p.isDaXoa());
        }
        List<KhungGio> khung = khungRepository.findByLichKhamIdOrderByChiSoKhungAsc(l.getId());
        KhungGio chon = khung.stream().filter(k -> !traCuu.khungDaKetThuc(k) && traCuu.conCho(k) > 0).findFirst()
                .orElseGet(() -> khung.getLast()); // khung cuối buổi, được vượt sức chứa
        if (traCuu.khungDaKetThuc(chon)) {
            throw new Loi("BUOI_DA_KET_THUC", "Buổi khám đã kết thúc.").ngoaiLe();
        }
        DichVuInfo dv = danhMuc.layDichVu(l.getDichVuId()).orElseThrow();
        VienPhiContracts.GiaoDichMoi gd = vienPhi.taoGiaoDich(new VienPhiContracts.TaoGiaoDich(
                LoaiThanhToan.DAT_KHAM, dv.gia(), PHI_DAT_KHAM, hoSo.taiKhoanId(), nguon, ip));
        PhieuDatKham p = taoPhieu(hoSo.id(), chon.getId(), dv.gia(), gd.giaoDichId(), KenhDat.QUAY);
        return new LichKhamDto.KetQuaQuay(traCuu.phieu(List.of(p), false).getFirst(), gd.urlThanhToan(), false);
    }
}
