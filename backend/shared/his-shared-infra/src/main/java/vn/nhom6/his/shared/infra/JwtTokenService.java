package vn.nhom6.his.shared.infra;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;
import vn.nhom6.his.shared.kernel.ITokenService;
import vn.nhom6.his.shared.kernel.KhoaJwt;

/**
 * JWT HS256, 60 phút. Claim giữ đúng như bản trước để frontend không phải sửa (modular-monolith.md mục 3):
 * mọi claim là chuỗi, {@code la_truong_khoa} = "true"/"false".
 */
@Service
public class JwtTokenService implements ITokenService {

    private final JwtProperties properties;
    private final Clock clock;
    private final JwtEncoder encoder;

    public JwtTokenService(JwtProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
        SecretKey khoa = KhoaJwt.tao(properties.key());
        this.encoder = new NimbusJwtEncoder(new ImmutableSecret<>(khoa));
    }

    @Override
    public TokenDaTao taoToken(ThongTinToken thongTin) {
        Instant bayGio = Instant.now(clock);
        Instant hetHan = bayGio.plusSeconds(properties.phutHetHan() * 60L);
        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuer(properties.issuer())
                .audience(List.of(properties.audience()))
                .issuedAt(bayGio)
                .expiresAt(hetHan)
                .subject(String.valueOf(thongTin.taiKhoanId()))
                .claim("tai_khoan_id", String.valueOf(thongTin.taiKhoanId()))
                .claim("vai_tro", thongTin.vaiTro().name())
                .claim("la_truong_khoa", thongTin.laTruongKhoa() ? "true" : "false");
        if (thongTin.nhanVienId() != null) {
            claims.claim("nhan_vien_id", String.valueOf(thongTin.nhanVienId()));
        }
        if (thongTin.khoaId() != null) {
            claims.claim("khoa_id", String.valueOf(thongTin.khoaId()));
        }
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = encoder.encode(JwtEncoderParameters.from(header, claims.build())).getTokenValue();
        return new TokenDaTao(token, OffsetDateTime.ofInstant(hetHan, clock.getZone()));
    }
}
