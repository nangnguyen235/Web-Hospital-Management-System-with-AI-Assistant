/**
 * Contracts của 6 module (mỗi module một artifact {@code his-<x>-contracts}): interface hỏi/lệnh, DTO, IntegrationEvent.
 * Đặt NGOÀI package module và khai báo MỞ để Spring Modulith không thấy vòng khi hai module nghe sự kiện của nhau
 * (modular-monolith.md mục 1).
 */
@ApplicationModule(type = ApplicationModule.Type.OPEN)
package vn.nhom6.his.contracts;

import org.springframework.modulith.ApplicationModule;
