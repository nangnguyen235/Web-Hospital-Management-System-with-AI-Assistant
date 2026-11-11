package vn.nhom6.his.hosobenhan.services;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom6.his.contracts.hosobenhan.BenhAnDaTaoIntegrationEvent;
import vn.nhom6.his.contracts.lichkham.LichKhamContracts;
import vn.nhom6.his.contracts.lichkham.LichKhamContracts.PhieuInfo;
import vn.nhom6.his.contracts.nguoidung.NguoiDungContracts;
import vn.nhom6.his.contracts.nguoidung.NguoiDungContracts.HoSoInfo;
import vn.nhom6.his.hosobenhan.dto.BenhAnDto;
import vn.nhom6.his.hosobenhan.entity.BenhAn;
import vn.nhom6.his.hosobenhan.repository.BenhAnRepository;
import vn.nhom6.his.shared.kernel.Loi;

/**
 * Tiếp nhận khách đặt trước (FR-DD-03…05, BR-13): kiểm phiếu qua LichKham, liên kết bệnh nhân bằng CCCD qua NguoiDung,
 * tạo bệnh án CHO_KHAM, phát BenhAnDaTao. Điều dưỡng chỉ thấy thông tin hành chính.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TiepNhanService {

    private final BenhAnRepository benhAnRepository;
    private final LichKhamContracts lichKham;
    private final NguoiDungContracts nguoiDung;
    private final ApplicationEventPublisher events;

    @Transactional(readOnly = true)
    public BenhAnDto.PhieuTiepNhan kiemTra(String maPhieu, int phongId) {
        PhieuInfo p = lichKham.kiemTraPhieuTiepNhan(maPhieu, phongId);
        HoSoInfo h = nguoiDung.layHoSo(p.hoSoId()).orElseThrow(() -> Loi.khongTimThay("hồ sơ").ngoaiLe());
        String ma = h.benhNhanId() == null ? null : nguoiDung.maBenhNhan(List.of(h.benhNhanId())).get(h.benhNhanId());
        return new BenhAnDto.PhieuTiepNhan(p.id(), p.maPhieu(), p.soThuTu(), h.id(), h.hoTen(), h.ngaySinh(), h.gioiTinh(),
                h.soCccd(), h.soDienThoai(), h.benhNhanId() != null, ma, p.benhAnId());
    }

    public BenhAnDto.KetQuaTiepNhan tiepNhan(BenhAnDto.TiepNhan r) {
        PhieuInfo p = lichKham.kiemTraPhieuTiepNhan(r.maPhieu(), r.phongId());
        HoSoInfo h = nguoiDung.layHoSo(p.hoSoId()).orElseThrow(() -> Loi.khongTimThay("hồ sơ").ngoaiLe());
        long benhNhanId = h.benhNhanId() != null ? h.benhNhanId() : nguoiDung.lienKetBenhNhan(h.id(), r.soCccd());
        long benhAnId = taoBenhAn(p.id(), p.lichKhamId(), benhNhanId, h.id());
        return new BenhAnDto.KetQuaTiepNhan(benhAnId, nguoiDung.maBenhNhan(List.of(benhNhanId)).get(benhNhanId), p.soThuTu(),
                h.hoTen());
    }

    /** Phiếu đã có bệnh án thì trả bệnh án cũ, không tạo trùng (BR-13). */
    long taoBenhAn(long phieuId, long lichKhamId, long benhNhanId, long hoSoId) {
        return benhAnRepository.findByPhieuDatKhamId(phieuId).map(BenhAn::getId).orElseGet(() -> {
            BenhAn b = new BenhAn();
            b.setPhieuDatKhamId(phieuId);
            b.setLichKhamId(lichKhamId);
            b.setBenhNhanId(benhNhanId);
            b.setHoSoId(hoSoId);
            benhAnRepository.save(b);
            events.publishEvent(new BenhAnDaTaoIntegrationEvent(b.getId(), phieuId));
            return b.getId();
        });
    }
}
