package vn.nhom6.his.shared.kernel;

/** Lỗi nghiệp vụ trả về client dạng ProblemDetail: {@code title} = mã, {@code detail} = mô tả tiếng Việt. */
public record Loi(String ma, String moTa, LoaiLoi loai) {

    public Loi(String ma, String moTa) {
        this(ma, moTa, LoaiLoi.NGHIEP_VU);
    }

    public static Loi khongTimThay(String doiTuong) {
        return new Loi("KHONG_TIM_THAY", "Không tìm thấy " + doiTuong + ".", LoaiLoi.KHONG_TIM_THAY);
    }

    public static Loi khongCoQuyen(String moTa) {
        return new Loi("KHONG_CO_QUYEN", moTa, LoaiLoi.KHONG_CO_QUYEN);
    }

    public static Loi xungDot(String ma, String moTa) {
        return new Loi(ma, moTa, LoaiLoi.XUNG_DOT);
    }

    public LoiNghiepVuException ngoaiLe() {
        return new LoiNghiepVuException(this);
    }
}
