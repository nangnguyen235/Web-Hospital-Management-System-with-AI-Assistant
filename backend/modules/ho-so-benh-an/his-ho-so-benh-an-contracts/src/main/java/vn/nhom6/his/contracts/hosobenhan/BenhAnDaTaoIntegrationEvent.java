package vn.nhom6.his.contracts.hosobenhan;

import vn.nhom6.his.shared.kernel.IntegrationEvent;

/** HoSoBenhAn phát khi tạo bệnh án (tiếp nhận hoặc phiếu quầy). LichKham ghi {@code lk_phieu_dat_kham.benh_an_id}. */
public record BenhAnDaTaoIntegrationEvent(long benhAnId, long phieuDatKhamId) implements IntegrationEvent {
}
