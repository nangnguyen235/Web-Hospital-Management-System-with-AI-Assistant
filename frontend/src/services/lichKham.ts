import { api } from './api'

export type Buoi = 'SANG' | 'CHIEU'
export type TrangThaiPhieu = 'CHO_THANH_TOAN' | 'HET_HAN' | 'DA_XAC_NHAN' | 'DA_TIEP_NHAN' | 'VANG_MAT'
export type KenhDat = 'WEB' | 'CHATBOT' | 'QUAY'

export const TEN_BUOI: Record<Buoi, string> = { SANG: 'Sáng', CHIEU: 'Chiều' }
export const TEN_TRANG_THAI_PHIEU: Record<TrangThaiPhieu, string> = {
  CHO_THANH_TOAN: 'Chờ thanh toán',
  HET_HAN: 'Hết hạn',
  DA_XAC_NHAN: 'Đã xác nhận',
  DA_TIEP_NHAN: 'Đã tiếp nhận',
  VANG_MAT: 'Vắng mặt',
}
export const MAU_TRANG_THAI_PHIEU: Record<TrangThaiPhieu, 'default' | 'warning' | 'success' | 'info' | 'error'> = {
  CHO_THANH_TOAN: 'warning',
  HET_HAN: 'default',
  DA_XAC_NHAN: 'success',
  DA_TIEP_NHAN: 'info',
  VANG_MAT: 'error',
}

/** thuTrongTuan: 1 = Thứ 2 … 7 = Chủ nhật (ISO). */
export const tenThu = (thu: number) => (thu === 7 ? 'Chủ nhật' : `Thứ ${thu + 1}`)

/** Chỉ số + phòng + khoa, không họ tên (NFR-03). */
export interface SoDangGoi {
  lichKhamId: number
  soDangGoi: number
  cacSoVuaGoi: number[]
  tenPhong: string
  tenKhoa: string
  ngay: string
  buoi: Buoi
}

export interface Khung {
  id: number
  chiSoKhung: number
  gioBatDau: string
  gioKetThuc: string
  soLuongToiDa: number
  soLuongDaDat: number
  conCho: number
  daQua: boolean
}

export interface Lich {
  id: number
  bacSiId: number
  tenBacSi: string
  phongId: number
  tenPhong: string
  dichVuId: number
  tenDichVu: string
  gia: number
  ngay: string
  buoi: Buoi
  thuTrongTuan: number
  soLuot: number
  soLuotMoiKhung: number
  soDangGoi: number
  soDaDat: number
  khung: Khung[]
}

export interface Phieu {
  id: number
  maPhieu: string
  soThuTu: number
  trangThai: TrangThaiPhieu
  hoSoId: number
  hoTen: string
  lichKhamId: number
  ngay: string
  buoi: Buoi
  gioBatDau: string
  gioKetThuc: string
  phongId: number
  tenPhong: string
  tenKhoa: string
  tenBacSi: string
  tenDichVu: string
  giaDichVu: number
  kenhDat: KenhDat
  giaoDichId: number
  benhAnId: number | null
  daDanhGia: boolean
  soDangGoi: number
  ngayTao: string
}

export interface KetQuaDatKham {
  giaoDichId: number
  urlThanhToan: string
  hetHan: string
  soTien: number
  phi: number
  phieu: Phieu[]
}

export interface KetQuaQuay {
  phieu: Phieu
  urlThanhToan: string | null
  daThanhToan: boolean
}

export interface GoiTiep {
  lichKhamId: number
  soDangGoi: number
  soLonNhat: number
  cacSoVuaGoi: number[]
}

export interface LichDieuDuong {
  id: number
  dieuDuongId: number
  tenDieuDuong: string
  phongId: number
  tenPhong: string
  thuTrongTuan: number
  buoi: Buoi
}

export interface PhongHomNay {
  phongId: number
  tenPhong: string
  lich: Lich[]
}

export interface TaoLich {
  bacSiId: number
  dichVuId: number
  phongId: number
  ngaySang: string[]
  ngayChieu: string[]
  soLuotMoiKhung: number
}

export interface PhanCong {
  dieuDuongId: number
  phongId: number
  cacCa: { thuTrongTuan: number; buoi: Buoi }[]
}

type KhoangNgay = { tu?: string; den?: string }

