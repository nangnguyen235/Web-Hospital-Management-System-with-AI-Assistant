package vn.nhom6.his.hosobenhan.services;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.nhom6.his.contracts.danhmuc.DanhMucContracts;
import vn.nhom6.his.contracts.nguoidung.NguoiDungContracts;
import vn.nhom6.his.contracts.nguoidung.NguoiDungContracts.HoSoInfo;
import vn.nhom6.his.contracts.vienphi.GiaoDichDaXuLyIntegrationEvent;
import vn.nhom6.his.contracts.vienphi.LoaiThanhToan;
import vn.nhom6.his.contracts.vienphi.VienPhiContracts;
import vn.nhom6.his.hosobenhan.constant.TrangThaiXetNghiem;
import vn.nhom6.his.hosobenhan.dto.BenhAnDto;
import vn.nhom6.his.hosobenhan.entity.BenhAn;
import vn.nhom6.his.hosobenhan.entity.XetNghiem;
import vn.nhom6.his.hosobenhan.repository.BenhAnRepository;
import vn.nhom6.his.hosobenhan.repository.XetNghiemRepository;
import vn.nhom6.his.shared.kernel.CurrentUser;
import vn.nhom6.his.shared.kernel.IStorageService;
import vn.nhom6.his.shared.kernel.Loi;
import vn.nhom6.his.shared.kernel.VaiTro;

