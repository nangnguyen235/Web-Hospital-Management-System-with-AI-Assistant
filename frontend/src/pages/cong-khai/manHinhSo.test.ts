import { describe, expect, it } from 'vitest'
import { CHU_KY_CAP_NHAT_MS, dinhDangSo } from './manHinhSo'

describe('màn hình số đang gọi', () => {
  it('hiển thị số 2 chữ số như TheDuck', () => {
    expect(dinhDangSo(5)).toBe('05')
    expect(dinhDangSo(12)).toBe('12')
  })

  it('cập nhật không quá 5 giây (NFR-01)', () => {
    expect(CHU_KY_CAP_NHAT_MS).toBeLessThanOrEqual(5000)
  })
})
