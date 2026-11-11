package vn.nhom6.his.contracts.vienphi;

import vn.nhom6.his.shared.kernel.IntegrationEvent;

/**
 * VienPhi phát khi return/IPN VNPAY đổi giao dịch sang THANH_CONG/THAT_BAI (BR-10).
 * LichKham (DAT_KHAM) xác nhận/nhả phiếu; HoSoBenhAn (XET_NGHIEM) đánh dấu đã thanh toán. Handler chống xử lý lại.
 */
public record GiaoDichDaXuLyIntegrationEvent(long giaoDichId, LoaiThanhToan loaiThanhToan, boolean thanhCong)
        implements IntegrationEvent {
}
