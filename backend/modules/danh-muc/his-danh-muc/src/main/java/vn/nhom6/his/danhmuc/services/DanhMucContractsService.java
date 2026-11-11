package vn.nhom6.his.danhmuc.services;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import vn.nhom6.his.contracts.danhmuc.LoaiPhong;
import vn.nhom6.his.danhmuc.entity.Phong;
import vn.nhom6.his.shared.kernel.Loi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom6.his.contracts.danhmuc.DanhMucContracts;
import vn.nhom6.his.contracts.danhmuc.DichVuInfo;
import vn.nhom6.his.contracts.danhmuc.PhongInfo;
import vn.nhom6.his.danhmuc.entity.Khoa;
import vn.nhom6.his.danhmuc.repository.DanTocRepository;
import vn.nhom6.his.danhmuc.repository.DichVuRepository;
import vn.nhom6.his.danhmuc.repository.KhoaRepository;
import vn.nhom6.his.danhmuc.repository.PhongRepository;

/** Cài đặt {@link DanhMucContracts} — cửa duy nhất để module khác hỏi/ra lệnh DanhMuc. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DanhMucContractsService implements DanhMucContracts {

    private final PhongRepository phongRepository;
    private final KhoaRepository khoaRepository;
    private final DichVuRepository dichVuRepository;
    private final DanTocRepository danTocRepository;
    private final vn.nhom6.his.danhmuc.repository.ThuocRepository thuocRepository;
    private final Clock clock;

    @Override
    public Optional<PhongInfo> layPhong(int phongId) {
        return phongRepository.findById(phongId).map(p -> new PhongInfo(
                p.getId(), p.getTenPhong(), p.getKhoaId(),
                khoaRepository.findById(p.getKhoaId()).map(Khoa::getTenKhoa).orElse(""),
                p.getLoaiPhong(), p.getDichVuId(), p.isDaXoa()));
    }

    @Override
    public Optional<DichVuInfo> layDichVu(int dichVuId) {
        return dichVuRepository.findById(dichVuId).map(d -> new DichVuInfo(d.getId(), d.getTenDichVu(), d.getKhoaId(),
                d.getLoaiDichVu(), d.getGia(), d.isDaXoa()));
    }

    @Override
    public long demKhoa() {
        return khoaRepository.countByDaXoaFalse();
    }

    @Override
    public boolean khoaTonTai(int khoaId) {
        return khoaRepository.findById(khoaId).filter(k -> !k.isDaXoa()).isPresent();
    }

    @Override
    public Map<Integer, String> tenCacKhoa() {
        return TienIch.tenKhoa(khoaRepository);
    }

    @Override
    public boolean danTocTonTai(int danTocId) {
        return danTocRepository.existsById(danTocId);
    }

    @Override
    public java.util.List<Integer> phongCuaKhoa(int khoaId) {
        return phongRepository.findAll().stream().filter(p -> p.getKhoaId() == khoaId).map(Phong::getId).toList();
    }

    @Override
    @Transactional
    public SoXetNghiem capSoXetNghiem(int dichVuId) {
        LocalDate homNay = LocalDate.now(clock);
        java.util.List<Phong> ds = phongRepository.findByDichVuIdAndLoaiPhongAndDaXoaFalse(dichVuId, LoaiPhong.PHONG_XET_NGHIEM);
        if (ds.isEmpty()) {
            throw new Loi("KHONG_CO_PHONG_XN", "Chưa có phòng xét nghiệm cho dịch vụ này.").ngoaiLe();
        }
        ds.forEach(p -> datLaiNeuSangNgay(p, homNay));
        Phong p = ds.stream().min(java.util.Comparator.comparingInt(Phong::getSoXnLonNhat)).orElseThrow();
        Phong khoa = phongRepository.khoaGhi(p.getId()).orElseThrow();
        datLaiNeuSangNgay(khoa, homNay);
        khoa.setSoXnLonNhat(khoa.getSoXnLonNhat() + 1);
        return so(khoa, khoa.getSoXnLonNhat());
    }

    @Override
    @Transactional
    public SoXetNghiem goiTiepXetNghiem(int phongId) {
        Phong p = phongRepository.khoaGhi(phongId).orElseThrow(() -> Loi.khongTimThay("phòng").ngoaiLe());
        datLaiNeuSangNgay(p, LocalDate.now(clock));
        if (p.getSoXnDangGoi() >= p.getSoXnLonNhat()) {
            throw new Loi("HET_LUOT", "Đã gọi tới số cuối cùng của phòng.").ngoaiLe();
        }
        p.setSoXnDangGoi(Math.min(p.getSoXnDangGoi() + 5, p.getSoXnLonNhat()));
        return so(p, p.getSoXnDangGoi());
    }

    @Override
    @Transactional
    public SoXetNghiem soXetNghiem(int phongId) {
        Phong p = phongRepository.findById(phongId).orElseThrow(() -> Loi.khongTimThay("phòng").ngoaiLe());
        if (p.getNgayDemXn() == null || !p.getNgayDemXn().equals(LocalDate.now(clock))) {
            return new SoXetNghiem(p.getId(), p.getTenPhong(), 0, 0, 0);
        }
        return so(p, p.getSoXnDangGoi());
    }

    @Override
    public Map<Integer, ThuocInfo> layCacThuoc(java.util.Collection<Integer> ids) {
        return thuocRepository.findAllById(ids).stream().collect(java.util.stream.Collectors.toMap(
                t -> t.getId(), t -> new ThuocInfo(t.getId(), t.getTenThuoc(), t.getGia(), t.getDonVi().name(), t.isDaXoa())));
    }

    @Override
    public Map<Integer, DichVuInfo> layCacDichVu(java.util.Collection<Integer> ids) {
        return dichVuRepository.findAllById(ids).stream().collect(java.util.stream.Collectors.toMap(
                d -> d.getId(), d -> new DichVuInfo(d.getId(), d.getTenDichVu(), d.getKhoaId(), d.getLoaiDichVu(), d.getGia(),
                        d.isDaXoa())));
    }

    /** Đặt lại bộ đếm khi sang ngày — xử lý lười thay job 0h của TheDuck (FR-HT-07). */
    private static void datLaiNeuSangNgay(Phong p, LocalDate homNay) {
        if (p.getNgayDemXn() == null || !p.getNgayDemXn().equals(homNay)) {
            p.setNgayDemXn(homNay);
            p.setSoXnDangGoi(0);
            p.setSoXnLonNhat(0);
        }
    }

    private static SoXetNghiem so(Phong p, int soThuTu) {
        return new SoXetNghiem(p.getId(), p.getTenPhong(), soThuTu, p.getSoXnDangGoi(), p.getSoXnLonNhat());
    }
}
