import { api } from './api'

export interface PagedResult<T> {
  items: T[]
  totalCount: number
  page: number
  pageSize: number
}

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

export type LoaiPhong = 'PHONG_KHAM' | 'PHONG_XET_NGHIEM'
export type LoaiDichVu = 'KHAM_BENH' | 'XET_NGHIEM' | 'KHAC'
export type DonViThuoc = 'TUYP' | 'CHAI' | 'HOP' | 'GOI' | 'CAI' | 'VIEN'

export const TEN_LOAI_PHONG: Record<LoaiPhong, string> = { PHONG_KHAM: 'Phòng khám', PHONG_XET_NGHIEM: 'Phòng xét nghiệm' }
export const TEN_LOAI_DICH_VU: Record<LoaiDichVu, string> = { KHAM_BENH: 'Khám bệnh', XET_NGHIEM: 'Xét nghiệm', KHAC: 'Khác' }
export const TEN_DON_VI: Record<DonViThuoc, string> = { TUYP: 'Tuýp', CHAI: 'Chai', HOP: 'Hộp', GOI: 'Gói', CAI: 'Cái', VIEN: 'Viên' }

export interface Phong {
  id: number
  tenPhong: string
  moTa: string | null
  khoaId: number
  tenKhoa: string
  loaiPhong: LoaiPhong
  dichVuId: number | null
  tenDichVu: string | null
  daXoa: boolean
}

export interface LuuPhong {
  tenPhong: string
  moTa: string
  khoaId: number | ''
  loaiPhong: LoaiPhong
  dichVuId: number | '' | null
}

export interface DichVu {
  id: number
  tenDichVu: string
  moTa: string | null
  khoaId: number
  tenKhoa: string
  loaiDichVu: LoaiDichVu
  gia: number
  daXoa: boolean
}

export interface LuuDichVu {
  tenDichVu: string
  moTa: string
  khoaId: number | ''
  loaiDichVu: LoaiDichVu
  gia: number | ''
}

export interface Thuoc {
  id: number
  tenThuoc: string
  gia: number
  soLuong: number
  donVi: DonViThuoc
  daXoa: boolean
}

export interface LuuThuoc {
  tenThuoc: string
  gia: number | ''
  soLuong: number | ''
  donVi: DonViThuoc
}

export interface DanToc {
  id: number
  tenDanToc: string
}

const p = (tuKhoa: string, page: number, pageSize: number, them?: Record<string, unknown>) => ({
  params: { tuKhoa: tuKhoa || undefined, page, pageSize, ...them },
})

export const danhMucApi = {
  khoaCongKhai: () => api.get<Khoa[]>('/api/danh-muc/khoa').then((r) => r.data),
  timKhoa: (tuKhoa: string, page: number, pageSize: number) =>
    api.get<PagedResult<Khoa>>('/api/danh-muc/khoa/quan-ly', p(tuKhoa, page, pageSize)).then((r) => r.data),
  taoKhoa: (body: LuuKhoa) => api.post<Khoa>('/api/danh-muc/khoa', body).then((r) => r.data),
  suaKhoa: (id: number, body: LuuKhoa) => api.put<Khoa>(`/api/danh-muc/khoa/${id}`, body).then((r) => r.data),
  xoaKhoa: (id: number) => api.delete(`/api/danh-muc/khoa/${id}`),
  khoiPhucKhoa: (id: number) => api.post(`/api/danh-muc/khoa/${id}/khoi-phuc`),

  danToc: () => api.get<DanToc[]>('/api/danh-muc/dan-toc').then((r) => r.data),

  phong: (khoaId?: number, loaiPhong?: LoaiPhong) =>
    api.get<Phong[]>('/api/danh-muc/phong', { params: { khoaId, loaiPhong } }).then((r) => r.data),
  timPhong: (tuKhoa: string, page: number, pageSize: number) =>
    api.get<PagedResult<Phong>>('/api/danh-muc/phong/quan-ly', p(tuKhoa, page, pageSize)).then((r) => r.data),
  taoPhong: (body: LuuPhong) => api.post<Phong>('/api/danh-muc/phong', body).then((r) => r.data),
  suaPhong: (id: number, body: LuuPhong) => api.put<Phong>(`/api/danh-muc/phong/${id}`, body).then((r) => r.data),
  xoaPhong: (id: number) => api.delete(`/api/danh-muc/phong/${id}`),
  khoiPhucPhong: (id: number) => api.post(`/api/danh-muc/phong/${id}/khoi-phuc`),

  dichVu: (khoaId?: number, loaiDichVu?: LoaiDichVu) =>
    api.get<DichVu[]>('/api/danh-muc/dich-vu', { params: { khoaId, loaiDichVu } }).then((r) => r.data),
  timDichVu: (tuKhoa: string, page: number, pageSize: number) =>
    api.get<PagedResult<DichVu>>('/api/danh-muc/dich-vu/quan-ly', p(tuKhoa, page, pageSize)).then((r) => r.data),
  taoDichVu: (body: LuuDichVu) => api.post<DichVu>('/api/danh-muc/dich-vu', body).then((r) => r.data),
  suaDichVu: (id: number, body: LuuDichVu) => api.put<DichVu>(`/api/danh-muc/dich-vu/${id}`, body).then((r) => r.data),
  xoaDichVu: (id: number) => api.delete(`/api/danh-muc/dich-vu/${id}`),
  khoiPhucDichVu: (id: number) => api.post(`/api/danh-muc/dich-vu/${id}/khoi-phuc`),

  chonThuoc: (tuKhoa: string) => api.get<Thuoc[]>('/api/danh-muc/thuoc', { params: { tuKhoa: tuKhoa || undefined } }).then((r) => r.data),
  timThuoc: (tuKhoa: string, page: number, pageSize: number) =>
    api.get<PagedResult<Thuoc>>('/api/danh-muc/thuoc/quan-ly', p(tuKhoa, page, pageSize)).then((r) => r.data),
  taoThuoc: (body: LuuThuoc) => api.post<Thuoc>('/api/danh-muc/thuoc', body).then((r) => r.data),
  suaThuoc: (id: number, body: LuuThuoc) => api.put<Thuoc>(`/api/danh-muc/thuoc/${id}`, body).then((r) => r.data),
  xoaThuoc: (id: number) => api.delete(`/api/danh-muc/thuoc/${id}`),
  khoiPhucThuoc: (id: number) => api.post(`/api/danh-muc/thuoc/${id}/khoi-phuc`),
}

export const dinhDangTien = (so: number | null | undefined) =>
  so == null ? '—' : new Intl.NumberFormat('vi-VN').format(so) + ' đ'
