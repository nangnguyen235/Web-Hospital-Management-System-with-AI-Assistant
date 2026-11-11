import { api } from './api'
import type { PagedResult } from './danhMuc'

export type TrangThaiGiaoDich = 'DANG_XU_LY' | 'THANH_CONG' | 'THAT_BAI'
export type LoaiThanhToan = 'DAT_KHAM' | 'XET_NGHIEM'

export const TEN_TRANG_THAI_GD: Record<TrangThaiGiaoDich, string> = {
  DANG_XU_LY: 'Đang xử lý',
  THANH_CONG: 'Thành công',
  THAT_BAI: 'Thất bại',
}
export const MAU_TRANG_THAI_GD: Record<TrangThaiGiaoDich, 'warning' | 'success' | 'error'> = {
  DANG_XU_LY: 'warning',
  THANH_CONG: 'success',
  THAT_BAI: 'error',
}
export const TEN_LOAI_TT: Record<LoaiThanhToan, string> = { DAT_KHAM: 'Đặt khám', XET_NGHIEM: 'Xét nghiệm' }

export interface GiaoDich {
  id: number
  soTien: number
  phi: number
  tongTien: number
  trangThai: TrangThaiGiaoDich
  maNganHang: string | null
  phuongThuc: string | null
  loaiThanhToan: LoaiThanhToan
  taiKhoanId: number | null
  ngayTao: string
  ngayCapNhat: string | null
}

export interface KetQuaThanhToan {
  giaoDichId: number | null
  thanhCong: boolean
  trangThai: TrangThaiGiaoDich | null
  loaiThanhToan: LoaiThanhToan | null
  tongTien: number | null
  thongBao: string
}

export const vienPhiApi = {
  /** Trang kết quả gửi lại nguyên query string VNPAY trả về (return URL). */
  ketQuaVnpay: (queryString: string) =>
    api.get<KetQuaThanhToan>(`/api/vien-phi/vnpay/ket-qua${queryString.startsWith('?') ? queryString : `?${queryString}`}`).then((r) => r.data),
  giaoDich: (id: number) => api.get<GiaoDich>(`/api/vien-phi/giao-dich/${id}`).then((r) => r.data),
  cuaToi: (page: number, pageSize: number) =>
    api.get<PagedResult<GiaoDich>>('/api/vien-phi/giao-dich/cua-toi', { params: { page, pageSize } }).then((r) => r.data),
  timKiem: (q: { tu?: string; den?: string; trangThai?: TrangThaiGiaoDich; loai?: LoaiThanhToan; page: number; pageSize: number }) =>
    api.get<PagedResult<GiaoDich>>('/api/vien-phi/admin/giao-dich', { params: q }).then((r) => r.data),
}
