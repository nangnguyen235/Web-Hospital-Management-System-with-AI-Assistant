package vn.nhom6.his.lichkham.services;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.nhom6.his.contracts.danhmuc.DanhMucContracts;
import vn.nhom6.his.contracts.danhmuc.DichVuInfo;
import vn.nhom6.his.contracts.danhmuc.PhongInfo;
import vn.nhom6.his.contracts.nguoidung.NguoiDungContracts;
import vn.nhom6.his.lichkham.constant.Buoi;
import vn.nhom6.his.lichkham.dto.LichKhamDto;
import vn.nhom6.his.lichkham.dto.LichKhamDto.TrangThaiPhieu;
import vn.nhom6.his.lichkham.entity.KhungGio;
import vn.nhom6.his.lichkham.entity.LichKhamBacSi;
import vn.nhom6.his.lichkham.entity.PhieuDatKham;
import vn.nhom6.his.lichkham.repository.KhungGioRepository;
import vn.nhom6.his.lichkham.repository.LichKhamBacSiRepository;
import vn.nhom6.his.lichkham.repository.PhieuDatKhamRepository;

/** Đổi entity → DTO kèm tên phòng/khoa/dịch vụ/bác sĩ (hỏi qua contracts), giờ khung, trạng thái phiếu. */
@Component
@RequiredArgsConstructor
class LichKhamTraCuu {

    static final int GIU_CHO_PHUT = 15; // BR-10

    private final Clock clock;
    private final DanhMucContracts danhMuc;
    private final NguoiDungContracts nguoiDung;
    private final KhungGioRepository khungRepository;
    private final LichKhamBacSiRepository lichRepository;
    private final PhieuDatKhamRepository phieuRepository;

    LocalDateTime bayGio() {
        return LocalDateTime.now(clock);
    }

    LocalDate homNay() {
        return LocalDate.now(clock);
    }

    /** Mốc tính phiếu giữ chỗ còn hạn: tạo sau thời điểm này. */
    LocalDateTime mocGiuCho() {
        return bayGio().minusMinutes(GIU_CHO_PHUT);
    }

    static LocalTime gioBatDau(int chiSoKhung) {
        return chiSoKhung < 4 ? LocalTime.of(7 + chiSoKhung, 0) : LocalTime.of(13 + chiSoKhung - 4, 0);
    }

    static LocalTime ketThucBuoi(Buoi buoi) {
        return buoi == Buoi.SANG ? LocalTime.of(11, 0) : LocalTime.of(17, 0);
    }

    /** 1 = Chủ nhật … 7 = Thứ 7 (như TheDuck). */
    static int thu(LocalDate ngay) {
        return ngay.getDayOfWeek().getValue() % 7 + 1;
    }

    static int chiSoKhungCuoi(Buoi buoi) {
        return buoi == Buoi.SANG ? 3 : 7;
    }

    boolean khungDaBatDau(KhungGio k) {
        return !k.getNgay().atTime(gioBatDau(k.getChiSoKhung())).isAfter(bayGio());
    }

    boolean khungDaKetThuc(KhungGio k) {
        return !k.getNgay().atTime(gioBatDau(k.getChiSoKhung()).plusHours(1)).isAfter(bayGio());
    }

    /** Chỗ còn = tối đa − đã đặt − đang giữ chỗ còn hạn (BR-10). */
    int conCho(KhungGio k) {
        return k.getSoLuongToiDa() - k.getSoLuongDaDat() - (int) phieuRepository.demDangGiuCho(k.getId(), mocGiuCho());
    }

    LichKhamDto.Khung khung(KhungGio k) {
        LocalTime bd = gioBatDau(k.getChiSoKhung());
        return new LichKhamDto.Khung(k.getId(), k.getChiSoKhung(), bd.toString(), bd.plusHours(1).toString(),
                k.getSoLuongToiDa(), k.getSoLuongDaDat(), Math.max(0, conCho(k)), khungDaBatDau(k));
    }

