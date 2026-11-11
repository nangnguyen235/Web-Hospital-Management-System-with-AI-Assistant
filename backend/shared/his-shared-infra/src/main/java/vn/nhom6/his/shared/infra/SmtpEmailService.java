package vn.nhom6.his.shared.infra;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import vn.nhom6.his.shared.kernel.IEmailService;
import vn.nhom6.his.shared.kernel.Loi;

/**
 * Gửi email qua SMTP (spring.mail.*): production dùng Brevo cổng 2525 vì DigitalOcean chặn 25/465/587; máy dev dùng được Gmail.
 * Địa chỉ gửi = his.email.nguoi-gui (địa chỉ đã xác minh ở Brevo); để trống thì dùng spring.mail.username (Gmail).
 * KHÔNG log nội dung thư vì có OTP (rule 3).
 */
@Service
public class SmtpEmailService implements IEmailService {

    private static final Logger LOG = LoggerFactory.getLogger(SmtpEmailService.class);

    private final ObjectProvider<JavaMailSender> mailSender;
    private final String nguoiGui;

    public SmtpEmailService(
            ObjectProvider<JavaMailSender> mailSender,
            @Value("${his.email.nguoi-gui:${spring.mail.username:}}") String nguoiGui) {
        this.mailSender = mailSender;
        this.nguoiGui = nguoiGui;
    }

    @Override
    public void gui(String den, String tieuDe, String noiDungHtml) {
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null || nguoiGui.isBlank()) {
            LOG.warn("Chưa cấu hình SPRING_MAIL_* — không gửi được email \"{}\"", tieuDe);
            throw new Loi("CHUA_CAU_HINH_EMAIL", "Máy chủ chưa cấu hình gửi email.").ngoaiLe();
        }
        try {
            MimeMessage thu = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(thu, "UTF-8");
            helper.setFrom(nguoiGui, "TheHos Hospital");
            helper.setTo(den);
            helper.setSubject(tieuDe);
            helper.setText(noiDungHtml, true);
            sender.send(thu);
        } catch (MessagingException | MailException | UnsupportedEncodingException e) {
            LOG.error("Gửi email \"{}\" thất bại: {}", tieuDe, e.getClass().getSimpleName());
            throw new Loi("GUI_EMAIL_THAT_BAI", "Không gửi được email, vui lòng thử lại sau.").ngoaiLe();
        }
    }
}
