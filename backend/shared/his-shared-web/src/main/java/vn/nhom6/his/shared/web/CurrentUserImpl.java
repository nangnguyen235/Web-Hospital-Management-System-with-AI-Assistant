package vn.nhom6.his.shared.web;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import vn.nhom6.his.shared.kernel.CurrentUser;
import vn.nhom6.his.shared.kernel.VaiTro;

/** Đọc người dùng hiện tại từ claim JWT của request (SecurityContext). */
@Component
public class CurrentUserImpl implements CurrentUser {

    private static JwtAuthenticationToken token() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth instanceof JwtAuthenticationToken jwt ? jwt : null;
    }

    private static String claim(String ten) {
        JwtAuthenticationToken t = token();
        return t == null ? null : t.getToken().getClaimAsString(ten);
    }

    @Override
    public boolean daDangNhap() {
        return token() != null;
    }

    @Override
    public long taiKhoanId() {
        String id = claim("tai_khoan_id");
        if (id == null) {
            throw new AuthenticationCredentialsNotFoundException("Chưa đăng nhập");
        }
        return Long.parseLong(id);
    }

    @Override
    public VaiTro vaiTro() {
        String vt = claim("vai_tro");
        return vt == null ? null : VaiTro.valueOf(vt);
    }

    @Override
    public Long nhanVienId() {
        String id = claim("nhan_vien_id");
        return id == null ? null : Long.valueOf(id);
    }

    @Override
    public Integer khoaId() {
        String id = claim("khoa_id");
        return id == null ? null : Integer.valueOf(id);
    }

    @Override
    public boolean laTruongKhoa() {
        return "true".equals(claim("la_truong_khoa"));
    }
}
