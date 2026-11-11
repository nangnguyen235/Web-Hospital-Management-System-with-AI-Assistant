package vn.nhom6.his.shared.infra;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** {@code his.r2.*} ← HIS_R2_ENDPOINT, HIS_R2_ACCESS_KEY, HIS_R2_SECRET_KEY, HIS_R2_BUCKET. */
@ConfigurationProperties(prefix = "his.r2")
public record R2Properties(String endpoint, String accessKey, String secretKey, String bucket) {

    boolean daCauHinh() {
        return coGiaTri(endpoint) && coGiaTri(accessKey) && coGiaTri(secretKey) && coGiaTri(bucket);
    }

    private static boolean coGiaTri(String s) {
        return s != null && !s.isBlank();
    }
}
