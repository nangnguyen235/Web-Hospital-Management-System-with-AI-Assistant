import VisibilityOffRoundedIcon from '@mui/icons-material/VisibilityOffRounded'
import VisibilityRoundedIcon from '@mui/icons-material/VisibilityRounded'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import IconButton from '@mui/material/IconButton'
import InputAdornment from '@mui/material/InputAdornment'
import Link from '@mui/material/Link'
import Tab from '@mui/material/Tab'
import Tabs from '@mui/material/Tabs'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { useSnackbar } from 'notistack'
import { useState, type FormEvent } from 'react'
import { Link as RouterLink, useNavigate, useSearchParams } from 'react-router-dom'
import { TRANG_CHU_VAI_TRO } from '../../auth/jwt'
import { useAuth } from '../../auth/useAuth'
import { layThongBaoLoi } from '../../services/api'
import { nguoiDungApi, type DangNhapResponse } from '../../services/nguoiDung'
import { GRADIENT } from '../../theme'

/** Đăng nhập bằng email/SĐT + mật khẩu (FR-BN-02, FR-BS-01, FR-DD-01) hoặc bằng OTP gửi email (FR-BN-03). */
export default function DangNhapPage() {
  const { dangNhap } = useAuth()
  const navigate = useNavigate()
  const [params] = useSearchParams()
  const { enqueueSnackbar } = useSnackbar()
  const [cach, setCach] = useState<'mat-khau' | 'otp'>('mat-khau')
  const [tenDangNhap, setTenDangNhap] = useState('')
  const [matKhau, setMatKhau] = useState('')
  const [hienMatKhau, setHienMatKhau] = useState(false)
  const [email, setEmail] = useState('')
  const [otp, setOtp] = useState('')
  const [daGuiOtp, setDaGuiOtp] = useState(false)
  const [daGui, setDaGui] = useState(false)
  const [dangGui, setDangGui] = useState(false)

  const thieuTen = daGui && !tenDangNhap.trim()
  const thieuMatKhau = daGui && !matKhau

  const vao = (kq: DangNhapResponse) => {
    const thongTin = dangNhap(kq.accessToken, kq.hoTen)
    enqueueSnackbar(`Xin chào ${kq.hoTen}`, { variant: 'success' })
    const quayLai = params.get('quay-lai')
    navigate(quayLai && quayLai.startsWith('/') ? quayLai : thongTin ? TRANG_CHU_VAI_TRO[thongTin.vaiTro] : '/', { replace: true })
  }

  const chay = async (viec: () => Promise<void>) => {
    setDangGui(true)
    try {
      await viec()
    } catch (err) {
      enqueueSnackbar(layThongBaoLoi(err), { variant: 'error' })
    } finally {
      setDangGui(false)
    }
  }

  const gui = (e: FormEvent) => {
    e.preventDefault()
    if (cach === 'mat-khau') {
      setDaGui(true)
      if (!tenDangNhap.trim() || !matKhau) return
      return chay(async () => vao(await nguoiDungApi.dangNhap(tenDangNhap.trim(), matKhau)))
    }
    if (!email.trim()) return enqueueSnackbar('Vui lòng nhập email', { variant: 'warning' })
    if (!daGuiOtp) {
      return chay(async () => {
        await nguoiDungApi.guiOtpDangNhap(email.trim())
        setDaGuiOtp(true)
        enqueueSnackbar('Đã gửi mã OTP tới email', { variant: 'info' })
      })
    }
    if (!/^\d{6}$/.test(otp)) return enqueueSnackbar('Mã OTP gồm 6 số', { variant: 'warning' })
    return chay(async () => vao(await nguoiDungApi.dangNhapOtp(email.trim(), otp)))
  }

  return (
    <Box component="form" onSubmit={gui} noValidate>
      <Tabs value={cach} onChange={(_, v) => setCach(v)} sx={{ mb: 2 }} variant="fullWidth">
        <Tab value="mat-khau" label="Mật khẩu" />
        <Tab value="otp" label="Mã OTP qua email" />
      </Tabs>
      {cach === 'mat-khau' ? (
        <>
          <Typography sx={{ fontWeight: 600, mb: 2 }}>Đăng nhập bằng email hoặc số điện thoại</Typography>
          <TextField
            label="Email hoặc số điện thoại"
            fullWidth
            autoFocus
            autoComplete="username"
            value={tenDangNhap}
            onChange={(e) => setTenDangNhap(e.target.value)}
            error={thieuTen}
            helperText={thieuTen ? 'Vui lòng nhập email hoặc số điện thoại' : ' '}
          />
          <TextField
            label="Mật khẩu"
            fullWidth
            type={hienMatKhau ? 'text' : 'password'}
            autoComplete="current-password"
            value={matKhau}
            onChange={(e) => setMatKhau(e.target.value)}
            error={thieuMatKhau}
            helperText={thieuMatKhau ? 'Vui lòng nhập mật khẩu' : ' '}
            slotProps={{
              input: {
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton onClick={() => setHienMatKhau((v) => !v)} edge="end" aria-label="Hiện mật khẩu">
                      {hienMatKhau ? <VisibilityOffRoundedIcon /> : <VisibilityRoundedIcon />}
                    </IconButton>
                  </InputAdornment>
                ),
              },
            }}
          />
        </>
      ) : (
        <>
          <Typography sx={{ fontWeight: 600, mb: 2 }}>Nhận mã đăng nhập qua email</Typography>
          <TextField label="Email" type="email" fullWidth value={email} disabled={daGuiOtp} onChange={(e) => setEmail(e.target.value)} sx={{ mb: 2 }} />
          {daGuiOtp && (
            <TextField
              label="Mã OTP (6 số, hạn 5 phút)"
              fullWidth
              autoFocus
              value={otp}
              onChange={(e) => setOtp(e.target.value.replace(/\D/g, '').slice(0, 6))}
              sx={{ mb: 2 }}
            />
          )}
        </>
      )}
      <Button type="submit" fullWidth size="large" variant="contained" disabled={dangGui} sx={{ mt: 1, py: 1.4, fontWeight: 700, background: GRADIENT.nut }}>
        {dangGui ? 'Đang xử lý...' : cach === 'otp' && !daGuiOtp ? 'Gửi mã OTP' : 'Đăng nhập'}
      </Button>
      {cach === 'otp' && daGuiOtp && (
        <Button fullWidth sx={{ mt: 1 }} onClick={() => setDaGuiOtp(false)}>
          Đổi email / gửi lại mã
        </Button>
      )}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 2 }}>
        <Link component={RouterLink} to="/quen-mat-khau" underline="hover">
          Quên mật khẩu?
        </Link>
        <Link component={RouterLink} to="/dang-ky" underline="hover">
          Đăng ký tài khoản
        </Link>
      </Box>
      <Box sx={{ textAlign: 'center', mt: 3 }}>
        <Link component={RouterLink} to="/" underline="hover" color="text.secondary">
          ← Về trang chủ
        </Link>
      </Box>
    </Box>
  )
}
