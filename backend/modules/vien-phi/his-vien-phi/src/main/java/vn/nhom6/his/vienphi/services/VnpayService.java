package vn.nhom6.his.vienphi.services;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.nhom6.his.shared.kernel.Loi;
import vn.nhom6.his.vienphi.config.VnpayProperties;
import vn.nhom6.his.vienphi.entity.GiaoDich;

/**
 * Ký và kiểm chữ ký VNPAY 2.1.0 bằng HMAC-SHA512 (tự viết bằng javax.crypto, không dùng SDK). Tham số sắp theo tên,
 * giá trị mã hóa URL (US-ASCII) — cùng chuỗi cho hashData và query string.
 */
@Service
@RequiredArgsConstructor
public class VnpayService {

    static final DateTimeFormatter DINH_DANG = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    static final int HAN_PHUT = 15; // BR-10

    private final VnpayProperties properties;

    public String taoUrl(GiaoDich g, String ipKhachHang) {
        if (!properties.daCauHinh()) {
            throw new Loi("CHUA_CAU_HINH_VNPAY", "Máy chủ chưa cấu hình VNPAY (HIS_VNPAY_*).").ngoaiLe();
        }
        LocalDateTime tao = g.getNgayTao();
        Map<String, String> p = new TreeMap<>();
        p.put("vnp_Version", "2.1.0");
        p.put("vnp_Command", "pay");
        p.put("vnp_TmnCode", properties.tmnCode());
        p.put("vnp_Amount", String.valueOf(soTienVnpay(g)));
        p.put("vnp_CurrCode", "VND");
        p.put("vnp_TxnRef", String.valueOf(g.getId()));
        p.put("vnp_OrderInfo", "Thanh toan giao dich " + g.getId());
        p.put("vnp_OrderType", "other");
        p.put("vnp_Locale", "vn");
        p.put("vnp_ReturnUrl", properties.returnUrl());
        p.put("vnp_IpAddr", ipKhachHang == null || ipKhachHang.isBlank() ? "127.0.0.1" : ipKhachHang);
        p.put("vnp_CreateDate", tao.format(DINH_DANG));
        p.put("vnp_ExpireDate", tao.plusMinutes(HAN_PHUT).format(DINH_DANG));
        String query = noi(p);
        return properties.payUrl() + "?" + query + "&vnp_SecureHash=" + hmac(query);
    }

    /** Kiểm chữ ký các tham số VNPAY gửi về (return URL hoặc IPN). */
    public boolean dungChuKy(Map<String, String> thamSo) {
        String chuKy = thamSo.get("vnp_SecureHash");
        if (chuKy == null || !properties.daCauHinh()) {
            return false;
        }
        Map<String, String> p = new TreeMap<>();
        thamSo.forEach((k, v) -> {
            if (k.startsWith("vnp_") && !k.equals("vnp_SecureHash") && !k.equals("vnp_SecureHashType") && v != null && !v.isEmpty()) {
                p.put(k, v);
            }
        });
        return hmac(noi(p)).equalsIgnoreCase(chuKy);
    }

    /** VNPAY tính tiền ×100 (đơn vị nhỏ nhất). */
    static long soTienVnpay(GiaoDich g) {
        return g.getSoTien().add(g.getPhi()).multiply(BigDecimal.valueOf(100)).longValueExact();
    }

    private static String noi(Map<String, String> p) {
        StringJoiner sj = new StringJoiner("&");
        p.forEach((k, v) -> sj.add(URLEncoder.encode(k, StandardCharsets.US_ASCII) + "="
                + URLEncoder.encode(v, StandardCharsets.US_ASCII)));
        return sj.toString();
    }

    String hmac(String duLieu) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(properties.hashSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            return HexFormat.of().formatHex(mac.doFinal(duLieu.getBytes(StandardCharsets.UTF_8)));
        } catch (java.security.GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }
}
