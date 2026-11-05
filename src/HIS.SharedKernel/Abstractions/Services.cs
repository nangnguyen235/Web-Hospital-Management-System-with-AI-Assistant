namespace HIS.SharedKernel.Abstractions;

/// <summary>Lưu tệp trên Cloudflare R2 (cloudflare-r2.md). DB chỉ lưu object key.</summary>
public interface IStorageService
{
    Task UploadAsync(Stream content, string objectKey, string contentType, CancellationToken cancellationToken = default);
    Task<string> GetPresignedUrlAsync(string objectKey, TimeSpan? expiresIn = null);
    Task DeleteAsync(string objectKey, CancellationToken cancellationToken = default);
}

/// <summary>Gửi email (OTP) qua Gmail SMTP.</summary>
public interface IEmailService
{
    Task SendAsync(string toEmail, string subject, string htmlBody, CancellationToken cancellationToken = default);
}

/// <summary>Người dùng của request hiện tại, đọc từ claim JWT.</summary>
public interface ICurrentUser
{
    bool DaDangNhap { get; }
    long? TaiKhoanId { get; }
    Auth.VaiTro? VaiTro { get; }
    long? NhanVienId { get; }
    int? KhoaId { get; }
    bool LaTruongKhoa { get; }
}
