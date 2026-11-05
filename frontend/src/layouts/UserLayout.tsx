import AddRoundedIcon from '@mui/icons-material/AddRounded'
import Box from '@mui/material/Box'
import Breadcrumbs from '@mui/material/Breadcrumbs'
import Button from '@mui/material/Button'
import Container from '@mui/material/Container'
import Grid from '@mui/material/Grid'
import Link from '@mui/material/Link'
import List from '@mui/material/List'
import ListItemButton from '@mui/material/ListItemButton'
import ListItemText from '@mui/material/ListItemText'
import Paper from '@mui/material/Paper'
import Typography from '@mui/material/Typography'
import { Link as RouterLink, Outlet, useLocation, useNavigate } from 'react-router-dom'
import { MENU_BENH_NHAN } from '../routes/menu'
import { GRADIENT, MAU } from '../theme'

/** Khu vực bệnh nhân kiểu TheDuck: breadcrumb + sidebar trái (nút Thêm hồ sơ + danh mục) + nội dung. */
export default function UserLayout() {
  const { pathname } = useLocation()
  const navigate = useNavigate()
  const hienTai = MENU_BENH_NHAN.find((m) => pathname.startsWith(m.toi))

  return (
    <Box sx={{ bgcolor: MAU.nenDatKham, py: 4, minHeight: '70vh' }}>
      <Container maxWidth="lg">
        <Breadcrumbs sx={{ mb: 3 }}>
          <Link component={RouterLink} to="/" underline="hover" color="inherit">
            Trang chủ
          </Link>
          <Typography color="primary" sx={{ fontWeight: 500 }}>
            {hienTai?.nhan ?? 'Tài khoản'}
          </Typography>
        </Breadcrumbs>
        <Grid container spacing={3}>
          <Grid size={{ xs: 12, md: 3 }}>
            <Button
              fullWidth
              variant="contained"
              startIcon={<AddRoundedIcon />}
              onClick={() => navigate('/tai-khoan/ho-so')}
              sx={{ background: GRADIENT.nut, py: 1.2, mb: 2, fontWeight: 700 }}
            >
              Thêm hồ sơ
            </Button>
            <Paper elevation={0} variant="outlined">
              <List disablePadding>
                {MENU_BENH_NHAN.map((m) => (
                  <ListItemButton key={m.toi} selected={pathname.startsWith(m.toi)} onClick={() => navigate(m.toi)}>
                    <ListItemText primary={m.nhan} />
                  </ListItemButton>
                ))}
              </List>
            </Paper>
          </Grid>
          <Grid size={{ xs: 12, md: 9 }}>
            <Outlet />
          </Grid>
        </Grid>
      </Container>
    </Box>
  )
}
