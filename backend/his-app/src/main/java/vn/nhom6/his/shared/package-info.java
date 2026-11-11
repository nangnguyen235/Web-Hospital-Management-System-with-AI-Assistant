/**
 * Phần dùng chung (kernel / infra / web — 3 artifact Maven). Khai báo module MỞ để mọi module dùng được các package con
 * mà Spring Modulith không coi là chạm vào phần nội bộ. Ranh giới kernel/infra/web do Maven + ArchUnit giữ.
 */
@ApplicationModule(type = ApplicationModule.Type.OPEN)
package vn.nhom6.his.shared;

import org.springframework.modulith.ApplicationModule;
