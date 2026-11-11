package vn.nhom6.his;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import vn.nhom6.his.shared.kernel.IEmailService;
import vn.nhom6.his.shared.kernel.ThoiGian;

/**
 * Hành trình đặt trước và khách quầy (logic-nghiep-vu.md mục 3.1, 3.2) qua HTTP với MySQL thật, đồng hồ giả lập
 * (06:30 sáng) và IPN VNPAY mô phỏng có chữ ký HMAC-SHA512 — gồm các ca biên ở mục 5 của rule.
 */
@SpringBootTest(properties = {
        "his.jwt.key=khoa-test-chi-dung-trong-test-tich-hop-0123456789",
        "his.seed.admin.email=admin@thehos.test",
        "his.seed.admin.so-dien-thoai=0900000000",
        "his.seed.admin.mat-khau=MatKhauTest@123",
        "his.vnpay.tmn-code=THUNGHIEM",
        "his.vnpay.hash-secret=" + HanhTrinhKhamTests.BI_MAT,
})
@AutoConfigureMockMvc
@Import({TestcontainersConfig.class, HanhTrinhKhamTests.DongHoConfig.class})
class HanhTrinhKhamTests {

    static final String BI_MAT = "BIMATVNPAYCHITHUNGHIEM0123456789";
    private static final AtomicInteger SO = new AtomicInteger(500);

    /** Đồng hồ chỉnh được — mọi service tiêm Clock và callback JPA (ThoiGian) cùng dùng. */
    static final class DongHo extends Clock {
        volatile Instant hienTai;

        @Override
        public ZoneId getZone() {
            return ThoiGian.MUI_GIO;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return hienTai;
        }

        void dat(LocalDateTime t) {
            hienTai = t.atZone(ThoiGian.MUI_GIO).toInstant();
        }

        void cong(Duration d) {
            hienTai = hienTai.plus(d);
        }
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class DongHoConfig {
        @Bean
        @Primary
        DongHo dongHoThu() {
            return new DongHo();
        }
    }

    @Autowired
    MockMvc mvc;

    @Autowired
    DongHo dongHo;

    @MockitoBean
    IEmailService email;

    LocalDate ngay;

    @BeforeEach
    void datDongHo() {
        LocalDateTime that = LocalDateTime.now(ThoiGian.MUI_GIO);
        // 06:30 sáng gần nhất ở tương lai: token JWT ký theo đồng hồ này vẫn còn hạn theo giờ thật
        ngay = that.toLocalTime().isBefore(java.time.LocalTime.of(6, 0)) ? that.toLocalDate() : that.toLocalDate().plusDays(1);
        dongHo.dat(ngay.atTime(6, 30));
        ThoiGian.dat(dongHo);
    }

    // ================= tiện ích =================
    private ResultActions json(String method, String url, String token, String body) throws Exception {
        var req = (method.equals("PUT") ? org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(url)
                : post(url)).contentType(MediaType.APPLICATION_JSON).content(body == null ? "" : body);
        if (token != null) {
            req.header("Authorization", "Bearer " + token);
        }
        return mvc.perform(req);
    }

    private ResultActions lay(String url, String token) throws Exception {
        var req = get(url);
        if (token != null) {
            req.header("Authorization", "Bearer " + token);
        }
        return mvc.perform(req);
    }

    private static <T> T doc(ResultActions r, String path) throws Exception {
        return JsonPath.read(r.andReturn().getResponse().getContentAsString(), path);
    }

    private String dangNhap(String ten, String matKhau) throws Exception {
        return doc(json("POST", "/api/nguoi-dung/dang-nhap", null,
                "{\"tenDangNhap\":\"" + ten + "\",\"matKhau\":\"" + matKhau + "\"}").andExpect(status().isOk()), "$.accessToken");
    }

    private String otpCuoi(String den) {
        ArgumentCaptor<String> noiDung = ArgumentCaptor.forClass(String.class);
        verify(email, atLeastOnce()).gui(eq(den), anyString(), noiDung.capture());
        Matcher m = Pattern.compile(">(\\d{6})<").matcher(noiDung.getValue());
        assertThat(m.find()).isTrue();
        return m.group(1);
    }

