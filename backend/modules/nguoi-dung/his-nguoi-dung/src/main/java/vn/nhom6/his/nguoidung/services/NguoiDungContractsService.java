package vn.nhom6.his.nguoidung.services;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom6.his.contracts.nguoidung.NguoiDungContracts;
import vn.nhom6.his.nguoidung.constant.LoaiNhanVien;
import vn.nhom6.his.nguoidung.entity.BenhNhan;
import vn.nhom6.his.nguoidung.entity.HoSoBenhNhan;
import vn.nhom6.his.nguoidung.entity.NhanVien;
import vn.nhom6.his.nguoidung.repository.BenhNhanRepository;
import vn.nhom6.his.nguoidung.repository.HoSoBenhNhanRepository;
import vn.nhom6.his.nguoidung.repository.NhanVienRepository;

/** Cài đặt {@link NguoiDungContracts} — cửa duy nhất để module khác hỏi/ra lệnh NguoiDung. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NguoiDungContractsService implements NguoiDungContracts {

    private final NhanVienRepository nhanVienRepository;
    private final HoSoBenhNhanRepository hoSoRepository;
    private final BenhNhanRepository benhNhanRepository;
    private final BenhNhanService benhNhanService;

    @Override
    public long demBacSi() {
        return nhanVienRepository.demBacSi();
    }

    @Override
    public long demBenhNhan() {
        return benhNhanRepository.countByDaXoaFalse();
    }

    @Override
    public Optional<NhanVienInfo> layNhanVien(long nhanVienId) {
        return nhanVienRepository.findById(nhanVienId).map(NguoiDungContractsService::info);
    }

    @Override
    public Map<Long, String> tenNhanVien(Collection<Long> ids) {
        return nhanVienRepository.findAllById(ids).stream().collect(Collectors.toMap(NhanVien::getId, NhanVien::getHoTen));
    }

    @Override
    public List<NhanVienInfo> bacSiCuaKhoa(int khoaId) {
        return nhanVienRepository.findByKhoaIdAndLoaiNhanVienAndDaXoaFalseOrderByHoTenAsc(khoaId, LoaiNhanVien.BAC_SI)
                .stream().map(NguoiDungContractsService::info).toList();
    }

    @Override
    public Optional<HoSoInfo> layHoSo(long hoSoId) {
        return hoSoRepository.findById(hoSoId).map(NguoiDungContractsService::info);
    }

    @Override
    public Map<Long, HoSoInfo> layCacHoSo(Collection<Long> ids) {
        return hoSoRepository.findAllById(ids).stream().map(NguoiDungContractsService::info)
                .collect(Collectors.toMap(HoSoInfo::id, Function.identity()));
    }

    @Override
    public Map<Long, String> maBenhNhan(Collection<Long> benhNhanIds) {
        return benhNhanRepository.findAllById(benhNhanIds).stream()
                .collect(Collectors.toMap(BenhNhan::getId, BenhNhan::getMaBenhNhan));
    }

    @Override
    @Transactional
    public long lienKetBenhNhan(long hoSoId, String soCccd) {
        return benhNhanService.lienKet(hoSoId, soCccd).getId();
    }

    static NhanVienInfo info(NhanVien n) {
        return new NhanVienInfo(n.getId(), n.getHoTen(), n.vaiTro().name(), n.getKhoaId(), n.isLaTruongKhoa(), n.isDaXoa());
    }

    static HoSoInfo info(HoSoBenhNhan h) {
        return new HoSoInfo(h.getId(), h.getTaiKhoanId(), h.getBenhNhanId(), h.getHoTen(), h.getNgaySinh(),
                h.getGioiTinh().name(), h.getSoCccd(), h.getSoDienThoai(), h.getEmail(), h.isDaXoa());
    }
}
