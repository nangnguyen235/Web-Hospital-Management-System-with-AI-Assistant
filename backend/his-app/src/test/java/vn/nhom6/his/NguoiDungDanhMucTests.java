package vn.nhom6.his;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import vn.nhom6.his.shared.kernel.IEmailService;

/** M1 — NguoiDung + DanhMuc: OTP (BR-02), hồ sơ ≤ 10 (BR-03), danh mục và nhân sự (BR-04). */
@SpringBootTest(properties = {
        "his.jwt.key=khoa-test-chi-dung-trong-test-tich-hop-0123456789",
        "his.seed.admin.email=admin@thehos.test",
        "his.seed.admin.so-dien-thoai=0900000000",
        "his.seed.admin.mat-khau=MatKhauTest@123",
})
@AutoConfigureMockMvc
@Import(TestcontainersConfig.class)
class NguoiDungDanhMucTests {

    private static final AtomicInteger SO = new AtomicInteger(100);

    @Autowired
    MockMvc mvc;

    @MockitoBean
    IEmailService email;

    // ---------- tiện ích ----------
    private ResultActions postJson(String url, String token, String body) throws Exception {
        var req = post(url).contentType(MediaType.APPLICATION_JSON).content(body);
        if (token != null) {
            req.header("Authorization", "Bearer " + token);
        }
        return mvc.perform(req);
    }

    private String otpCuoi(String den) {
        ArgumentCaptor<String> noiDung = ArgumentCaptor.forClass(String.class);
        verify(email, atLeastOnce()).gui(eq(den), anyString(), noiDung.capture());
        Matcher m = Pattern.compile(">(\\d{6})<").matcher(noiDung.getValue());
        assertThat(m.find()).isTrue();
        return m.group(1);
    }

    private String token(ResultActions r) throws Exception {
        return JsonPath.read(r.andReturn().getResponse().getContentAsString(), "$.accessToken");
    }

    private String admin() throws Exception {
        return token(postJson("/api/nguoi-dung/dang-nhap", null,
                "{\"tenDangNhap\":\"admin@thehos.test\",\"matKhau\":\"MatKhauTest@123\"}").andExpect(status().isOk()));
    }

    /** Đăng ký một bệnh nhân mới, trả token. */
    private String dangKy(String mail) throws Exception {
        String sdt = "09" + String.format("%08d", SO.incrementAndGet());
        postJson("/api/nguoi-dung/dang-ky/gui-otp", null, "{\"email\":\"" + mail + "\",\"soDienThoai\":\"" + sdt + "\"}")
                .andExpect(status().isNoContent());
        return token(postJson("/api/nguoi-dung/dang-ky", null, """
                {"email":"%s","soDienThoai":"%s","hoTen":"Bệnh Nhân Thử","matKhau":"matkhau1","otp":"%s"}
                """.formatted(mail, sdt, otpCuoi(mail))).andExpect(status().isOk())
                .andExpect(jsonPath("$.vaiTro").value("BENH_NHAN")));
    }

    private String hoSoJson(String ten) {
        return """
                {"hoTen":"%s","gioiTinh":"NAM","ngaySinh":"1990-05-01","soDienThoai":"0912345678","email":"hs@thehos.test","danTocId":1}
                """.formatted(ten);
    }

    // ---------- OTP ----------
    @Test
    void otpSaiNamLanThiHetHieuLuc() throws Exception {
        String mail = "otp" + SO.incrementAndGet() + "@thehos.test";
        postJson("/api/nguoi-dung/dang-ky/gui-otp", null, "{\"email\":\"" + mail + "\",\"soDienThoai\":\"0987000111\"}")
                .andExpect(status().isNoContent());
        String dung = otpCuoi(mail);
        String sai = dung.equals("000000") ? "111111" : "000000";
        String body = "{\"email\":\"%s\",\"soDienThoai\":\"0987000111\",\"hoTen\":\"A\",\"matKhau\":\"matkhau1\",\"otp\":\"%s\"}";
        for (int i = 0; i < 5; i++) {
            postJson("/api/nguoi-dung/dang-ky", null, body.formatted(mail, sai))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.title").value("OTP_SAI"));
        }
        // Sau 5 lần sai, mã đúng cũng không dùng được nữa
        postJson("/api/nguoi-dung/dang-ky", null, body.formatted(mail, dung))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.title").value("OTP_HET_HIEU_LUC"));
    }

