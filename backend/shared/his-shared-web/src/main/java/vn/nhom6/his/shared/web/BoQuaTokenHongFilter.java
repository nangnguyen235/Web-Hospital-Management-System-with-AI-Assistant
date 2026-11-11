package vn.nhom6.his.shared.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Token hết hạn/hỏng: coi như khách (bỏ header Authorization) thay vì trả 401 ngay, để endpoint công khai
 * (trang chủ, màn hình số) vẫn chạy khi token trong trình duyệt đã cũ — như bản .NET ([AllowAnonymous]).
 * Endpoint cần đăng nhập vẫn trả 401 nhờ {@code @PreAuthorize}.
 */
class BoQuaTokenHongFilter extends OncePerRequestFilter {

    private final JwtDecoder decoder;

    BoQuaTokenHongFilter(JwtDecoder decoder) {
        this.decoder = decoder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.regionMatches(true, 0, "Bearer ", 0, 7)) {
            try {
                decoder.decode(header.substring(7).trim());
            } catch (JwtException e) {
                chain.doFilter(new KhongCoToken(request), response);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private static final class KhongCoToken extends HttpServletRequestWrapper {
        KhongCoToken(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getHeader(String name) {
            return HttpHeaders.AUTHORIZATION.equalsIgnoreCase(name) ? null : super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            return HttpHeaders.AUTHORIZATION.equalsIgnoreCase(name) ? Collections.emptyEnumeration() : super.getHeaders(name);
        }
    }
}