    List<LichKhamDto.Lich> lich(Collection<LichKhamBacSi> ds) {
        if (ds.isEmpty()) {
            return List.of();
        }
        Map<Long, String> tenBacSi = nguoiDung.tenNhanVien(ds.stream().map(LichKhamBacSi::getBacSiId).distinct().toList());
        Map<Long, List<KhungGio>> khung = khungRepository
                .findByLichKhamIdInOrderByChiSoKhungAsc(ds.stream().map(LichKhamBacSi::getId).toList()).stream()
                .collect(Collectors.groupingBy(KhungGio::getLichKhamId));
        Map<Integer, PhongInfo> phong = new HashMap<>();
        Map<Integer, DichVuInfo> dichVu = new HashMap<>();
        // Sắp ngày → buổi (SANG trước CHIEU theo thứ tự enum, không theo chữ cái) → phòng
        return ds.stream().sorted(java.util.Comparator.comparing(LichKhamBacSi::getNgay)
                .thenComparing(LichKhamBacSi::getBuoi).thenComparing(LichKhamBacSi::getPhongId)).map(l -> {
            PhongInfo p = phong.computeIfAbsent(l.getPhongId(), id -> danhMuc.layPhong(id).orElse(null));
            DichVuInfo d = dichVu.computeIfAbsent(l.getDichVuId(), id -> danhMuc.layDichVu(id).orElse(null));
            List<KhungGio> ks = khung.getOrDefault(l.getId(), List.of());
            return new LichKhamDto.Lich(l.getId(), l.getBacSiId(), tenBacSi.getOrDefault(l.getBacSiId(), ""), l.getPhongId(),
                    p == null ? "" : p.tenPhong(), l.getDichVuId(), d == null ? "" : d.tenDichVu(), d == null ? null : d.gia(),
                    l.getNgay(), l.getBuoi(), l.getThuTrongTuan(), l.getSoLuot(), l.getSoLuot() / 4, l.getSoDangGoi(),
                    ks.stream().mapToInt(KhungGio::getSoLuongDaDat).sum(), ks.stream().map(this::khung).toList());
        }).toList();
    }

    TrangThaiPhieu trangThai(PhieuDatKham p, KhungGio k) {
        if (p.isDaXoa()) {
            return p.isDaHuy() || !p.getNgayTao().isAfter(mocGiuCho()) ? TrangThaiPhieu.HET_HAN : TrangThaiPhieu.CHO_THANH_TOAN;
        }
        if (p.getBenhAnId() != null) {
            return TrangThaiPhieu.DA_TIEP_NHAN;
        }
        return p.isDaHuy() || k.getNgay().isBefore(homNay()) ? TrangThaiPhieu.VANG_MAT : TrangThaiPhieu.DA_XAC_NHAN;
    }

    /** Ghi lười (FR-HT-04, 05): phiếu giữ chỗ quá hạn và phiếu vắng mặt → {@code da_huy = true}. */
    void ghiLuoi(PhieuDatKham p, TrangThaiPhieu t) {
        if (!p.isDaHuy() && (t == TrangThaiPhieu.HET_HAN || t == TrangThaiPhieu.VANG_MAT)) {
            p.setDaHuy(true);
        }
    }

    List<LichKhamDto.Phieu> phieu(Collection<PhieuDatKham> ds, boolean ghiLuoi) {
        if (ds.isEmpty()) {
            return List.of();
        }
        Map<Long, KhungGio> khung = khungRepository.findAllById(ds.stream().map(PhieuDatKham::getKhungGioId).distinct().toList())
                .stream().collect(Collectors.toMap(KhungGio::getId, Function.identity()));
        Map<Long, LichKhamBacSi> lich = lichRepository.findAllById(khung.values().stream().map(KhungGio::getLichKhamId).distinct().toList())
                .stream().collect(Collectors.toMap(LichKhamBacSi::getId, Function.identity()));
        Map<Long, String> tenBacSi = nguoiDung.tenNhanVien(lich.values().stream().map(LichKhamBacSi::getBacSiId).distinct().toList());
        Map<Long, NguoiDungContracts.HoSoInfo> hoSo = nguoiDung.layCacHoSo(ds.stream().map(PhieuDatKham::getHoSoId).distinct().toList());
        Map<Integer, PhongInfo> phong = new HashMap<>();
        Map<Integer, DichVuInfo> dichVu = new HashMap<>();
        return ds.stream().map(p -> {
            KhungGio k = khung.get(p.getKhungGioId());
            LichKhamBacSi l = lich.get(k.getLichKhamId());
            PhongInfo ph = phong.computeIfAbsent(l.getPhongId(), id -> danhMuc.layPhong(id).orElse(null));
            DichVuInfo d = dichVu.computeIfAbsent(l.getDichVuId(), id -> danhMuc.layDichVu(id).orElse(null));
            TrangThaiPhieu t = trangThai(p, k);
            if (ghiLuoi) {
                ghiLuoi(p, t);
            }
            LocalTime bd = gioBatDau(k.getChiSoKhung());
            NguoiDungContracts.HoSoInfo hs = hoSo.get(p.getHoSoId());
            return new LichKhamDto.Phieu(p.getId(), p.getMaPhieu(), p.getSoThuTu(), t, p.getHoSoId(), hs == null ? "" : hs.hoTen(),
                    l.getId(), k.getNgay(), l.getBuoi(), bd.toString(), bd.plusHours(1).toString(), l.getPhongId(),
                    ph == null ? "" : ph.tenPhong(), ph == null ? "" : ph.tenKhoa(), tenBacSi.getOrDefault(l.getBacSiId(), ""),
                    d == null ? "" : d.tenDichVu(), p.getGiaDichVu(), p.getKenhDat(), p.getGiaoDichId(), p.getBenhAnId(),
                    p.isDaDanhGia(), l.getSoDangGoi(), p.getNgayTao());
        }).toList();
    }
}
