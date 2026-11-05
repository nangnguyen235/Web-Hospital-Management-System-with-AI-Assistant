import { ThemeProvider } from '@mui/material/styles'
import { render, screen } from '@testing-library/react'
import { SnackbarProvider } from 'notistack'
import { MemoryRouter } from 'react-router-dom'
import { afterEach, describe, expect, it, vi } from 'vitest'
import App from '../App'
import { AuthProvider } from '../auth/AuthProvider'
import { KHOA_TOKEN } from '../services/api'
import { theme } from '../theme'

const { khoa } = vi.hoisted(() => ({
  khoa: { id: 1, tenKhoa: 'Tim mạch', moTa: 'Khám và điều trị bệnh tim', tuKhoaTrieuChung: 'đau ngực', daXoa: false },
}))
vi.mock('../services/danhMuc', () => ({
  danhMucApi: {
    khoaCongKhai: vi.fn().mockResolvedValue([khoa]),
    timKhoa: vi.fn().mockResolvedValue({ items: [khoa], totalCount: 1, page: 1, pageSize: 10 }),
  },
}))
vi.mock('../services/thongKe', () => ({
  thongKeApi: { trangChu: vi.fn().mockResolvedValue({ soBacSi: 3, soKhoa: 1, soLuotKham: 42 }) },
}))
vi.mock('../services/lichKham', () => ({
  lichKhamApi: {
    soDangGoi: vi.fn().mockResolvedValue({
      lichKhamId: 1, soDangGoi: 5, cacSoVuaGoi: [1, 2, 3, 4, 5], tenPhong: 'A101', tenKhoa: 'Tim mạch', ngay: '2026-11-05', buoi: 'SANG',
    }),
  },
}))

const token = (vaiTro: string, laTruongKhoa = false) =>
  `x.${btoa(JSON.stringify({ tai_khoan_id: '1', vai_tro: vaiTro, la_truong_khoa: String(laTruongKhoa), exp: Date.now() / 1000 + 3600 }))}.y`

function moTrang(duongDan: string, tokenDangNhap?: string) {
  if (tokenDangNhap) localStorage.setItem(KHOA_TOKEN, tokenDangNhap)
  return render(
    <ThemeProvider theme={theme}>
      <SnackbarProvider>
        <MemoryRouter initialEntries={[duongDan]}>
          <AuthProvider>
            <App />
          </AuthProvider>
        </MemoryRouter>
      </SnackbarProvider>
    </ThemeProvider>,
  )
}

afterEach(() => localStorage.clear())

describe('router + layout (smoke test)', () => {
  it('trang chủ hiện hero, số liệu và chuyên khoa từ API', async () => {
    moTrang('/')
    expect(screen.getByRole('button', { name: 'Đặt khám ngay' })).toBeInTheDocument()
    expect(await screen.findByText('42')).toBeInTheDocument()
    expect(await screen.findByText('Khám và điều trị bệnh tim')).toBeInTheDocument()
  })

  it('Admin vào được trang quản lý khoa', async () => {
    moTrang('/admin/khoa', token('QUAN_TRI'))
    expect(screen.getByText('Quản lý khoa')).toBeInTheDocument()
    expect(await screen.findByText('Hoạt động')).toBeInTheDocument()
  })

  it('chưa đăng nhập vào /admin thì chuyển sang trang đăng nhập (TheDuck quên bọc /admin)', () => {
    moTrang('/admin/khoa')
    expect(screen.getByText('Đăng nhập bằng email hoặc số điện thoại')).toBeInTheDocument()
  })

  it('bác sĩ thường không vào được trang quản lý lịch của trưởng khoa', () => {
    moTrang('/bac-si/quan-ly-lich', token('BAC_SI'))
    expect(screen.getByRole('button', { name: 'Đặt khám ngay' })).toBeInTheDocument()
  })

  it('bệnh nhân không vào được khu vực Admin', () => {
    moTrang('/admin/khoa', token('BENH_NHAN'))
    expect(screen.queryByText('Quản lý khoa')).not.toBeInTheDocument()
  })

  it('màn hình số chỉ hiện số, khoa, phòng — không có họ tên (NFR-03)', async () => {
    moTrang('/man-hinh-so/1')
    expect(await screen.findByText('Phòng khám A101')).toBeInTheDocument()
    expect(screen.getAllByText('05').length).toBeGreaterThanOrEqual(2)
    expect(screen.getAllByText('Mời vào phòng khám')).toHaveLength(5)
  })
})
