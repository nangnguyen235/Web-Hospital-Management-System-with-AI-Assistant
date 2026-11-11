package vn.nhom6.his.hosobenhan.services;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.nhom6.his.contracts.danhmuc.DanhMucContracts;
import vn.nhom6.his.contracts.danhmuc.DichVuInfo;
import vn.nhom6.his.contracts.danhmuc.PhongInfo;
import vn.nhom6.his.contracts.lichkham.LichKhamContracts;
import vn.nhom6.his.contracts.lichkham.LichKhamContracts.LichInfo;
import vn.nhom6.his.contracts.lichkham.LichKhamContracts.PhieuInfo;
import vn.nhom6.his.contracts.nguoidung.NguoiDungContracts;
import vn.nhom6.his.contracts.nguoidung.NguoiDungContracts.HoSoInfo;
import vn.nhom6.his.hosobenhan.dto.BenhAnDto;
import vn.nhom6.his.hosobenhan.entity.BenhAn;
import vn.nhom6.his.hosobenhan.entity.ChiTietDonThuoc;
import vn.nhom6.his.hosobenhan.entity.DonThuoc;
import vn.nhom6.his.hosobenhan.entity.XetNghiem;
import vn.nhom6.his.hosobenhan.repository.ChiTietDonThuocRepository;
import vn.nhom6.his.hosobenhan.repository.DonThuocRepository;
import vn.nhom6.his.hosobenhan.repository.XetNghiemRepository;

/** Đổi entity → DTO, hỏi tên phòng/khoa/dịch vụ/thuốc/bác sĩ/hồ sơ qua contracts. */
@Component
@RequiredArgsConstructor
class BenhAnTraCuu {

    private final Clock clock;
    private final DanhMucContracts danhMuc;
    private final NguoiDungContracts nguoiDung;
    private final LichKhamContracts lichKham;
    private final XetNghiemRepository xetNghiemRepository;
    private final DonThuocRepository donThuocRepository;
    private final ChiTietDonThuocRepository chiTietRepository;

    LocalDate homNay() {
        return LocalDate.now(clock);
    }

    java.time.LocalDateTime bayGio() {
        return java.time.LocalDateTime.now(clock);
    }

    List<BenhAnDto.BenhAnGon> gon(Collection<BenhAn> ds) {
        Map<Long, HoSoInfo> hoSo = nguoiDung.layCacHoSo(ds.stream().map(BenhAn::getHoSoId).distinct().toList());
        Map<Long, String> ma = nguoiDung.maBenhNhan(ds.stream().map(BenhAn::getBenhNhanId).distinct().toList());
        Map<Long, PhieuInfo> phieu = lichKham.layCacPhieu(ds.stream().map(BenhAn::getPhieuDatKhamId).toList());
        return ds.stream().map(b -> {
            HoSoInfo h = hoSo.get(b.getHoSoId());
            PhieuInfo p = phieu.get(b.getPhieuDatKhamId());
            return new BenhAnDto.BenhAnGon(b.getId(), b.getPhieuDatKhamId(), p == null ? 0 : p.soThuTu(), b.getHoSoId(),
                    h == null ? "" : h.hoTen(), h == null ? null : h.ngaySinh(), h == null ? null : h.gioiTinh(),
                    ma.get(b.getBenhNhanId()), b.getTrangThai(), b.getLichKhamId(), b.getNgayTao());
        }).sorted(java.util.Comparator.comparingInt(BenhAnDto.BenhAnGon::soThuTu)).toList();
    }

    BenhAnDto.ChiTiet chiTiet(BenhAn b) {
        HoSoInfo h = nguoiDung.layHoSo(b.getHoSoId()).orElse(null);
        LichInfo l = lichKham.layLich(b.getLichKhamId()).orElse(null);
        PhongInfo phong = l == null ? null : danhMuc.layPhong(l.phongId()).orElse(null);
        String tenBacSi = l == null ? "" : nguoiDung.tenNhanVien(List.of(l.bacSiId())).getOrDefault(l.bacSiId(), "");
        boolean daDanhGia = lichKham.layPhieu(b.getPhieuDatKhamId()).map(PhieuInfo::daDanhGia).orElse(false);
        return new BenhAnDto.ChiTiet(b.getId(), b.getPhieuDatKhamId(), b.getLichKhamId(), b.getLichKhamTruocId(),
                b.getBenhNhanId(), nguoiDung.maBenhNhan(List.of(b.getBenhNhanId())).get(b.getBenhNhanId()), b.getHoSoId(),
                h == null ? "" : h.hoTen(), h == null ? null : h.ngaySinh(), h == null ? null : h.gioiTinh(), b.getTrangThai(),
                b.getTrieuChung(), b.getChanDoan(), b.getNgayTaiKham(), l == null ? null : l.ngay(), tenBacSi,
                phong == null ? "" : phong.tenKhoa(), phong == null ? "" : phong.tenPhong(), daDanhGia,
                xetNghiem(xetNghiemRepository.findByBenhAnIdAndDaXoaFalseOrderByIdAsc(b.getId()), null),
                donThuocRepository.findByBenhAnId(b.getId()).map(this::donThuoc).orElse(null), b.getNgayTao());
    }

