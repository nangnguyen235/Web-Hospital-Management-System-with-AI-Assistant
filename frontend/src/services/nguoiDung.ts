import type { VaiTro } from '../auth/jwt'
import { api } from './api'

export interface DangNhapResponse {
  accessToken: string
  hetHan: string
  vaiTro: VaiTro
  hoTen: string
}

export interface ThongTinTaiKhoan {
  id: number
  hoTen: string
  email: string | null
  soDienThoai: string
  vaiTro: VaiTro
  khoaId: number | null
  laTruongKhoa: boolean
  anhDaiDienUrl: string | null
}

export const nguoiDungApi = {
  dangNhap: (tenDangNhap: string, matKhau: string) =>
    api.post<DangNhapResponse>('/api/nguoi-dung/dang-nhap', { tenDangNhap, matKhau }).then((r) => r.data),
  toi: () => api.get<ThongTinTaiKhoan>('/api/nguoi-dung/toi').then((r) => r.data),
}
