package vn.nhom6.his.hosobenhan.services;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom6.his.contracts.danhmuc.DanhMucContracts;
import vn.nhom6.his.contracts.danhmuc.DichVuInfo;
import vn.nhom6.his.contracts.danhmuc.LoaiDichVu;
import vn.nhom6.his.contracts.lichkham.LichKhamContracts;
import vn.nhom6.his.contracts.lichkham.LichKhamContracts.LichInfo;
import vn.nhom6.his.hosobenhan.constant.TrangThaiBenhAn;
import vn.nhom6.his.hosobenhan.dto.BenhAnDto;
import vn.nhom6.his.hosobenhan.entity.BenhAn;
import vn.nhom6.his.hosobenhan.entity.ChiTietDonThuoc;
import vn.nhom6.his.hosobenhan.entity.DonThuoc;
import vn.nhom6.his.hosobenhan.entity.XetNghiem;
import vn.nhom6.his.hosobenhan.repository.BenhAnRepository;
import vn.nhom6.his.hosobenhan.repository.ChiTietDonThuocRepository;
import vn.nhom6.his.hosobenhan.repository.DonThuocRepository;
import vn.nhom6.his.hosobenhan.repository.XetNghiemRepository;
import vn.nhom6.his.shared.kernel.CurrentUser;
import vn.nhom6.his.shared.kernel.Loi;
import vn.nhom6.his.shared.kernel.MaNgauNhien;

