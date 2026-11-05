using HIS.SharedKernel.Domain;

namespace HIS.Modules.NguoiDung.Domain;

public enum GioiTinh { NAM, NU }

/// <summary>Cột phân loại TPH của tk_nhan_vien (thay cột dtype của bảng staff trong TheDuck).</summary>
public enum LoaiNhanVien { BAC_SI, DIEU_DUONG, QUAN_TRI }

public enum HocHam { BS, THS, TS, PGS, GS }

public enum TrangThaiThongBao { CHUA_NHAN, DA_NHAN, DA_XEM, DA_XOA }
