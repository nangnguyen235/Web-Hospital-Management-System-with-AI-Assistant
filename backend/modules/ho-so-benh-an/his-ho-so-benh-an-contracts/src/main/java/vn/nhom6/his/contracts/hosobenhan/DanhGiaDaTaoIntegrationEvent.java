package vn.nhom6.his.contracts.hosobenhan;

import java.math.BigDecimal;
import vn.nhom6.his.shared.kernel.IntegrationEvent;

/**
 * HoSoBenhAn phát khi bệnh nhân đánh giá (BR-20). NguoiDung ghi {@code tk_nhan_vien.diem_danh_gia},
 * LichKham ghi {@code lk_phieu_dat_kham.da_danh_gia = true}.
 */
public record DanhGiaDaTaoIntegrationEvent(long phieuDatKhamId, long bacSiId, BigDecimal diemTrungBinh)
        implements IntegrationEvent {
}
