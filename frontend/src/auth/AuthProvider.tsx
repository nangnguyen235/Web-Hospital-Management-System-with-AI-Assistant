import { useCallback, useMemo, useState, type ReactNode } from 'react'
import { KHOA_HO_TEN, KHOA_TOKEN } from '../services/api'
import { AuthContext, type AuthState } from './AuthContext'
import { giaiMaToken } from './jwt'

function docLocalStorage(khoa: string): string | null {
  try {
    return localStorage.getItem(khoa)
  } catch {
    return null
  }
}

/** Giữ token (localStorage) và vai trò đọc từ claim JWT (frontend.md mục 6). */
export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(() =>
    giaiMaToken(docLocalStorage(KHOA_TOKEN)) ? docLocalStorage(KHOA_TOKEN) : null,
  )
  const [hoTen, setHoTen] = useState<string | null>(() => docLocalStorage(KHOA_HO_TEN))

  const dangNhap = useCallback((tokenMoi: string, hoTenMoi: string) => {
    try {
      localStorage.setItem(KHOA_TOKEN, tokenMoi)
      localStorage.setItem(KHOA_HO_TEN, hoTenMoi)
    } catch {
      // trình duyệt chặn localStorage: vẫn đăng nhập trong phiên hiện tại
    }
    setToken(tokenMoi)
    setHoTen(hoTenMoi)
    return giaiMaToken(tokenMoi)
  }, [])

  const dangXuat = useCallback(() => {
    try {
      localStorage.removeItem(KHOA_TOKEN)
      localStorage.removeItem(KHOA_HO_TEN)
    } catch {
      // bỏ qua
    }
    setToken(null)
    setHoTen(null)
  }, [])

  const value = useMemo<AuthState>(
    () => ({ token, thongTin: giaiMaToken(token), hoTen, dangNhap, dangXuat }),
    [token, hoTen, dangNhap, dangXuat],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
