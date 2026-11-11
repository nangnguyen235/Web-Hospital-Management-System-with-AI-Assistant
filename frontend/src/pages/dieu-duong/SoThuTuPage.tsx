import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import MenuItem from '@mui/material/MenuItem'
import Paper from '@mui/material/Paper'
import Stack from '@mui/material/Stack'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { useSnackbar } from 'notistack'
import { useEffect, useState } from 'react'
import ChonPhong from '../../components/General/ChonPhong'
import PageHeader from '../../components/General/PageHeader'
import { layThongBaoLoi } from '../../services/api'
import type { Phong } from '../../services/danhMuc'
import { lichKhamApi, TEN_BUOI, type Lich, type SoDangGoi } from '../../services/lichKham'
import { GRADIENT } from '../../theme'

/** FR-DD-07, BR-15: "Gọi tiếp" 5 số của lịch phòng hôm nay; thông báo đến lượt gửi qua sự kiện LuotKhamDaGoi. */
export default function SoThuTuPage() {
  const { enqueueSnackbar } = useSnackbar()
  const [phong, setPhong] = useState<Phong | null>(null)
  const [lich, setLich] = useState<Lich[]>([])
  const [lichId, setLichId] = useState<number | ''>('')
  const [so, setSo] = useState<SoDangGoi | null>(null)
  const [soLonNhat, setSoLonNhat] = useState<number | null>(null)

  useEffect(() => {
    if (!phong) return
    lichKhamApi
      .lichHomNayCuaPhong(phong.id)
      .then((d) => {
        setLich(d)
        setLichId(d[0]?.id ?? '')
      })
      .catch((e) => enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' }))
  }, [phong, enqueueSnackbar])

  useEffect(() => {
    if (lichId === '') return
    lichKhamApi.soDangGoi(lichId).then(setSo).catch(() => undefined)
  }, [lichId])

  const goiTiep = async () => {
    if (lichId === '') return
    try {
      const kq = await lichKhamApi.goiTiep(lichId)
      setSoLonNhat(kq.soLonNhat)
      setSo((s) => (s ? { ...s, soDangGoi: kq.soDangGoi, cacSoVuaGoi: kq.cacSoVuaGoi } : s))
    } catch (e) {
      enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' })
    }
  }

  return (
    <>
      <PageHeader tieuDe="Số thứ tự" />
      <Paper elevation={2} sx={{ p: 2.5, mb: 3 }}>
        <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
          <ChonPhong
            loai="PHONG_KHAM"
            onChon={(p) => {
              setPhong(p)
              setLich([])
              setLichId('')
              setSo(null)
            }}
          />
          <TextField select label="Lịch khám hôm nay" value={lichId} onChange={(e) => {
              setLichId(Number(e.target.value))
              setSo(null)
              setSoLonNhat(null)
            }} sx={{ minWidth: 300 }} disabled={lich.length === 0}
            helperText={phong && lich.length === 0 ? 'Phòng không có lịch khám hôm nay' : ' '}>
            {lich.map((l) => (
              <MenuItem key={l.id} value={l.id}>
                Buổi {TEN_BUOI[l.buoi].toLowerCase()} · {l.tenBacSi} · {l.soDaDat} lượt
              </MenuItem>
            ))}
          </TextField>
        </Stack>
      </Paper>
      {so && (
        <Paper elevation={2} sx={{ p: 4, textAlign: 'center' }}>
          <Typography variant="h6">Số đang gọi</Typography>
          <Typography sx={{ fontSize: 96, fontWeight: 700, color: 'primary.main', lineHeight: 1.2 }}>
            {so.soDangGoi > 0 ? so.soDangGoi : '--'}
          </Typography>
          <Typography color="text.secondary" sx={{ mb: 3 }}>
            Vừa gọi: {so.cacSoVuaGoi.length ? so.cacSoVuaGoi.join(', ') : 'chưa gọi'}
            {soLonNhat != null && ` · số lớn nhất đã cấp: ${soLonNhat}`}
          </Typography>
          <Box sx={{ display: 'flex', gap: 2, justifyContent: 'center' }}>
            <Button size="large" variant="contained" onClick={goiTiep} sx={{ background: GRADIENT.nut, fontWeight: 700, px: 6 }}>
              Gọi tiếp 5 số
            </Button>
            <Button size="large" variant="outlined" href={`/man-hinh-so/${so.lichKhamId}`} target="_blank">
              Mở màn hình số
            </Button>
          </Box>
        </Paper>
      )}
    </>
  )
}
