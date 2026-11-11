package vn.nhom6.his.shared.kernel;

/** Loại lỗi nghiệp vụ → mã HTTP (GlobalExceptionHandler). */
public enum LoaiLoi {
    /** 400. */
    NGHIEP_VU,
    /** 404. */
    KHONG_TIM_THAY,
    /** 409. */
    XUNG_DOT,
    /** 403. */
    KHONG_CO_QUYEN
}
