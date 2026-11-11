package vn.nhom6.his.shared.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import vn.nhom6.his.shared.kernel.KhoaJwt;

/**
 * Spring Security: JWT HS256 (OAuth2 Resource Server), CORS cho frontend trên Pages, không session.
 * Lọc ở mức URL để mở; quyền từng endpoint đặt bằng {@code @PreAuthorize} (endpoint công khai đánh dấu {@code @CongKhai}).
 * Thiếu/hỏng token khi gọi endpoint cần đăng nhập → 401; sai vai trò → 403 (frontend.md mục 6).
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    /** Quyền phụ cho bác sĩ trưởng khoa / điều dưỡng trưởng (claim la_truong_khoa = "true"). */
    public static final String TRUONG_KHOA = "TRUONG_KHOA";

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {
        var loi = new ProblemSecurityHandler();
        http
                .csrf(c -> c.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a.anyRequest().permitAll())
                .addFilterBefore(new BoQuaTokenHongFilter(jwtDecoder), BearerTokenAuthenticationFilter.class)
                .oauth2ResourceServer(o -> o
                        .jwt(j -> j.decoder(jwtDecoder).jwtAuthenticationConverter(new VaiTroConverter()))
                        .authenticationEntryPoint(loi)
                        .accessDeniedHandler(loi))
                .exceptionHandling(e -> e.authenticationEntryPoint(loi).accessDeniedHandler(loi));
        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder(@Value("${his.jwt.key:}") String key,
                          @Value("${his.jwt.issuer:his-api}") String issuer,
                          @Value("${his.jwt.audience:his-frontend}") String audience) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(KhoaJwt.tao(key))
                .macAlgorithm(MacAlgorithm.HS256).build();
        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(issuer),
                new JwtClaimValidator<List<String>>("aud", aud -> aud != null && aud.contains(audience)));
        decoder.setJwtValidator(validator);
        return decoder;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(@Value("${his.cors.allowed-origins:}") String origins) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.stream(origins.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /** {@code vai_tro} → ROLE_&lt;VAI_TRO&gt;; {@code la_truong_khoa = "true"} → thêm TRUONG_KHOA. */
    static final class VaiTroConverter implements Converter<Jwt, AbstractAuthenticationToken> {
        @Override
        public AbstractAuthenticationToken convert(Jwt jwt) {
            Collection<GrantedAuthority> quyen = new ArrayList<>();
            String vaiTro = jwt.getClaimAsString("vai_tro");
            if (vaiTro != null) {
                quyen.add(new SimpleGrantedAuthority("ROLE_" + vaiTro));
            }
            if ("true".equals(jwt.getClaimAsString("la_truong_khoa"))) {
                quyen.add(new SimpleGrantedAuthority(TRUONG_KHOA));
            }
            return new JwtAuthenticationToken(jwt, quyen, jwt.getClaimAsString("tai_khoan_id"));
        }
    }
}
