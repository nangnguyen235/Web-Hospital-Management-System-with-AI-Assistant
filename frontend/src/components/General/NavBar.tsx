import AccountCircleRoundedIcon from '@mui/icons-material/AccountCircleRounded'
import LogoutRoundedIcon from '@mui/icons-material/LogoutRounded'
import MenuRoundedIcon from '@mui/icons-material/MenuRounded'
import AppBar from '@mui/material/AppBar'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Container from '@mui/material/Container'
import Divider from '@mui/material/Divider'
import Drawer from '@mui/material/Drawer'
import IconButton from '@mui/material/IconButton'
import List from '@mui/material/List'
import ListItemButton from '@mui/material/ListItemButton'
import ListItemText from '@mui/material/ListItemText'
import Menu from '@mui/material/Menu'
import MenuItem from '@mui/material/MenuItem'
import Toolbar from '@mui/material/Toolbar'
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../auth/useAuth'
import { MENU_TAI_KHOAN } from '../../routes/menu'
import { GRADIENT, MAU } from '../../theme'
import Logo from './Logo'

const MENU_CHINH = [
  { nhan: 'Trang chủ', toi: '/' },
  { nhan: 'Giới thiệu', toi: '/#gioi-thieu' },
  { nhan: 'Quy trình', toi: '/#quy-trinh' },
  { nhan: 'Thắc mắc', toi: '/#thac-mac' },
  { nhan: 'Liên hệ', toi: '/#lien-he' },
]

/** Header trang công khai kiểu TheDuck: logo trái, menu giữa, nút Đăng nhập / tài khoản phải. */
export default function NavBar() {
  const { thongTin, hoTen, dangXuat } = useAuth()
  const navigate = useNavigate()
  const [anchor, setAnchor] = useState<HTMLElement | null>(null)
  const [moDrawer, setMoDrawer] = useState(false)
  const menuVaiTro = thongTin ? MENU_TAI_KHOAN[thongTin.vaiTro] : []

  const di = (toi: string) => {
    setAnchor(null)
    setMoDrawer(false)
    navigate(toi)
  }
  const thoat = () => {
    dangXuat()
    di('/')
  }

  return (
    <AppBar position="sticky" color="inherit" elevation={0} sx={{ borderBottom: '1px solid', borderColor: 'divider' }}>
      <Container maxWidth="lg">
        <Toolbar disableGutters sx={{ minHeight: { xs: 72, md: 96 }, gap: 2 }}>
          <Logo coChu={30} />
          <Box sx={{ flex: 1, display: { xs: 'none', md: 'flex' }, justifyContent: 'center', gap: 1 }}>
            {MENU_CHINH.map((m) => (
              <Button key={m.toi} onClick={() => di(m.toi)} sx={{ color: MAU.chinh, fontSize: 17, fontWeight: 500, textTransform: 'none' }}>
                {m.nhan}
              </Button>
            ))}
          </Box>
          <Box sx={{ flex: { xs: 1, md: 'none' } }} />
          {thongTin ? (
            <Button
              variant="outlined"
              startIcon={<AccountCircleRoundedIcon />}
              onClick={(e) => setAnchor(e.currentTarget)}
              sx={{ display: { xs: 'none', md: 'inline-flex' }, py: 1.2, px: 2.5, fontWeight: 700 }}
            >
              {hoTen ?? 'Tài khoản'}
            </Button>
          ) : (
            <Button
              variant="contained"
              onClick={() => di('/dang-nhap')}
              sx={{ display: { xs: 'none', md: 'inline-flex' }, background: GRADIENT.dangNhap, py: 1.2, px: 3, fontWeight: 700 }}
            >
              Đăng nhập
            </Button>
          )}
          <IconButton sx={{ display: { md: 'none' } }} onClick={() => setMoDrawer(true)} aria-label="Mở menu">
            <MenuRoundedIcon />
          </IconButton>
        </Toolbar>
      </Container>

      <Menu anchorEl={anchor} open={Boolean(anchor)} onClose={() => setAnchor(null)}>
        {menuVaiTro.map((m) => (
          <MenuItem key={m.toi} onClick={() => di(m.toi)}>
            {m.nhan}
          </MenuItem>
        ))}
        <Divider />
        <MenuItem onClick={thoat} sx={{ color: 'error.main' }}>
          <LogoutRoundedIcon fontSize="small" sx={{ mr: 1 }} /> Đăng xuất
        </MenuItem>
      </Menu>

      <Drawer anchor="right" open={moDrawer} onClose={() => setMoDrawer(false)}>
        <Box sx={{ width: 260, pt: 2 }}>
          <List>
            {MENU_CHINH.map((m) => (
              <ListItemButton key={m.toi} onClick={() => di(m.toi)}>
                <ListItemText primary={m.nhan} />
              </ListItemButton>
            ))}
            <Divider sx={{ my: 1 }} />
            {thongTin ? (
              <>
                {menuVaiTro.map((m) => (
                  <ListItemButton key={m.toi} onClick={() => di(m.toi)}>
                    <ListItemText primary={m.nhan} />
                  </ListItemButton>
                ))}
                <ListItemButton onClick={thoat}>
                  <ListItemText primary="Đăng xuất" slotProps={{ primary: { color: 'error' } }} />
                </ListItemButton>
              </>
            ) : (
              <ListItemButton onClick={() => di('/dang-nhap')}>
                <ListItemText primary="Đăng nhập" />
              </ListItemButton>
            )}
          </List>
        </Box>
      </Drawer>
    </AppBar>
  )
}
