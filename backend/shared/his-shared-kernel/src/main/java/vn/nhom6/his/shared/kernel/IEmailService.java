package vn.nhom6.his.shared.kernel;

/** Gửi email (OTP) qua Gmail SMTP. KHÔNG ghi nội dung thư (chứa OTP) vào log. */
public interface IEmailService {

    void gui(String den, String tieuDe, String noiDungHtml);
}
