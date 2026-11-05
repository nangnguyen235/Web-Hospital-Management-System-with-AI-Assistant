import AccountCircleRoundedIcon from '@mui/icons-material/AccountCircleRounded'
import BarChartRoundedIcon from '@mui/icons-material/BarChartRounded'
import CalendarMonthRoundedIcon from '@mui/icons-material/CalendarMonthRounded'
import ElderlyRoundedIcon from '@mui/icons-material/ElderlyRounded'
import LocalHospitalRoundedIcon from '@mui/icons-material/LocalHospitalRounded'
import LogoutRoundedIcon from '@mui/icons-material/LogoutRounded'
import MedicationRoundedIcon from '@mui/icons-material/MedicationRounded'
import MeetingRoomRoundedIcon from '@mui/icons-material/MeetingRoomRounded'
import MenuRoundedIcon from '@mui/icons-material/MenuRounded'
import MedicalServicesRoundedIcon from '@mui/icons-material/MedicalServicesRounded'
import PeopleAltRoundedIcon from '@mui/icons-material/PeopleAltRounded'
import ReceiptLongRoundedIcon from '@mui/icons-material/ReceiptLongRounded'
import Box from '@mui/material/Box'
import Drawer from '@mui/material/Drawer'
import IconButton from '@mui/material/IconButton'
import List from '@mui/material/List'
import ListItemButton from '@mui/material/ListItemButton'
import ListItemIcon from '@mui/material/ListItemIcon'
import ListItemText from '@mui/material/ListItemText'
import Tooltip from '@mui/material/Tooltip'
import Typography from '@mui/material/Typography'
import { useState, type ReactNode } from 'react'
import { Outlet, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/useAuth'
import Logo from '../components/General/Logo'
import { BRAND } from '../config/brand'
import { MENU_ADMIN } from '../routes/menu'
import { GRADIENT } from '../theme'

const RONG = 280
const ICON: Record<string, ReactNode> = {
  '/admin/thong-ke': <BarChartRoundedIcon />,
  '/admin/tai-khoan': <AccountCircleRoundedIcon />,
  '/admin/nhan-vien': <PeopleAltRoundedIcon />,
  '/admin/benh-nhan': <ElderlyRoundedIcon />,
  '/admin/khoa': <LocalHospitalRoundedIcon />,
  '/admin/phong': <MeetingRoomRoundedIcon />,
  '/admin/dich-vu': <MedicalServicesRoundedIcon />,
  '/admin/thuoc': <MedicationRoundedIcon />,
  '/admin/giao-dich': <ReceiptLongRoundedIcon />,
  '/admin/lich-kham': <CalendarMonthRoundedIcon />,
}

/** Trang quản trị kiểu TheDuck: sidebar gradient tối 280 px, nhóm THỐNG KÊ / QUẢN LÝ. */
export default function AdminLayout() {
  const { pathname } = useLocation()
  const navigate = useNavigate()
  const { hoTen, dangXuat } = useAuth()
  const [moMobile, setMoMobile] = useState(false)

  const noiDungSidebar = (
    <Box sx={{ height: '100%', background: GRADIENT.sidebarAdmin, color: '#fff', overflowY: 'auto' }}>
      <Box sx={{ py: 3, textAlign: 'center' }}>
        <Logo bienThe="sang" coChu={34} />
      </Box>
      {MENU_ADMIN.map((nhom) => (
        <Box key={nhom.nhom} sx={{ mb: 1 }}>
          <Typography sx={{ px: 3.5, py: 1.5, fontWeight: 700, fontSize: 18 }}>{nhom.nhom}</Typography>
          <List disablePadding>
            {nhom.muc.map((m) => (
              <ListItemButton
                key={m.toi}
                selected={pathname.startsWith(m.toi)}
                onClick={() => {
                  setMoMobile(false)
                  navigate(m.toi)
                }}
                sx={{
                  px: 4.5,
                  py: 1.3,
                  '&.Mui-selected, &.Mui-selected:hover': { bgcolor: 'rgba(255,255,255,0.16)' },
                  '&:hover': { bgcolor: 'rgba(255,255,255,0.08)' },
                }}
              >
                <ListItemIcon sx={{ color: '#fff', minWidth: 42 }}>{ICON[m.toi]}</ListItemIcon>
                <ListItemText primary={m.nhan} slotProps={{ primary: { sx: { fontWeight: 600, fontSize: 17 } } }} />
              </ListItemButton>
            ))}
          </List>
        </Box>
      ))}
    </Box>
  )

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh', bgcolor: 'background.default' }}>
      <Drawer variant="permanent" sx={{ display: { xs: 'none', md: 'block' }, width: RONG, '& .MuiDrawer-paper': { width: RONG, border: 0 } }}>
        {noiDungSidebar}
      </Drawer>
      <Drawer open={moMobile} onClose={() => setMoMobile(false)} sx={{ display: { md: 'none' }, '& .MuiDrawer-paper': { width: RONG } }}>
        {noiDungSidebar}
      </Drawer>
      <Box sx={{ flex: 1, minWidth: 0 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', gap: 1.5, px: { xs: 2, md: 5 }, py: 2 }}>
          <IconButton sx={{ display: { md: 'none' }, mr: 'auto' }} onClick={() => setMoMobile(true)} aria-label="Mở menu">
            <MenuRoundedIcon />
          </IconButton>
          <Typography sx={{ fontWeight: 700 }}>{BRAND.tenDayDu}</Typography>
          <Typography color="text.secondary" sx={{ display: { xs: 'none', sm: 'block' } }}>
            · {hoTen}
          </Typography>
          <Tooltip title="Đăng xuất">
            <IconButton
              onClick={() => {
                dangXuat()
                navigate('/')
              }}
              color="error"
            >
              <LogoutRoundedIcon />
            </IconButton>
          </Tooltip>
        </Box>
        <Box sx={{ px: { xs: 2, md: 6 }, pb: 6 }}>
          <Outlet />
        </Box>
      </Box>
    </Box>
  )
}
