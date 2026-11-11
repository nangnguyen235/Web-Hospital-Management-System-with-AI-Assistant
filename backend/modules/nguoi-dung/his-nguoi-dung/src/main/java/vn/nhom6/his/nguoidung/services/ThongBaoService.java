package vn.nhom6.his.nguoidung.services;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom6.his.nguoidung.constant.TrangThaiThongBao;
import vn.nhom6.his.nguoidung.dto.QuanTriDto;
import vn.nhom6.his.nguoidung.entity.ThongBao;
import vn.nhom6.his.nguoidung.repository.ThongBaoRepository;
import vn.nhom6.his.shared.kernel.CurrentUser;
import vn.nhom6.his.shared.kernel.Loi;
import vn.nhom6.his.shared.kernel.PageResponse;
import vn.nhom6.his.shared.kernel.PhanTrang;

/** Thông báo in-app (FR-BN-16, BR-22): CHUA_NHAN → DA_NHAN (lấy về) → DA_XEM; xóa → DA_XOA. Không push. */
@Service
@RequiredArgsConstructor
@Transactional
public class ThongBaoService {

    private final ThongBaoRepository thongBaoRepository;
    private final CurrentUser currentUser;

    public PageResponse<QuanTriDto.ThongBao> cuaToi(Integer page, Integer pageSize) {
        long tk = currentUser.taiKhoanId();
        PhanTrang pt = PhanTrang.cua(page, pageSize);
        Page<ThongBao> kq = thongBaoRepository.findByTaiKhoanIdAndTrangThaiNot(tk, TrangThaiThongBao.DA_XOA,
                PageRequest.of(pt.chiSoTrang(), pt.pageSize(), Sort.by(Sort.Direction.DESC, "id")));
        List<QuanTriDto.ThongBao> ds = kq.getContent().stream().map(ThongBaoService::dto).toList();
        thongBaoRepository.danhDauDaNhan(tk);
        return new PageResponse<>(ds, kq.getTotalElements(), pt.page(), pt.pageSize());
    }

    @Transactional(readOnly = true)
    public long soChuaXem() {
        return thongBaoRepository.countByTaiKhoanIdAndTrangThaiIn(currentUser.taiKhoanId(),
                List.of(TrangThaiThongBao.CHUA_NHAN, TrangThaiThongBao.DA_NHAN));
    }

    public void daXem(long id) {
        cuaToi(id).setTrangThai(TrangThaiThongBao.DA_XEM);
    }

    public void xoa(long id) {
        ThongBao t = cuaToi(id);
        t.setTrangThai(TrangThaiThongBao.DA_XOA);
        t.setDaXoa(true);
    }

    /** Dùng cho sự kiện LuotKhamDaGoi (BR-15). */
    public void tao(long taiKhoanId, String tieuDe, String noiDung, String duLieu) {
        ThongBao t = new ThongBao();
        t.setTaiKhoanId(taiKhoanId);
        t.setTieuDe(tieuDe);
        t.setNoiDung(noiDung);
        t.setDuLieu(duLieu);
        thongBaoRepository.save(t);
    }

    private ThongBao cuaToi(long id) {
        ThongBao t = thongBaoRepository.findById(id).orElseThrow(() -> Loi.khongTimThay("thông báo").ngoaiLe());
        if (t.getTaiKhoanId() != currentUser.taiKhoanId()) {
            throw Loi.khongTimThay("thông báo").ngoaiLe();
        }
        return t;
    }

    static QuanTriDto.ThongBao dto(ThongBao t) {
        return new QuanTriDto.ThongBao(t.getId(), t.getTieuDe(), t.getNoiDung(), t.getDuLieu(), t.getTrangThai(),
                t.getNgayTao());
    }
}
