package vn.nhom6.his.contracts.nguoidung;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Hỏi/lệnh NguoiDung từ module khác (modular-monolith.md mục 3.2). Cài đặt: NguoiDungContractsService. */
public interface NguoiDungContracts {

    /** Số bác sĩ đang làm việc (thống kê trang chủ, BR-25). */
    long demBacSi();

    /** Số bệnh nhân (tk_benh_nhan) — thống kê Admin (BR-25). */
    long demBenhNhan();

    Optional<NhanVienInfo> layNhanVien(long nhanVienId);

    /** Họ tên theo id (kể cả đã xóa) — để hiển thị. */
    Map<Long, String> tenNhanVien(Collection<Long> ids);

    /** Bác sĩ chưa xóa của khoa. */
    List<NhanVienInfo> bacSiCuaKhoa(int khoaId);

    Optional<HoSoInfo> layHoSo(long hoSoId);

    Map<Long, HoSoInfo> layCacHoSo(Collection<Long> ids);

    /** Mã bệnh nhân theo id. */
    Map<Long, String> maBenhNhan(Collection<Long> benhNhanIds);

    /**
     * Tiếp nhận (BR-13, BR-14): hồ sơ chưa liên kết → tìm {@code tk_benh_nhan} theo CCCD, chưa có thì tạo từ thông tin hồ sơ;
     * ghi CCCD vào hồ sơ. Hồ sơ đã liên kết thì trả bệnh nhân cũ. Trả {@code benhNhanId}.
     */
    long lienKetBenhNhan(long hoSoId, String soCccd);

    record NhanVienInfo(long id, String hoTen, String vaiTro, Integer khoaId, boolean laTruongKhoa, boolean daXoa) {
    }

    record HoSoInfo(long id, Long taiKhoanId, Long benhNhanId, String hoTen, LocalDate ngaySinh, String gioiTinh,
                    String soCccd, String soDienThoai, String email, boolean daXoa) {
    }
}
