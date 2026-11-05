namespace HIS.SharedKernel.Domain;

/// <summary>Giờ Việt Nam (NFR-06). Luôn lấy thời gian qua <see cref="TimeProvider"/> để test được.</summary>
public static class GioVietNam
{
    public static readonly TimeZoneInfo MuiGio = TimeZoneInfo.FindSystemTimeZoneById("Asia/Ho_Chi_Minh");

    public static DateTime HienTai(this TimeProvider thoiGian) =>
        TimeZoneInfo.ConvertTime(thoiGian.GetUtcNow(), MuiGio).DateTime;

    public static DateOnly HomNay(this TimeProvider thoiGian) => DateOnly.FromDateTime(thoiGian.HienTai());
}
