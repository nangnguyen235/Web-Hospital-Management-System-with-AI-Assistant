import type { VaiTro } from '../auth/jwt'
import { api } from './api'
import type { PagedResult } from './danhMuc'

export type GioiTinh = 'NAM' | 'NU'
export type HocHam = 'BS' | 'THS' | 'TS' | 'PGS' | 'GS'
export type LoaiNhanVien = 'BAC_SI' | 'DIEU_DUONG' | 'QUAN_TRI'

export const TEN_GIOI_TINH: Record<GioiTinh, string> = { NAM: 'Nam', NU: 'Nữ' }
export const TEN_HOC_HAM: Record<HocHam, string> = { BS: 'BS.', THS: 'ThS.BS.', TS: 'TS.BS.', PGS: 'PGS.TS.', GS: 'GS.TS.' }
export const TEN_LOAI_NV: Record<LoaiNhanVien, string> = { BAC_SI: 'Bác sĩ', DIEU_DUONG: 'Điều dưỡng', QUAN_TRI: 'Quản trị viên' }

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
  nhanVienId: number | null
  khoaId: number | null
  laTruongKhoa: boolean
  anhDaiDienUrl: string | null
}

export interface HoSo {
  id: number
  hoTen: string
  soDienThoai: string | null
  email: string | null
  gioiTinh: GioiTinh
  soCccd: string | null
  diaChi: string | null
  ngaySinh: string
  danTocId: number | null
  benhNhanId: number | null
  maBenhNhan: string | null
  taiKhoanId: number | null
  daXoa: boolean
}

export interface LuuHoSo {
  hoTen: string
  soDienThoai: string
  email: string
  gioiTinh: GioiTinh | ''
  soCccd: string
  diaChi: string
  ngaySinh: string
  danTocId: number | '' | null
}

export interface HoSoTimLai {
  id: number
  hoTen: string
  ngaySinh: string
  gioiTinh: GioiTinh
  emailChe: string | null
  coEmail: boolean
}

export interface ThongBao {
  id: number
  tieuDe: string
  noiDung: string
  duLieu: string | null
  trangThai: 'CHUA_NHAN' | 'DA_NHAN' | 'DA_XEM' | 'DA_XOA'
  ngayTao: string
}

export interface BacSiCongKhai {
  id: number
  hoTen: string
  gioiTinh: GioiTinh
  hocHam: HocHam | null
  khoaId: number
  tenKhoa: string
  diemDanhGia: number | null
  anhDaiDienUrl: string | null
}

export interface NhanVien {
  id: number
  hoTen: string
  email: string | null
  soDienThoai: string
  soCccd: string | null
  gioiTinh: GioiTinh
  ngaySinh: string | null
  loaiNhanVien: LoaiNhanVien
  hocHam: HocHam | null
  khoaId: number | null
  tenKhoa: string | null
  laTruongKhoa: boolean
  diemDanhGia: number | null
  anhDaiDienUrl: string | null
  taiKhoanId: number | null
  daXoa: boolean
}

export interface LuuNhanVien {
  hoTen: string
  email: string
  soDienThoai: string
  matKhau: string
  gioiTinh: GioiTinh | ''
  ngaySinh: string
  soCccd: string
  loaiNhanVien: LoaiNhanVien
  hocHam: HocHam | ''
  khoaId: number | ''
}

export interface NhanVienGon {
  id: number
  hoTen: string
  hocHam: HocHam | null
  laTruongKhoa: boolean
}

export interface TaiKhoanAdmin {
  id: number
  hoTen: string
  email: string | null
  soDienThoai: string
  vaiTro: VaiTro
  soLuongHoSo: number
  daXoa: boolean
  ngayTao: string
}

export interface BenhNhanAdmin {
  id: number
  maBenhNhan: string
  hoTen: string
  soDienThoai: string | null
  soCccd: string | null
  ngaySinh: string
  gioiTinh: GioiTinh
  daXoa: boolean
}

/** Bỏ chuỗi rỗng trước khi gửi (backend coi null là "không có"). */
export const gonHoSo = (h: LuuHoSo) => ({
  ...h,
  gioiTinh: h.gioiTinh || null,
  danTocId: h.danTocId === '' ? null : h.danTocId,
  soCccd: h.soCccd || null,
  email: h.email || null,
  soDienThoai: h.soDienThoai || null,
  ngaySinh: h.ngaySinh || null,
})