    /** Bệnh nhân mới + một hồ sơ; trả {token, hoSoId}. */
    private Object[] benhNhanMoi() throws Exception {
        int n = SO.incrementAndGet();
        String mail = "bn" + n + "@thehos.test";
        String sdt = "098" + String.format("%07d", n);
        json("POST", "/api/nguoi-dung/dang-ky/gui-otp", null, "{\"email\":\"" + mail + "\",\"soDienThoai\":\"" + sdt + "\"}")
                .andExpect(status().isNoContent());
        String token = doc(json("POST", "/api/nguoi-dung/dang-ky", null, """
                {"email":"%s","soDienThoai":"%s","hoTen":"Bệnh Nhân %d","matKhau":"matkhau1","otp":"%s"}
                """.formatted(mail, sdt, n, otpCuoi(mail))).andExpect(status().isOk()), "$.accessToken");
        int hoSo = doc(json("POST", "/api/nguoi-dung/ho-so", token, """
                {"hoTen":"Người Bệnh %d","gioiTinh":"NU","ngaySinh":"1985-03-02","email":"%s"}
                """.formatted(n, mail)).andExpect(status().isOk()), "$.id");
        return new Object[]{token, hoSo};
    }

    private ResultActions datKham(String token, int hoSoId, int khungId) throws Exception {
        return json("POST", "/api/lich-kham/dat-kham", token, "{\"hoSoId\":%d,\"khungGioIds\":[%d]}".formatted(hoSoId, khungId));
    }