    @Test
    void dangKyTrungEmailBiTuChoiVaQuenMatKhau() throws Exception {
        String mail = "quen" + SO.incrementAndGet() + "@thehos.test";
        dangKy(mail);
        postJson("/api/nguoi-dung/dang-ky/gui-otp", null, "{\"email\":\"" + mail + "\",\"soDienThoai\":\"0911222333\"}")
                .andExpect(status().isConflict()).andExpect(jsonPath("$.title").value("EMAIL_DA_DUNG"));

        postJson("/api/nguoi-dung/quen-mat-khau/gui-otp", null, "{\"email\":\"" + mail + "\"}").andExpect(status().isNoContent());
        postJson("/api/nguoi-dung/quen-mat-khau", null, """
                {"email":"%s","otp":"%s","matKhauMoi":"matkhaumoi","nhapLaiMatKhau":"matkhaumoi"}
                """.formatted(mail, otpCuoi(mail))).andExpect(status().isNoContent());
        postJson("/api/nguoi-dung/dang-nhap", null, "{\"tenDangNhap\":\"" + mail.toUpperCase() + "\",\"matKhau\":\"matkhaumoi\"}")
                .andExpect(status().isOk());

        postJson("/api/nguoi-dung/dang-nhap-otp/gui-otp", null, "{\"email\":\"" + mail + "\"}").andExpect(status().isNoContent());
        postJson("/api/nguoi-dung/dang-nhap-otp", null, "{\"email\":\"" + mail + "\",\"otp\":\"" + otpCuoi(mail) + "\"}")
                .andExpect(status().isOk()).andExpect(jsonPath("$.vaiTro").value("BENH_NHAN"));
    }

    // ---------- Hồ sơ ----------
    @Test
    void toiDaMuoiHoSoVaTimLaiHoSoCuaTaiKhoanKhac() throws Exception {
        String a = dangKy("a" + SO.incrementAndGet() + "@thehos.test");
        for (int i = 1; i <= 10; i++) {
            postJson("/api/nguoi-dung/ho-so", a, hoSoJson("Hồ Sơ " + i)).andExpect(status().isOk());
        }
        postJson("/api/nguoi-dung/ho-so", a, hoSoJson("Hồ Sơ 11"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.title").value("QUA_SO_HO_SO"));

        int id = JsonPath.read(mvc.perform(get("/api/nguoi-dung/ho-so").header("Authorization", "Bearer " + a))
                .andExpect(jsonPath("$.length()").value(10)).andReturn().getResponse().getContentAsString(), "$[0].id");

        // Tài khoản B không xem được hồ sơ của A, nhưng tìm lại được bằng OTP gửi email của hồ sơ
        String b = dangKy("b" + SO.incrementAndGet() + "@thehos.test");
        mvc.perform(get("/api/nguoi-dung/ho-so/" + id).header("Authorization", "Bearer " + b)).andExpect(status().isNotFound());
        mvc.perform(get("/api/nguoi-dung/ho-so/tim-lai").header("Authorization", "Bearer " + b)
                        .param("hoTen", "hồ sơ 1").param("namSinh", "1990").param("gioiTinh", "NAM"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].emailChe").value("h***@thehos.test"));
        postJson("/api/nguoi-dung/ho-so/tim-lai/" + id + "/gui-otp", b, "").andExpect(status().isNoContent());
        postJson("/api/nguoi-dung/ho-so/tim-lai/" + id, b, "{\"otp\":\"" + otpCuoi("hs@thehos.test") + "\"}")
                .andExpect(status().isOk());
        mvc.perform(get("/api/nguoi-dung/ho-so").header("Authorization", "Bearer " + a)).andExpect(jsonPath("$.length()").value(9));
        // A đã giảm xuống 9 → tạo thêm được
        postJson("/api/nguoi-dung/ho-so", a, hoSoJson("Hồ Sơ mới")).andExpect(status().isOk());
    }

    @Test
    void benhNhanKhongVaoDuocTrangAdmin() throws Exception {
        String bn = dangKy("bn" + SO.incrementAndGet() + "@thehos.test");
        mvc.perform(get("/api/nguoi-dung/admin/tai-khoan").header("Authorization", "Bearer " + bn))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.title").value("KHONG_CO_QUYEN"));
    }

    // ---------- Danh mục + nhân sự ----------
    @Test
    void danhMucVaTruongKhoa() throws Exception {
        String ad = admin();
        String tenKhoa = "Khoa Thử " + SO.incrementAndGet();
        int khoa = JsonPath.read(postJson("/api/danh-muc/khoa", ad, "{\"tenKhoa\":\"" + tenKhoa + "\"}")
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), "$.id");
        int dvKham = JsonPath.read(postJson("/api/danh-muc/dich-vu", ad,
                "{\"tenDichVu\":\"Khám tổng quát\",\"khoaId\":%d,\"loaiDichVu\":\"KHAM_BENH\",\"gia\":150000}".formatted(khoa))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), "$.id");
        int dvXn = JsonPath.read(postJson("/api/danh-muc/dich-vu", ad,
                "{\"tenDichVu\":\"Xét nghiệm máu\",\"khoaId\":%d,\"loaiDichVu\":\"XET_NGHIEM\",\"gia\":80000}".formatted(khoa))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), "$.id");

        // BR-04: phòng xét nghiệm phải gắn dịch vụ XET_NGHIEM cùng khoa
        postJson("/api/danh-muc/phong", ad, "{\"tenPhong\":\"XN-%d\",\"khoaId\":%d,\"loaiPhong\":\"PHONG_XET_NGHIEM\",\"dichVuId\":%d}"
                .formatted(khoa, khoa, dvKham)).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("DICH_VU_KHONG_HOP_LE"));
        postJson("/api/danh-muc/phong", ad, "{\"tenPhong\":\"XN-%d\",\"khoaId\":%d,\"loaiPhong\":\"PHONG_XET_NGHIEM\",\"dichVuId\":%d}"
                .formatted(khoa, khoa, dvXn)).andExpect(status().isOk()).andExpect(jsonPath("$.tenKhoa").value(tenKhoa));

