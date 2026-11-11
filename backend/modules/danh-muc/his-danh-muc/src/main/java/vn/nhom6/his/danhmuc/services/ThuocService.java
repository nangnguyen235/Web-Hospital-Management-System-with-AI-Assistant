package vn.nhom6.his.danhmuc.services;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom6.his.danhmuc.dto.DanhMucDto;
import vn.nhom6.his.danhmuc.entity.Thuoc;
import vn.nhom6.his.danhmuc.repository.ThuocRepository;
import vn.nhom6.his.shared.kernel.Loi;
import vn.nhom6.his.shared.kernel.PageResponse;
import vn.nhom6.his.shared.kernel.PhanTrang;

/** Quản lý thuốc (FR-AD-07, BR-04) + danh sách để bác sĩ chọn khi kê đơn (BR-18). */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ThuocService {

    private final ThuocRepository thuocRepository;

    /** Bác sĩ chọn thuốc: chỉ thuốc chưa xóa, tối đa 50 dòng. */
    public List<DanhMucDto.Thuoc> chon(String tuKhoa) {
        return thuocRepository.chon(TienIch.trong(tuKhoa), PageRequest.of(0, 50)).stream().map(ThuocService::dto).toList();
    }

    public PageResponse<DanhMucDto.Thuoc> timKiem(String tuKhoa, Integer page, Integer pageSize) {
        PhanTrang pt = PhanTrang.cua(page, pageSize);
        Page<Thuoc> kq = thuocRepository.timKiem(TienIch.trong(tuKhoa),
                PageRequest.of(pt.chiSoTrang(), pt.pageSize(), Sort.by("daXoa", "tenThuoc")));
        return new PageResponse<>(kq.map(ThuocService::dto).getContent(), kq.getTotalElements(), pt.page(), pt.pageSize());
    }

    @Transactional
    public DanhMucDto.Thuoc tao(DanhMucDto.LuuThuoc r) {
        Thuoc t = new Thuoc();
        gan(t, r);
        return dto(thuocRepository.save(t));
    }

    @Transactional
    public DanhMucDto.Thuoc capNhat(int id, DanhMucDto.LuuThuoc r) {
        Thuoc t = tim(id);
        gan(t, r);
        return dto(t);
    }

    @Transactional
    public void doiTrangThai(int id, boolean daXoa) {
        tim(id).setDaXoa(daXoa);
    }

    private void gan(Thuoc t, DanhMucDto.LuuThuoc r) {
        String ten = r.tenThuoc().trim();
        if (thuocRepository.existsByTenThuocAndDaXoaFalseAndIdNot(ten, t.getId() == null ? -1 : t.getId())) {
            throw Loi.xungDot("TRUNG_TEN_THUOC", "Đã có thuốc tên \"" + ten + "\".").ngoaiLe();
        }
        t.setTenThuoc(ten);
        t.setGia(r.gia());
        t.setSoLuong(r.soLuong());
        t.setDonVi(r.donVi());
    }

    private Thuoc tim(int id) {
        return thuocRepository.findById(id).orElseThrow(() -> Loi.khongTimThay("thuốc").ngoaiLe());
    }

    static DanhMucDto.Thuoc dto(Thuoc t) {
        return new DanhMucDto.Thuoc(t.getId(), t.getTenThuoc(), t.getGia(), t.getSoLuong(), t.getDonVi(), t.isDaXoa());
    }
}
