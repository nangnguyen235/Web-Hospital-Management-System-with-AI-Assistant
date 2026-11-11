package vn.nhom6.his.hosobenhan.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom6.his.contracts.danhmuc.DanhMucContracts;
import vn.nhom6.his.contracts.nguoidung.NguoiDungContracts.HoSoInfo;
import vn.nhom6.his.hosobenhan.dto.BenhAnDto;
import vn.nhom6.his.hosobenhan.entity.BenhAn;
import vn.nhom6.his.hosobenhan.entity.ChiTietDonThuoc;
import vn.nhom6.his.hosobenhan.entity.ChiTietNhacThuoc;
import vn.nhom6.his.hosobenhan.entity.DonThuoc;
import vn.nhom6.his.hosobenhan.entity.NhacThuoc;
import vn.nhom6.his.hosobenhan.repository.BenhAnRepository;
import vn.nhom6.his.hosobenhan.repository.ChiTietDonThuocRepository;
import vn.nhom6.his.hosobenhan.repository.ChiTietNhacThuocRepository;
import vn.nhom6.his.hosobenhan.repository.DonThuocRepository;
import vn.nhom6.his.hosobenhan.repository.NhacThuocRepository;
import vn.nhom6.his.shared.kernel.CurrentUser;
import vn.nhom6.his.shared.kernel.Loi;

