import { api } from './api'

export interface ThongKeTrangChu {
  soBacSi: number
  soKhoa: number
  soLuotKham: number
}

export const thongKeApi = {
  trangChu: () => api.get<ThongKeTrangChu>('/api/thong-ke/trang-chu').then((r) => r.data),
}
