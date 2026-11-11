import { api } from './api'

export interface ThongKeTrangChu {
  soBacSi: number
  soKhoa: number
  soLuotKham: number
}

export interface TheoKhoa {
  khoaId: number
  tenKhoa: string
  luotDat: number
  doanhThu: number
}

export interface TongQuan {
  tu: string
  den: string
  tongDoanhThu: number
  luotDat: number
  soBenhNhan: number
  soKhoa: number
  soBacSi: number
  theoKhoa: TheoKhoa[]
  theoNgay: { ngay: string; luotDat: number }[]
  khoaNhieuLuotNhat: TheoKhoa | null
}

export interface LichSuKhamAdmin {
  benhAnId: number
  lichKhamId: number
  trangThai: string
  chanDoan: string | null
  ngayTaiKham: string | null
  ngayTao: string
}

export const thongKeApi = {
  trangChu: () => api.get<ThongKeTrangChu>('/api/thong-ke/trang-chu').then((r) => r.data),
  admin: (tu?: string, den?: string) => api.get<TongQuan>('/api/thong-ke/admin', { params: { tu, den } }).then((r) => r.data),
  lichSuBenhNhan: (benhNhanId: number) =>
    api.get<LichSuKhamAdmin[]>(`/api/thong-ke/admin/benh-nhan/${benhNhanId}/lich-su`).then((r) => r.data),
}