/**
 * Bác sĩ khám (BR-16…19): danh sách bệnh án của lịch, nhận khám (kể cả nhận thay cùng khoa), ghi bệnh án, hoàn tất
 * (không khóa — như TheDuck), chỉ định/xóa xét nghiệm (BR-17), kê/xóa thuốc (BR-18, BR-26 cảnh báo trùng), lịch sử trong khoa.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class KhamService {

    private final BenhAnRepository benhAnRepository;
    private final XetNghiemRepository xetNghiemRepository;
    private final DonThuocRepository donThuocRepository;
    private final ChiTietDonThuocRepository chiTietRepository;
    private final LichKhamContracts lichKham;
    private final DanhMucContracts danhMuc;
    private final BenhAnTraCuu traCuu;
    private final CurrentUser currentUser;

    /** FR-BS-03: bệnh án của lịch (của mình, hoặc lịch hôm nay/hôm qua cùng khoa để nhận thay), sắp theo số thứ tự. */
    @Transactional(readOnly = true)
    public List<BenhAnDto.BenhAnGon> cuaLich(long lichKhamId, TrangThaiBenhAn trangThai) {
        LichInfo l = lichKham.layLich(lichKhamId).orElseThrow(() -> Loi.khongTimThay("lịch khám").ngoaiLe());
        boolean cuaToi = l.bacSiId() == bacSi();
        boolean cungKhoa = currentUser.khoaId() != null && currentUser.khoaId() == l.khoaId()
                && !l.ngay().isBefore(traCuu.homNay().minusDays(1));
        if (!cuaToi && !cungKhoa) {
            throw Loi.khongCoQuyen("Bạn không xem được bệnh án của lịch này.").ngoaiLe();
        }
        return traCuu.gon(benhAnRepository.findByLichKhamIdAndDaXoaFalse(lichKhamId).stream()
                .filter(b -> trangThai == null || b.getTrangThai() == trangThai).toList());
    }

    @Transactional(readOnly = true)
    public BenhAnDto.ChiTiet chiTiet(long id) {
        BenhAn b = tim(id);
        LichInfo l = lichKham.layLich(b.getLichKhamId()).orElseThrow();
        if (l.bacSiId() != bacSi() && (currentUser.khoaId() == null || currentUser.khoaId() != l.khoaId())) {
            throw Loi.khongCoQuyen("Bệnh án không thuộc khoa của bạn.").ngoaiLe();
        }
        return traCuu.chiTiet(b);
    }

    /** BR-16: CHO_KHAM → DANG_KHAM; lịch hôm nay/hôm qua cùng khoa của bác sĩ khác → chuyển sang lịch hôm nay của người nhận. */
    public BenhAnDto.ChiTiet nhanKham(long id) {
        BenhAn b = tim(id);
        if (b.getTrangThai() != TrangThaiBenhAn.CHO_KHAM) {
            throw new Loi("KHONG_CHO_KHAM", "Bệnh án không ở trạng thái chờ khám.").ngoaiLe();
        }
        LichInfo l = lichKham.layLich(b.getLichKhamId()).orElseThrow();
        if (l.bacSiId() != bacSi()) {
            if (currentUser.khoaId() == null || currentUser.khoaId() != l.khoaId()
                    || l.ngay().isBefore(traCuu.homNay().minusDays(1))) {
                throw Loi.khongCoQuyen("Chỉ nhận khám thay bệnh án hôm nay/hôm qua của bác sĩ cùng khoa.").ngoaiLe();
            }
            LichInfo cuaToi = lichKham.lichCuaBacSi(bacSi(), traCuu.homNay()).stream().findFirst()
                    .orElseThrow(() -> new Loi("KHONG_CO_LICH", "Bạn không có lịch khám hôm nay để nhận khám thay.").ngoaiLe());
            b.setLichKhamTruocId(b.getLichKhamId());
            b.setLichKhamId(cuaToi.id());
        }
        b.setTrangThai(TrangThaiBenhAn.DANG_KHAM);
        return traCuu.chiTiet(b);
    }

    /** BR-16: chỉ bác sĩ của lịch hiện tại; ngày tái khám không trước hôm nay; ghi được cả sau khi hoàn tất. */
    public BenhAnDto.ChiTiet ghi(long id, BenhAnDto.GhiBenhAn r) {
        BenhAn b = cuaToi(id);
        if (r.ngayTaiKham() != null && r.ngayTaiKham().isBefore(traCuu.homNay())) {
            throw new Loi("NGAY_TAI_KHAM", "Ngày tái khám không được trước hôm nay.").ngoaiLe();
        }
        b.setTrieuChung(r.trieuChung());
        b.setChanDoan(r.chanDoan());
        b.setNgayTaiKham(r.ngayTaiKham());
        return traCuu.chiTiet(b);
    }

    /** BR-19: hoàn tất → DA_KHAM, không bắt buộc chẩn đoán, không khóa. */
    public BenhAnDto.ChiTiet hoanTat(long id) {
        BenhAn b = cuaToi(id);
        if (b.getTrangThai() == TrangThaiBenhAn.CHO_KHAM) {
            throw new Loi("CHUA_NHAN_KHAM", "Hãy nhận khám trước khi hoàn tất.").ngoaiLe();
        }
        b.setTrangThai(TrangThaiBenhAn.DA_KHAM);
        return traCuu.chiTiet(b);
    }

    /** FR-BS-09: lịch sử của bệnh nhân trong cùng khoa, trước lần khám này. */
    @Transactional(readOnly = true)
    public List<BenhAnDto.LichSu> lichSu(long id) {
        BenhAn b = tim(id);
        LichInfo l = lichKham.layLich(b.getLichKhamId()).orElseThrow();
        List<BenhAn> truoc = benhAnRepository.findByBenhNhanIdAndDaXoaFalseOrderByIdDesc(b.getBenhNhanId()).stream()
                .filter(x -> !x.getId().equals(b.getId()) && x.getNgayTao().isBefore(b.getNgayTao())).toList();
        var lich = lichKham.layCacLich(truoc.stream().map(BenhAn::getLichKhamId).distinct().toList());
        return traCuu.lichSu(truoc.stream().filter(x -> lich.containsKey(x.getLichKhamId())
                && lich.get(x.getLichKhamId()).khoaId() == l.khoaId()).toList());
    }

    /** BR-17: dịch vụ XET_NGHIEM; DanhMuc chọn phòng ít hàng nhất và cấp số; chưa thanh toán. */
    public BenhAnDto.XetNghiem chiDinh(long benhAnId, BenhAnDto.ChiDinh r) {
        BenhAn b = cuaToi(benhAnId);
        DichVuInfo dv = danhMuc.layDichVu(r.dichVuId()).filter(d -> !d.daXoa() && d.loaiDichVu() == LoaiDichVu.XET_NGHIEM)
                .orElseThrow(() -> new Loi("DICH_VU_KHONG_HOP_LE", "Vui lòng chọn dịch vụ xét nghiệm đang hoạt động.").ngoaiLe());
        DanhMucContracts.SoXetNghiem so = danhMuc.capSoXetNghiem(dv.id());
        XetNghiem x = new XetNghiem();
        String ma;
        do {
            ma = "XN" + MaNgauNhien.hex(12);
        } while (xetNghiemRepository.existsByMaXetNghiem(ma));
        x.setMaXetNghiem(ma);
        x.setSoThuTu(so.soThuTu());
        x.setGhiChu(r.ghiChu());
        x.setGia(dv.gia());
        x.setDichVuId(dv.id());
        x.setBenhAnId(b.getId());
        x.setPhongId(so.phongId());
        x.setNgay(traCuu.homNay());
        xetNghiemRepository.save(x);
        return traCuu.xetNghiem(List.of(x), null).getFirst();
    }

    /** BR-17: bác sĩ xóa được khi chưa thanh toán. */
    public void xoaXetNghiem(long xetNghiemId) {
        XetNghiem x = xetNghiemRepository.findById(xetNghiemId).filter(v -> !v.isDaXoa())
                .orElseThrow(() -> Loi.khongTimThay("xét nghiệm").ngoaiLe());
        cuaToi(x.getBenhAnId());
        if (x.isDaThanhToan()) {
            throw new Loi("DA_THANH_TOAN", "Xét nghiệm đã thanh toán, không xóa được.").ngoaiLe();
        }
        x.setDaXoa(true);
    }

    /** BR-18: một đơn/bệnh án, tạo khi kê thuốc đầu tiên; giá chốt lúc kê; chỉ kê, không trừ kho. BR-26: cảnh báo trùng. */
    public BenhAnDto.KetQuaKeThuoc keThuoc(long benhAnId, BenhAnDto.KeThuoc r) {
        BenhAn b = cuaToi(benhAnId);
        DanhMucContracts.ThuocInfo t = danhMuc.layCacThuoc(List.of(r.thuocId())).get(r.thuocId());
        if (t == null || t.daXoa()) {
            throw new Loi("THUOC_KHONG_HOP_LE", "Thuốc không tồn tại hoặc đã ngừng dùng.").ngoaiLe();
        }
        DonThuoc don = donThuocRepository.findByBenhAnId(b.getId()).orElseGet(() -> {
            DonThuoc d = new DonThuoc();
            String ma;
            do {
                ma = MaNgauNhien.hex(12);
            } while (donThuocRepository.existsByMaDonThuoc(ma));
            d.setMaDonThuoc(ma);
            d.setBenhAnId(b.getId());
            return donThuocRepository.save(d);
        });
        List<ChiTietDonThuoc> cu = chiTietRepository.findByDonThuocIdAndDaXoaFalseOrderByIdAsc(don.getId());
        String canhBao = cu.stream().anyMatch(c -> c.getThuocId().equals(r.thuocId()))
                ? "Thuốc " + t.tenThuoc() + " đã có trong đơn — kiểm tra lại liều dùng." : null;
        ChiTietDonThuoc c = new ChiTietDonThuoc();
        c.setDonThuocId(don.getId());
        c.setMaDonThuoc(don.getMaDonThuoc());
        c.setThuocId(t.id());
        c.setSoLuong(r.soLuong());
        c.setGia(t.gia());
        c.setThanhTien(t.gia().multiply(BigDecimal.valueOf(r.soLuong())));
        c.setSoLanMoiNgay(r.soLanMoiNgay());
        c.setSoNgay(r.soNgay());
        c.setSoLuongMoiLan(r.soLuongMoiLan());
        c.setSang(r.sang());
        c.setTrua(r.trua());
        c.setChieu(r.chieu());
        c.setToi(r.toi());
        c.setHuongDan(r.huongDan());
        chiTietRepository.save(c);
        tinhTongTien(don);
        return new BenhAnDto.KetQuaKeThuoc(traCuu.donThuoc(don), canhBao);
    }

    public BenhAnDto.DonThuoc xoaThuoc(long chiTietId) {
        ChiTietDonThuoc c = chiTietRepository.findById(chiTietId).filter(v -> !v.isDaXoa())
                .orElseThrow(() -> Loi.khongTimThay("dòng thuốc").ngoaiLe());
        DonThuoc don = donThuocRepository.findById(c.getDonThuocId()).orElseThrow();
        cuaToi(don.getBenhAnId());
        c.setDaXoa(true);
        tinhTongTien(don);
        return traCuu.donThuoc(don);
    }

    /** Bất biến 10: tong_tien = tổng thanh_tien các dòng chưa xóa. */
    private void tinhTongTien(DonThuoc don) {
        chiTietRepository.flush();
        don.setTongTien(chiTietRepository.findByDonThuocIdAndDaXoaFalseOrderByIdAsc(don.getId()).stream()
                .map(ChiTietDonThuoc::getThanhTien).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    /** Bệnh án mà bác sĩ đăng nhập là bác sĩ của lịch hiện tại (BR-16). */
    private BenhAn cuaToi(long id) {
        BenhAn b = tim(id);
        LichInfo l = lichKham.layLich(b.getLichKhamId()).orElseThrow();
        if (l.bacSiId() != bacSi()) {
            throw Loi.khongCoQuyen("Chỉ bác sĩ của lịch khám hiện tại mới được ghi bệnh án.").ngoaiLe();
        }
        return b;
    }

    private BenhAn tim(long id) {
        return benhAnRepository.findById(id).filter(b -> !b.isDaXoa()).orElseThrow(() -> Loi.khongTimThay("bệnh án").ngoaiLe());
    }

    private long bacSi() {
        Long id = currentUser.nhanVienId();
        if (id == null) {
            throw Loi.khongCoQuyen("Chỉ bác sĩ mới thao tác bệnh án.").ngoaiLe();
        }
        return id;
    }
}