    /** IPN VNPAY mô phỏng, ký bằng cùng bí mật. */
    private ResultActions ipn(long giaoDichId, long tongTien, boolean thanhCong) throws Exception {
        Map<String, String> p = new TreeMap<>();
        p.put("vnp_TmnCode", "THUNGHIEM");
        p.put("vnp_TxnRef", String.valueOf(giaoDichId));
        p.put("vnp_Amount", String.valueOf(tongTien * 100));
        p.put("vnp_ResponseCode", thanhCong ? "00" : "24");
        p.put("vnp_TransactionStatus", thanhCong ? "00" : "02");
        p.put("vnp_BankCode", "NCB");
        p.put("vnp_OrderInfo", "Thanh toan giao dich " + giaoDichId);
        StringJoiner sj = new StringJoiner("&");
        p.forEach((k, v) -> sj.add(k + "=" + URLEncoder.encode(v, StandardCharsets.US_ASCII)));
        Mac mac = Mac.getInstance("HmacSHA512");
        mac.init(new SecretKeySpec(BI_MAT.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
        String chuKy = HexFormat.of().formatHex(mac.doFinal(sj.toString().getBytes(StandardCharsets.UTF_8)));
        var req = get("/api/vien-phi/vnpay/ipn");
        p.forEach(req::param);
        req.param("vnp_SecureHash", chuKy);
        return mvc.perform(req);
    }

    // ================= hành trình =================
    @Test
    void hanhTrinhDayDu() throws Exception {
        String ad = dangNhap("admin@thehos.test", "MatKhauTest@123");
        int n = SO.incrementAndGet();

        // --- Admin dựng danh mục + nhân sự ---
        int khoa = doc(json("POST", "/api/danh-muc/khoa", ad, "{\"tenKhoa\":\"Nội " + n + "\"}").andExpect(status().isOk()), "$.id");
        int dvKham = doc(json("POST", "/api/danh-muc/dich-vu", ad,
                "{\"tenDichVu\":\"Khám nội\",\"khoaId\":%d,\"loaiDichVu\":\"KHAM_BENH\",\"gia\":150000}".formatted(khoa)), "$.id");
        int dvXn = doc(json("POST", "/api/danh-muc/dich-vu", ad,
                "{\"tenDichVu\":\"Công thức máu\",\"khoaId\":%d,\"loaiDichVu\":\"XET_NGHIEM\",\"gia\":80000}".formatted(khoa)), "$.id");
        int phongKham = doc(json("POST", "/api/danh-muc/phong", ad,
                "{\"tenPhong\":\"P.Khám %d\",\"khoaId\":%d,\"loaiPhong\":\"PHONG_KHAM\"}".formatted(n, khoa)), "$.id");
        int phongXn = doc(json("POST", "/api/danh-muc/phong", ad,
                "{\"tenPhong\":\"P.XN %d\",\"khoaId\":%d,\"loaiPhong\":\"PHONG_XET_NGHIEM\",\"dichVuId\":%d}".formatted(n, khoa, dvXn)), "$.id");
        int thuoc = doc(json("POST", "/api/danh-muc/thuoc", ad,
                "{\"tenThuoc\":\"Paracetamol %d\",\"gia\":2000,\"soLuong\":500,\"donVi\":\"VIEN\"}".formatted(n)), "$.id");
        int bacSi = doc(json("POST", "/api/nguoi-dung/admin/nhan-vien", ad, """
                {"hoTen":"BS Trưởng %d","email":"bs%d@thehos.test","soDienThoai":"091%07d","matKhau":"matkhau1",
                 "gioiTinh":"NAM","loaiNhanVien":"BAC_SI","hocHam":"TS","khoaId":%d}""".formatted(n, n, n, khoa)), "$.id");
        int dieuDuong = doc(json("POST", "/api/nguoi-dung/admin/nhan-vien", ad, """
                {"hoTen":"ĐD Trưởng %d","email":"dd%d@thehos.test","soDienThoai":"092%07d","matKhau":"matkhau1",
                 "gioiTinh":"NU","loaiNhanVien":"DIEU_DUONG","khoaId":%d}""".formatted(n, n, n, khoa)), "$.id");
        json("POST", "/api/nguoi-dung/admin/nhan-vien/" + bacSi + "/truong-khoa", ad, null).andExpect(status().isOk());
        json("POST", "/api/nguoi-dung/admin/nhan-vien/" + dieuDuong + "/truong-khoa", ad, null).andExpect(status().isOk());
        String bs = dangNhap("bs" + n + "@thehos.test", "matkhau1");
        String dd = dangNhap("dd" + n + "@thehos.test", "matkhau1");

        // --- Trưởng khoa tạo lịch sáng + chiều hôm nay, 1 lượt mỗi khung (FR-BS-11, BR-05, BR-06) ---
        ResultActions taoLich = json("POST", "/api/lich-kham/lich", bs, """
                {"bacSiId":%d,"dichVuId":%d,"phongId":%d,"ngaySang":["%s"],"ngayChieu":["%s"],"soLuotMoiKhung":1}
                """.formatted(bacSi, dvKham, phongKham, ngay, ngay)).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2)).andExpect(jsonPath("$[0].khung.length()").value(4));
        int lichSang = doc(taoLich, "$[0].id");
        List<Integer> khungSang = doc(taoLich, "$[0].khung[*].id");
        List<Integer> khungChieu = doc(taoLich, "$[1].khung[*].id");
        json("POST", "/api/lich-kham/lich", bs, """
                {"bacSiId":%d,"dichVuId":%d,"phongId":%d,"ngaySang":["%s"],"soLuotMoiKhung":1}
                """.formatted(bacSi, dvKham, phongKham, ngay)).andExpect(status().isConflict());

        // --- Phân công điều dưỡng (FR-DD-12) ---
        int thu = ngay.getDayOfWeek().getValue() % 7 + 1;
        json("POST", "/api/lich-kham/phan-cong", dd, """
                {"dieuDuongId":%d,"phongId":%d,"cacCa":[{"thuTrongTuan":%d,"buoi":"SANG"}]}""".formatted(dieuDuong, phongKham, thu))
                .andExpect(status().isOk());
        lay("/api/lich-kham/dieu-duong/phong-hom-nay", dd).andExpect(jsonPath("$[0].lich[0].id").value(lichSang));

        // --- Bệnh nhân A đặt khung 07:00, giữ chỗ 15 phút (FR-BN-08, BR-09, BR-10) ---
        Object[] a = benhNhanMoi();
        ResultActions datA = datKham((String) a[0], (int) a[1], khungSang.get(0)).andExpect(status().isOk())
                .andExpect(jsonPath("$.urlThanhToan").value(org.hamcrest.Matchers.containsString("vnp_SecureHash=")))
                .andExpect(jsonPath("$.phieu[0].trangThai").value("CHO_THANH_TOAN"));
        int gdA = doc(datA, "$.giaoDichId");
        int phieuA = doc(datA, "$.phieu[0].id");
        String maPhieuA = doc(datA, "$.phieu[0].maPhieu");

        // B không lấy được chỗ A đang giữ
        Object[] b = benhNhanMoi();
        datKham((String) b[0], (int) b[1], khungSang.get(0)).andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("KHUNG_DA_DAY"));

        // IPN thành công → xác nhận, số 1; IPN gọi lại → 02, không ghi nhận lại
        ipn(gdA, 165_000, true).andExpect(jsonPath("$.RspCode").value("00"));
        ipn(gdA, 165_000, true).andExpect(jsonPath("$.RspCode").value("02"));
        ipn(gdA, 1, true).andExpect(jsonPath("$.RspCode").value("04"));
        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> lay("/api/lich-kham/phieu/" + phieuA, (String) a[0])
                .andExpect(jsonPath("$.trangThai").value("DA_XAC_NHAN")).andExpect(jsonPath("$.soThuTu").value(1)));

