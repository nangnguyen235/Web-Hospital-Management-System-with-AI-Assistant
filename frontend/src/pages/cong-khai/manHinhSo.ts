/** Số thứ tự hiển thị 2 chữ số như TheDuck (5 → "05"). */
export const dinhDangSo = (so: number) => String(so).padStart(2, '0')

/** NFR-01: màn hình số đang gọi cập nhật ≤ 5 giây. */
export const CHU_KY_CAP_NHAT_MS = 5000
