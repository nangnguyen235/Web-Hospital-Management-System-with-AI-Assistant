package vn.nhom6.his.lichkham.services;

import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom6.his.contracts.danhmuc.DanhMucContracts;
import vn.nhom6.his.contracts.danhmuc.PhongInfo;
import vn.nhom6.his.lichkham.dto.SoDangGoiDto;
import vn.nhom6.his.lichkham.entity.LichKhamBacSi;
import vn.nhom6.his.lichkham.repository.LichKhamBacSiRepository;
import vn.nhom6.his.shared.kernel.Loi;

/** Màn hình số đang gọi công khai (FR-BN-10, BR-15). Hỏi DanhMuc tên phòng/khoa qua Contracts. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SoDangGoiService {

    public static final int SO_MOI_LAN_GOI = 5;

    private final LichKhamBacSiRepository lichKhamRepository;
    private final DanhMucContracts danhMuc;

    public SoDangGoiDto lay(long lichKhamId) {
        LichKhamBacSi lich = lichKhamRepository.findByIdAndDaXoaFalse(lichKhamId)
                .orElseThrow(() -> Loi.khongTimThay("lịch khám").ngoaiLe());
        PhongInfo phong = danhMuc.layPhong(lich.getPhongId()).orElse(null);
        return new SoDangGoiDto(lich.getId(), lich.getSoDangGoi(), cacSoVuaGoi(lich.getSoDangGoi()),
                phong == null ? "" : phong.tenPhong(), phong == null ? "" : phong.tenKhoa(), lich.getNgay(), lich.getBuoi());
    }

    /** 5 số cuối cùng đã gọi (tới {@code soDangGoi}). */
    static List<Integer> cacSoVuaGoi(int soDangGoi) {
        if (soDangGoi <= 0) {
            return List.of();
        }
        int tu = Math.max(1, soDangGoi - SO_MOI_LAN_GOI + 1);
        return IntStream.rangeClosed(tu, soDangGoi).boxed().toList();
    }
}