/**
 * Nhắc uống thuốc (FR-BN-15, FR-HT-08, BR-21): sinh chi tiết từng ngày theo danh sách giờ tới khi hết tổng số lượng.
 * Không job nền, không push: app hiện các lần có thoi_diem_nhac ≤ hiện tại; bệnh nhân xác nhận đã nhận/đã uống/bỏ qua.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class NhacThuocService {

    private final NhacThuocRepository nhacRepository;
    private final ChiTietNhacThuocRepository chiTietNhacRepository;
    private final ChiTietDonThuocRepository chiTietDonRepository;
    private final DonThuocRepository donThuocRepository;
    private final BenhAnRepository benhAnRepository;
    private final DanhMucContracts danhMuc;
    private final BenhNhanKhamService benhNhanKham;
    private final BenhAnTraCuu traCuu;
    private final CurrentUser currentUser;

    /** Đã có nhắc cho dòng thuốc + hồ sơ thì thay (bất biến 11). */
    public BenhAnDto.NhacThuoc tao(BenhAnDto.TaoNhacThuoc r) {
        HoSoInfo hoSo = benhNhanKham.hoSoCuaToi(r.hoSoId());
        ChiTietDonThuoc ct = chiTietDonRepository.findById(r.chiTietDonThuocId()).filter(c -> !c.isDaXoa())
                .orElseThrow(() -> Loi.khongTimThay("dòng thuốc").ngoaiLe());
        DonThuoc don = donThuocRepository.findById(ct.getDonThuocId()).orElseThrow();
        BenhAn b = benhAnRepository.findById(don.getBenhAnId()).orElseThrow();
        boolean cuaHoSo = b.getHoSoId().equals(hoSo.id()) || (hoSo.benhNhanId() != null && hoSo.benhNhanId().equals(b.getBenhNhanId()));
        if (!cuaHoSo) {
            throw Loi.khongTimThay("dòng thuốc").ngoaiLe();
        }
        if (r.ngayBatDau().isBefore(traCuu.homNay())) {
            throw new Loi("NGAY_BAT_DAU", "Ngày bắt đầu không được trước hôm nay.").ngoaiLe();
        }
        nhacRepository.findFirstByChiTietDonThuocIdAndHoSoIdAndDaXoaFalse(ct.getId(), hoSo.id()).ifPresent(this::xoaNhac);
        NhacThuoc n = new NhacThuoc();
        n.setChiTietDonThuocId(ct.getId());
        n.setHoSoId(hoSo.id());
        n.setNgayBatDau(r.ngayBatDau());
        n.setTongSoLuong(r.tongSoLuong());
        n.setSoLuongConLai(r.tongSoLuong());
        n.setNgayKetThuc(r.ngayBatDau().atTime(LocalTime.MAX.withNano(0)));
        nhacRepository.save(n);
        sinhChiTiet(n, ct, hoSo.hoTen(), r.ngayBatDau().atStartOfDay(), r.tongSoLuong(), r.cacLan(), 1);
        return dto(n, ct);
    }

    /** Sửa: sinh lại các lần chưa tới giờ với số lượng còn lại. */
    public BenhAnDto.NhacThuoc sua(long id, BenhAnDto.TaoNhacThuoc r) {
        NhacThuoc n = cuaToi(id);
        ChiTietDonThuoc ct = chiTietDonRepository.findById(n.getChiTietDonThuocId()).orElseThrow();
        LocalDateTime bayGio = traCuu.bayGio();
        List<ChiTietNhacThuoc> cu = chiTietNhacRepository.findByNhacThuocIdAndDaXoaFalseOrderByThoiDiemNhacAsc(id);
        BigDecimal daQua = cu.stream().filter(c -> !c.getThoiDiemNhac().isAfter(bayGio))
                .map(ChiTietNhacThuoc::getSoLuong).reduce(BigDecimal.ZERO, BigDecimal::add);
        cu.stream().filter(c -> c.getThoiDiemNhac().isAfter(bayGio)).forEach(c -> c.setDaXoa(true));
        int thuTu = (int) cu.stream().filter(c -> !c.getThoiDiemNhac().isAfter(bayGio)).count() + 1;
        n.setTongSoLuong(r.tongSoLuong());
        BigDecimal conLai = r.tongSoLuong().subtract(daQua).max(BigDecimal.ZERO);
        LocalDateTime tu = r.ngayBatDau().atStartOfDay().isAfter(bayGio) ? r.ngayBatDau().atStartOfDay() : bayGio;
        String hoTen = cu.isEmpty() ? benhNhanKham.hoSoCuaToi(n.getHoSoId()).hoTen() : cu.getFirst().getHoTen();
        sinhChiTiet(n, ct, hoTen, tu, conLai, r.cacLan(), thuTu);
        return dto(n, ct);
    }

    public void xoa(long id) {
        xoaNhac(cuaToi(id));
    }

    @Transactional(readOnly = true)
    public List<BenhAnDto.NhacThuoc> cuaHoSo(long hoSoId) {
        benhNhanKham.hoSoCuaToi(hoSoId);
        return nhacRepository.findByHoSoIdAndDaXoaFalseOrderByIdDesc(hoSoId).stream()
                .map(n -> dto(n, chiTietDonRepository.findById(n.getChiTietDonThuocId()).orElseThrow())).toList();
    }

    /** Các lần nhắc trong một ngày của tài khoản (xem theo ngày, lịch sử). */
    @Transactional(readOnly = true)
    public List<BenhAnDto.LanNhac> theoNgay(LocalDate ngay) {
        return chiTietNhacRepository.findByTaiKhoanIdAndDaXoaFalseAndThoiDiemNhacBetweenOrderByThoiDiemNhacAsc(
                currentUser.taiKhoanId(), ngay.atStartOfDay(), ngay.atTime(LocalTime.MAX)).stream().map(NhacThuocService::dto).toList();
    }

    /** Lần nhắc đã tới giờ mà chưa uống/bỏ qua (app hiện khi mở — không push). */
    @Transactional(readOnly = true)
    public List<BenhAnDto.LanNhac> denGio() {
        LocalDateTime bayGio = traCuu.bayGio();
        return chiTietNhacRepository.findByTaiKhoanIdAndDaXoaFalseAndThoiDiemNhacBetweenOrderByThoiDiemNhacAsc(
                        currentUser.taiKhoanId(), bayGio.minusDays(1), bayGio).stream()
                .filter(c -> !c.isDaUong() && !c.isBoQua()).map(NhacThuocService::dto).toList();
    }

    public BenhAnDto.LanNhac xacNhan(long chiTietId, BenhAnDto.HanhDongNhac hanhDong) {
        ChiTietNhacThuoc c = chiTietNhacRepository.findById(chiTietId).filter(x -> !x.isDaXoa()
                && x.getTaiKhoanId() == currentUser.taiKhoanId()).orElseThrow(() -> Loi.khongTimThay("lần nhắc").ngoaiLe());
        switch (hanhDong) {
            case DA_NHAN -> c.setDaNhan(true);
            case BO_QUA -> c.setBoQua(true);
            case DA_UONG -> {
                if (!c.isDaUong()) {
                    c.setDaUong(true);
                    NhacThuoc n = nhacRepository.findById(c.getNhacThuocId()).orElseThrow();
                    n.setSoLuongConLai(n.getSoLuongConLai().subtract(c.getSoLuong()).max(BigDecimal.ZERO));
                }
            }
        }
        return dto(c);
    }

    /** Sinh lần lượt từng ngày theo danh sách giờ tới khi hết số lượng; ngày kết thúc = cuối ngày cuối (FR-HT-08). */
    private void sinhChiTiet(NhacThuoc n, ChiTietDonThuoc ct, String hoTen, LocalDateTime tu, BigDecimal soLuong,
                             List<BenhAnDto.LanUong> cacLan, int thuTuDau) {
        DanhMucContracts.ThuocInfo thuoc = danhMuc.layCacThuoc(List.of(ct.getThuocId())).get(ct.getThuocId());
        List<BenhAnDto.LanUong> lan = cacLan.stream().sorted(Comparator.comparing(BenhAnDto.LanUong::gio)).toList();
        BigDecimal conLai = soLuong;
        LocalDate ngay = tu.toLocalDate();
        LocalDateTime cuoi = n.getNgayBatDau().atTime(LocalTime.MAX.withNano(0));
        int thuTu = thuTuDau;
        for (int vong = 0; conLai.signum() > 0 && vong < 366; vong++, ngay = ngay.plusDays(1)) {
            for (BenhAnDto.LanUong l : lan) {
                LocalDateTime t = ngay.atTime(LocalTime.parse(l.gio()));
                if (conLai.signum() <= 0 || t.isBefore(tu)) {
                    continue;
                }
                BigDecimal sl = l.soLuong().min(conLai);
                ChiTietNhacThuoc c = new ChiTietNhacThuoc();
                c.setMaXacNhan(UUID.randomUUID().toString());
                c.setNhacThuocId(n.getId());
                c.setThoiDiemNhac(t);
                c.setThuTuNhac(thuTu++);
                c.setTenThuoc(thuoc == null ? "" : thuoc.tenThuoc());
                c.setDonVi(thuoc == null ? "" : thuoc.donVi());
                c.setHoTen(hoTen);
                c.setSoLuong(sl);
                c.setTaiKhoanId(currentUser.taiKhoanId());
                chiTietNhacRepository.save(c);
                conLai = conLai.subtract(sl);
                cuoi = ngay.atTime(LocalTime.MAX.withNano(0));
            }
        }
        n.setNgayKetThuc(cuoi);
    }

    private void xoaNhac(NhacThuoc n) {
        n.setDaXoa(true);
        LocalDateTime bayGio = traCuu.bayGio();
        chiTietNhacRepository.findByNhacThuocIdAndDaXoaFalseOrderByThoiDiemNhacAsc(n.getId()).stream()
                .filter(c -> c.getThoiDiemNhac().isAfter(bayGio)).forEach(c -> c.setDaXoa(true));
    }

    private NhacThuoc cuaToi(long id) {
        NhacThuoc n = nhacRepository.findById(id).filter(x -> !x.isDaXoa()).orElseThrow(() -> Loi.khongTimThay("nhắc thuốc").ngoaiLe());
        benhNhanKham.hoSoCuaToi(n.getHoSoId());
        return n;
    }

    private BenhAnDto.NhacThuoc dto(NhacThuoc n, ChiTietDonThuoc ct) {
        DanhMucContracts.ThuocInfo t = danhMuc.layCacThuoc(List.of(ct.getThuocId())).get(ct.getThuocId());
        return new BenhAnDto.NhacThuoc(n.getId(), n.getChiTietDonThuocId(), n.getHoSoId(), t == null ? "" : t.tenThuoc(),
                n.getNgayBatDau(), n.getNgayKetThuc(), n.getTongSoLuong(), n.getSoLuongConLai());
    }

    static BenhAnDto.LanNhac dto(ChiTietNhacThuoc c) {
        return new BenhAnDto.LanNhac(c.getId(), c.getNhacThuocId(), c.getMaXacNhan(), c.getThoiDiemNhac(), c.getThuTuNhac(),
                c.getTenThuoc(), c.getDonVi(), c.getHoTen(), c.getSoLuong(), c.isDaNhan(), c.isDaUong(), c.isBoQua());
    }
}
