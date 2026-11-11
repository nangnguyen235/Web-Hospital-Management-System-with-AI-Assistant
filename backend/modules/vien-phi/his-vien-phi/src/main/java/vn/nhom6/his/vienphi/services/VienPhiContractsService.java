package vn.nhom6.his.vienphi.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom6.his.contracts.vienphi.VienPhiContracts;
import vn.nhom6.his.shared.kernel.Loi;
import vn.nhom6.his.vienphi.constant.TrangThaiGiaoDich;
import vn.nhom6.his.vienphi.entity.GiaoDich;
import vn.nhom6.his.vienphi.repository.GiaoDichRepository;

/** Cài đặt {@link VienPhiContracts}: LichKham (đặt khám, quầy) và HoSoBenhAn (xét nghiệm) ra lệnh tạo giao dịch. */
@Service
@RequiredArgsConstructor
@Transactional
public class VienPhiContractsService implements VienPhiContracts {

    private final GiaoDichRepository giaoDichRepository;
    private final VnpayService vnpay;
    private final GiaoDichService giaoDichService;

    @Override
    public GiaoDichMoi taoGiaoDich(TaoGiaoDich r) {
        GiaoDich g = new GiaoDich();
        g.setLoaiThanhToan(r.loai());
        g.setSoTien(r.soTien());
        g.setPhi(r.phi());
        g.setTaiKhoanId(r.taiKhoanId());
        g.setNguon(r.nguon());
        giaoDichRepository.save(g);
        return new GiaoDichMoi(g.getId(), vnpay.taoUrl(g, r.ipKhachHang()), g.getNgayTao().plusMinutes(VnpayService.HAN_PHUT));
    }

    @Override
    public String urlThanhToan(long giaoDichId, String ipKhachHang) {
        GiaoDich g = giaoDichRepository.findById(giaoDichId).orElseThrow(() -> Loi.khongTimThay("giao dịch").ngoaiLe());
        if (g.getTrangThai() != TrangThaiGiaoDich.DANG_XU_LY) {
            throw new Loi("GIAO_DICH_DA_XU_LY", "Giao dịch đã được xử lý.").ngoaiLe();
        }
        return vnpay.taoUrl(g, ipKhachHang);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GiaoDichInfo> layGiaoDich(long id) {
        return giaoDichRepository.findById(id).map(g -> new GiaoDichInfo(g.getId(), g.getLoaiThanhToan(), g.getTrangThai().name(),
                g.getSoTien(), g.getPhi(), g.getTaiKhoanId(), g.getNgayTao()));
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal doanhThu(LocalDate tu, LocalDate den) {
        return giaoDichService.doanhThu(tu, den);
    }
}
