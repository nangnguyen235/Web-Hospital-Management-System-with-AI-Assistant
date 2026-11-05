import { createContext } from 'react'
import type { ThongTinToken } from './jwt'

export interface AuthState {
  token: string | null
  thongTin: ThongTinToken | null
  hoTen: string | null
  dangNhap: (token: string, hoTen: string) => ThongTinToken | null
  dangXuat: () => void
}

export const AuthContext = createContext<AuthState | null>(null)
