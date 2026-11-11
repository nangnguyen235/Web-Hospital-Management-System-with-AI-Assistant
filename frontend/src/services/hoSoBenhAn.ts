import { api } from './api'

export type TrangThaiBenhAn = 'CHO_KHAM' | 'DANG_KHAM' | 'DA_KHAM'
export type TrangThaiXetNghiem = 'CHO_THUC_HIEN' | 'DANG_THUC_HIEN' | 'HOAN_THANH'
export type HanhDongNhac = 'DA_NHAN' | 'DA_UONG' | 'BO_QUA'

export const TEN_TRANG_THAI_BENH_AN: Record<TrangThaiBenhAn, string> = {
  CHO_KHAM: 'Chờ khám',
  DANG_KHAM: 'Đang khám',
  DA_KHAM: 'Đã khám',
}
export const TEN_TRANG_THAI_XN: Record<TrangThaiXetNghiem, string> = {
  CHO_THUC_HIEN: 'Chờ thực hiện',
  DANG_THUC_HIEN: 'Đang thực hiện',
  HOAN_THANH: 'Hoàn thành',
}

export interface PhieuTiepNhan {
  phieuId: number
  maPhieu: string
  soThuTu: number
  hoSoId: number
  hoTen: string
  ngaySinh: string
  gioiTinh: string
  soCccd: string | null
  soDienThoai: string | null
  daLienKet: boolean
  maBenhNhan: string | null
  benhAnId: number | null
}

export interface KetQuaTiepNhan {
  benhAnId: number
  maBenhNhan: string
  soThuTu: number
  hoTen: string
}

export interface BenhAnGon {
  id: number
  phieuDatKhamId: number
  soThuTu: number
  hoSoId: number
  hoTen: string
  ngaySinh: string
  gioiTinh: string
  maBenhNhan: string
  trangThai: TrangThaiBenhAn
  lichKhamId: number
  ngayTao: string
}

export interface XetNghiem {
  id: number
  maXetNghiem: string
  soThuTu: number
  dichVuId: number
  tenDichVu: string
  phongId: number
  tenPhong: string
  gia: number
  ghiChu: string | null
  ketQua: string | null
  coFileKetQua: boolean
  trangThai: TrangThaiXetNghiem
  daThanhToan: boolean
  giaoDichId: number | null
  ngay: string
  benhAnId: number | null
  hoTen: string | null
}

export interface DongThuoc {
  id: number
  thuocId: number
  tenThuoc: string
  donVi: string
  soLuong: number
  gia: number
  thanhTien: number
  soLanMoiNgay: number
  soNgay: number
  soLuongMoiLan: number
  sang: boolean
  trua: boolean
  chieu: boolean
  toi: boolean
  huongDan: string | null
}

export interface DonThuoc {
  id: number
  maDonThuoc: string
  tongTien: number
  thuoc: DongThuoc[]
  ngayTao: string
}

export interface BenhAn {
  id: number
  phieuDatKhamId: number
  lichKhamId: number
  lichKhamTruocId: number | null
  benhNhanId: number
  maBenhNhan: string
  hoSoId: number
  hoTen: string
  ngaySinh: string
  gioiTinh: string
  trangThai: TrangThaiBenhAn
  trieuChung: string | null
  chanDoan: string | null
  ngayTaiKham: string | null
  ngayKham: string
  tenBacSi: string | null
  tenKhoa: string | null
  tenPhong: string | null
  daDanhGia: boolean
  xetNghiem: XetNghiem[]
  donThuoc: DonThuoc | null
  ngayTao: string
}

export interface GhiBenhAn {
  trieuChung: string
  chanDoan: string
  ngayTaiKham: string | null
}

export interface LichSu {
  benhAnId: number
  phieuDatKhamId: number
  ngayKham: string
  tenBacSi: string | null
  tenKhoa: string | null
  chanDoan: string | null
  trangThai: TrangThaiBenhAn
  ngayTaiKham: string | null
  daDanhGia: boolean
  hoTen: string
}

export interface KeThuoc {
  thuocId: number
  soLuong: number
  soLanMoiNgay: number
  soNgay: number
  soLuongMoiLan: number
  sang: boolean
  trua: boolean
  chieu: boolean
  toi: boolean
  huongDan: string
}

export interface ThanhToanXetNghiem {
  xetNghiemId: number
  giaoDichId: number
  urlThanhToan: string
  tongTien: number
}

export interface SoPhongXetNghiem {
  phongId: number
  tenPhong: string
  soDangGoi: number
  soLonNhat: number
}

export interface NhacThuoc {
  id: number
  chiTietDonThuocId: number
  hoSoId: number
  tenThuoc: string
  ngayBatDau: string
  ngayKetThuc: string | null
  tongSoLuong: number
  soLuongConLai: number
}

export interface TaoNhacThuoc {
  hoSoId: number
  chiTietDonThuocId: number
  ngayBatDau: string
  tongSoLuong: number
  cacLan: { gio: string; soLuong: number }[]
}

export interface LanNhac {
  id: number
  nhacThuocId: number
  maXacNhan: string
  thoiDiemNhac: string
  thuTuNhac: number
  tenThuoc: string
  donVi: string
  hoTen: string
  soLuong: number
  daNhan: boolean
  daUong: boolean
  boQua: boolean
}

const goc = '/api/ho-so-benh-an'

