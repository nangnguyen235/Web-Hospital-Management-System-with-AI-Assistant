using HIS.SharedKernel.Events;

namespace HIS.Modules.LichKham.Contracts;

public interface ILichKhamThongKe
{
    /// <summary>Lượt đặt = phiếu đã thanh toán (BR-25).</summary>
    Task<int> DemLuotKhamAsync(CancellationToken cancellationToken = default);
}

public sealed record LuotGoi(long HoSoId, int SoThuTu);

/// <summary>Điều dưỡng "Gọi tiếp" → NguoiDung tạo thông báo "Đến lượt khám" (BR-15).</summary>
public sealed record LuotKhamDaGoiIntegrationEvent(long LichKhamId, string TenPhong, IReadOnlyList<LuotGoi> CacLuot) : IIntegrationEvent;
