import Box from '@mui/material/Box'
import Container from '@mui/material/Container'
import Grid from '@mui/material/Grid'
import Link from '@mui/material/Link'
import Typography from '@mui/material/Typography'
import { Link as RouterLink } from 'react-router-dom'
import { BRAND } from '../../config/brand'
import { MAU } from '../../theme'
import Logo from './Logo'

const LIEN_KET = [
  { nhan: 'Đặt khám', toi: '/dat-kham/chon-ho-so' },
  { nhan: 'Tìm bác sĩ', toi: '/tim-bac-si' },
  { nhan: 'Tra cứu kết quả xét nghiệm', toi: '/tai-khoan/xet-nghiem' },
  { nhan: 'Đăng nhập', toi: '/dang-nhap' },
]

export default function Footer() {
  return (
    <Box component="footer" id="lien-he" sx={{ bgcolor: '#fff', borderTop: '1px solid', borderColor: 'divider' }}>
      <Container maxWidth="lg" sx={{ py: 5 }}>
        <Grid container spacing={4}>
          <Grid size={{ xs: 12, md: 4 }}>
            <Logo coChu={28} />
            <Typography sx={{ mt: 2, color: 'text.secondary' }}>
              Hệ thống đặt khám ngoại trú: đặt trước, thanh toán VNPAY, theo dõi số thứ tự và kết quả xét nghiệm trực tuyến.
            </Typography>
          </Grid>
          <Grid size={{ xs: 12, sm: 6, md: 3 }}>
            <Typography sx={{ fontWeight: 700, mb: 1.5, color: MAU.chinh }}>LIÊN HỆ</Typography>
            <Typography>Địa chỉ: {BRAND.diaChi}</Typography>
            <Typography>Hotline: {BRAND.hotline}</Typography>
            <Typography>Email: {BRAND.email}</Typography>
            <Typography>Website: {BRAND.website}</Typography>
          </Grid>
          <Grid size={{ xs: 12, sm: 6, md: 3 }}>
            <Typography sx={{ fontWeight: 700, mb: 1.5, color: MAU.chinh }}>LIÊN KẾT</Typography>
            {LIEN_KET.map((l) => (
              <Link key={l.toi} component={RouterLink} to={l.toi} underline="hover" sx={{ display: 'block', color: 'text.primary', mb: 0.5 }}>
                {l.nhan}
              </Link>
            ))}
          </Grid>
          <Grid size={{ xs: 12, md: 2 }}>
            <Typography sx={{ fontWeight: 700, mb: 1.5, color: MAU.chinh }}>GIỜ LÀM VIỆC</Typography>
            {BRAND.gioLamViec.map((g) => (
              <Typography key={g}>{g}</Typography>
            ))}
          </Grid>
        </Grid>
      </Container>
      <Box sx={{ bgcolor: MAU.chinh, color: '#fff', py: 1.5, textAlign: 'center' }}>
        <Typography variant="body2">
          © {new Date().getFullYear()} {BRAND.tenDayDu} · Đồ án Nhóm 6 — ĐH Xây dựng Hà Nội
        </Typography>
      </Box>
    </Box>
  )
}
