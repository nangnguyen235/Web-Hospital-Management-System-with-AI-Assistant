package vn.nhom6.his.nguoidung.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HexFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.nhom6.his.nguoidung.entity.CoOtp;
import vn.nhom6.his.shared.kernel.IEmailService;
import vn.nhom6.his.shared.kernel.Loi;

/**
 * OTP như TheDuck (BR-02, FR-HT-10): 6 số, hạn 5 phút, sai 5 lần thì mã hết hiệu lực; xin lại thì thay mã cũ; dùng xong xóa.
 * Lưu dạng băm SHA-256; KHÔNG ghi OTP vào log. Chỉ gửi email (Gmail SMTP).
 * Phương thức gọi {@link #kiemTra} PHẢI khai {@code @Transactional(noRollbackFor = LoiNghiepVuException.class)} để số lần sai được lưu.
 */
@Service
@RequiredArgsConstructor
public class OtpService {

    static final int HAN_PHUT = 5;
    static final int SO_LAN_SAI_TOI_DA = 5;
    private static final SecureRandom NGAU_NHIEN = new SecureRandom();

    private final Clock clock;
    private final IEmailService email;

    /** Sinh mã mới (thay mã cũ), gửi email. */
    public void taoVaGui(CoOtp doiTuong, String denEmail, String mucDich) {
        String ma = String.format("%06d", NGAU_NHIEN.nextInt(1_000_000));
        LocalDateTime bayGio = LocalDateTime.now(clock);
        doiTuong.setOtp(bam(ma));
        doiTuong.setSoLanNhapOtp(0);
        doiTuong.setOtpTaoLuc(bayGio);
        doiTuong.setOtpHetHan(bayGio.plusMinutes(HAN_PHUT));
        email.gui(denEmail, "[TheHos] Mã xác thực " + mucDich, noiDungThu(ma, mucDich));
    }

    /** Đúng mã thì xóa mã (dùng một lần); sai thì tăng số lần sai và báo lỗi. */
    public void kiemTra(CoOtp doiTuong, String ma) {
        if (doiTuong.getOtp() == null || doiTuong.getOtpHetHan() == null
                || LocalDateTime.now(clock).isAfter(doiTuong.getOtpHetHan())
                || doiTuong.getSoLanNhapOtp() >= SO_LAN_SAI_TOI_DA) {
            throw new Loi("OTP_HET_HIEU_LUC", "Mã OTP đã hết hiệu lực, vui lòng xin mã mới.").ngoaiLe();
        }
        if (ma == null || !MessageDigest.isEqual(bam(ma.trim()).getBytes(StandardCharsets.UTF_8),
                doiTuong.getOtp().getBytes(StandardCharsets.UTF_8))) {
            int conLai = SO_LAN_SAI_TOI_DA - doiTuong.getSoLanNhapOtp() - 1;
            doiTuong.setSoLanNhapOtp(doiTuong.getSoLanNhapOtp() + 1);
            throw new Loi("OTP_SAI", conLai > 0
                    ? "Mã OTP không đúng, còn " + conLai + " lần thử."
                    : "Mã OTP không đúng. Đã sai 5 lần, vui lòng xin mã mới.").ngoaiLe();
        }
        xoa(doiTuong);
    }

    public static void xoa(CoOtp doiTuong) {
        // tk_nguoi_dung_tam.otp NOT NULL → chuỗi rỗng; hết hạn = null nên mã cũ không dùng lại được
        doiTuong.setOtp(doiTuong instanceof vn.nhom6.his.nguoidung.entity.NguoiDungTam ? "" : null);
        doiTuong.setSoLanNhapOtp(0);
        doiTuong.setOtpHetHan(null);
    }

    static String bam(String ma) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(ma.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String noiDungThu(String ma, String mucDich) {
        return """
                <div style="font-family:Roboto,Arial,sans-serif;max-width:480px;margin:auto;color:#003553">
                  <h2 style="color:#003a91">TheHos Hospital</h2>
                  <p>Mã xác thực %s của bạn là:</p>
                  <p style="font-size:32px;letter-spacing:8px;font-weight:bold;color:#003a91">%s</p>
                  <p>Mã có hiệu lực trong %d phút. Không chia sẻ mã này cho bất kỳ ai.</p>
                </div>""".formatted(mucDich, ma, HAN_PHUT);
    }
}
