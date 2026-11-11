import { afterEach, describe, expect, it } from 'vitest'
import { docDon, themKhung, xoaDon, type KhungDaChon } from './datKham'

const khung = (khungId: number, khoaId: number): KhungDaChon => ({
  khungId, lichId: khungId, khoaId, tenKhoa: `Khoa ${khoaId}`, tenBacSi: 'BS', tenPhong: 'P1', tenDichVu: 'Khám', gia: 100000,
  ngay: '2026-11-20', buoi: 'SANG', gio: '07:00 - 08:00',
})

afterEach(() => xoaDon())

describe('đặt khám: tối đa 3 khung khác khoa (BR-09)', () => {
  it('khung cùng khoa thay khung cũ', () => {
    themKhung(docDon(), khung(1, 1))
    const { don } = themKhung(docDon(), khung(2, 1))
    expect(don?.chon.map((c) => c.khungId)).toEqual([2])
  })

  it('khoa thứ tư bị từ chối', () => {
    themKhung(docDon(), khung(1, 1))
    themKhung(docDon(), khung(2, 2))
    themKhung(docDon(), khung(3, 3))
    const kq = themKhung(docDon(), khung(4, 4))
    expect(kq.loi).toBeTruthy()
    expect(docDon().chon).toHaveLength(3)
  })
})
