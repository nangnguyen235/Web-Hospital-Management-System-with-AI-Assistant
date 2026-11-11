package vn.nhom6.his.shared.kernel;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Đánh dấu endpoint công khai (không cần đăng nhập). Mọi phương thức controller khác PHẢI có
 * {@code @PreAuthorize} — test {@code PhanQuyenEndpointTests} trong his-app kiểm tra điều này.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface CongKhai {
}
