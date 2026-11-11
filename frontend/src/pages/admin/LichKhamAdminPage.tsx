import MenuItem from '@mui/material/MenuItem'
import Paper from '@mui/material/Paper'
import Stack from '@mui/material/Stack'
import TextField from '@mui/material/TextField'
import { useSnackbar } from 'notistack'
import { useEffect, useState } from 'react'
import BangLich from '../../components/General/BangLich'
import PageHeader from '../../components/General/PageHeader'
import { layThongBaoLoi } from '../../services/api'
import { danhMucApi, type Khoa, type Phong } from '../../services/danhMuc'
import { congNgay, lichKhamApi, ngayISO, type Lich } from '../../services/lichKham'

/** FR-AD-10: xem lịch khám theo khoa, phòng, khoảng ngày. */
export default function LichKhamAdminPage() {
  const { enqueueSnackbar } = useSnackbar()
  const homNay = ngayISO(new Date())
  const [khoa, setKhoa] = useState<Khoa[]>([])
  const [phong, setPhong] = useState<Phong[]>([])
  const [khoaId, setKhoaId] = useState<number | ''>('')
  const [phongId, setPhongId] = useState<number | ''>('')
  const [tu, setTu] = useState(homNay)
  const [den, setDen] = useState(congNgay(homNay, 6))
  const [lich, setLich] = useState<Lich[] | null>(null)

  useEffect(() => {
    danhMucApi.khoaCongKhai().then(setKhoa).catch(() => undefined)
  }, [])
  useEffect(() => {
    if (khoaId === '') return
    danhMucApi.phong(khoaId, 'PHONG_KHAM').then(setPhong).catch(() => setPhong([]))
  }, [khoaId])
  useEffect(() => {
    lichKhamApi
      .lichAdmin({ khoaId: khoaId || undefined, phongId: phongId || undefined, tu: tu || undefined, den: den || undefined })
      .then(setLich)
      .catch((e) => {
        setLich([])
        enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' })
      })
  }, [khoaId, phongId, tu, den, enqueueSnackbar])

  return (
    <>
      <PageHeader tieuDe="Lịch khám" />
      <Paper elevation={2} sx={{ p: 2.5, mb: 3 }}>
        <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
          <TextField select label="Khoa" value={khoaId} onChange={(e) => {
              setKhoaId(e.target.value === '' ? '' : Number(e.target.value))
              setPhongId('')
              setPhong([])
            }} sx={{ minWidth: 200 }}>
            <MenuItem value="">Tất cả</MenuItem>
            {khoa.map((k) => (
              <MenuItem key={k.id} value={k.id}>{k.tenKhoa}</MenuItem>
            ))}
          </TextField>
          <TextField select label="Phòng" value={phongId} disabled={khoaId === ''} onChange={(e) => setPhongId(e.target.value === '' ? '' : Number(e.target.value))} sx={{ minWidth: 180 }}>
            <MenuItem value="">Tất cả</MenuItem>
            {phong.map((p) => (
              <MenuItem key={p.id} value={p.id}>{p.tenPhong}</MenuItem>
            ))}
          </TextField>
          <TextField label="Từ ngày" type="date" value={tu} onChange={(e) => setTu(e.target.value)} slotProps={{ inputLabel: { shrink: true } }} />
          <TextField label="Đến ngày" type="date" value={den} onChange={(e) => setDen(e.target.value)} slotProps={{ inputLabel: { shrink: true } }} />
        </Stack>
      </Paper>
      <BangLich lich={lich} />
    </>
  )
}
