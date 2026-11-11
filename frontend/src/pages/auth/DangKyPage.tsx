import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Link from '@mui/material/Link'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { useSnackbar } from 'notistack'
import { useState, type FormEvent } from 'react'
import { Link as RouterLink, useNavigate } from 'react-router-dom'
import { TRANG_CHU_VAI_TRO } from '../../auth/jwt'
import { useAuth } from '../../auth/useAuth'
import { layThongBaoLoi } from '../../services/api'
import { nguoiDungApi } from '../../services/nguoiDung'
import { GRADIENT } from '../../theme'

const EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const SDT = /^0\d{9}$/

/** FR-BN-01, BR-02: email + SĐT + họ tên + mật khẩu → OTP email → tạo tài khoản và đăng nhập luôn. */
export default function DangKyPage() {
  const { dangNhap } = useAuth()
  const navigate = useNavigate()
  const { enqueueSnackbar } = useSnackbar()
  const [g, setG] = useState({ email: '', soDienThoai: '', hoTen: '', matKhau: '', nhapLai: '', otp: '' })
  const [daGuiOtp, setDaGuiOtp] = useState(false)
  const [daBam, setDaBam] = useState(false)
  const [dangGui, setDangGui] = useState(false)

  const loi = {
    email: !EMAIL.test(g.email.trim()) && 'Email không hợp lệ',
    soDienThoai: !SDT.test(g.soDienThoai.trim()) && 'Số điện thoại gồm 10 số, bắt đầu bằng 0',
    hoTen: !g.hoTen.trim() && 'Vui lòng nhập họ tên',
    matKhau: g.matKhau.length < 6 && 'Mật khẩu tối thiểu 6 ký tự',
    nhapLai: g.nhapLai !== g.matKhau && 'Mật khẩu nhập lại không khớp',
    otp: daGuiOtp && !/^\d{6}$/.test(g.otp) && 'Mã OTP gồm 6 số',
  }
  const coLoi = Object.values(loi).some(Boolean)
  const truong = (k: keyof typeof g, nhan: string, them: object = {}) => (
    <TextField
      label={nhan}
      fullWidth
      value={g[k]}
      onChange={(e) => setG({ ...g, [k]: e.target.value })}
      error={daBam && Boolean(loi[k as keyof typeof loi])}
      helperText={(daBam && loi[k as keyof typeof loi]) || ' '}
      disabled={daGuiOtp && k !== 'otp'}
      {...them}
    />
  )

  const gui = async (e: FormEvent) => {
    e.preventDefault()
    setDaBam(true)
    if (coLoi) return
    setDangGui(true)
    try {
      if (!daGuiOtp) {
        await nguoiDungApi.guiOtpDangKy(g.email.trim(), g.soDienThoai.trim())
        setDaGuiOtp(true)
        setDaBam(false)
        enqueueSnackbar('Đã gửi mã OTP tới email của bạn', { variant: 'info' })
      } else {
        const kq = await nguoiDungApi.dangKy({ email: g.email.trim(), soDienThoai: g.soDienThoai.trim(), hoTen: g.hoTen.trim(), matKhau: g.matKhau, otp: g.otp })
        const tt = dangNhap(kq.accessToken, kq.hoTen)
        enqueueSnackbar('Đăng ký thành công', { variant: 'success' })
        navigate(tt ? TRANG_CHU_VAI_TRO[tt.vaiTro] : '/', { replace: true })
      }
    } catch (err) {
      enqueueSnackbar(layThongBaoLoi(err), { variant: 'error' })
    } finally {
      setDangGui(false)
    }
  }

  return (
    <Box component="form" onSubmit={gui} noValidate>
      <Typography sx={{ fontWeight: 600, mb: 2 }}>Đăng ký tài khoản bệnh nhân</Typography>
      {truong('hoTen', 'Họ và tên', { autoFocus: true })}
      {truong('email', 'Email', { type: 'email', autoComplete: 'email' })}
      {truong('soDienThoai', 'Số điện thoại', { autoComplete: 'tel' })}
      {truong('matKhau', 'Mật khẩu', { type: 'password', autoComplete: 'new-password' })}
      {truong('nhapLai', 'Nhập lại mật khẩu', { type: 'password', autoComplete: 'new-password' })}
      {daGuiOtp && truong('otp', 'Mã OTP (6 số, hạn 5 phút)', { autoFocus: true, inputMode: 'numeric' })}
      <Button type="submit" fullWidth size="large" variant="contained" disabled={dangGui} sx={{ py: 1.4, fontWeight: 700, background: GRADIENT.nut }}>
        {daGuiOtp ? 'Xác nhận đăng ký' : 'Gửi mã OTP'}
      </Button>
      {daGuiOtp && (
        <Button fullWidth sx={{ mt: 1 }} onClick={() => setDaGuiOtp(false)}>
          Sửa thông tin / gửi lại mã
        </Button>
      )}
      <Box sx={{ textAlign: 'center', mt: 2 }}>
        Đã có tài khoản?{' '}
        <Link component={RouterLink} to="/dang-nhap" underline="hover">
          Đăng nhập
        </Link>
      </Box>
    </Box>
  )
}
