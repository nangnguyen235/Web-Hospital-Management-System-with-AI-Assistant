package vn.nhom6.his.shared.kernel;

/** Chuẩn hóa tham số phân trang (NFR-01: mặc định 20, tối đa 100, trang bắt đầu từ 1). */
public record PhanTrang(int page, int pageSize) {

    public static final int MAC_DINH = 20;

    public static PhanTrang cua(Integer page, Integer pageSize) {
        int p = page == null || page < 1 ? 1 : page;
        int s = pageSize == null ? MAC_DINH : Math.clamp(pageSize, 1, 100);
        return new PhanTrang(p, s);
    }

    /** Vị trí bắt đầu (0-based) cho truy vấn. */
    public int chiSoTrang() {
        return page - 1;
    }
}
