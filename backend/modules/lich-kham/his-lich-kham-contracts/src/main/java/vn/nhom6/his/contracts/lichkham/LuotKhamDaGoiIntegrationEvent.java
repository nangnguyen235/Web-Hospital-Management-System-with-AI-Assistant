package vn.nhom6.his.contracts.lichkham;

import java.util.List;
import vn.nhom6.his.shared.kernel.IntegrationEvent;

/**
 * LichKham phát khi điều dưỡng "Gọi tiếp" (BR-15). NguoiDung tạo thông báo "Đến lượt khám" cho hồ sơ có tài khoản.
 */
public record LuotKhamDaGoiIntegrationEvent(long lichKhamId, String tenPhong, List<SoDaGoi> danhSach)
        implements IntegrationEvent {

    public record SoDaGoi(long hoSoId, int soThuTu) {
    }
}
