import FullscreenRoundedIcon from '@mui/icons-material/FullscreenRounded'
import Box from '@mui/material/Box'
import IconButton from '@mui/material/IconButton'
import Typography from '@mui/material/Typography'
import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { BRAND } from '../../config/brand'
import { layThongBaoLoi } from '../../services/api'
import { lichKhamApi, type SoDangGoi } from '../../services/lichKham'
import { MAU } from '../../theme'
import { CHU_KY_CAP_NHAT_MS, dinhDangSo } from './manHinhSo'

/**
 * Màn hình số đang gọi công khai (FR-BN-10, BR-15) — bố cục như TheDuck: trái 5 số vừa gọi, phải số hiện tại,
 * dưới tên bệnh viện + khoa + phòng. KHÁC TheDuck: KHÔNG hiện họ tên (NFR-03); cập nhật 5 giây (NFR-01).
 */
export default function ManHinhSoPage() {
  const { lichKhamId = '' } = useParams()
  const [duLieu, setDuLieu] = useState<SoDangGoi | null>(null)
  const [loi, setLoi] = useState<string | null>(null)

  useEffect(() => {
    let huy = false
    const tai = () =>
      lichKhamApi
        .soDangGoi(lichKhamId)
        .then((d) => {
          if (huy) return
          setDuLieu(d)
          setLoi(null)
        })
        .catch((e) => !huy && setLoi(layThongBaoLoi(e)))
    tai()
    const id = window.setInterval(tai, CHU_KY_CAP_NHAT_MS)
    return () => {
      huy = true
      window.clearInterval(id)
    }
  }, [lichKhamId])

  const toanManHinh = () => {
    document.documentElement.requestFullscreen?.().catch(() => undefined)
  }

  const cacSo = duLieu?.cacSoVuaGoi ?? []
  return (
    <Box sx={{ height: '100vh', display: 'grid', gridTemplateRows: '1fr auto', bgcolor: '#f3fafd', color: MAU.chu, position: 'relative' }}>
      <IconButton onClick={toanManHinh} sx={{ position: 'absolute', top: 8, right: 8, opacity: 0.4 }} aria-label="Toàn màn hình">
        <FullscreenRoundedIcon />
      </IconButton>
      <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', md: '3fr 2fr' }, borderBottom: '4px solid #000' }}>
        <Box sx={{ borderRight: { md: '4px solid #000' } }}>
          {[0, 1, 2, 3, 4].map((i) => (
            <Box key={i} sx={{ height: '20%', minHeight: 70, display: 'flex', alignItems: 'center', px: { xs: 3, md: 8 }, borderBottom: i < 4 ? '1px solid #dfe9ee' : 0 }}>
              <Typography sx={{ fontSize: { xs: 40, md: 64 }, fontWeight: 500, color: MAU.chinh, width: 140 }}>
                {cacSo[i] !== undefined ? dinhDangSo(cacSo[i]) : ''}
              </Typography>
              {cacSo[i] !== undefined && (
                <Typography sx={{ fontSize: { xs: 22, md: 34 }, fontWeight: 500 }}>Mời vào phòng khám</Typography>
              )}
            </Box>
          ))}
        </Box>
        <Box sx={{ display: 'grid', placeItems: 'center', textAlign: 'center', py: 4 }}>
          <Box>
            <Typography sx={{ fontSize: { xs: 28, md: 44 }, fontWeight: 500 }}>Số thứ tự hiện tại</Typography>
            <Typography sx={{ fontSize: { xs: 120, md: 220 }, lineHeight: 1.1, color: '#0094d4' }}>
              {duLieu && duLieu.soDangGoi > 0 ? dinhDangSo(duLieu.soDangGoi) : '--'}
            </Typography>
          </Box>
        </Box>
      </Box>
      <Box sx={{ textAlign: 'center', py: { xs: 3, md: 5 } }}>
        <Typography sx={{ fontSize: { xs: 36, md: 60 }, fontWeight: 700, color: '#00a9dd', textTransform: 'uppercase' }}>
          {BRAND.tenDayDu}
        </Typography>
        {loi ? (
          <Typography sx={{ fontSize: { xs: 22, md: 34 }, color: 'error.main' }}>{loi}</Typography>
        ) : (
          <>
            <Typography sx={{ fontSize: { xs: 24, md: 40 }, fontWeight: 500 }}>{duLieu?.tenKhoa}</Typography>
            <Typography sx={{ fontSize: { xs: 24, md: 40 }, fontWeight: 500 }}>{duLieu && `Phòng khám ${duLieu.tenPhong}`}</Typography>
          </>
        )}
      </Box>
    </Box>
  )
}
