using HIS.SharedKernel.Abstractions;
using MailKit.Net.Smtp;
using MailKit.Security;
using Microsoft.Extensions.Options;
using MimeKit;

namespace HIS.SharedKernel.Infrastructure.Email;

public sealed class SmtpOptions
{
    public string Host { get; set; } = "smtp.gmail.com";
    public int Port { get; set; } = 587;
    public string User { get; set; } = "";
    public string AppPassword { get; set; } = "";
    public string TenNguoiGui { get; set; } = "TheHos Hospital";
}

/// <summary>Gửi email qua Gmail SMTP (STARTTLS 587, App Password). KHÔNG ghi nội dung OTP vào log.</summary>
public sealed class SmtpEmailService(IOptions<SmtpOptions> options) : IEmailService
{
    public async Task SendAsync(string toEmail, string subject, string htmlBody, CancellationToken cancellationToken = default)
    {
        var o = options.Value;
        var message = new MimeMessage();
        message.From.Add(new MailboxAddress(o.TenNguoiGui, o.User));
        message.To.Add(MailboxAddress.Parse(toEmail));
        message.Subject = subject;
        message.Body = new TextPart("html") { Text = htmlBody };

        using var smtp = new SmtpClient();
        await smtp.ConnectAsync(o.Host, o.Port, SecureSocketOptions.StartTls, cancellationToken);
        await smtp.AuthenticateAsync(o.User, o.AppPassword, cancellationToken);
        await smtp.SendAsync(message, cancellationToken);
        await smtp.DisconnectAsync(true, cancellationToken);
    }
}
