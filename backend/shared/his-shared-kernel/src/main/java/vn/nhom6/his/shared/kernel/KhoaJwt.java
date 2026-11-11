package vn.nhom6.his.shared.kernel;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/** Khóa HMAC-SHA256 của JWT, dùng chung cho bên ký (infra) và bên kiểm (web). */
public final class KhoaJwt {

    private KhoaJwt() {
    }

    /** Thiếu hoặc ngắn hơn 32 ký tự thì dừng khởi động (không chạy với khóa yếu). */
    public static SecretKey tao(String key) {
        if (key == null || key.length() < 32) {
            throw new IllegalStateException("HIS_JWT_KEY phải có ít nhất 32 ký tự ngẫu nhiên.");
        }
        return new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }
}