        // --- Trả tiền tới muộn khi khung đã đầy → dồn sang khung cuối buổi (BR-10) ---
        int gdB = doc(datKham((String) b[0], (int) b[1], khungSang.get(1)).andExpect(status().isOk()), "$.giaoDichId");
        int phieuB = doc(datKham((String) b[0], (int) b[1], khungSang.get(2)).andExpect(status().isConflict()), "$.status");
        assertThat(phieuB).isEqualTo(409); // đã có phiếu (đang giữ chỗ) ở cùng lịch
        dongHo.cong(Duration.ofMinutes(16)); // B quá 15 phút → nhả chỗ
        Object[] c = benhNhanMoi();
        int gdC = doc(datKham((String) c[0], (int) c[1], khungSang.get(1)).andExpect(status().isOk()), "$.giaoDichId");
        ipn(gdC, 165_000, true).andExpect(jsonPath("$.RspCode").value("00"));
        ipn(gdB, 165_000, true).andExpect(jsonPath("$.RspCode").value("00")); // IPN tới muộn vẫn xác nhận
        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> {
            lay("/api/lich-kham/phieu/giao-dich/" + gdC, (String) c[0]).andExpect(jsonPath("$[0].soThuTu").value(2));
            lay("/api/lich-kham/phieu/giao-dich/" + gdB, (String) b[0])
                    .andExpect(jsonPath("$[0].trangThai").value("DA_XAC_NHAN"))
                    .andExpect(jsonPath("$[0].soThuTu").value(4))      // khung cuối: so_bat_dau 4
                    .andExpect(jsonPath("$[0].gioBatDau").value("10:00"));
        });

        // --- Hai người tranh chỗ cuối cùng lúc: đúng một người được (khung chiều 13:00, 1 chỗ) ---
        Object[] d1 = benhNhanMoi();
        Object[] d2 = benhNhanMoi();
        var pool = Executors.newFixedThreadPool(2);
        List<Callable<Integer>> viec = new ArrayList<>();
        for (Object[] x : List.of(d1, d2)) {
            viec.add(() -> datKham((String) x[0], (int) x[1], khungChieu.get(0)).andReturn().getResponse().getStatus());
        }
        List<Integer> ketQua = new ArrayList<>();
        for (var f : pool.invokeAll(viec)) {
            ketQua.add(f.get());
        }
        pool.shutdown();
        assertThat(ketQua).containsExactlyInAnyOrder(200, 409);

        // --- Gọi tiếp 5 số → thông báo "Đến lượt khám" (FR-DD-07, BR-15) ---
        json("POST", "/api/lich-kham/" + lichSang + "/goi-tiep", dd, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.soDangGoi").value(4));
        json("POST", "/api/lich-kham/" + lichSang + "/goi-tiep", dd, null).andExpect(jsonPath("$.title").value("HET_LUOT"));
        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> lay("/api/nguoi-dung/thong-bao", (String) a[0])
                .andExpect(jsonPath("$.items[0].tieuDe").value("Đến lượt khám bệnh của bạn")));
        lay("/api/lich-kham/" + lichSang + "/so-dang-goi", null).andExpect(jsonPath("$.cacSoVuaGoi.length()").value(4));

        // --- Tiếp nhận A bằng CCCD (FR-DD-03…05, BR-13) ---
        lay("/api/ho-so-benh-an/tiep-nhan/kiem-tra?maPhieu=" + maPhieuA + "&phongId=" + phongXn, dd)
                .andExpect(jsonPath("$.title").value("SAI_PHONG"));
        lay("/api/ho-so-benh-an/tiep-nhan/kiem-tra?maPhieu=" + maPhieuA + "&phongId=" + phongKham, dd)
                .andExpect(jsonPath("$.daLienKet").value(false));
        int benhAn = doc(json("POST", "/api/ho-so-benh-an/tiep-nhan", dd,
                "{\"maPhieu\":\"%s\",\"phongId\":%d,\"soCccd\":\"0012340%05d\"}".formatted(maPhieuA, phongKham, n))
                .andExpect(status().isOk()).andExpect(jsonPath("$.maBenhNhan").value(org.hamcrest.Matchers.startsWith("BN"))), "$.benhAnId");
        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> lay("/api/lich-kham/phieu/" + phieuA, (String) a[0])
                .andExpect(jsonPath("$.trangThai").value("DA_TIEP_NHAN")));
        // Điều dưỡng không xem được nội dung bệnh án (NFR-02)
        lay("/api/ho-so-benh-an/benh-an/" + benhAn, dd).andExpect(status().isForbidden());

        // --- Bác sĩ khám (FR-BS-03…10) ---
        lay("/api/ho-so-benh-an/lich/" + lichSang, bs).andExpect(jsonPath("$[0].trangThai").value("CHO_KHAM"));
        json("POST", "/api/ho-so-benh-an/benh-an/" + benhAn + "/nhan-kham", bs, null).andExpect(jsonPath("$.trangThai").value("DANG_KHAM"));
        json("PUT", "/api/ho-so-benh-an/benh-an/" + benhAn, bs,
                "{\"trieuChung\":\"Sốt 38,5 độ\",\"chanDoan\":\"Viêm họng cấp\",\"ngayTaiKham\":\"%s\"}".formatted(ngay.plusDays(7)))
                .andExpect(jsonPath("$.chanDoan").value("Viêm họng cấp"));
        ResultActions xn = json("POST", "/api/ho-so-benh-an/benh-an/" + benhAn + "/xet-nghiem", bs,
                "{\"dichVuId\":%d,\"ghiChu\":\"Nhịn ăn\"}".formatted(dvXn)).andExpect(status().isOk())
                .andExpect(jsonPath("$.phongId").value(phongXn)).andExpect(jsonPath("$.soThuTu").value(1));
        String maXn = doc(xn, "$.maXetNghiem");
        int xnId = doc(xn, "$.id");
        String keThuoc = """
                {"thuocId":%d,"soLuong":10,"soLanMoiNgay":2,"soNgay":5,"soLuongMoiLan":1,"sang":true,"toi":true}""".formatted(thuoc);
        json("POST", "/api/ho-so-benh-an/benh-an/" + benhAn + "/thuoc", bs, keThuoc)
                .andExpect(jsonPath("$.canhBao").doesNotExist()).andExpect(jsonPath("$.donThuoc.tongTien").value(20000.0));
        ResultActions trung = json("POST", "/api/ho-so-benh-an/benh-an/" + benhAn + "/thuoc", bs, keThuoc)
                .andExpect(jsonPath("$.canhBao").isString()).andExpect(jsonPath("$.donThuoc.tongTien").value(40000.0));
        int dongThuoc = doc(trung, "$.donThuoc.thuoc[0].id");
        json("POST", "/api/ho-so-benh-an/benh-an/" + benhAn + "/hoan-tat", bs, null).andExpect(jsonPath("$.trangThai").value("DA_KHAM"));

        // --- Xét nghiệm: chưa trả không nhận được; trả VNPAY → nhận → hoàn tất (BR-17) ---
        json("POST", "/api/ho-so-benh-an/xet-nghiem/" + xnId + "/nhan", dd, null).andExpect(jsonPath("$.title").value("CHUA_THANH_TOAN"));
        int gdXn = doc(json("POST", "/api/ho-so-benh-an/xet-nghiem/ma/" + maXn + "/thanh-toan", (String) a[0], null)
                .andExpect(status().isOk()).andExpect(jsonPath("$.tongTien").value(81500.0)), "$.giaoDichId");
        ipn(gdXn, 81_500, true).andExpect(jsonPath("$.RspCode").value("00"));
        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> lay("/api/ho-so-benh-an/xet-nghiem/ma/" + maXn, (String) a[0])
                .andExpect(jsonPath("$.daThanhToan").value(true)));
        json("POST", "/api/ho-so-benh-an/xet-nghiem/phong/" + phongXn + "/goi-tiep", dd, null).andExpect(jsonPath("$.soDangGoi").value(1));
        json("POST", "/api/ho-so-benh-an/xet-nghiem/" + xnId + "/nhan", dd, null).andExpect(jsonPath("$.trangThai").value("DANG_THUC_HIEN"));
        mvc.perform(multipart("/api/ho-so-benh-an/xet-nghiem/" + xnId + "/hoan-tat")
                        .param("ketQua", "Bạch cầu 12 G/L").header("Authorization", "Bearer " + dd))
                .andExpect(status().isOk()).andExpect(jsonPath("$.trangThai").value("HOAN_THANH"));

        // --- Bệnh nhân: lịch sử, bệnh án, đánh giá một lần (FR-BN-11, 14; BR-20) ---
        lay("/api/ho-so-benh-an/lich-su?hoSoId=" + a[1], (String) a[0])
                .andExpect(jsonPath("$[0].chanDoan").value("Viêm họng cấp")).andExpect(jsonPath("$[0].daDanhGia").value(false));
        lay("/api/ho-so-benh-an/cua-toi/" + benhAn, (String) b[0]).andExpect(status().isNotFound()); // không phải của B
        lay("/api/ho-so-benh-an/cua-toi/" + benhAn, (String) a[0])
                .andExpect(jsonPath("$.xetNghiem[0].ketQua").value("Bạch cầu 12 G/L"))
                .andExpect(jsonPath("$.donThuoc.thuoc.length()").value(2));
        json("POST", "/api/ho-so-benh-an/cua-toi/" + benhAn + "/danh-gia", (String) a[0], "{\"soDiem\":5,\"nhanXet\":\"Tận tâm\"}")
                .andExpect(status().isNoContent());
        json("POST", "/api/ho-so-benh-an/cua-toi/" + benhAn + "/danh-gia", (String) a[0], "{\"soDiem\":1}")
                .andExpect(status().isConflict());
        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> lay("/api/nguoi-dung/bac-si/" + bacSi, null)
                .andExpect(jsonPath("$.diemDanhGia").value(5.0)));

        // --- Nhắc thuốc: tổng 3 viên, 2 lần/ngày → 3 lần nhắc trong 2 ngày (BR-21) ---
        json("POST", "/api/ho-so-benh-an/nhac-thuoc", (String) a[0], """
                {"hoSoId":%s,"chiTietDonThuocId":%d,"ngayBatDau":"%s","tongSoLuong":3,
                 "cacLan":[{"gio":"20:00","soLuong":1},{"gio":"08:00","soLuong":1}]}""".formatted(a[1], dongThuoc, ngay))
                .andExpect(status().isOk()).andExpect(jsonPath("$.ngayKetThuc").value(ngay.plusDays(1) + "T23:59:59"));
        lay("/api/ho-so-benh-an/nhac-thuoc/theo-ngay?ngay=" + ngay, (String) a[0]).andExpect(jsonPath("$.length()").value(2));
        lay("/api/ho-so-benh-an/nhac-thuoc/theo-ngay?ngay=" + ngay.plusDays(1), (String) a[0]).andExpect(jsonPath("$.length()").value(1));

        // --- Khách vãng lai tại quầy: hồ sơ không tài khoản, trả QR → có số và bệnh án ngay (BR-14) ---
        int hoSoQuay = doc(json("POST", "/api/nguoi-dung/dieu-duong/ho-so", dd, """
                {"hoTen":"Khách Quầy %d","gioiTinh":"NAM","ngaySinh":"1970-01-01","soCccd":"0019990%05d"}""".formatted(n, n))
                .andExpect(status().isOk()).andExpect(jsonPath("$.taiKhoanId").doesNotExist()), "$.id");
        ResultActions quay = json("POST", "/api/lich-kham/quay/phieu", dd, "{\"hoSoId\":%d,\"lichKhamId\":%d}".formatted(hoSoQuay, lichSang))
                .andExpect(status().isOk()).andExpect(jsonPath("$.urlThanhToan").isString());
        int gdQuay = doc(quay, "$.phieu.giaoDichId");
        ipn(gdQuay, 165_000, true).andExpect(jsonPath("$.RspCode").value("00"));
        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> lay("/api/lich-kham/phieu/giao-dich/" + gdQuay, dd)
                .andExpect(jsonPath("$[0].trangThai").value("DA_TIEP_NHAN"))
                .andExpect(jsonPath("$[0].soThuTu").value(3))); // BR-14: khung còn chỗ sớm nhất (09:00, so_bat_dau 3)

        // --- Admin: thống kê, giao dịch, lịch (FR-AD-01, 09, 10) ---
        lay("/api/thong-ke/admin?tu=" + ngay + "&den=" + ngay, ad).andExpect(status().isOk())
                .andExpect(jsonPath("$.luotDat").value(org.hamcrest.Matchers.greaterThanOrEqualTo(4)))
                .andExpect(jsonPath("$.theoKhoa[0].tenKhoa").isString());
        lay("/api/vien-phi/admin/giao-dich?trangThai=THANH_CONG", ad).andExpect(jsonPath("$.totalCount").value(org.hamcrest.Matchers.greaterThanOrEqualTo(5)));
        lay("/api/lich-kham/admin/lich?khoaId=" + khoa + "&tu=" + ngay + "&den=" + ngay, ad).andExpect(jsonPath("$.length()").value(2));
    }
}
