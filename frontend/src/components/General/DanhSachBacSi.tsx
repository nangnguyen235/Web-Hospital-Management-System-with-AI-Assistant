import SearchRoundedIcon from '@mui/icons-material/SearchRounded'
import Avatar from '@mui/material/Avatar'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Card from '@mui/material/Card'
import CardContent from '@mui/material/CardContent'
import Grid from '@mui/material/Grid'
import InputAdornment from '@mui/material/InputAdornment'
import MenuItem from '@mui/material/MenuItem'
import Pagination from '@mui/material/Pagination'
import Paper from '@mui/material/Paper'
import Stack from '@mui/material/Stack'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { useSnackbar } from 'notistack'
import { useEffect, useState } from 'react'
import { layThongBaoLoi } from '../../services/api'
import { danhMucApi, type Khoa, type PagedResult } from '../../services/danhMuc'
import { nguoiDungApi, TEN_HOC_HAM, type BacSiCongKhai, type HocHam } from '../../services/nguoiDung'
import { GRADIENT } from '../../theme'
import Loading from './Loading'

const MOI_TRANG = 9

/** Tìm bác sĩ theo tên, khoa, học hàm (FR-BN-07) — dùng ở trang công khai và bước 2 đặt khám. */
export default function DanhSachBacSi({ onChon, nutChon = 'Đặt khám' }: { onChon: (b: BacSiCongKhai) => void; nutChon?: string }) {
  const { enqueueSnackbar } = useSnackbar()
  const [khoa, setKhoa] = useState<Khoa[]>([])
  const [tuKhoa, setTuKhoa] = useState('')
  const [loc, setLoc] = useState<{ tuKhoa: string; khoaId: number | ''; hocHam: HocHam | '' }>({ tuKhoa: '', khoaId: '', hocHam: '' })
  const [trang, setTrang] = useState(1)
  const [kq, setKq] = useState<PagedResult<BacSiCongKhai> | null>(null)

  useEffect(() => {
    danhMucApi.khoaCongKhai().then(setKhoa).catch(() => undefined)
  }, [])

  useEffect(() => {
    nguoiDungApi
      .timBacSi({ tuKhoa: loc.tuKhoa || undefined, khoaId: loc.khoaId || undefined, hocHam: loc.hocHam || undefined, page: trang, pageSize: MOI_TRANG })
      .then(setKq)
      .catch((e) => enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' }))
  }, [loc, trang, enqueueSnackbar])

  const doiLoc = (p: Partial<typeof loc>) => {
    setTrang(1)
    setLoc({ ...loc, ...p })
  }

  return (
    <>
      <Paper elevation={2} sx={{ p: 2.5, mb: 3 }}>
        <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
          <Box
            component="form"
            sx={{ flex: 1 }}
            onSubmit={(e) => {
              e.preventDefault()
              doiLoc({ tuKhoa: tuKhoa.trim() })
            }}
          >
            <TextField
              fullWidth
              placeholder="Tìm theo tên bác sĩ (Enter để tìm)"
              value={tuKhoa}
              onChange={(e) => setTuKhoa(e.target.value)}
              slotProps={{ input: { startAdornment: <InputAdornment position="start"><SearchRoundedIcon /></InputAdornment> } }}
            />
          </Box>
          <TextField select label="Chuyên khoa" value={loc.khoaId} onChange={(e) => doiLoc({ khoaId: e.target.value === '' ? '' : Number(e.target.value) })} sx={{ minWidth: 200 }}>
            <MenuItem value="">Tất cả</MenuItem>
            {khoa.map((k) => (
              <MenuItem key={k.id} value={k.id}>{k.tenKhoa}</MenuItem>
            ))}
          </TextField>
          <TextField select label="Học hàm" value={loc.hocHam} onChange={(e) => doiLoc({ hocHam: e.target.value as HocHam | '' })} sx={{ minWidth: 160 }}>
            <MenuItem value="">Tất cả</MenuItem>
            {Object.entries(TEN_HOC_HAM).map(([k, v]) => (
              <MenuItem key={k} value={k}>{v}</MenuItem>
            ))}
          </TextField>
        </Stack>
      </Paper>
      {!kq ? (
        <Loading />
      ) : (
        <>
          {kq.items.length === 0 && <Typography sx={{ textAlign: 'center', py: 5 }}>Không tìm thấy bác sĩ phù hợp.</Typography>}
          <Grid container spacing={2.5}>
            {kq.items.map((b) => (
              <Grid key={b.id} size={{ xs: 12, sm: 6, md: 4 }}>
                <Card elevation={2} sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
                  <CardContent sx={{ flex: 1, display: 'flex', gap: 2 }}>
                    <Avatar src={b.anhDaiDienUrl ?? undefined} sx={{ width: 64, height: 64, bgcolor: 'secondary.main' }}>
                      {b.hoTen.charAt(0)}
                    </Avatar>
                    <Box>
                      <Typography sx={{ fontWeight: 700 }}>
                        {b.hocHam ? `${TEN_HOC_HAM[b.hocHam]} ` : ''}
                        {b.hoTen}
                      </Typography>
                      <Typography color="text.secondary">Chuyên khoa: {b.tenKhoa}</Typography>
                      <Typography color="text.secondary">Đánh giá: {b.diemDanhGia != null ? `${b.diemDanhGia} ★` : 'chưa có'}</Typography>
                    </Box>
                  </CardContent>
                  <Box sx={{ p: 2, pt: 0 }}>
                    <Button fullWidth variant="contained" onClick={() => onChon(b)} sx={{ background: GRADIENT.nut, fontWeight: 700 }}>
                      {nutChon}
                    </Button>
                  </Box>
                </Card>
              </Grid>
            ))}
          </Grid>
          {kq.totalCount > MOI_TRANG && (
            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 3 }}>
              <Pagination count={Math.ceil(kq.totalCount / MOI_TRANG)} page={trang} onChange={(_, p) => setTrang(p)} color="primary" />
            </Box>
          )}
        </>
      )}
    </>
  )
}
