import AddRoundedIcon from '@mui/icons-material/AddRounded'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Card from '@mui/material/Card'
import CardActionArea from '@mui/material/CardActionArea'
import CardContent from '@mui/material/CardContent'
import Grid from '@mui/material/Grid'
import Typography from '@mui/material/Typography'
import { useSnackbar } from 'notistack'
import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Loading from '../../components/General/Loading'
import { layThongBaoLoi } from '../../services/api'
import { hienNgay } from '../../services/lichKham'
import { nguoiDungApi, TEN_GIOI_TINH, type HoSo } from '../../services/nguoiDung'
import BuocDatKham from './BuocDatKham'
import { docDon, ghiDon } from './datKham'

/** Bước 1 đặt khám (FR-BN-08): chọn hồ sơ của tài khoản. */
export default function ChonHoSoPage() {
  const navigate = useNavigate()
  const { enqueueSnackbar } = useSnackbar()
  const [ds, setDs] = useState<HoSo[] | null>(null)

  useEffect(() => {
    nguoiDungApi.hoSo().then(setDs).catch((e) => enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' }))
  }, [enqueueSnackbar])

  const chon = (h: HoSo) => {
    const cu = docDon()
    // đổi hồ sơ thì bỏ các khung đã chọn cho hồ sơ cũ
    ghiDon({ hoSoId: h.id, hoTen: h.hoTen, chon: cu.hoSoId === h.id ? cu.chon : [] })
    navigate(cu.bacSi ? '/dat-kham/chon-ngay' : '/dat-kham/chon-bac-si')
  }

  return (
    <BuocDatKham buoc={0} tieuDe="Chọn hồ sơ bệnh nhân">
      {!ds ? (
        <Loading />
      ) : (
        <Grid container spacing={2.5}>
          {ds.map((h) => (
            <Grid key={h.id} size={{ xs: 12, sm: 6, md: 4 }}>
              <Card elevation={2}>
                <CardActionArea onClick={() => chon(h)} sx={{ p: 1 }}>
                  <CardContent>
                    <Typography variant="h6" sx={{ fontWeight: 700 }}>{h.hoTen}</Typography>
                    <Typography color="text.secondary">
                      {TEN_GIOI_TINH[h.gioiTinh]} · {hienNgay(h.ngaySinh)}
                    </Typography>
                    <Typography color="text.secondary">{h.soDienThoai ?? 'Chưa có SĐT'}</Typography>
                  </CardContent>
                </CardActionArea>
              </Card>
            </Grid>
          ))}
          <Grid size={12}>
            <Box sx={{ textAlign: 'center' }}>
              {ds.length === 0 && <Typography sx={{ mb: 2 }}>Bạn chưa có hồ sơ nào.</Typography>}
              <Button startIcon={<AddRoundedIcon />} variant="outlined" onClick={() => navigate('/tai-khoan/ho-so')}>
                Thêm hồ sơ mới
              </Button>
            </Box>
          </Grid>
        </Grid>
      )}
    </BuocDatKham>
  )
}
