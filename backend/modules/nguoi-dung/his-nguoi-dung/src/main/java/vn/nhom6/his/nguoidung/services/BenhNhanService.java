package vn.nhom6.his.nguoidung.services;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom6.his.nguoidung.dto.HoSoDto;
import vn.nhom6.his.nguoidung.entity.BenhNhan;
import vn.nhom6.his.nguoidung.entity.HoSoBenhNhan;
import vn.nhom6.his.nguoidung.repository.BenhNhanRepository;
import vn.nhom6.his.nguoidung.repository.HoSoBenhNhanRepository;
import vn.nhom6.his.shared.kernel.Loi;
import vn.nhom6.his.shared.kernel.MaNgauNhien;

/**
 * Điều dưỡng: tra bệnh nhân theo CCCD (FR-DD-04), tra/sửa hồ sơ (FR-DD-06), tạo hồ sơ khách quầy không tài khoản
 * (FR-DD-08) và liên kết hồ sơ ↔ bệnh nhân bằng CCCD khi tiếp nhận (BR-13, BR-14). Chỉ thông tin hành chính.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BenhNhanService {

    private final BenhNhanRepository benhNhanRepository;
    private final HoSoBenhNhanRepository hoSoRepository;
    private final HoSoService hoSoService;

    public List<HoSoDto> traHoSo(String hoTen, String soCccd) {
        return hoSoRepository.traCuu(HoSoService.trong(hoTen), HoSoService.trong(soCccd),
                        PageRequest.of(0, 50, Sort.by("hoTen"))).getContent().stream().map(hoSoService::dto).toList();
    }

    /** Tra bệnh nhân theo CCCD: mã bệnh nhân, họ tên (FR-DD-04). */
    public BenhNhan traCccd(String soCccd) {
        return benhNhanRepository.findFirstBySoCccd(soCccd.trim())
                .orElseThrow(() -> Loi.khongTimThay("bệnh nhân có CCCD này").ngoaiLe());
    }

    /** Khách vãng lai: hồ sơ {@code tai_khoan_id = NULL}, liên kết luôn bệnh nhân theo CCCD (BR-14). */
    @Transactional
    public HoSoDto taoHoSoQuay(HoSoDto.Luu r) {
        if (r.soCccd() == null || r.soCccd().isBlank()) {
            throw new Loi("THIEU_CCCD", "Khách tại quầy phải có CCCD để đối chiếu.").ngoaiLe();
        }
        HoSoBenhNhan h = new HoSoBenhNhan();
        hoSoService.ganThongTin(h, r);
        hoSoRepository.save(h);
        lienKet(h.getId(), r.soCccd());
        return hoSoService.dto(h);
    }

    /** Điều dưỡng sửa hồ sơ khi tiếp nhận (FR-DD-06) — gồm cả bổ sung email để bệnh nhân tìm lại hồ sơ sau này. */
    @Transactional
    public HoSoDto suaHoSo(long hoSoId, HoSoDto.Luu r) {
        HoSoBenhNhan h = hoSoRepository.findByIdAndDaXoaFalse(hoSoId).orElseThrow(() -> Loi.khongTimThay("hồ sơ").ngoaiLe());
        hoSoService.ganThongTin(h, r);
        return hoSoService.dto(h);
    }

    @Transactional
    public HoSoDto lienKetVaTra(long hoSoId, String soCccd) {
        lienKet(hoSoId, soCccd);
        return hoSoService.dto(hoSoRepository.findById(hoSoId).orElseThrow());
    }

    @Transactional
    public BenhNhan lienKet(long hoSoId, String soCccd) {
        HoSoBenhNhan h = hoSoRepository.findByIdAndDaXoaFalse(hoSoId).orElseThrow(() -> Loi.khongTimThay("hồ sơ").ngoaiLe());
        if (h.getBenhNhanId() != null) {
            return benhNhanRepository.findById(h.getBenhNhanId()).orElseThrow();
        }
        String cccd = soCccd == null ? null : soCccd.trim();
        if (cccd == null || !cccd.matches("\\d{12}")) {
            throw new Loi("CCCD_KHONG_HOP_LE", "Vui lòng nhập CCCD gồm 12 chữ số để tiếp nhận.").ngoaiLe();
        }
        BenhNhan b = benhNhanRepository.findFirstBySoCccd(cccd).orElseGet(() -> {
            BenhNhan moi = new BenhNhan();
            String ma;
            do {
                ma = "BN" + MaNgauNhien.hex(8);
            } while (benhNhanRepository.existsByMaBenhNhan(ma));
            moi.setMaBenhNhan(ma);
            moi.setHoTen(h.getHoTen());
            moi.setSoDienThoai(h.getSoDienThoai());
            moi.setSoCccd(cccd);
            moi.setNgaySinh(h.getNgaySinh());
            moi.setGioiTinh(h.getGioiTinh());
            return benhNhanRepository.save(moi);
        });
        h.setBenhNhanId(b.getId());
        h.setSoCccd(cccd);
        return b;
    }
}
