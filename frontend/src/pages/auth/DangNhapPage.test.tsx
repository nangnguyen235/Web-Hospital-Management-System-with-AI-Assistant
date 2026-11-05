import { fireEvent, render, screen } from '@testing-library/react'
import { SnackbarProvider } from 'notistack'
import { MemoryRouter } from 'react-router-dom'
import { describe, expect, it } from 'vitest'
import { AuthProvider } from '../../auth/AuthProvider'
import DangNhapPage from './DangNhapPage'

describe('DangNhapPage', () => {
  it('báo lỗi khi bấm đăng nhập mà chưa nhập gì', () => {
    render(
      <SnackbarProvider>
        <MemoryRouter>
          <AuthProvider>
            <DangNhapPage />
          </AuthProvider>
        </MemoryRouter>
      </SnackbarProvider>,
    )
    fireEvent.click(screen.getByRole('button', { name: 'Đăng nhập' }))
    expect(screen.getByText('Vui lòng nhập email hoặc số điện thoại')).toBeInTheDocument()
    expect(screen.getByText('Vui lòng nhập mật khẩu')).toBeInTheDocument()
  })
})
