package vn.nhom6.his.shared.infra;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** {@code his.jwt.*} ← biến HIS_JWT_KEY (≥ 32 ký tự), HIS_JWT_ISSUER, HIS_JWT_AUDIENCE. */
@ConfigurationProperties(prefix = "his.jwt")
public record JwtProperties(String key, String issuer, String audience, Integer soPhutHetHan) {

    public int phutHetHan() {
        return soPhutHetHan == null ? 60 : soPhutHetHan; // BR-02
    }
}