    List<BenhAnDto.XetNghiem> xetNghiem(Collection<XetNghiem> ds, Map<Long, String> hoTenTheoBenhAn) {
        Map<Integer, DichVuInfo> dv = danhMuc.layCacDichVu(ds.stream().map(XetNghiem::getDichVuId).distinct().toList());
        Map<Integer, PhongInfo> phong = new HashMap<>();
        return ds.stream().map(x -> {
            PhongInfo p = phong.computeIfAbsent(x.getPhongId(), id -> danhMuc.layPhong(id).orElse(null));
            DichVuInfo d = dv.get(x.getDichVuId());
            return new BenhAnDto.XetNghiem(x.getId(), x.getMaXetNghiem(), x.getSoThuTu(), x.getDichVuId(),
                    d == null ? "" : d.tenDichVu(), x.getPhongId(), p == null ? "" : p.tenPhong(), x.getGia(), x.getGhiChu(),
                    x.getKetQua(), x.getFileKetQuaKey() != null, x.getTrangThai(), x.isDaThanhToan(), x.getGiaoDichId(),
                    x.getNgay(), x.getBenhAnId(), hoTenTheoBenhAn == null ? null : hoTenTheoBenhAn.get(x.getBenhAnId()));
        }).toList();
    }

    BenhAnDto.DonThuoc donThuoc(DonThuoc d) {
        List<ChiTietDonThuoc> ct = chiTietRepository.findByDonThuocIdAndDaXoaFalseOrderByIdAsc(d.getId());
        Map<Integer, DanhMucContracts.ThuocInfo> thuoc = danhMuc.layCacThuoc(ct.stream().map(ChiTietDonThuoc::getThuocId).distinct().toList());
        return new BenhAnDto.DonThuoc(d.getId(), d.getMaDonThuoc(), d.getTongTien(), ct.stream().map(c -> {
            DanhMucContracts.ThuocInfo t = thuoc.get(c.getThuocId());
            return new BenhAnDto.DongThuoc(c.getId(), c.getThuocId(), t == null ? "" : t.tenThuoc(), t == null ? "" : t.donVi(),
                    c.getSoLuong(), c.getGia(), c.getThanhTien(), c.getSoLanMoiNgay(), c.getSoNgay(), c.getSoLuongMoiLan(),
                    c.isSang(), c.isTrua(), c.isChieu(), c.isToi(), c.getHuongDan());
        }).toList(), d.getNgayTao());
    }

    /** Lịch sử khám (FR-BN-11, FR-BS-09) — tên bác sĩ, khoa qua contracts. */
    List<BenhAnDto.LichSu> lichSu(List<BenhAn> ds) {
        Map<Long, LichInfo> lich = lichKham.layCacLich(ds.stream().map(BenhAn::getLichKhamId).distinct().toList());
        Map<Long, String> tenBacSi = nguoiDung.tenNhanVien(lich.values().stream().map(LichInfo::bacSiId).distinct().toList());
        Map<Integer, String> tenKhoa = danhMuc.tenCacKhoa();
        Map<Long, PhieuInfo> phieu = lichKham.layCacPhieu(ds.stream().map(BenhAn::getPhieuDatKhamId).toList());
        Map<Long, HoSoInfo> hoSo = nguoiDung.layCacHoSo(ds.stream().map(BenhAn::getHoSoId).distinct().toList());
        return ds.stream().map(b -> {
            LichInfo l = lich.get(b.getLichKhamId());
            PhieuInfo p = phieu.get(b.getPhieuDatKhamId());
            HoSoInfo h = hoSo.get(b.getHoSoId());
            return new BenhAnDto.LichSu(b.getId(), b.getPhieuDatKhamId(), l == null ? null : l.ngay(),
                    l == null ? "" : tenBacSi.getOrDefault(l.bacSiId(), ""), l == null ? "" : tenKhoa.getOrDefault(l.khoaId(), ""),
                    b.getChanDoan(), b.getTrangThai(), b.getNgayTaiKham(), p != null && p.daDanhGia(), h == null ? "" : h.hoTen());
        }).toList();
    }
}
