package vn.nhom6.his;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test tích hợp với MySQL thật (Testcontainers): Flyway tạo đủ bảng, Hibernate validate khớp entity, seed Admin,
 * hợp đồng API giữ đúng như bản trước (JSON, ProblemDetail, 401/403/204, claim JWT).
 */
@SpringBootTest(properties = {
        "his.jwt.key=khoa-test-chi-dung-trong-test-tich-hop-0123456789",
        "his.seed.admin.email=admin@thehos.test",
        "his.seed.admin.so-dien-thoai=0900000000",
        "his.seed.admin.mat-khau=MatKhauTest@123",
})
@AutoConfigureMockMvc
@Import(TestcontainersConfig.class)
class TichHopTests {

    @Autowired
    MockMvc mvc;

    private String dangNhapAdmin() throws Exception {
        String body = mvc.perform(post("/api/nguoi-dung/dang-nhap").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenDangNhap\":\"admin@thehos.test\",\"matKhau\":\"MatKhauTest@123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vaiTro").value("QUAN_TRI"))
                .andExpect(jsonPath("$.hetHan").isString())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.accessToken");
    }

    @Test
    void healthVaDanTocSeed() throws Exception {
        mvc.perform(get("/health")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("UP"));
        mvc.perform(get("/api/danh-muc/dan-toc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(54))
                .andExpect(jsonPath("$[0].tenDanToc").value("Kinh"));
    }

    @Test
    void dangNhapSaiTraProblemDetail() throws Exception {
        mvc.perform(post("/api/nguoi-dung/dang-nhap").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenDangNhap\":\"admin@thehos.test\",\"matKhau\":\"sai\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("DANG_NHAP_THAT_BAI"))
                .andExpect(jsonPath("$.detail").value("Sai tên đăng nhập hoặc mật khẩu."));
    }

    @Test
    void validationTraErrorsTheoTruong() throws Exception {
        String token = dangNhapAdmin();
        mvc.perform(post("/api/danh-muc/khoa").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"tenKhoa\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.tenKhoa[0]").value("Tên khoa là bắt buộc."))
                .andExpect(jsonPath("$.detail").value("Tên khoa là bắt buộc."));
    }

    @Test
    void crudKhoaTheoHopDongCu() throws Exception {
        String token = dangNhapAdmin();
        String auth = "Bearer " + token;

        String tao = mvc.perform(post("/api/danh-muc/khoa").header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenKhoa\":\"Tim mạch\",\"moTa\":\"Khoa tim\",\"tuKhoaTrieuChung\":\"đau ngực\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenKhoa").value("Tim mạch"))
                .andExpect(jsonPath("$.daXoa").value(false))
                .andReturn().getResponse().getContentAsString();
        int id = JsonPath.read(tao, "$.id");

        mvc.perform(post("/api/danh-muc/khoa").header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"tenKhoa\":\"Tim mạch\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("TRUNG_TEN_KHOA"));

        mvc.perform(put("/api/danh-muc/khoa/" + id).header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"tenKhoa\":\"Tim mạch can thiệp\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenKhoa").value("Tim mạch can thiệp"));

        mvc.perform(get("/api/danh-muc/khoa/quan-ly").header("Authorization", auth).param("tuKhoa", "can thiệp"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.pageSize").value(20))
                .andExpect(jsonPath("$.items[0].id").value(id));

        mvc.perform(delete("/api/danh-muc/khoa/" + id).header("Authorization", auth)).andExpect(status().isNoContent());
        String congKhai = mvc.perform(get("/api/danh-muc/khoa")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(congKhai).doesNotContain("Tim mạch can thiệp");

        mvc.perform(post("/api/danh-muc/khoa/" + id + "/khoi-phuc").header("Authorization", auth))
                .andExpect(status().isNoContent());
        mvc.perform(put("/api/danh-muc/khoa/99999").header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"tenKhoa\":\"X\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("KHONG_TIM_THAY"));
    }

    @Test
    void phanQuyen401Va403() throws Exception {
        mvc.perform(get("/api/danh-muc/khoa/quan-ly")).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("CHUA_DANG_NHAP"));
        mvc.perform(get("/api/nguoi-dung/toi")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/nguoi-dung/toi").header("Authorization", "Bearer token.hong.roi"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenHongVanGoiDuocEndpointCongKhai() throws Exception {
        mvc.perform(get("/api/thong-ke/trang-chu").header("Authorization", "Bearer token.hong.roi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.soKhoa").isNumber())
                .andExpect(jsonPath("$.soBacSi").isNumber())
                .andExpect(jsonPath("$.soLuotKham").isNumber());
    }

    @Test
    void toiTraThongTinTuJwt() throws Exception {
        String token = dangNhapAdmin();
        mvc.perform(get("/api/nguoi-dung/toi").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vaiTro").value("QUAN_TRI"))
                .andExpect(jsonPath("$.laTruongKhoa").value(false))
                .andExpect(jsonPath("$.anhDaiDienUrl").value(org.hamcrest.Matchers.nullValue()));
    }

    @Test
    void soDangGoiLichKhongTonTaiTra404() throws Exception {
        mvc.perform(get("/api/lich-kham/123456/so-dang-goi"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Không tìm thấy lịch khám."));
    }
}