/**
 * Xét nghiệm (BR-17): điều dưỡng phòng xét nghiệm xem danh sách hôm nay, gọi tiếp, nhận (chỉ khi đã thanh toán), hoàn tất
 * kèm file lên R2; bệnh nhân tra theo mã, thanh toán VNPAY (phí 1.500), xem kết quả qua Presigned URL.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class XetNghiemService {

    static final BigDecimal PHI_XET_NGHIEM = BigDecimal.valueOf(1_500); // như TheDuck
    private static final Map<String, String> DINH_DANG = Map.of("application/pdf", "pdf", "image/jpeg", "jpg", "image/png", "png");
    private static final long FILE_TOI_DA = 10L * 1024 * 1024; // NFR-04

    private final XetNghiemRepository xetNghiemRepository;
    private final BenhAnRepository benhAnRepository;
    private final DanhMucContracts danhMuc;
    private final NguoiDungContracts nguoiDung;
    private final VienPhiContracts vienPhi;
    private final IStorageService storage;
    private final BenhAnTraCuu traCuu;
    private final CurrentUser currentUser;

    /** FR-DD-09: xét nghiệm hôm nay của phòng, sắp theo số (điều dưỡng chỉ thấy họ tên + dịch vụ). */
    @Transactional(readOnly = true)
    public List<BenhAnDto.XetNghiem> homNayCuaPhong(int phongId, TrangThaiXetNghiem trangThai) {
        List<XetNghiem> ds = xetNghiemRepository.findByPhongIdAndNgayAndDaXoaFalseOrderBySoThuTuAsc(phongId, traCuu.homNay())
                .stream().filter(x -> trangThai == null || x.getTrangThai() == trangThai).toList();
        return traCuu.xetNghiem(ds, hoTen(ds));
    }

    @Transactional(readOnly = true)
    public BenhAnDto.SoPhongXetNghiem soCuaPhong(int phongId) {
        DanhMucContracts.SoXetNghiem s = danhMuc.soXetNghiem(phongId);
        return new BenhAnDto.SoPhongXetNghiem(s.phongId(), s.tenPhong(), s.soDangGoi(), s.soLonNhat());
    }

    /** FR-DD-10. */
    public BenhAnDto.SoPhongXetNghiem goiTiep(int phongId) {
        DanhMucContracts.SoXetNghiem s = danhMuc.goiTiepXetNghiem(phongId);
        return new BenhAnDto.SoPhongXetNghiem(s.phongId(), s.tenPhong(), s.soDangGoi(), s.soLonNhat());
    }

    /** FR-DD-11: nhận (CHO_THUC_HIEN → DANG_THUC_HIEN) chỉ khi đã thanh toán. */
    public BenhAnDto.XetNghiem nhan(long id) {
        XetNghiem x = tim(id);
        if (!x.isDaThanhToan()) {
            throw new Loi("CHUA_THANH_TOAN", "Xét nghiệm chưa thanh toán.").ngoaiLe();
        }
        if (x.getTrangThai() != TrangThaiXetNghiem.CHO_THUC_HIEN) {
            throw new Loi("SAI_TRANG_THAI", "Xét nghiệm không ở trạng thái chờ thực hiện.").ngoaiLe();
        }
        x.setTrangThai(TrangThaiXetNghiem.DANG_THUC_HIEN);
        x.setDieuDuongId(currentUser.nhanVienId());
        return traCuu.xetNghiem(List.of(x), hoTen(List.of(x))).getFirst();
    }

    /** FR-DD-11: nhập kết quả và/hoặc file (pdf/jpg/png ≤ 10 MB lên R2) → HOAN_THANH. */
    public BenhAnDto.XetNghiem hoanTat(long id, String ketQua, MultipartFile file) {
        XetNghiem x = tim(id);
        if (x.getTrangThai() != TrangThaiXetNghiem.DANG_THUC_HIEN) {
            throw new Loi("SAI_TRANG_THAI", "Hãy nhận xét nghiệm trước khi hoàn tất.").ngoaiLe();
        }
        boolean coFile = file != null && !file.isEmpty();
        if ((ketQua == null || ketQua.isBlank()) && !coFile) {
            throw new Loi("THIEU_KET_QUA", "Vui lòng nhập kết quả hoặc tải file kết quả.").ngoaiLe();
        }
        if (coFile) {
            String duoi = DINH_DANG.get(file.getContentType());
            if (duoi == null) {
                throw new Loi("SAI_DINH_DANG", "File kết quả phải là pdf, jpg hoặc png.").ngoaiLe();
            }
            if (file.getSize() > FILE_TOI_DA) {
                throw new Loi("QUA_KICH_THUOC", "File kết quả tối đa 10 MB.").ngoaiLe();
            }
            String key = "xet-nghiem/" + x.getId() + "/" + UUID.randomUUID().toString().replace("-", "") + "." + duoi;
            try (InputStream in = file.getInputStream()) {
                storage.upload(in, file.getSize(), key, file.getContentType());
            } catch (IOException e) {
                throw new Loi("TAI_LEN_THAT_BAI", "Không đọc được file tải lên.").ngoaiLe();
            }
            x.setFileKetQuaKey(key);
        }
        x.setKetQua(ketQua);
        x.setTrangThai(TrangThaiXetNghiem.HOAN_THANH);
        return traCuu.xetNghiem(List.of(x), hoTen(List.of(x))).getFirst();
    }

    /** FR-BN-12: tra theo mã (bệnh nhân, điều dưỡng quầy). */
    @Transactional(readOnly = true)
    public BenhAnDto.XetNghiem traMa(String ma) {
        XetNghiem x = xetNghiemRepository.findByMaXetNghiemAndDaXoaFalse(ma == null ? "" : ma.trim().toUpperCase())
                .orElseThrow(() -> Loi.khongTimThay("xét nghiệm có mã này").ngoaiLe());
        return traCuu.xetNghiem(List.of(x), hoTen(List.of(x))).getFirst();
    }

    /** FR-BN-12, BR-14: giao dịch XET_NGHIEM 1-1, phí 1.500; điều dưỡng thanh toán hộ khách quầy (QR). */
    public BenhAnDto.ThanhToan thanhToan(String ma, String nguon, String ip) {
        XetNghiem x = xetNghiemRepository.findByMaXetNghiemAndDaXoaFalse(ma == null ? "" : ma.trim().toUpperCase())
                .orElseThrow(() -> Loi.khongTimThay("xét nghiệm có mã này").ngoaiLe());
        if (x.isDaThanhToan()) {
            throw new Loi("DA_THANH_TOAN", "Xét nghiệm đã được thanh toán.").ngoaiLe();
        }
        if (x.getGiaoDichId() != null) {
            var gd = vienPhi.layGiaoDich(x.getGiaoDichId()).orElse(null);
            if (gd != null && "DANG_XU_LY".equals(gd.trangThai())
                    && gd.ngayTao().isAfter(traCuu.bayGio().minusMinutes(14))) {
                return new BenhAnDto.ThanhToan(x.getId(), gd.id(), vienPhi.urlThanhToan(gd.id(), ip), gd.soTien().add(gd.phi()));
            }
        }
        Long taiKhoanId = currentUser.vaiTro() == VaiTro.BENH_NHAN ? currentUser.taiKhoanId() : null;
        VienPhiContracts.GiaoDichMoi gd = vienPhi.taoGiaoDich(new VienPhiContracts.TaoGiaoDich(
                LoaiThanhToan.XET_NGHIEM, x.getGia(), PHI_XET_NGHIEM, taiKhoanId, nguon, ip));
        x.setGiaoDichId(gd.giaoDichId());
        return new BenhAnDto.ThanhToan(x.getId(), gd.giaoDichId(), gd.urlThanhToan(), x.getGia().add(PHI_XET_NGHIEM));
    }

    /** FR-BN-13: Presigned URL 15 phút — chỉ chủ hồ sơ của bệnh án, bác sĩ, điều dưỡng (NFR-04). */
    @Transactional(readOnly = true)
    public String urlKetQua(long id) {
        XetNghiem x = tim(id);
        if (x.getFileKetQuaKey() == null) {
            throw Loi.khongTimThay("file kết quả").ngoaiLe();
        }
        if (currentUser.vaiTro() == VaiTro.BENH_NHAN) {
            BenhAn b = benhAnRepository.findById(x.getBenhAnId()).orElseThrow();
            boolean cuaToi = nguoiDung.layHoSo(b.getHoSoId()).map(HoSoInfo::taiKhoanId)
                    .filter(tk -> tk == currentUser.taiKhoanId()).isPresent();
            if (!cuaToi) {
                throw Loi.khongTimThay("file kết quả").ngoaiLe();
            }
        }
        return storage.presignedUrl(x.getFileKetQuaKey());
    }

    /** Giao dịch XET_NGHIEM thành công → {@code da_thanh_toan = true} (nhận trùng không đổi gì). */
    @ApplicationModuleListener
    void giaoDichDaXuLy(GiaoDichDaXuLyIntegrationEvent e) {
        if (e.loaiThanhToan() == LoaiThanhToan.XET_NGHIEM && e.thanhCong()) {
            xetNghiemRepository.findByGiaoDichId(e.giaoDichId()).ifPresent(x -> x.setDaThanhToan(true));
        }
    }

    private XetNghiem tim(long id) {
        return xetNghiemRepository.findById(id).filter(x -> !x.isDaXoa()).orElseThrow(() -> Loi.khongTimThay("xét nghiệm").ngoaiLe());
    }

    private Map<Long, String> hoTen(List<XetNghiem> ds) {
        List<BenhAn> ba = benhAnRepository.findAllById(ds.stream().map(XetNghiem::getBenhAnId).distinct().toList());
        Map<Long, HoSoInfo> hs = nguoiDung.layCacHoSo(ba.stream().map(BenhAn::getHoSoId).distinct().toList());
        return ba.stream().collect(Collectors.toMap(BenhAn::getId,
                b -> hs.containsKey(b.getHoSoId()) ? hs.get(b.getHoSoId()).hoTen() : ""));
    }
}
