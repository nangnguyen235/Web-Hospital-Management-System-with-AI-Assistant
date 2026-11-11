package vn.nhom6.his.shared.kernel;

import java.time.OffsetDateTime;

/** Tạo JWT access token 60 phút (BR-02). Cài đặt ở his-shared-infra. */
public interface ITokenService {

    TokenDaTao taoToken(ThongTinToken thongTin);

    record ThongTinToken(long taiKhoanId, VaiTro vaiTro, Long nhanVienId, Integer khoaId, boolean laTruongKhoa) {
    }

    record TokenDaTao(String accessToken, OffsetDateTime hetHan) {
    }
}
