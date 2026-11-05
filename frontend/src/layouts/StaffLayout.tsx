import LogoutRoundedIcon from '@mui/icons-material/LogoutRounded'
import MeetingRoomRoundedIcon from '@mui/icons-material/MeetingRoomRounded'
import MenuRoundedIcon from '@mui/icons-material/MenuRounded'
import Avatar from '@mui/material/Avatar'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Divider from '@mui/material/Divider'
import Drawer from '@mui/material/Drawer'
import IconButton from '@mui/material/IconButton'
import List from '@mui/material/List'
import ListItemButton from '@mui/material/ListItemButton'
import ListItemText from '@mui/material/ListItemText'
import Typography from '@mui/material/Typography'
import { useState } from 'react'
import { Outlet, useLocation, useNavigate } from 'react-router-dom'
import { TEN_VAI_TRO } from '../auth/jwt'
import { useAuth } from '../auth/useAuth'
import Logo from '../components/General/Logo'
import { MENU_BAC_SI, MENU_DIEU_DUONG } from '../routes/menu'

const RONG = 280

/** Khu vực bác sĩ/điều dưỡng kiểu TheDuck: sidebar trắng, khối tên + nút Đăng xuất đỏ, thanh trên "Phòng – Khoa". */
export default function StaffLayout() {
  const { thongTin, hoTen, dangXuat } = useAuth()
  const { pathname } = useLocation()
  const navigate = useNavigate()
  const [moMobile, setMoMobile] = useState(false)
  const laBacSi = thongTin?.vaiTro === 'BAC_SI'
  const menu = (laBacSi ? MENU_BAC_SI : MENU_DIEU_DUONG).filter((m) => !m.chiTruongKhoa || thongTin?.laTruongKhoa)
  const tenVaiTro = thongTin ? TEN_VAI_TRO[thongTin.vaiTro] : ''

  const sidebar = (
    <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column', bgcolor: '#fff', borderRight: '1px solid', borderColor: 'divider' }}>
      <Box sx={{ py: 3, textAlign: 'center' }}>
        <Logo coChu={32} />
      </Box>
      <Typography sx={{ px: 3, pb: 1, fontWeight: 700, fontSize: 20, textTransform: 'uppercase' }}>
        {tenVaiTro}
        {thongTin?.laTruongKhoa ? (laBacSi ? ' · Trưởng khoa' : ' · Trưởng') : ''}
      </Typography>
      <List sx={{ flex: 1 }}>
        {menu.map((m) => (
          <ListItemButton
            key={m.toi}
            selected={pathname.startsWith(m.toi)}
            onClick={() => {
              setMoMobile(false)
              navigate(m.toi)
            }}
            sx={{ px: 4 }}
          >
            <ListItemText primary={m.nhan} />
          </ListItemButton>
        ))}
      </List>
      <Divider />
      <Box sx={{ p: 2.5 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, mb: 2 }}>
          <Avatar sx={{ bgcolor: 'secondary.main' }}>{hoTen?.charAt(0) ?? '?'}</Avatar>
          <Box>
            <Typography variant="body2" color="text.secondary">
              {tenVaiTro}
            </Typography>
            <Typography sx={{ fontWeight: 600 }}>{hoTen}</Typography>
          </Box>
        </Box>
        <Button
          fullWidth
          variant="contained"
          color="error"
          endIcon={<LogoutRoundedIcon />}
          onClick={() => {
            dangXuat()
            navigate('/')
          }}
        >
          Đăng xuất
        </Button>
      </Box>
    </Box>
  )

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh', bgcolor: 'background.default' }}>
      <Drawer variant="permanent" sx={{ display: { xs: 'none', md: 'block' }, width: RONG, '& .MuiDrawer-paper': { width: RONG, border: 0 } }}>
        {sidebar}
      </Drawer>
      <Drawer open={moMobile} onClose={() => setMoMobile(false)} sx={{ display: { md: 'none' }, '& .MuiDrawer-paper': { width: RONG } }}>
        {sidebar}
      </Drawer>
      <Box sx={{ flex: 1, minWidth: 0 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, px: { xs: 2, md: 4 }, py: 2, bgcolor: '#fff', borderBottom: '1px solid', borderColor: 'divider' }}>
          <IconButton sx={{ display: { md: 'none' } }} onClick={() => setMoMobile(true)} aria-label="Mở menu">
            <MenuRoundedIcon />
          </IconButton>
          <MeetingRoomRoundedIcon color="primary" />
          {/* Phòng – Khoa lấy từ lịch phân công/lịch khám hôm nay khi module LichKham có API (FR-BS-02, FR-DD-02) */}
          <Typography variant="h6" sx={{ fontWeight: 500 }}>
            Chưa chọn phòng làm việc
          </Typography>
        </Box>
        <Box sx={{ px: { xs: 2, md: 5 }, py: 4 }}>
          <Outlet />
        </Box>
      </Box>
    </Box>
  )
}
