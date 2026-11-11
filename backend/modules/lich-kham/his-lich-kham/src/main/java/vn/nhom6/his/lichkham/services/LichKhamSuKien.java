package vn.nhom6.his.lichkham.services;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import vn.nhom6.his.contracts.hosobenhan.BenhAnDaTaoIntegrationEvent;
import vn.nhom6.his.contracts.hosobenhan.DanhGiaDaTaoIntegrationEvent;
import vn.nhom6.his.contracts.hosobenhan.HoSoBenhAnContracts;
import vn.nhom6.his.contracts.nguoidung.NguoiDungContracts;
import vn.nhom6.his.contracts.vienphi.GiaoDichDaXuLyIntegrationEvent;
import vn.nhom6.his.contracts.vienphi.LoaiThanhToan;
import vn.nhom6.his.lichkham.constant.KenhDat;
import vn.nhom6.his.lichkham.entity.KhungGio;
import vn.nhom6.his.lichkham.entity.LichKhamBacSi;
import vn.nhom6.his.lichkham.entity.PhieuDatKham;
import vn.nhom6.his.lichkham.repository.KhungGioRepository;
import vn.nhom6.his.lichkham.repository.LichKhamBacSiRepository;
import vn.nhom6.his.lichkham.repository.PhieuDatKhamRepository;

/**
 * LichKham nghe sự kiện (modular-monolith.md mục 3.1) — chạy sau commit, transaction mới; nhận trùng không đổi gì.
 * Handler lỗi thì Event Publication Registry phát lại khi khởi động lại.
 */
@Service
@RequiredArgsConstructor
public class LichKhamSuKien {

    private static final Logger LOG = LoggerFactory.getLogger(LichKhamSuKien.class);

    private final PhieuDatKhamRepository phieuRepository;
    private final KhungGioRepository khungRepository;
    private final LichKhamBacSiRepository lichRepository;
    private final HoSoBenhAnContracts hoSoBenhAn;
    private final NguoiDungContracts nguoiDung;

    /**
     * BR-10, BR-11: thành công → mỗi phiếu chưa xác nhận: {@code da_xoa = false}, {@code so_thu_tu = so_bat_dau + so_luong_da_dat},
     * {@code so_luong_da_dat + 1}; khung đã đầy (trả tiền tới muộn) → chuyển sang khung cuối buổi, vượt sức chứa.
     * Phiếu QUAY → lệnh HoSoBenhAn tạo bệnh án ngay. Thất bại → {@code da_huy = true} (nhả chỗ).
     */
    @ApplicationModuleListener
    void giaoDichDaXuLy(GiaoDichDaXuLyIntegrationEvent e) {
        if (e.loaiThanhToan() != LoaiThanhToan.DAT_KHAM) {
            return;
        }
        List<PhieuDatKham> ds = phieuRepository.khoaTheoGiaoDich(e.giaoDichId());
        for (PhieuDatKham p : ds) {
            if (!p.isDaXoa()) {
                continue; // đã xác nhận — chống xử lý lại
            }
            if (!e.thanhCong()) {
                p.setDaHuy(true);
                continue;
            }
            KhungGio k = khungRepository.khoaGhi(p.getKhungGioId()).orElseThrow();
            if (k.getSoLuongDaDat() >= k.getSoLuongToiDa()) {
                LichKhamBacSi l = lichRepository.findById(k.getLichKhamId()).orElseThrow();
                int cuoi = LichKhamTraCuu.chiSoKhungCuoi(l.getBuoi());
                if (k.getChiSoKhung() != cuoi) {
                    k = khungRepository.findByLichKhamIdAndChiSoKhung(l.getId(), cuoi)
                            .flatMap(x -> khungRepository.khoaGhi(x.getId())).orElseThrow();
                    p.setKhungGioId(k.getId());
                }
            }
            p.setSoThuTu(k.getSoBatDau() + k.getSoLuongDaDat()); // công thức TheDuck
            k.setSoLuongDaDat(k.getSoLuongDaDat() + 1);
            p.setDaXoa(false);
            p.setDaHuy(false); // phiếu hết hạn nhưng IPN thành công tới muộn: vẫn xác nhận
            if (p.getKenhDat() == KenhDat.QUAY) {
                long benhNhanId = nguoiDung.layHoSo(p.getHoSoId()).map(NguoiDungContracts.HoSoInfo::benhNhanId).orElseThrow();
                long benhAnId = hoSoBenhAn.taoBenhAnChoPhieu(p.getId(), k.getLichKhamId(), benhNhanId, p.getHoSoId());
                p.setBenhAnId(benhAnId);
            }
            LOG.info("Phiếu {} xác nhận, số {}", p.getMaPhieu(), p.getSoThuTu());
        }
    }

    /** Tiếp nhận xong: phiếu ghi {@code benh_an_id}. */
    @ApplicationModuleListener
    void benhAnDaTao(BenhAnDaTaoIntegrationEvent e) {
        phieuRepository.findById(e.phieuDatKhamId()).ifPresent(p -> p.setBenhAnId(e.benhAnId()));
    }

    /** Đánh giá xong: {@code da_danh_gia = true} (BR-20). */
    @ApplicationModuleListener
    void danhGiaDaTao(DanhGiaDaTaoIntegrationEvent e) {
        phieuRepository.findById(e.phieuDatKhamId()).ifPresent(p -> p.setDaDanhGia(true));
    }
}
