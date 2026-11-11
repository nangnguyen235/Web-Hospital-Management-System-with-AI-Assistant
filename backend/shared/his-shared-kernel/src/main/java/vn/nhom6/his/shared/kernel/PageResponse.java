package vn.nhom6.his.shared.kernel;

import java.util.List;

/** Trang dữ liệu trả cho frontend: {items, totalCount, page (từ 1), pageSize} — KHÔNG trả Page của Spring. */
public record PageResponse<T>(List<T> items, long totalCount, int page, int pageSize) {
}
