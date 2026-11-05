import VisibilityOffRoundedIcon from '@mui/icons-material/VisibilityOffRounded'
import VisibilityRoundedIcon from '@mui/icons-material/VisibilityRounded'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import IconButton from '@mui/material/IconButton'
import InputAdornment from '@mui/material/InputAdornment'
import Link from '@mui/material/Link'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { useSnackbar } from 'notistack'
import { useState, type FormEvent } from 'react'
import { Link as RouterLink, useNavigate, useSearchParams } from 'react-router-dom'
import { TRANG_CHU_VAI_TRO } from '../../auth/jwt'
import { useAuth } from '../../auth/useAuth'
import { layThongBaoLoi } from '../../services/api'
import { nguoiDungApi } from '../../services/nguoiDung'
import { GRADIENT } from '../../theme'

/** Đăng nhập bằng email/SĐT + mật khẩu (FR-BN-02, FR-BS-01, FR-DD-01). Đăng nhập bằng OTP email làm ở sprint NguoiDung. */
export default function DangNhapPage() {
  const { dangNhap } = useAuth()
  const navigate = useNavigate()
  const [params] = useSearchParams()
  const { enqueueSnackbar } = useSnackbar()
  const [tenDangNhap, setTenDangNhap] = useState('')
  const [matKhau, setMatKhau] = useState('')
  const [hienMatKhau, setHienMatKhau] = useState(false)
  const [daGui, setDaGui] = useState(false)
  const [dangGui, setDangGui] = useState(false)

  const thieuTen = daGui && !tenDangNhap.trim()
  const thieuMatKhau = daGui && !matKhau

  const gui = async (e: FormEvent) => {
    e.preventDefault()
    setDaGui(true)
    if (!tenDangNhap.trim() || !matKhau) return
    setDangGui(true)
    try {
      const kq = await nguoiDungApi.dangNhap(tenDangNhap.trim(), matKhau)
      const thongTin = dangNhap(kq.accessToken, kq.hoTen)
      enqueueSnackbar(`Xin chào ${kq.hoTen}`, { variant: 'success' })
      const quayLai = params.get('quay-lai')
      navigate(quayLai && quayLai.startsWith('/') ? quayLai : thongTin ? TRANG_CHU_VAI_TRO[thongTin.vaiTro] : '/', { replace: true })
    } catch (err) {
      enqueueSnackbar(layThongBaoLoi(err), { variant: 'error' })
    } finally {
      setDangGui(false)
    }
  }

  return (
    <Box component="form" onSubmit={gui} noValidate>
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
      <Button
        type="submit"
        fullWidth
        size="large"
        variant="contained"
        disabled={dangGui}
        sx={{ mt: 1, py: 1.4, fontWeight: 700, background: GRADIENT.nut }}
      >
        {dangGui ? 'Đang đăng nhập...' : 'Đăng nhập'}
      </Button>
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
