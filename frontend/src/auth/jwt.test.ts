import { describe, expect, it } from 'vitest'
import { giaiMaToken } from './jwt'

const taoToken = (payload: object) =>
  `x.${btoa(String.fromCharCode(...new TextEncoder().encode(JSON.stringify(payload))))
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=+$/, '')}.y`

describe('giaiMaToken', () => {
  const bayGio = Date.UTC(2026, 10, 5)
  const exp = bayGio / 1000 + 3600

  it('đọc vai trò và cờ trưởng khoa từ claim JWT', () => {
    const t = giaiMaToken(taoToken({ tai_khoan_id: '7', vai_tro: 'BAC_SI', la_truong_khoa: 'true', exp }), bayGio)
    expect(t).toEqual({ taiKhoanId: 7, vaiTro: 'BAC_SI', laTruongKhoa: true, hetHan: exp })
  })

  it('token hết hạn thì coi như chưa đăng nhập', () => {
    expect(giaiMaToken(taoToken({ vai_tro: 'QUAN_TRI', exp: bayGio / 1000 - 1 }), bayGio)).toBeNull()
  })

  it('token hỏng hoặc rỗng trả null', () => {
    expect(giaiMaToken('khong-phai-jwt', bayGio)).toBeNull()
    expect(giaiMaToken(null, bayGio)).toBeNull()
  })
})
