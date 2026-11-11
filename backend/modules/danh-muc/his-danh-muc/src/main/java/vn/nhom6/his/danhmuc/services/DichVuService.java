package vn.nhom6.his.danhmuc.services;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom6.his.contracts.danhmuc.LoaiDichVu;
import vn.nhom6.his.danhmuc.dto.DanhMucDto;
import vn.nhom6.his.danhmuc.entity.DichVu;
import vn.nhom6.his.danhmuc.repository.DichVuRepository;
import vn.nhom6.his.danhmuc.repository.KhoaRepository;
import vn.nhom6.his.shared.kernel.Loi;
import vn.nhom6.his.shared.kernel.PageResponse;
import vn.nhom6.his.shared.kernel.PhanTrang;

/** Quản lý dịch vụ (FR-AD-06, BR-04): loại, khoa, giá. Giá khám nằm ở dịch vụ. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DichVuService {

    private final DichVuRepository dichVuRepository;
    private final KhoaRepository khoaRepository;

    public List<DanhMucDto.DichVu> danhSach(Integer khoaId, LoaiDichVu loai) {
        return dto(dichVuRepository.danhSach(khoaId, loai));
    }

    public PageResponse<DanhMucDto.DichVu> timKiem(String tuKhoa, Integer khoaId, Integer page, Integer pageSize) {
        PhanTrang pt = PhanTrang.cua(page, pageSize);
        Page<DichVu> kq = dichVuRepository.timKiem(TienIch.trong(tuKhoa), khoaId,
                PageRequest.of(pt.chiSoTrang(), pt.pageSize(), Sort.by("daXoa", "tenDichVu")));
        return new PageResponse<>(dto(kq.getContent()), kq.getTotalElements(), pt.page(), pt.pageSize());
    }

    @Transactional
    public DanhMucDto.DichVu tao(DanhMucDto.LuuDichVu r) {
        DichVu d = new DichVu();
        gan(d, r);
        dichVuRepository.save(d);
        return dto(List.of(d)).getFirst();
    }

    @Transactional
    public DanhMucDto.DichVu capNhat(int id, DanhMucDto.LuuDichVu r) {
        DichVu d = tim(id);
        gan(d, r);
        return dto(List.of(d)).getFirst();
    }

    @Transactional
    public void doiTrangThai(int id, boolean daXoa) {
        tim(id).setDaXoa(daXoa);
    }

    private void gan(DichVu d, DanhMucDto.LuuDichVu r) {
        if (khoaRepository.findById(r.khoaId()).filter(k -> !k.isDaXoa()).isEmpty()) {
            throw new Loi("KHOA_KHONG_HOP_LE", "Khoa không tồn tại hoặc đã xóa.").ngoaiLe();
        }
        d.setTenDichVu(r.tenDichVu().trim());
        d.setMoTa(TienIch.trong(r.moTa()));
        d.setKhoaId(r.khoaId());
        d.setLoaiDichVu(r.loaiDichVu());
        d.setGia(r.gia());
    }

    private DichVu tim(int id) {
        return dichVuRepository.findById(id).orElseThrow(() -> Loi.khongTimThay("dịch vụ").ngoaiLe());
    }

    private List<DanhMucDto.DichVu> dto(List<DichVu> ds) {
        Map<Integer, String> khoa = TienIch.tenKhoa(khoaRepository);
        return ds.stream().map(d -> new DanhMucDto.DichVu(d.getId(), d.getTenDichVu(), d.getMoTa(), d.getKhoaId(),
                khoa.getOrDefault(d.getKhoaId(), ""), d.getLoaiDichVu(), d.getGia(), d.isDaXoa())).toList();
    }
}
