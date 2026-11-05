using HIS.SharedKernel.Events;

namespace HIS.Modules.VienPhi.Contracts;

public static class LoaiThanhToanSuKien
{
    public const string DatKham = "DAT_KHAM";
    public const string XetNghiem = "XET_NGHIEM";
}

/// <summary>
/// IPN VNPAY đổi giao dịch sang THANH_CONG/THAT_BAI. Handler phải chống xử lý lại: IPN gặp giao dịch đã xử lý
/// sẽ phát lại sự kiện này (modular-monolith.md mục 3.1).
/// </summary>
public sealed record GiaoDichDaXuLyIntegrationEvent(long GiaoDichId, string LoaiThanhToan, bool ThanhCong) : IIntegrationEvent;
