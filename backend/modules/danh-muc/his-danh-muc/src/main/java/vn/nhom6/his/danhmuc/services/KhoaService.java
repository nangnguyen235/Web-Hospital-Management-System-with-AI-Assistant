package vn.nhom6.his.danhmuc.services;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nhom6.his.danhmuc.dto.KhoaDto;
import vn.nhom6.his.danhmuc.dto.LuuKhoaRequest;
import vn.nhom6.his.danhmuc.entity.Khoa;
import vn.nhom6.his.danhmuc.repository.KhoaRepository;
import vn.nhom6.his.shared.kernel.Loi;
import vn.nhom6.his.shared.kernel.PageResponse;
import vn.nhom6.his.shared.kernel.PhanTrang;

/**
 * LÁT MẪU cho cả nhóm chép theo: Admin quản lý khoa (FR-AD-04, BR-04) + danh sách công khai (FR-BN-07).
 * Controller gọi service → service kiểm nghiệp vụ, lỗi ném {@code LoiNghiepVuException} → GlobalExceptionHandler.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KhoaService {

    private final KhoaRepository khoaRepository;

    public List<KhoaDto> danhSachCongKhai() {
        return khoaRepository.findByDaXoaFalseOrderByTenKhoaAsc().stream().map(KhoaDto::tu).toList();
    }

    /** Admin: tìm theo tên, gồm cả khoa đã xóa (để khôi phục), phân trang (NFR-01). */
    public PageResponse<KhoaDto> timKiem(String tuKhoa, Integer page, Integer pageSize) {
        PhanTrang pt = PhanTrang.cua(page, pageSize);
        PageRequest pageable = PageRequest.of(pt.chiSoTrang(), pt.pageSize(), Sort.by("daXoa", "tenKhoa"));
        Page<Khoa> kq = tuKhoa == null || tuKhoa.isBlank()
                ? khoaRepository.findAll(pageable)
                : khoaRepository.findByTenKhoaContaining(tuKhoa.trim(), pageable);
        return new PageResponse<>(kq.map(KhoaDto::tu).getContent(), kq.getTotalElements(), pt.page(), pt.pageSize());
    }

    @Transactional
    public KhoaDto tao(LuuKhoaRequest request) {
        String ten = request.tenKhoa().trim();
        if (khoaRepository.existsByTenKhoaAndDaXoaFalse(ten)) {
            throw trungTen(ten);
        }
        Khoa khoa = khoaRepository.save(new Khoa(ten, request.moTa(), request.tuKhoaTrieuChung()));
        return KhoaDto.tu(khoa);
    }

    @Transactional
    public KhoaDto capNhat(int id, LuuKhoaRequest request) {
        Khoa khoa = tim(id);
        String ten = request.tenKhoa().trim();
        if (khoaRepository.existsByTenKhoaAndDaXoaFalseAndIdNot(ten, id)) {
            throw trungTen(ten);
        }
        khoa.capNhat(ten, request.moTa(), request.tuKhoaTrieuChung());
        return KhoaDto.tu(khoa);
    }

    /** Xóa mềm: khoa không hiện khi chọn mới, dữ liệu cũ vẫn hiện đúng tên (BR-04). */
    @Transactional
    public void xoa(int id) {
        tim(id).setDaXoa(true);
    }

    @Transactional
    public void khoiPhuc(int id) {
        tim(id).setDaXoa(false);
    }

    private Khoa tim(int id) {
        return khoaRepository.findById(id).orElseThrow(() -> Loi.khongTimThay("khoa").ngoaiLe());
    }

    private static RuntimeException trungTen(String ten) {
        return Loi.xungDot("TRUNG_TEN_KHOA", "Đã có khoa tên \"" + ten + "\".").ngoaiLe();
    }
}