export const lichKhamApi = {
  // Công khai
  soDangGoi: (lichKhamId: string | number) =>
    api.get<SoDangGoi>(`/api/lich-kham/${lichKhamId}/so-dang-goi`).then((r) => r.data),
  lichCuaBacSi: (bacSiId: number, q: KhoangNgay = {}) =>
    api.get<Lich[]>(`/api/lich-kham/bac-si/${bacSiId}/lich`, { params: q }).then((r) => r.data),
  chiTietLich: (id: number) => api.get<Lich>(`/api/lich-kham/lich/${id}`).then((r) => r.data),

  // Trưởng khoa (FR-BS-11…14)
  taoLich: (body: TaoLich) => api.post<Lich[]>('/api/lich-kham/lich', body).then((r) => r.data),
  suaLich: (id: number, body: { bacSiId: number; soLuotMoiKhung: number }) =>
    api.put<Lich>(`/api/lich-kham/lich/${id}`, body).then((r) => r.data),
  xoaLich: (id: number) => api.delete(`/api/lich-kham/lich/${id}`),
  lichKhoa: (q: KhoangNgay & { phongId?: number; bacSiId?: number } = {}) =>
    api.get<Lich[]>('/api/lich-kham/lich/khoa', { params: q }).then((r) => r.data),
  bacSiTrong: (ngay: string, buoi?: Buoi) =>
    api.get<{ id: number; hoTen: string }[]>('/api/lich-kham/lich/khoa/bac-si-trong', { params: { ngay, buoi } }).then((r) => r.data),

  // Bác sĩ (FR-BS-02)
  lichCuaToi: (q: KhoangNgay = {}) => api.get<Lich[]>('/api/lich-kham/lich/cua-toi', { params: q }).then((r) => r.data),

  // Bệnh nhân (FR-BN-08…10)
  datKham: (hoSoId: number, khungGioIds: number[]) =>
    api.post<KetQuaDatKham>('/api/lich-kham/dat-kham', { hoSoId, khungGioIds }).then((r) => r.data),
  phieuCuaHoSo: (hoSoId: number) => api.get<Phieu[]>('/api/lich-kham/phieu', { params: { hoSoId } }).then((r) => r.data),
  phieu: (id: number) => api.get<Phieu>(`/api/lich-kham/phieu/${id}`).then((r) => r.data),
  phieuCuaGiaoDich: (giaoDichId: number) =>
    api.get<Phieu[]>(`/api/lich-kham/phieu/giao-dich/${giaoDichId}`).then((r) => r.data),

  // Điều dưỡng (FR-DD-02, 07, 08)
  goiTiep: (lichKhamId: number) => api.post<GoiTiep>(`/api/lich-kham/${lichKhamId}/goi-tiep`).then((r) => r.data),
  phongHomNay: () => api.get<PhongHomNay[]>('/api/lich-kham/dieu-duong/phong-hom-nay').then((r) => r.data),
  lichHomNayCuaPhong: (phongId: number) => api.get<Lich[]>(`/api/lich-kham/phong/${phongId}/hom-nay`).then((r) => r.data),
  phanCongCuaToi: () => api.get<LichDieuDuong[]>('/api/lich-kham/phan-cong/cua-toi').then((r) => r.data),
  taoPhieuQuay: (hoSoId: number, lichKhamId: number) =>
    api.post<KetQuaQuay>('/api/lich-kham/quay/phieu', { hoSoId, lichKhamId }).then((r) => r.data),

  // Điều dưỡng trưởng (FR-DD-12)
  phanCongKhoa: () => api.get<LichDieuDuong[]>('/api/lich-kham/phan-cong').then((r) => r.data),
  phanCong: (body: PhanCong) => api.post<LichDieuDuong[]>('/api/lich-kham/phan-cong', body).then((r) => r.data),
  xoaPhanCong: (ids: number[]) =>
    api.delete('/api/lich-kham/phan-cong', { params: { ids: ids.join(',') } }),

  // Admin (FR-AD-10)
  lichAdmin: (q: KhoangNgay & { khoaId?: number; phongId?: number; bacSiId?: number } = {}) =>
    api.get<Lich[]>('/api/lich-kham/admin/lich', { params: q }).then((r) => r.data),
}

/** Ngày dạng yyyy-MM-dd theo giờ máy (giờ Việt Nam ở client người dùng). */
export const ngayISO = (d: Date) => {
  const z = (n: number) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${z(d.getMonth() + 1)}-${z(d.getDate())}`
}

export const congNgay = (iso: string, soNgay: number) => {
  const [y, m, d] = iso.split('-').map(Number)
  return ngayISO(new Date(y, m - 1, d + soNgay))
}

/** yyyy-MM-dd → dd/MM/yyyy */
export const hienNgay = (iso: string | null | undefined) => {
  if (!iso) return '—'
  const [y, m, d] = iso.slice(0, 10).split('-')
  return `${d}/${m}/${y}`
}

export const hienNgayGio = (iso: string | null | undefined) =>
  iso ? `${iso.slice(11, 16)} ${hienNgay(iso)}` : '—'
