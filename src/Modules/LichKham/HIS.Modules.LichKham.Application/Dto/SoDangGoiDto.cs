using HIS.Modules.LichKham.Domain;

namespace HIS.Modules.LichKham.Application;

/// <summary>Chỉ số + phòng + khoa, KHÔNG có họ tên (NFR-03).</summary>
public sealed record SoDangGoiDto(
    long LichKhamId, int SoDangGoi, IReadOnlyList<int> CacSoVuaGoi, string TenPhong, string TenKhoa, DateOnly Ngay, Buoi Buoi);
