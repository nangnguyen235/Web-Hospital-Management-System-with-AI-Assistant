import axios, { AxiosError } from 'axios'

export const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost'
export const KHOA_TOKEN = 'thehos_token'
export const KHOA_HO_TEN = 'thehos_ho_ten'

/** Một axios dùng chung: gắn Bearer, 401 → về trang đăng nhập (TheDuck không có bước này). */
export const api = axios.create({ baseURL: API_URL, timeout: 20000 })

api.interceptors.request.use((config) => {
  try {
    const token = localStorage.getItem(KHOA_TOKEN)
    if (token) config.headers.Authorization = `Bearer ${token}`
  } catch {
    // localStorage bị chặn: gửi không kèm token
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    const laDangNhap = error.config?.url?.includes('/dang-nhap')
    if (error.response?.status === 401 && !laDangNhap && window.location.pathname !== '/dang-nhap') {
      try {
        localStorage.removeItem(KHOA_TOKEN)
      } catch {
        // bỏ qua
      }
      window.location.assign(`/dang-nhap?quay-lai=${encodeURIComponent(window.location.pathname)}`)
    }
    return Promise.reject(error)
  },
)

interface ProblemDetails {
  title?: string
  detail?: string
  errors?: Record<string, string[]>
}

/** Lấy câu báo lỗi tiếng Việt từ ProblemDetails của backend để hiện toast. */
export function layThongBaoLoi(error: unknown): string {
  if (error instanceof AxiosError) {
    const data = error.response?.data as ProblemDetails | undefined
    if (data?.detail) return data.detail
    const loiDauTien = data?.errors && Object.values(data.errors)[0]?.[0]
    if (loiDauTien) return loiDauTien
    if (!error.response) return 'Không kết nối được máy chủ. Vui lòng thử lại.'
  }
  return 'Có lỗi xảy ra, vui lòng thử lại.'
}
