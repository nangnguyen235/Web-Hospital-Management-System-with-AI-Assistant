package vn.nhom6.his.lichkham.services;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom6.his.contracts.danhmuc.DanhMucContracts;
import vn.nhom6.his.contracts.danhmuc.PhongInfo;
import vn.nhom6.his.contracts.lichkham.LichKhamContracts;
import vn.nhom6.his.lichkham.entity.KhungGio;
import vn.nhom6.his.lichkham.entity.LichKhamBacSi;
import vn.nhom6.his.lichkham.entity.PhieuDatKham;
import vn.nhom6.his.lichkham.repository.KhungGioRepository;
import vn.nhom6.his.lichkham.repository.LichKhamBacSiRepository;
import vn.nhom6.his.lichkham.repository.PhieuDatKhamRepository;
import vn.nhom6.his.shared.kernel.Loi;

/** Cài đặt {@link LichKhamContracts} — cửa duy nhất để module khác hỏi/ra lệnh LichKham. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LichKhamContractsService implements LichKhamContracts {

    private final PhieuDatKhamRepository phieuRepository;
    private final LichKhamBacSiRepository lichRepository;
    private final KhungGioRepository khungRepository;
    private final DanhMucContracts danhMuc;
    private final Clock clock;

    @Override
    public long demLuotKham() {
        return phieuRepository.countByDaXoaFalse();
    }

    @Override
    public boolean bacSiConLichSapToi(long bacSiId) {
        return lichRepository.existsByBacSiIdAndDaXoaFalseAndNgayGreaterThanEqual(bacSiId, LocalDate.now(clock));
    }

    @Override
    public Optional<LichInfo> layLich(long lichKhamId) {
        return lichRepository.findById(lichKhamId).map(this::info);
    }

    @Override
    public Map<Long, LichInfo> layCacLich(Collection<Long> ids) {
        return lichRepository.findAllById(ids).stream().map(this::info).collect(Collectors.toMap(LichInfo::id, Function.identity()));
    }

    @Override
    public List<LichInfo> lichCuaBacSi(long bacSiId, LocalDate ngay) {
        return lichRepository.findByBacSiIdAndNgayAndDaXoaFalseOrderByBuoiAsc(bacSiId, ngay).stream().map(this::info).toList();
    }

    @Override
    public PhieuInfo kiemTraPhieuTiepNhan(String maPhieu, int phongId) {
        PhieuDatKham p = phieuRepository.findByMaPhieu(maPhieu == null ? "" : maPhieu.trim().toUpperCase())
                .orElseThrow(() -> Loi.khongTimThay("phiếu khám có mã này").ngoaiLe());
        if (p.isDaXoa()) {
            throw new Loi("PHIEU_CHUA_THANH_TOAN", "Phiếu chưa thanh toán.").ngoaiLe();
        }
        if (p.isDaHuy()) {
            throw new Loi("PHIEU_DA_HUY", "Phiếu đã bị hủy (vắng mặt hoặc hết hạn).").ngoaiLe();
        }
        KhungGio k = khungRepository.findById(p.getKhungGioId()).orElseThrow();
        LichKhamBacSi l = lichRepository.findById(k.getLichKhamId()).orElseThrow();
        if (l.getPhongId() != phongId) {
            String ten = danhMuc.layPhong(l.getPhongId()).map(PhongInfo::tenPhong).orElse("khác");
            throw new Loi("SAI_PHONG", "Phiếu thuộc phòng " + ten + ".").ngoaiLe();
        }
        if (!k.getNgay().equals(LocalDate.now(clock))) {
            throw new Loi("KHONG_PHAI_HOM_NAY", "Phiếu khám ngày " + k.getNgay() + ", không phải hôm nay.").ngoaiLe();
        }
        return info(p, l.getId());
    }

    @Override
    public Optional<PhieuInfo> layPhieu(long phieuId) {
        return phieuRepository.findById(phieuId)
                .map(p -> info(p, khungRepository.findById(p.getKhungGioId()).map(KhungGio::getLichKhamId).orElse(0L)));
    }

    @Override
    public Map<Long, PhieuInfo> layCacPhieu(Collection<Long> ids) {
        List<PhieuDatKham> ds = phieuRepository.findAllById(ids);
        Map<Long, Long> lichCuaKhung = khungRepository.findAllById(ds.stream().map(PhieuDatKham::getKhungGioId).toList())
                .stream().collect(Collectors.toMap(KhungGio::getId, KhungGio::getLichKhamId));
        return ds.stream().map(p -> info(p, lichCuaKhung.getOrDefault(p.getKhungGioId(), 0L)))
                .collect(Collectors.toMap(PhieuInfo::id, Function.identity()));
    }

    @Override
    public ThongKe thongKe(LocalDate tu, LocalDate den) {
        List<PhieuDatKham> ds = phieuRepository.daThanhToanTrongKhoang(tu, den);
        Map<Long, KhungGio> khung = khungRepository.findAllById(ds.stream().map(PhieuDatKham::getKhungGioId).distinct().toList())
                .stream().collect(Collectors.toMap(KhungGio::getId, Function.identity()));
        Map<Long, LichKhamBacSi> lich = lichRepository.findAllById(khung.values().stream().map(KhungGio::getLichKhamId).distinct().toList())
                .stream().collect(Collectors.toMap(LichKhamBacSi::getId, Function.identity()));
        Map<Integer, Integer> khoaCuaDichVu = new HashMap<>();
        Map<Integer, Long> luot = new HashMap<>();
        Map<Integer, BigDecimal> doanhThu = new HashMap<>();
        Map<LocalDate, Long> theoNgay = new TreeMap<>();
        for (PhieuDatKham p : ds) {
            KhungGio k = khung.get(p.getKhungGioId());
            LichKhamBacSi l = lich.get(k.getLichKhamId());
            int khoa = khoaCuaDichVu.computeIfAbsent(l.getDichVuId(), id -> danhMuc.layDichVu(id).map(d -> d.khoaId()).orElse(0));
            luot.merge(khoa, 1L, Long::sum);
            doanhThu.merge(khoa, p.getGiaDichVu(), BigDecimal::add);
            theoNgay.merge(k.getNgay(), 1L, Long::sum);
        }
        return new ThongKe(ds.size(), luot, doanhThu, theoNgay);
    }

    private PhieuInfo info(PhieuDatKham p, long lichKhamId) {
        return new PhieuInfo(p.getId(), p.getMaPhieu(), p.getHoSoId(), lichKhamId, p.getSoThuTu(), p.getBenhAnId(),
                p.isDaDanhGia(), !p.isDaXoa());
    }

    private LichInfo info(LichKhamBacSi l) {
        int khoa = danhMuc.layDichVu(l.getDichVuId()).map(d -> d.khoaId()).orElse(0);
        return new LichInfo(l.getId(), l.getBacSiId(), l.getPhongId(), l.getDichVuId(), khoa, l.getNgay(), l.getBuoi().name(),
                l.getSoDangGoi());
    }
}
