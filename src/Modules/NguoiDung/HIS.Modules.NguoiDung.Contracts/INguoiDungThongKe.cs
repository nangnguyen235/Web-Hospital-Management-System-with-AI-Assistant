namespace HIS.Modules.NguoiDung.Contracts;

/// <summary>Số liệu NguoiDung cho thống kê (FR-AD-01, BR-25).</summary>
public interface INguoiDungThongKe
{
    Task<int> DemBacSiAsync(CancellationToken cancellationToken = default);
    Task<int> DemBenhNhanAsync(CancellationToken cancellationToken = default);
}