export const hoSoBenhAnApi = {
  // Điều dưỡng: tiếp nhận (FR-DD-03…05)
  kiemTraPhieu: (maPhieu: string, phongId: number) =>
    api.get<PhieuTiepNhan>(`${goc}/tiep-nhan/kiem-tra`, { params: { maPhieu, phongId } }).then((r) => r.data),
  tiepNhan: (maPhieu: string, phongId: number, soCccd: string | null) =>
    api.post<KetQuaTiepNhan>(`${goc}/tiep-nhan`, { maPhieu, phongId, soCccd }).then((r) => r.data),

  // Bác sĩ (FR-BS-03…10)
  benhAnCuaLich: (lichKhamId: number, trangThai?: TrangThaiBenhAn) =>
    api.get<BenhAnGon[]>(`${goc}/lich/${lichKhamId}`, { params: { trangThai } }).then((r) => r.data),
  chiTiet: (id: number) => api.get<BenhAn>(`${goc}/benh-an/${id}`).then((r) => r.data),
  nhanKham: (id: number) => api.post<BenhAn>(`${goc}/benh-an/${id}/nhan-kham`).then((r) => r.data),
  ghi: (id: number, body: GhiBenhAn) =>
    api.put<BenhAn>(`${goc}/benh-an/${id}`, { ...body, ngayTaiKham: body.ngayTaiKham || null }).then((r) => r.data),
  hoanTat: (id: number) => api.post<BenhAn>(`${goc}/benh-an/${id}/hoan-tat`).then((r) => r.data),
  lichSuTrongKhoa: (id: number) => api.get<LichSu[]>(`${goc}/benh-an/${id}/lich-su`).then((r) => r.data),
  chiDinh: (id: number, dichVuId: number, ghiChu: string) =>
    api.post<XetNghiem>(`${goc}/benh-an/${id}/xet-nghiem`, { dichVuId, ghiChu }).then((r) => r.data),
  xoaXetNghiem: (id: number) => api.delete(`${goc}/xet-nghiem/${id}`),
  keThuoc: (id: number, body: KeThuoc) =>
    api.post<{ donThuoc: DonThuoc; canhBao: string | null }>(`${goc}/benh-an/${id}/thuoc`, body).then((r) => r.data),
  xoaThuoc: (chiTietId: number) => api.delete<DonThuoc>(`${goc}/thuoc/${chiTietId}`).then((r) => r.data),

  // Điều dưỡng phòng xét nghiệm (FR-DD-09…11)
  xetNghiemCuaPhong: (phongId: number, trangThai?: TrangThaiXetNghiem) =>
    api.get<XetNghiem[]>(`${goc}/xet-nghiem/phong/${phongId}`, { params: { trangThai } }).then((r) => r.data),
  soPhong: (phongId: number) => api.get<SoPhongXetNghiem>(`${goc}/xet-nghiem/phong/${phongId}/so`).then((r) => r.data),
  goiTiepXetNghiem: (phongId: number) =>
    api.post<SoPhongXetNghiem>(`${goc}/xet-nghiem/phong/${phongId}/goi-tiep`).then((r) => r.data),
  nhanXetNghiem: (id: number) => api.post<XetNghiem>(`${goc}/xet-nghiem/${id}/nhan`).then((r) => r.data),
  hoanTatXetNghiem: (id: number, ketQua: string, file: File | null) => {
    const form = new FormData()
    if (ketQua) form.append('ketQua', ketQua)
    if (file) form.append('file', file)
    return api.post<XetNghiem>(`${goc}/xet-nghiem/${id}/hoan-tat`, form).then((r) => r.data)
  },

  // Tra mã, thanh toán, kết quả (FR-BN-12, 13)
  traMa: (ma: string) => api.get<XetNghiem>(`${goc}/xet-nghiem/ma/${encodeURIComponent(ma)}`).then((r) => r.data),
  thanhToan: (ma: string) =>
    api.post<ThanhToanXetNghiem>(`${goc}/xet-nghiem/ma/${encodeURIComponent(ma)}/thanh-toan`).then((r) => r.data),
  urlKetQua: (id: number) => api.get<string>(`${goc}/xet-nghiem/${id}/ket-qua`).then((r) => r.data),

  // Bệnh nhân (FR-BN-11, 14, 15)
  lichSu: (hoSoId: number) => api.get<LichSu[]>(`${goc}/lich-su`, { params: { hoSoId } }).then((r) => r.data),
  benhAnCuaToi: (id: number) => api.get<BenhAn>(`${goc}/cua-toi/${id}`).then((r) => r.data),
  danhGia: (benhAnId: number, soDiem: number, nhanXet: string) =>
    api.post(`${goc}/cua-toi/${benhAnId}/danh-gia`, { soDiem, nhanXet }),
  nhacThuoc: (hoSoId: number) => api.get<NhacThuoc[]>(`${goc}/nhac-thuoc`, { params: { hoSoId } }).then((r) => r.data),
  taoNhacThuoc: (body: TaoNhacThuoc) => api.post<NhacThuoc>(`${goc}/nhac-thuoc`, body).then((r) => r.data),
  suaNhacThuoc: (id: number, body: TaoNhacThuoc) => api.put<NhacThuoc>(`${goc}/nhac-thuoc/${id}`, body).then((r) => r.data),
  xoaNhacThuoc: (id: number) => api.delete(`${goc}/nhac-thuoc/${id}`),
  lanNhacTheoNgay: (ngay: string) =>
    api.get<LanNhac[]>(`${goc}/nhac-thuoc/theo-ngay`, { params: { ngay } }).then((r) => r.data),
  lanNhacDenGio: () => api.get<LanNhac[]>(`${goc}/nhac-thuoc/den-gio`).then((r) => r.data),
  xacNhanLan: (chiTietId: number, hanhDong: HanhDongNhac) =>
    api.post<LanNhac>(`${goc}/nhac-thuoc/lan/${chiTietId}/${hanhDong}`).then((r) => r.data),
}
