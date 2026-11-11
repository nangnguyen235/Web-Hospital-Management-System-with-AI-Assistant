package vn.nhom6.his.hosobenhan.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom6.his.contracts.hosobenhan.DanhGiaDaTaoIntegrationEvent;
import vn.nhom6.his.contracts.lichkham.LichKhamContracts;
import vn.nhom6.his.contracts.lichkham.LichKhamContracts.PhieuInfo;
import vn.nhom6.his.contracts.nguoidung.NguoiDungContracts;
import vn.nhom6.his.contracts.nguoidung.NguoiDungContracts.HoSoInfo;
import vn.nhom6.his.hosobenhan.constant.TrangThaiBenhAn;
import vn.nhom6.his.hosobenhan.dto.BenhAnDto;
import vn.nhom6.his.hosobenhan.entity.BenhAn;
import vn.nhom6.his.hosobenhan.entity.DanhGia;
import vn.nhom6.his.hosobenhan.repository.BenhAnRepository;
import vn.nhom6.his.hosobenhan.repository.DanhGiaRepository;
import vn.nhom6.his.shared.kernel.CurrentUser;
import vn.nhom6.his.shared.kernel.Loi;

/** Bệnh nhân: lịch sử khám, bệnh án (FR-BN-11), đánh giá bác sĩ (FR-BN-14, BR-20). Chỉ hồ sơ thuộc tài khoản mình. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BenhNhanKhamService {

    private final BenhAnRepository benhAnRepository;
    private final DanhGiaRepository danhGiaRepository;
    private final NguoiDungContracts nguoiDung;
    private final LichKhamContracts lichKham;
    private final BenhAnTraCuu traCuu;
    private final ApplicationEventPublisher events;
    private final CurrentUser currentUser;

    /** Lịch sử đi theo bệnh nhân thật (BR-03): hồ sơ đã liên kết thì lấy theo benh_nhan_id. */
    public List<BenhAnDto.LichSu> lichSu(long hoSoId) {
        HoSoInfo h = hoSoCuaToi(hoSoId);
        List<BenhAn> ds = h.benhNhanId() != null
                ? benhAnRepository.findByBenhNhanIdAndDaXoaFalseOrderByIdDesc(h.benhNhanId())
                : benhAnRepository.findByHoSoIdAndDaXoaFalseOrderByIdDesc(hoSoId);
        return traCuu.lichSu(ds);
    }

    public BenhAnDto.ChiTiet chiTiet(long benhAnId) {
        return traCuu.chiTiet(benhAnCuaToi(benhAnId));
    }

    /** BR-20: phiếu của hồ sơ mình, bệnh án DA_KHAM, chưa đánh giá; điểm TB = trung bình mọi lần, làm tròn 1 chữ số. */
    @Transactional
    public void danhGia(long benhAnId, BenhAnDto.DanhGia r) {
        BenhAn b = benhAnCuaToi(benhAnId);
        if (b.getTrangThai() != TrangThaiBenhAn.DA_KHAM) {
            throw new Loi("CHUA_KHAM_XONG", "Chỉ đánh giá được lần khám đã hoàn tất.").ngoaiLe();
        }
        PhieuInfo p = lichKham.layPhieu(b.getPhieuDatKhamId()).orElseThrow();
        if (p.daDanhGia() || danhGiaRepository.existsByPhieuDatKhamId(p.id())) {
            throw Loi.xungDot("DA_DANH_GIA", "Lần khám này đã được đánh giá.").ngoaiLe();
        }
        long bacSiId = lichKham.layLich(b.getLichKhamId()).orElseThrow().bacSiId();
        DanhGia d = new DanhGia();
        d.setSoDiem(r.soDiem());
        d.setNhanXet(r.nhanXet());
        d.setBenhNhanId(b.getBenhNhanId());
        d.setBacSiId(bacSiId);
        d.setPhieuDatKhamId(p.id());
        danhGiaRepository.saveAndFlush(d);
        Double tb = danhGiaRepository.diemTrungBinh(bacSiId);
        events.publishEvent(new DanhGiaDaTaoIntegrationEvent(p.id(), bacSiId,
                BigDecimal.valueOf(tb == null ? r.soDiem() : tb).setScale(1, RoundingMode.HALF_UP)));
    }

    BenhAn benhAnCuaToi(long benhAnId) {
        BenhAn b = benhAnRepository.findById(benhAnId).filter(x -> !x.isDaXoa())
                .orElseThrow(() -> Loi.khongTimThay("bệnh án").ngoaiLe());
        hoSoCuaToi(b.getHoSoId());
        return b;
    }

    HoSoInfo hoSoCuaToi(long hoSoId) {
        return nguoiDung.layHoSo(hoSoId).filter(h -> !h.daXoa() && h.taiKhoanId() != null
                        && h.taiKhoanId() == currentUser.taiKhoanId())
                .orElseThrow(() -> Loi.khongTimThay("hồ sơ").ngoaiLe());
    }
}
