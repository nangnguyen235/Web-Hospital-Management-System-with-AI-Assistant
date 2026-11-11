import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Chip from '@mui/material/Chip'
import Grid from '@mui/material/Grid'
import Paper from '@mui/material/Paper'
import Typography from '@mui/material/Typography'
import { useSnackbar } from 'notistack'
import { useEffect, useMemo, useState } from 'react'
import { Navigate, useNavigate } from 'react-router-dom'
import Loading from '../../components/General/Loading'
import { layThongBaoLoi } from '../../services/api'
import { dinhDangTien } from '../../services/danhMuc'
import { congNgay, hienNgay, lichKhamApi, ngayISO, TEN_BUOI, tenThu, type Khung, type Lich } from '../../services/lichKham'
import { TEN_HOC_HAM } from '../../services/nguoiDung'
import { MAU } from '../../theme'
import BuocDatKham from './BuocDatKham'
import { docDon, themKhung } from './datKham'

/** Bước 3 đặt khám (FR-BN-08, FR-HT-01): ngày có lịch trong 30 ngày tới → khung giờ còn chỗ. */
export default function ChonNgayPage() {
  const navigate = useNavigate()
  const { enqueueSnackbar } = useSnackbar()
  // đọc một lần: object mới mỗi lần render sẽ làm effect tải lịch chạy lặp
  const [don] = useState(docDon)
  const bacSi = don.bacSi
  const [lich, setLich] = useState<Lich[] | null>(null)
  const [ngay, setNgay] = useState<string | null>(null)

  useEffect(() => {
    if (!bacSi) return
    const homNay = ngayISO(new Date())
    lichKhamApi
      .lichCuaBacSi(bacSi.id, { tu: homNay, den: congNgay(homNay, 30) })
      .then((d) => {
        setLich(d)
        setNgay(d[0]?.ngay ?? null)
      })
      .catch((e) => enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' }))
  }, [bacSi, enqueueSnackbar])

  const cacNgay = useMemo(() => [...new Set((lich ?? []).map((l) => l.ngay))].sort(), [lich])

  if (!don.hoSoId) return <Navigate to="/dat-kham/chon-ho-so" replace />
  if (!bacSi) return <Navigate to="/dat-kham/chon-bac-si" replace />

  const chonKhung = (l: Lich, k: Khung) => {
    const kq = themKhung(docDon(), {
      khungId: k.id, lichId: l.id, khoaId: bacSi.khoaId, tenKhoa: bacSi.tenKhoa, tenBacSi: bacSi.hoTen, tenPhong: l.tenPhong,
      tenDichVu: l.tenDichVu, gia: l.gia, ngay: l.ngay, buoi: l.buoi, gio: `${k.gioBatDau} - ${k.gioKetThuc}`,
    })
    if (kq.loi) return enqueueSnackbar(kq.loi, { variant: 'warning' })
    navigate('/dat-kham/xac-nhan')
  }

  return (
    <BuocDatKham buoc={2} tieuDe="Chọn ngày và giờ khám">
      <Paper elevation={2} sx={{ p: 2.5, mb: 3 }}>
        <Typography>
          Bệnh nhân: <b>{don.hoTen}</b> · Bác sĩ:{' '}
          <b>
            {bacSi.hocHam ? `${TEN_HOC_HAM[bacSi.hocHam]} ` : ''}
            {bacSi.hoTen}
          </b>{' '}
          · Chuyên khoa: <b>{bacSi.tenKhoa}</b>
        </Typography>
      </Paper>
      {!lich ? (
        <Loading />
      ) : cacNgay.length === 0 ? (
        <Box sx={{ textAlign: 'center', py: 5 }}>
          <Typography sx={{ mb: 2 }}>Bác sĩ chưa có lịch khám trong 30 ngày tới.</Typography>
          <Button variant="outlined" onClick={() => navigate('/dat-kham/chon-bac-si')}>Chọn bác sĩ khác</Button>
        </Box>
      ) : (
        <Grid container spacing={3}>
          <Grid size={{ xs: 12, md: 4 }}>
            <Paper elevation={2} sx={{ p: 2 }}>
              <Typography sx={{ fontWeight: 700, mb: 1.5 }}>Ngày có lịch</Typography>
              <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                {cacNgay.map((n) => {
                  const l = lich.find((x) => x.ngay === n)!
                  return (
                    <Chip
                      key={n}
                      label={`${tenThu(l.thuTrongTuan)} ${hienNgay(n).slice(0, 5)}`}
                      color={n === ngay ? 'primary' : 'default'}
                      onClick={() => setNgay(n)}
                    />
                  )
                })}
              </Box>
            </Paper>
          </Grid>
          <Grid size={{ xs: 12, md: 8 }}>
            {lich
              .filter((l) => l.ngay === ngay)
              .map((l) => (
                <Paper key={l.id} elevation={2} sx={{ p: 2.5, mb: 2 }}>
                  <Typography sx={{ fontWeight: 700, color: MAU.chinh }}>
                    Buổi {TEN_BUOI[l.buoi].toLowerCase()} · Phòng {l.tenPhong} · {l.tenDichVu} · {dinhDangTien(l.gia)}
                  </Typography>
                  <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1.5, mt: 2 }}>
                    {l.khung.map((k) => {
                      const het = k.daQua || k.conCho <= 0
                      return (
                        <Button key={k.id} variant="outlined" disabled={het} onClick={() => chonKhung(l, k)} sx={{ minWidth: 150, flexDirection: 'column' }}>
                          <span>{k.gioBatDau} - {k.gioKetThuc}</span>
                          <Typography variant="caption">{k.daQua ? 'Đã qua' : het ? 'Hết chỗ' : `Còn ${k.conCho} chỗ`}</Typography>
                        </Button>
                      )
                    })}
                  </Box>
                </Paper>
              ))}
          </Grid>
        </Grid>
      )}
    </BuocDatKham>
  )
}
