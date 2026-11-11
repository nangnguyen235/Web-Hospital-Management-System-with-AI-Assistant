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
import vn.nhom6.his.contracts.danhmuc.LoaiPhong;
import vn.nhom6.his.danhmuc.dto.DanhMucDto;
import vn.nhom6.his.danhmuc.entity.DichVu;
import vn.nhom6.his.danhmuc.entity.Khoa;
import vn.nhom6.his.danhmuc.entity.Phong;
import vn.nhom6.his.danhmuc.repository.DichVuRepository;
import vn.nhom6.his.danhmuc.repository.KhoaRepository;
import vn.nhom6.his.danhmuc.repository.PhongRepository;
import vn.nhom6.his.shared.kernel.Loi;
import vn.nhom6.his.shared.kernel.PageResponse;
import vn.nhom6.his.shared.kernel.PhanTrang;

/** Quản lý phòng (FR-AD-05, BR-04): phòng xét nghiệm PHẢI gắn một dịch vụ XET_NGHIEM cùng khoa. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhongService {

    private final PhongRepository phongRepository;
    private final KhoaRepository khoaRepository;
    private final DichVuRepository dichVuRepository;

    public List<DanhMucDto.Phong> danhSach(Integer khoaId, LoaiPhong loai) {
        return dto(phongRepository.danhSach(khoaId, loai));
    }

    public PageResponse<DanhMucDto.Phong> timKiem(String tuKhoa, Integer khoaId, Integer page, Integer pageSize) {
        PhanTrang pt = PhanTrang.cua(page, pageSize);
        Page<Phong> kq = phongRepository.timKiem(TienIch.trong(tuKhoa), khoaId,
                PageRequest.of(pt.chiSoTrang(), pt.pageSize(), Sort.by("daXoa", "tenPhong")));
        return new PageResponse<>(dto(kq.getContent()), kq.getTotalElements(), pt.page(), pt.pageSize());
    }

    @Transactional
    public DanhMucDto.Phong tao(DanhMucDto.LuuPhong r) {
        Phong p = new Phong();
        gan(p, r);
        phongRepository.save(p);
        return dto(List.of(p)).getFirst();
    }

    @Transactional
    public DanhMucDto.Phong capNhat(int id, DanhMucDto.LuuPhong r) {
        Phong p = tim(id);
        gan(p, r);
        return dto(List.of(p)).getFirst();
    }

    @Transactional
    public void doiTrangThai(int id, boolean daXoa) {
        tim(id).setDaXoa(daXoa);
    }

    private void gan(Phong p, DanhMucDto.LuuPhong r) {
        Khoa khoa = khoaRepository.findById(r.khoaId()).filter(k -> !k.isDaXoa())
                .orElseThrow(() -> new Loi("KHOA_KHONG_HOP_LE", "Khoa không tồn tại hoặc đã xóa.").ngoaiLe());
        String ten = r.tenPhong().trim();
        if (phongRepository.existsByTenPhongAndDaXoaFalseAndIdNot(ten, p.getId() == null ? -1 : p.getId())) {
            throw Loi.xungDot("TRUNG_TEN_PHONG", "Đã có phòng tên \"" + ten + "\".").ngoaiLe();
        }
        Integer dichVuId = null;
        if (r.loaiPhong() == LoaiPhong.PHONG_XET_NGHIEM) {
            DichVu dv = r.dichVuId() == null ? null : dichVuRepository.findById(r.dichVuId()).orElse(null);
            if (dv == null || dv.isDaXoa() || dv.getLoaiDichVu() != LoaiDichVu.XET_NGHIEM || !dv.getKhoaId().equals(khoa.getId())) {
                throw new Loi("DICH_VU_KHONG_HOP_LE",
                        "Phòng xét nghiệm phải gắn một dịch vụ xét nghiệm của cùng khoa.").ngoaiLe();
            }
            dichVuId = dv.getId();
        }
        p.setTenPhong(ten);
        p.setMoTa(TienIch.trong(r.moTa()));
        p.setKhoaId(khoa.getId());
        p.setLoaiPhong(r.loaiPhong());
        p.setDichVuId(dichVuId);
    }

    private Phong tim(int id) {
        return phongRepository.findById(id).orElseThrow(() -> Loi.khongTimThay("phòng").ngoaiLe());
    }

    private List<DanhMucDto.Phong> dto(List<Phong> ds) {
        Map<Integer, String> khoa = TienIch.tenKhoa(khoaRepository);
        return ds.stream().map(p -> new DanhMucDto.Phong(p.getId(), p.getTenPhong(), p.getMoTa(), p.getKhoaId(),
                khoa.getOrDefault(p.getKhoaId(), ""), p.getLoaiPhong(), p.getDichVuId(),
                p.getDichVuId() == null ? null : dichVuRepository.findById(p.getDichVuId()).map(DichVu::getTenDichVu).orElse(null),
                p.isDaXoa())).toList();
    }
}