const trang = (page: number, pageSize: number, them?: Record<string, unknown>) => ({ params: { page, pageSize, ...them } })

export const nguoiDungApi = {
  dangNhap: (tenDangNhap: string, matKhau: string) =>
    api.post<DangNhapResponse>('/api/nguoi-dung/dang-nhap', { tenDangNhap, matKhau }).then((r) => r.data),
  toi: () => api.get<ThongTinTaiKhoan>('/api/nguoi-dung/toi').then((r) => r.data),
  doiMatKhau: (matKhauCu: string, matKhauMoi: string) => api.post('/api/nguoi-dung/toi/doi-mat-khau', { matKhauCu, matKhauMoi }),

  guiOtpDangKy: (email: string, soDienThoai: string) => api.post('/api/nguoi-dung/dang-ky/gui-otp', { email, soDienThoai }),
  dangKy: (body: { email: string; soDienThoai: string; hoTen: string; matKhau: string; otp: string }) =>
    api.post<DangNhapResponse>('/api/nguoi-dung/dang-ky', body).then((r) => r.data),
  guiOtpDangNhap: (email: string) => api.post('/api/nguoi-dung/dang-nhap-otp/gui-otp', { email }),
  dangNhapOtp: (email: string, otp: string) =>
    api.post<DangNhapResponse>('/api/nguoi-dung/dang-nhap-otp', { email, otp }).then((r) => r.data),
  guiOtpQuenMatKhau: (email: string) => api.post('/api/nguoi-dung/quen-mat-khau/gui-otp', { email }),
  datLaiMatKhau: (body: { email: string; otp: string; matKhauMoi: string; nhapLaiMatKhau: string }) =>
    api.post('/api/nguoi-dung/quen-mat-khau', body),

  hoSo: () => api.get<HoSo[]>('/api/nguoi-dung/ho-so').then((r) => r.data),
  taoHoSo: (body: LuuHoSo) => api.post<HoSo>('/api/nguoi-dung/ho-so', gonHoSo(body)).then((r) => r.data),
  suaHoSo: (id: number, body: LuuHoSo) => api.put<HoSo>(`/api/nguoi-dung/ho-so/${id}`, gonHoSo(body)).then((r) => r.data),
  xoaHoSo: (id: number) => api.delete(`/api/nguoi-dung/ho-so/${id}`),
  timLaiHoSo: (q: { maBenhNhan?: string; hoTen?: string; namSinh?: number; gioiTinh?: GioiTinh }) =>
    api.get<HoSoTimLai[]>('/api/nguoi-dung/ho-so/tim-lai', { params: q }).then((r) => r.data),
  guiOtpTimLai: (id: number) => api.post(`/api/nguoi-dung/ho-so/tim-lai/${id}/gui-otp`),
  xacNhanTimLai: (id: number, otp: string) => api.post<HoSo>(`/api/nguoi-dung/ho-so/tim-lai/${id}`, { otp }).then((r) => r.data),

  thongBao: (page: number, pageSize: number) =>
    api.get<PagedResult<ThongBao>>('/api/nguoi-dung/thong-bao', trang(page, pageSize)).then((r) => r.data),
  soThongBaoChuaXem: () => api.get<number>('/api/nguoi-dung/thong-bao/so-chua-xem').then((r) => r.data),
  daXemThongBao: (id: number) => api.post(`/api/nguoi-dung/thong-bao/${id}/da-xem`),
  xoaThongBao: (id: number) => api.delete(`/api/nguoi-dung/thong-bao/${id}`),

  timBacSi: (q: { tuKhoa?: string; khoaId?: number; hocHam?: HocHam; page: number; pageSize: number }) =>
    api.get<PagedResult<BacSiCongKhai>>('/api/nguoi-dung/bac-si', { params: q }).then((r) => r.data),
  bacSi: (id: number) => api.get<BacSiCongKhai>(`/api/nguoi-dung/bac-si/${id}`).then((r) => r.data),
  nhanVienKhoaToi: (loai: LoaiNhanVien) =>
    api.get<NhanVienGon[]>('/api/nguoi-dung/nhan-vien/khoa-toi', { params: { loai } }).then((r) => r.data),

  // Điều dưỡng
  traHoSo: (hoTen: string, soCccd: string) =>
    api.get<HoSo[]>('/api/nguoi-dung/dieu-duong/ho-so', { params: { hoTen: hoTen || undefined, soCccd: soCccd || undefined } }).then((r) => r.data),
  traCccd: (soCccd: string) =>
    api.get<BenhNhanAdmin>('/api/nguoi-dung/dieu-duong/benh-nhan', { params: { soCccd } }).then((r) => r.data),
  taoHoSoQuay: (body: LuuHoSo) => api.post<HoSo>('/api/nguoi-dung/dieu-duong/ho-so', gonHoSo(body)).then((r) => r.data),
  suaHoSoDieuDuong: (id: number, body: LuuHoSo) => api.put<HoSo>(`/api/nguoi-dung/dieu-duong/ho-so/${id}`, gonHoSo(body)).then((r) => r.data),
  lienKetCccd: (id: number, soCccd: string) => api.post<HoSo>(`/api/nguoi-dung/dieu-duong/ho-so/${id}/lien-ket`, { soCccd }).then((r) => r.data),

  // Admin
  timNhanVien: (tuKhoa: string, page: number, pageSize: number, loai?: LoaiNhanVien) =>
    api.get<PagedResult<NhanVien>>('/api/nguoi-dung/admin/nhan-vien', trang(page, pageSize, { tuKhoa: tuKhoa || undefined, loai })).then((r) => r.data),
  taoNhanVien: (body: LuuNhanVien) => api.post<NhanVien>('/api/nguoi-dung/admin/nhan-vien', gonNhanVien(body)).then((r) => r.data),
  suaNhanVien: (id: number, body: LuuNhanVien) => api.put<NhanVien>(`/api/nguoi-dung/admin/nhan-vien/${id}`, gonNhanVien(body)).then((r) => r.data),
  doiKhoa: (id: number, khoaId: number | null) => api.put<NhanVien>(`/api/nguoi-dung/admin/nhan-vien/${id}/khoa`, { khoaId }).then((r) => r.data),
  datTruongKhoa: (id: number) => api.post<NhanVien>(`/api/nguoi-dung/admin/nhan-vien/${id}/truong-khoa`).then((r) => r.data),
  boTruongKhoa: (id: number) => api.delete<NhanVien>(`/api/nguoi-dung/admin/nhan-vien/${id}/truong-khoa`).then((r) => r.data),
  xoaNhanVien: (id: number) => api.delete(`/api/nguoi-dung/admin/nhan-vien/${id}`),
  khoiPhucNhanVien: (id: number) => api.post(`/api/nguoi-dung/admin/nhan-vien/${id}/khoi-phuc`),

  timTaiKhoan: (tuKhoa: string, page: number, pageSize: number, loai?: string) =>
    api.get<PagedResult<TaiKhoanAdmin>>('/api/nguoi-dung/admin/tai-khoan', trang(page, pageSize, { tuKhoa: tuKhoa || undefined, loai })).then((r) => r.data),
  chiTietTaiKhoan: (id: number) =>
    api.get<{ taiKhoan: TaiKhoanAdmin; hoSo: HoSo[] }>(`/api/nguoi-dung/admin/tai-khoan/${id}`).then((r) => r.data),
  xoaTaiKhoan: (id: number) => api.delete(`/api/nguoi-dung/admin/tai-khoan/${id}`),
  khoiPhucTaiKhoan: (id: number) => api.post(`/api/nguoi-dung/admin/tai-khoan/${id}/khoi-phuc`),

  timBenhNhan: (tuKhoa: string, page: number, pageSize: number) =>
    api.get<PagedResult<BenhNhanAdmin>>('/api/nguoi-dung/admin/benh-nhan', trang(page, pageSize, { tuKhoa: tuKhoa || undefined })).then((r) => r.data),
  chiTietBenhNhan: (id: number) =>
    api.get<{ benhNhan: BenhNhanAdmin; hoSo: HoSo[] }>(`/api/nguoi-dung/admin/benh-nhan/${id}`).then((r) => r.data),
  xoaHoSoAdmin: (id: number) => api.delete(`/api/nguoi-dung/admin/ho-so/${id}`),
  khoiPhucHoSoAdmin: (id: number) => api.post(`/api/nguoi-dung/admin/ho-so/${id}/khoi-phuc`),
}

function gonNhanVien(n: LuuNhanVien) {
  return {
    ...n,
    gioiTinh: n.gioiTinh || null,
    ngaySinh: n.ngaySinh || null,
    soCccd: n.soCccd || null,
    hocHam: n.hocHam || null,
    khoaId: n.khoaId === '' ? null : n.khoaId,
    matKhau: n.matKhau || null,
  }
}
