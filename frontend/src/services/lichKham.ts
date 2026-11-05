import { api } from './api'

/** Chỉ số + phòng + khoa, không họ tên (NFR-03). */
export interface SoDangGoi {
  lichKhamId: number
  soDangGoi: number
  cacSoVuaGoi: number[]
  tenPhong: string
  tenKhoa: string
  ngay: string
  buoi: 'SANG' | 'CHIEU'
}

export const lichKhamApi = {
  soDangGoi: (lichKhamId: string) =>
    api.get<SoDangGoi>(`/api/lich-kham/${lichKhamId}/so-dang-goi`).then((r) => r.data),
}
