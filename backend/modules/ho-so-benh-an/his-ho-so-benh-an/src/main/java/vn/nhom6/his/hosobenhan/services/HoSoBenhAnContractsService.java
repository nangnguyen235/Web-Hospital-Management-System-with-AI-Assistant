package vn.nhom6.his.hosobenhan.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom6.his.contracts.danhmuc.DanhMucContracts;
import vn.nhom6.his.contracts.danhmuc.DichVuInfo;
import vn.nhom6.his.contracts.hosobenhan.HoSoBenhAnContracts;
import vn.nhom6.his.hosobenhan.entity.XetNghiem;
import vn.nhom6.his.hosobenhan.repository.BenhAnRepository;
import vn.nhom6.his.hosobenhan.repository.XetNghiemRepository;

/** Cài đặt {@link HoSoBenhAnContracts} — cửa duy nhất để module khác hỏi/ra lệnh HoSoBenhAn. */
@Service
@RequiredArgsConstructor
@Transactional
public class HoSoBenhAnContractsService implements HoSoBenhAnContracts {

    private final TiepNhanService tiepNhanService;
    private final BenhAnRepository benhAnRepository;
    private final XetNghiemRepository xetNghiemRepository;
    private final DanhMucContracts danhMuc;

    @Override
    public long taoBenhAnChoPhieu(long phieuDatKhamId, long lichKhamId, long benhNhanId, long hoSoId) {
        return tiepNhanService.taoBenhAn(phieuDatKhamId, lichKhamId, benhNhanId, hoSoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LichSuKham> lichSuCuaBenhNhan(long benhNhanId) {
        return benhAnRepository.findByBenhNhanIdAndDaXoaFalseOrderByIdDesc(benhNhanId).stream()
                .map(b -> new LichSuKham(b.getId(), b.getLichKhamId(), b.getTrangThai().name(), b.getChanDoan(),
                        b.getNgayTaiKham(), b.getNgayTao())).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Integer, BigDecimal> doanhThuXetNghiemTheoKhoa(LocalDate tu, LocalDate den) {
        List<XetNghiem> ds = xetNghiemRepository.findByDaThanhToanTrueAndNgayBetween(tu, den);
        Map<Integer, DichVuInfo> dv = danhMuc.layCacDichVu(ds.stream().map(XetNghiem::getDichVuId).distinct().toList());
        Map<Integer, BigDecimal> kq = new HashMap<>();
        ds.forEach(x -> kq.merge(dv.containsKey(x.getDichVuId()) ? dv.get(x.getDichVuId()).khoaId() : 0, x.getGia(), BigDecimal::add));
        return kq;
    }
}