        postJson("/api/danh-muc/thuoc", ad, "{\"tenThuoc\":\"Paracetamol %d\",\"gia\":2000,\"soLuong\":100,\"donVi\":\"VIEN\"}"
                .formatted(khoa)).andExpect(status().isOk());

        // Hai bác sĩ của khoa; đặt trưởng khoa người thứ hai thì người thứ nhất mất cờ
        int bs1 = taoBacSi(ad, khoa);
        int bs2 = taoBacSi(ad, khoa);
        mvc.perform(post("/api/nguoi-dung/admin/nhan-vien/" + bs1 + "/truong-khoa").header("Authorization", "Bearer " + ad))
                .andExpect(status().isOk()).andExpect(jsonPath("$.laTruongKhoa").value(true));
        mvc.perform(post("/api/nguoi-dung/admin/nhan-vien/" + bs2 + "/truong-khoa").header("Authorization", "Bearer " + ad))
                .andExpect(status().isOk());
        mvc.perform(get("/api/nguoi-dung/admin/nhan-vien/" + bs1).header("Authorization", "Bearer " + ad))
                .andExpect(jsonPath("$.laTruongKhoa").value(false));

        // Công khai: tìm bác sĩ theo khoa
        mvc.perform(get("/api/nguoi-dung/bac-si").param("khoaId", String.valueOf(khoa)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalCount").value(2))
                .andExpect(jsonPath("$.items[0].tenKhoa").value(tenKhoa));

        // Bác sĩ trưởng khoa đăng nhập: claim la_truong_khoa = "true", xem được bác sĩ khoa mình
        String tkBs2 = token(postJson("/api/nguoi-dung/dang-nhap", null,
                "{\"tenDangNhap\":\"bs" + bs2 + "@thehos.test\",\"matKhau\":\"matkhau1\"}").andExpect(status().isOk()));
        mvc.perform(get("/api/nguoi-dung/nhan-vien/khoa-toi").param("loai", "BAC_SI").header("Authorization", "Bearer " + tkBs2))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));

        // Xóa nhân viên → không đăng nhập được
        mvc.perform(delete("/api/nguoi-dung/admin/nhan-vien/" + bs2).header("Authorization", "Bearer " + ad))
                .andExpect(status().isNoContent());
        postJson("/api/nguoi-dung/dang-nhap", null, "{\"tenDangNhap\":\"bs" + bs2 + "@thehos.test\",\"matKhau\":\"matkhau1\"}")
                .andExpect(status().isBadRequest());
    }

    private int taoBacSi(String ad, int khoa) throws Exception {
        int n = SO.incrementAndGet();
        String body = """
                {"hoTen":"Bác Sĩ %d","email":"tam%d@thehos.test","soDienThoai":"097%07d","matKhau":"matkhau1",
                 "gioiTinh":"NU","loaiNhanVien":"BAC_SI","hocHam":"THS","khoaId":%d}
                """.formatted(n, n, n, khoa);
        String kq = postJson("/api/nguoi-dung/admin/nhan-vien", ad, body).andExpect(status().isOk())
                .andExpect(jsonPath("$.loaiNhanVien").value("BAC_SI")).andReturn().getResponse().getContentAsString();
        int id = JsonPath.read(kq, "$.id");
        // đổi email theo id để đăng nhập dễ trong test
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/nguoi-dung/admin/nhan-vien/" + id)
                .header("Authorization", "Bearer " + ad).contentType(MediaType.APPLICATION_JSON)
                .content(body.replace("tam" + n, "bs" + id))).andExpect(status().isOk());
        return id;
    }
}
