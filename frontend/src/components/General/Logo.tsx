import LocalHospitalRoundedIcon from '@mui/icons-material/LocalHospitalRounded'
import Box from '@mui/material/Box'
import Typography from '@mui/material/Typography'
import { Link as RouterLink } from 'react-router-dom'
import { BRAND } from '../../config/brand'

interface LogoProps {
  /** `sang` cho nền tối (sidebar Admin). */
  bienThe?: 'thuong' | 'sang'
  coKhauHieu?: boolean
  coChu?: number
}

/** Logo chữ của TheHos (logo riêng — không dùng logo TheDuck). */
export default function Logo({ bienThe = 'thuong', coKhauHieu = true, coChu = 34 }: LogoProps) {
  const mauChu = bienThe === 'sang' ? '#4dc9e6' : '#0974f1'
  const mauPhu = bienThe === 'sang' ? '#cfe2f3' : '#0b5394'
  return (
    <Box component={RouterLink} to="/" sx={{ textDecoration: 'none', display: 'inline-flex', flexDirection: 'column', alignItems: 'center' }}>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
        <LocalHospitalRoundedIcon sx={{ color: mauChu, fontSize: coChu * 0.95 }} />
        <Typography sx={{ fontWeight: 900, fontSize: coChu, lineHeight: 1, letterSpacing: -1, color: mauChu }}>
          {BRAND.ten.toUpperCase()}
        </Typography>
      </Box>
      <Typography sx={{ fontSize: coChu * 0.3, letterSpacing: 4, color: mauPhu, lineHeight: 1.4 }}>— HOSPITAL —</Typography>
      {coKhauHieu && (
        <Typography sx={{ fontSize: coChu * 0.3, fontWeight: 500, color: mauPhu, lineHeight: 1.2 }}>{BRAND.khauHieu}</Typography>
      )}
    </Box>
  )
}
