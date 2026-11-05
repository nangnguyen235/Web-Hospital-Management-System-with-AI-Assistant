import { useSnackbar } from 'notistack'
import { useEffect } from 'react'
import { Navigate, Outlet, useLocation } from 'react-router-dom'
import type { VaiTro } from '../auth/jwt'
import { useAuth } from '../auth/useAuth'

interface Props {
  vaiTro: VaiTro[]
  /** Chỉ trưởng khoa / điều dưỡng trưởng (claim la_truong_khoa). */
  canTruongKhoa?: boolean
}

/** Bọc MỌI khu vực theo vai trò (TheDuck quên bọc /admin — frontend.md mục 7). */
export default function ProtectedLayout({ vaiTro, canTruongKhoa }: Props) {
  const { thongTin } = useAuth()
  const location = useLocation()
  const { enqueueSnackbar } = useSnackbar()
  const duocVao = thongTin && vaiTro.includes(thongTin.vaiTro) && (!canTruongKhoa || thongTin.laTruongKhoa)

  useEffect(() => {
    if (thongTin && !duocVao) enqueueSnackbar('Bạn không có quyền vào trang này.', { variant: 'warning' })
  }, [thongTin, duocVao, enqueueSnackbar])

  if (!thongTin) {
    return <Navigate to={`/dang-nhap?quay-lai=${encodeURIComponent(location.pathname)}`} replace />
  }
  if (!duocVao) return <Navigate to="/" replace />
  return <Outlet />
}
