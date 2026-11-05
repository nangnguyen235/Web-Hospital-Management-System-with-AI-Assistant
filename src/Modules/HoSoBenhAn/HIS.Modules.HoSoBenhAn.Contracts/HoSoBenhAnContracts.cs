using HIS.SharedKernel.Events;

namespace HIS.Modules.HoSoBenhAn.Contracts;

/// <summary>Tạo bệnh án (tiếp nhận hoặc phiếu quầy) → LichKham ghi lk_phieu_dat_kham.benh_an_id.</summary>
public sealed record BenhAnDaTaoIntegrationEvent(long BenhAnId, long PhieuDatKhamId) : IIntegrationEvent;

/// <summary>Bệnh nhân đánh giá → NguoiDung cập nhật điểm bác sĩ, LichKham đặt da_danh_gia.</summary>
public sealed record DanhGiaDaTaoIntegrationEvent(long PhieuDatKhamId, long BacSiId, decimal DiemTrungBinh) : IIntegrationEvent;
