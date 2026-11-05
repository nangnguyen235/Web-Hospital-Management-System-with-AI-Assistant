/** Vai trò trong claim `vai_tro` của JWT (BR-01). Backend mới là nơi kiểm quyền. */
export type VaiTro = 'BENH_NHAN' | 'BAC_SI' | 'DIEU_DUONG' | 'QUAN_TRI'

export interface ThongTinToken {
  taiKhoanId: number
  vaiTro: VaiTro
  laTruongKhoa: boolean
  hetHan: number // epoch giây
}

/** Đọc payload JWT (không kiểm chữ ký — chỉ để ẩn/hiện giao diện). Token hỏng hoặc hết hạn → null. */
export function giaiMaToken(token: string | null, bayGio = Date.now()): ThongTinToken | null {
  if (!token) return null
  try {
    const phan = token.split('.')[1]
    const json = decodeURIComponent(
      atob(phan.replace(/-/g, '+').replace(/_/g, '/'))
        .split('')
        .map((c) => '%' + c.charCodeAt(0).toString(16).padStart(2, '0'))
        .join(''),
    )
    const p = JSON.parse(json) as Record<string, string | number>
    const thongTin: ThongTinToken = {
      taiKhoanId: Number(p.tai_khoan_id),
      vaiTro: p.vai_tro as VaiTro,
      laTruongKhoa: p.la_truong_khoa === 'true',
      hetHan: Number(p.exp),
    }
    return thongTin.hetHan * 1000 > bayGio ? thongTin : null
  } catch {
    return null
  }
}

/** Trang đầu tiên của từng vai trò sau khi đăng nhập. */
export const TRANG_CHU_VAI_TRO: Record<VaiTro, string> = {
  BENH_NHAN: '/tai-khoan/ho-so',
  BAC_SI: '/bac-si/benh-nhan',
  DIEU_DUONG: '/dieu-duong/tiep-nhan',
  QUAN_TRI: '/admin/khoa',
}

export const TEN_VAI_TRO: Record<VaiTro, string> = {
  BENH_NHAN: 'Bệnh nhân',
  BAC_SI: 'Bác sĩ',
  DIEU_DUONG: 'Điều dưỡng',
  QUAN_TRI: 'Quản trị viên',
}
