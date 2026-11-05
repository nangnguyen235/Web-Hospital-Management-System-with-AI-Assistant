import { api } from './api'

export interface Khoa {
  id: number
  tenKhoa: string
  moTa: string | null
  tuKhoaTrieuChung: string | null
  daXoa: boolean
}

export interface LuuKhoa {
  tenKhoa: string
  moTa: string
  tuKhoaTrieuChung: string
}

export interface PagedResult<T> {
  items: T[]
  totalCount: number
  page: number
  pageSize: number
}

export const danhMucApi = {
  khoaCongKhai: () => api.get<Khoa[]>('/api/danh-muc/khoa').then((r) => r.data),
  timKhoa: (tuKhoa: string, page: number, pageSize: number) =>
    api
      .get<PagedResult<Khoa>>('/api/danh-muc/khoa/quan-ly', { params: { tuKhoa: tuKhoa || undefined, page, pageSize } })
      .then((r) => r.data),
  taoKhoa: (body: LuuKhoa) => api.post<Khoa>('/api/danh-muc/khoa', body).then((r) => r.data),
  suaKhoa: (id: number, body: LuuKhoa) => api.put<Khoa>(`/api/danh-muc/khoa/${id}`, body).then((r) => r.data),
  xoaKhoa: (id: number) => api.delete(`/api/danh-muc/khoa/${id}`),
  khoiPhucKhoa: (id: number) => api.post(`/api/danh-muc/khoa/${id}/khoi-phuc`),
}
