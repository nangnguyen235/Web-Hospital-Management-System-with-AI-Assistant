import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Link from '@mui/material/Link'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { useSnackbar } from 'notistack'
import { useState, type FormEvent } from 'react'
import { Link as RouterLink, useNavigate } from 'react-router-dom'
import { layThongBaoLoi } from '../../services/api'
import { nguoiDungApi } from '../../services/nguoiDung'
import { GRADIENT } from '../../theme'

/** FR-BN-04, BR-02: email → OTP → mật khẩu mới (nhập lại khớp). */
export default function QuenMatKhauPage() {
  const navigate = useNavigate()
  const { enqueueSnackbar } = useSnackbar()
  const [email, setEmail] = useState('')
  const [otp, setOtp] = useState('')
  const [matKhau, setMatKhau] = useState('')
  const [nhapLai, setNhapLai] = useState('')
  const [daGuiOtp, setDaGuiOtp] = useState(false)
  const [dangGui, setDangGui] = useState(false)

  const gui = async (e: FormEvent) => {
    e.preventDefault()
    if (!email.trim()) return enqueueSnackbar('Vui lòng nhập email', { variant: 'warning' })
    if (daGuiOtp) {
      if (!/^\d{6}$/.test(otp)) return enqueueSnackbar('Mã OTP gồm 6 số', { variant: 'warning' })
      if (matKhau.length < 6) return enqueueSnackbar('Mật khẩu tối thiểu 6 ký tự', { variant: 'warning' })
      if (matKhau !== nhapLai) return enqueueSnackbar('Mật khẩu nhập lại không khớp', { variant: 'warning' })
    }
    setDangGui(true)
    try {
      if (!daGuiOtp) {
        await nguoiDungApi.guiOtpQuenMatKhau(email.trim())
        setDaGuiOtp(true)
        enqueueSnackbar('Nếu email đã đăng ký, mã OTP đã được gửi', { variant: 'info' })
      } else {
        await nguoiDungApi.datLaiMatKhau({ email: email.trim(), otp, matKhauMoi: matKhau, nhapLaiMatKhau: nhapLai })
        enqueueSnackbar('Đã đặt lại mật khẩu, mời đăng nhập', { variant: 'success' })
        navigate('/dang-nhap', { replace: true })
      }
    } catch (err) {
      enqueueSnackbar(layThongBaoLoi(err), { variant: 'error' })
    } finally {
      setDangGui(false)
    }
  }

  return (
    <Box component="form" onSubmit={gui} noValidate>
      <Typography sx={{ fontWeight: 600, mb: 2 }}>Quên mật khẩu</Typography>
      <TextField label="Email đã đăng ký" type="email" fullWidth autoFocus value={email} disabled={daGuiOtp} onChange={(e) => setEmail(e.target.value)} sx={{ mb: 2 }} />
      {daGuiOtp && (
        <>
          <TextField label="Mã OTP" fullWidth value={otp} onChange={(e) => setOtp(e.target.value.replace(/\D/g, '').slice(0, 6))} sx={{ mb: 2 }} />
          <TextField label="Mật khẩu mới" type="password" fullWidth value={matKhau} onChange={(e) => setMatKhau(e.target.value)} sx={{ mb: 2 }} />
          <TextField label="Nhập lại mật khẩu mới" type="password" fullWidth value={nhapLai} onChange={(e) => setNhapLai(e.target.value)} sx={{ mb: 2 }} />
        </>
      )}
      <Button type="submit" fullWidth size="large" variant="contained" disabled={dangGui} sx={{ py: 1.4, fontWeight: 700, background: GRADIENT.nut }}>
        {daGuiOtp ? 'Đặt lại mật khẩu' : 'Gửi mã OTP'}
      </Button>
      <Box sx={{ textAlign: 'center', mt: 2 }}>
        <Link component={RouterLink} to="/dang-nhap" underline="hover">
          ← Quay lại đăng nhập
        </Link>
      </Box>
    </Box>
  )
}
