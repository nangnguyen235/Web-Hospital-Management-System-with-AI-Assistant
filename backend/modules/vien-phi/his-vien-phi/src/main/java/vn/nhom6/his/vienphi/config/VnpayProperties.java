package vn.nhom6.his.vienphi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * {@code his.vnpay.*} ← HIS_VNPAY_TMN_CODE, HIS_VNPAY_HASH_SECRET, HIS_VNPAY_PAY_URL, HIS_VNPAY_RETURN_URL.
 * Tài khoản sandbox CỦA NHÓM — KHÔNG dùng khóa có trong repo TheDuck (rule 6).
 */
@ConfigurationProperties(prefix = "his.vnpay")
public record VnpayProperties(String tmnCode, String hashSecret, String payUrl, String returnUrl) {

    public boolean daCauHinh() {
        return tmnCode != null && !tmnCode.isBlank() && hashSecret != null && !hashSecret.isBlank();
    }
}
