import { useSnackbar } from 'notistack'
import { useCallback, useEffect, useState, type ReactNode } from 'react'
import { layThongBaoLoi } from '../services/api'
import type { PagedResult } from '../services/danhMuc'

export interface Cot<T> {
  nhan: string
  hien: (dong: T) => ReactNode
  /** Ẩn cột trên màn hình nhỏ hơn mốc này. */
  anDuoi?: 'sm' | 'md' | 'lg'
  rong?: number
}

export interface ThaoTac<T> {
  nhan: (dong: T) => string
  onChon: (dong: T) => void
  mau?: (dong: T) => 'error.main' | 'success.main' | undefined
  an?: (dong: T) => boolean
}

/** Thao tác Xóa / Khôi phục dùng chung cho danh mục xóa mềm. */
export function thaoTacXoa<T extends { daXoa: boolean }>(onChon: (dong: T) => void): ThaoTac<T> {
  return {
    nhan: (d) => (d.daXoa ? 'Khôi phục' : 'Xóa'),
    mau: (d) => (d.daXoa ? 'success.main' : 'error.main'),
    onChon,
  }
}

/** Tải danh sách phân trang + tìm kiếm (tách từ mẫu KhoaPage). Đổi bộ lọc ngoài thì gọi lamMoi(). */
export function useDanhSachPhanTrang<T>(tai: (tuKhoa: string, page: number, pageSize: number) => Promise<PagedResult<T>>) {
  const { enqueueSnackbar } = useSnackbar()
  const [duLieu, setDuLieu] = useState<PagedResult<T> | null>(null)
  const [timTheo, setTimTheo] = useState('')
  const [trang, setTrang] = useState(0)
  const [soDong, setSoDong] = useState(10)
  const [taiLai, setTaiLai] = useState(0)

  useEffect(() => {
    let huy = false
    tai(timTheo, trang + 1, soDong)
      .then((d) => !huy && setDuLieu(d))
      .catch((e) => !huy && enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' }))
    return () => {
      huy = true
    }
    // `tai` là hàm gọi API của trang (đổi theo mỗi render); trang gọi lamMoi() khi đổi bộ lọc
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [timTheo, trang, soDong, taiLai, enqueueSnackbar])

  const lamMoi = useCallback(() => setTaiLai((x) => x + 1), [])
  const tim = useCallback((tuKhoa: string) => {
    setTrang(0)
    setTimTheo(tuKhoa.trim())
  }, [])
  return { duLieu, trang, soDong, setTrang, setSoDong, lamMoi, tim }
}

export type DanhSachPhanTrang<T> = ReturnType<typeof useDanhSachPhanTrang<T>>
