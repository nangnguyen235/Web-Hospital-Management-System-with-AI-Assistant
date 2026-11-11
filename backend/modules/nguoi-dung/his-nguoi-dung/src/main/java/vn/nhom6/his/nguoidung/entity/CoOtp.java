package vn.nhom6.his.nguoidung.entity;

import java.time.LocalDateTime;

/** Bảng có 4 cột OTP (tk_tai_khoan, tk_nguoi_dung_tam, tk_ho_so_benh_nhan) — dùng chung OtpService (BR-02). */
public interface CoOtp {

    String getOtp();

    void setOtp(String otp);

    int getSoLanNhapOtp();

    void setSoLanNhapOtp(int soLan);

    LocalDateTime getOtpTaoLuc();

    void setOtpTaoLuc(LocalDateTime thoiDiem);

    LocalDateTime getOtpHetHan();

    void setOtpHetHan(LocalDateTime thoiDiem);
}
