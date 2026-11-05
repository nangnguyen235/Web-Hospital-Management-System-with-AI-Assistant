import Box from '@mui/material/Box'
import Typography from '@mui/material/Typography'
import { Outlet } from 'react-router-dom'
import Logo from '../components/General/Logo'
import { GRADIENT } from '../theme'

/** Trang đăng nhập/đăng ký kiểu TheDuck: chia đôi, trái là ảnh/khẩu hiệu, phải là form. */
export default function LogoLayout() {
  return (
    <Box sx={{ minHeight: '100vh', display: 'flex' }}>
      <Box
        sx={{
          flex: 1.3,
          display: { xs: 'none', md: 'flex' },
          flexDirection: 'column',
          justifyContent: 'space-between',
          p: 5,
          color: '#fff',
          background: GRADIENT.hero,
          clipPath: 'polygon(0 0, 100% 0, 92% 100%, 0 100%)',
        }}
      >
        <Logo bienThe="sang" coKhauHieu={false} coChu={30} />
        <Box sx={{ maxWidth: 460 }}>
          <Typography sx={{ fontSize: 40, fontStyle: 'italic', fontWeight: 300, lineHeight: 1.3 }}>
            “Đặt khám trước,
            <br />
            đến đúng giờ,
            <br />
            không còn xếp hàng chờ đợi.”
          </Typography>
        </Box>
        <Typography sx={{ opacity: 0.85 }}>Đặt khám trực tuyến · Thanh toán VNPAY · Trợ lý AI</Typography>
      </Box>
      <Box sx={{ flex: 1, display: 'grid', placeItems: 'center', p: 3, bgcolor: '#fafbfd' }}>
        <Box sx={{ width: '100%', maxWidth: 440 }}>
          <Box sx={{ textAlign: 'center', mb: 3 }}>
            <Logo coChu={44} />
          </Box>
          <Outlet />
        </Box>
      </Box>
    </Box>
  )
}
