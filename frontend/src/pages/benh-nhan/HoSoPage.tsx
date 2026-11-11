import AddRoundedIcon from '@mui/icons-material/AddRounded'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Card from '@mui/material/Card'
import CardActions from '@mui/material/CardActions'
import CardContent from '@mui/material/CardContent'
import Grid from '@mui/material/Grid'
import Typography from '@mui/material/Typography'
import { useSnackbar } from 'notistack'
import { useCallback, useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import DialogConfirm from '../../components/General/DialogConfirm'
import DialogHoSo from '../../components/General/DialogHoSo'
import Loading from '../../components/General/Loading'
import { layThongBaoLoi } from '../../services/api'
import { hienNgay } from '../../services/lichKham'
import { nguoiDungApi, TEN_GIOI_TINH, type HoSo, type LuuHoSo } from '../../services/nguoiDung'
import { GRADIENT } from '../../theme'
import { ghiDon } from './datKham'

const TOI_DA = 10

/** FR-BN-05, BR-03: tối đa 10 hồ sơ; thêm, sửa, xóa mềm; đặt khám nhanh cho từng hồ sơ. */
export default function HoSoPage() {
  const navigate = useNavigate()
  const { enqueueSnackbar } = useSnackbar()
  const [ds, setDs] = useState<HoSo[] | null>(null)
  const [form, setForm] = useState<{ hoSo: HoSo | null } | null>(null)
  const [dangGui, setDangGui] = useState(false)
  const [xoa, setXoa] = useState<HoSo | null>(null)

  const tai = useCallback(() => {
    nguoiDungApi.hoSo().then(setDs).catch((e) => enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' }))
  }, [enqueueSnackbar])
  useEffect(tai, [tai])

  const luu = async (g: LuuHoSo) => {
    if (!form) return
    setDangGui(true)
    try {
      if (form.hoSo) await nguoiDungApi.suaHoSo(form.hoSo.id, g)
      else await nguoiDungApi.taoHoSo(g)
      enqueueSnackbar('Đã lưu hồ sơ', { variant: 'success' })
      setForm(null)
      tai()
    } catch (e) {
      enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' })
    } finally {
      setDangGui(false)
    }
  }

  const xacNhanXoa = async () => {
    const h = xoa
    setXoa(null)
    if (!h) return
    try {
      await nguoiDungApi.xoaHoSo(h.id)
      tai()
    } catch (e) {
      enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' })
    }
  }

  return (
    <>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2, flexWrap: 'wrap', gap: 1 }}>
        <Typography variant="h5" sx={{ fontWeight: 700 }}>
          Hồ sơ bệnh nhân {ds && `(${ds.length}/${TOI_DA})`}
        </Typography>
        <Box sx={{ display: 'flex', gap: 1 }}>
          <Button onClick={() => navigate('/tim-lai-ho-so')}>Tìm lại hồ sơ đã mất</Button>
          <Button
            variant="contained"
            startIcon={<AddRoundedIcon />}
            disabled={!ds || ds.length >= TOI_DA}
            onClick={() => setForm({ hoSo: null })}
            sx={{ background: GRADIENT.nut, fontWeight: 700 }}
          >
            Thêm hồ sơ
          </Button>
        </Box>
      </Box>
      {!ds ? (
        <Loading />
      ) : ds.length === 0 ? (
        <Typography color="text.secondary" sx={{ py: 5, textAlign: 'center' }}>
          Bạn chưa có hồ sơ nào. Thêm hồ sơ để đặt khám.
        </Typography>
      ) : (
        <Grid container spacing={2}>
          {ds.map((h) => (
            <Grid key={h.id} size={{ xs: 12, md: 6 }}>
              <Card elevation={2}>
                <CardContent>
                  <Typography variant="h6" sx={{ fontWeight: 700 }}>{h.hoTen}</Typography>
                  <Typography color="text.secondary">
                    {TEN_GIOI_TINH[h.gioiTinh]} · Ngày sinh {hienNgay(h.ngaySinh)}
                  </Typography>
                  <Typography color="text.secondary">SĐT: {h.soDienThoai ?? '—'} · Email: {h.email ?? '—'}</Typography>
                  <Typography color="text.secondary">Mã bệnh nhân: {h.maBenhNhan ?? 'chưa có (cấp khi tiếp nhận lần đầu)'}</Typography>
                </CardContent>
                <CardActions>
                  <Button
                    size="small"
                    onClick={() => {
                      ghiDon({ hoSoId: h.id, hoTen: h.hoTen, chon: [] })
                      navigate('/dat-kham/chon-bac-si')
                    }}
                  >
                    Đặt khám
                  </Button>
                  <Button size="small" onClick={() => setForm({ hoSo: h })}>Sửa</Button>
                  <Button size="small" color="error" onClick={() => setXoa(h)}>Xóa</Button>
                </CardActions>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}
      <DialogHoSo mo={Boolean(form)} hoSo={form?.hoSo ?? null} dangGui={dangGui} onDong={() => setForm(null)} onLuu={luu} />
      <DialogConfirm
        mo={Boolean(xoa)}
        tieuDe="Xóa hồ sơ"
        noiDung={`Xóa hồ sơ "${xoa?.hoTen}" khỏi tài khoản? Lịch sử khám vẫn được giữ, có thể tìm lại bằng OTP.`}
        mauNguyHiem
        onDong={() => setXoa(null)}
        onXacNhan={xacNhanXoa}
      />
    </>
  )
}
