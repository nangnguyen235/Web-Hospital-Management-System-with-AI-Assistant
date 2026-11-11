import type { BacSiCongKhai } from '../../services/nguoiDung'

/** Một khung đã chọn trong lượt đặt khám (BR-09: tối đa 3 khung, mỗi khung một khoa khác nhau). */
export interface KhungDaChon {
  khungId: number
  lichId: number
  khoaId: number
  tenKhoa: string
  tenBacSi: string
  tenPhong: string
  tenDichVu: string
  gia: number
  ngay: string
  buoi: 'SANG' | 'CHIEU'
  gio: string
}

export interface DonDatKham {
  hoSoId: number | null
  hoTen: string | null
  bacSi: BacSiCongKhai | null
  chon: KhungDaChon[]
}

const KHOA = 'thehos_dat_kham'
export const TOI_DA_KHUNG = 3
export const PHI_DAT_KHAM = 15000

const TRONG: DonDatKham = { hoSoId: null, hoTen: null, bacSi: null, chon: [] }

export function docDon(): DonDatKham {
  try {
    const s = sessionStorage.getItem(KHOA)
    return s ? { ...TRONG, ...(JSON.parse(s) as DonDatKham) } : TRONG
  } catch {
    return TRONG
  }
}

export function ghiDon(don: Partial<DonDatKham>): DonDatKham {
  const moi = { ...docDon(), ...don }
  try {
    sessionStorage.setItem(KHOA, JSON.stringify(moi))
  } catch {
    // trình duyệt chặn sessionStorage: luồng vẫn chạy trong trang hiện tại
  }
  return moi
}

export function xoaDon() {
  try {
    sessionStorage.removeItem(KHOA)
  } catch {
    // bỏ qua
  }
}

/** Thêm khung; khung cùng khoa với khung đã chọn thì thay khung cũ (BR-09). Trả lỗi nếu đã đủ 3 khoa. */
export function themKhung(don: DonDatKham, k: KhungDaChon): { don?: DonDatKham; loi?: string } {
  const khac = don.chon.filter((c) => c.khoaId !== k.khoaId)
  if (khac.length >= TOI_DA_KHUNG) return { loi: `Mỗi lần đặt tối đa ${TOI_DA_KHUNG} khung giờ ở ${TOI_DA_KHUNG} khoa khác nhau.` }
  return { don: ghiDon({ chon: [...khac, k] }) }
}
