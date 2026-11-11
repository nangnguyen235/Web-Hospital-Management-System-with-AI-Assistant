import Box from '@mui/material/Box'
import Grid from '@mui/material/Grid'
import LinearProgress from '@mui/material/LinearProgress'
import Paper from '@mui/material/Paper'
import Stack from '@mui/material/Stack'
import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { useSnackbar } from 'notistack'
import { useEffect, useState } from 'react'
import Loading from '../../components/General/Loading'
import PageHeader from '../../components/General/PageHeader'
import { layThongBaoLoi } from '../../services/api'
import { dinhDangTien } from '../../services/danhMuc'
import { congNgay, hienNgay, ngayISO } from '../../services/lichKham'
import { thongKeApi, type TongQuan } from '../../services/thongKe'
import { GRADIENT } from '../../theme'

function The({ nhan, giaTri }: { nhan: string; giaTri: string | number }) {
  return (
    <Paper elevation={2} sx={{ p: 2.5, background: GRADIENT.dauThe, color: '#fff' }}>
      <Typography variant="body2" sx={{ opacity: 0.9 }}>
        {nhan}
      </Typography>
      <Typography variant="h5" sx={{ fontWeight: 700, mt: 0.5 }}>
        {giaTri}
      </Typography>
    </Paper>
  )
}

/** FR-AD-01, BR-25: doanh thu (giao dịch thành công, không tính phí cổng), lượt đặt, theo khoa, theo ngày. */
export default function ThongKePage() {
  const { enqueueSnackbar } = useSnackbar()
  const homNay = ngayISO(new Date())
  const [tu, setTu] = useState(congNgay(homNay, -29))
  const [den, setDen] = useState(homNay)
  const [tk, setTk] = useState<TongQuan | null>(null)

  useEffect(() => {
    thongKeApi
      .admin(tu || undefined, den || undefined)
      .then(setTk)
      .catch((e) => enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' }))
  }, [tu, den, enqueueSnackbar])

  const maxNgay = Math.max(1, ...(tk?.theoNgay.map((n) => n.luotDat) ?? [1]))

  return (
    <>
      <PageHeader
        tieuDe="Thống kê"
        hanhDong={
          <Stack direction="row" spacing={2}>
            <TextField size="small" label="Từ ngày" type="date" value={tu} onChange={(e) => setTu(e.target.value)} slotProps={{ inputLabel: { shrink: true } }} />
            <TextField size="small" label="Đến ngày" type="date" value={den} onChange={(e) => setDen(e.target.value)} slotProps={{ inputLabel: { shrink: true } }} />
          </Stack>
        }
      />
      {!tk ? (
        <Loading />
      ) : (
        <>
          <Grid container spacing={2.5} sx={{ mb: 3 }}>
            <Grid size={{ xs: 12, sm: 6, md: 3 }}><The nhan="Doanh thu" giaTri={dinhDangTien(tk.tongDoanhThu)} /></Grid>
            <Grid size={{ xs: 12, sm: 6, md: 3 }}><The nhan="Lượt đặt khám" giaTri={tk.luotDat} /></Grid>
            <Grid size={{ xs: 12, sm: 6, md: 3 }}><The nhan="Bệnh nhân" giaTri={tk.soBenhNhan} /></Grid>
            <Grid size={{ xs: 12, sm: 6, md: 3 }}><The nhan="Khoa · Bác sĩ" giaTri={`${tk.soKhoa} · ${tk.soBacSi}`} /></Grid>
          </Grid>
          {tk.khoaNhieuLuotNhat && (
            <Typography sx={{ mb: 2 }}>
              Khoa nhiều lượt khám nhất: <b>{tk.khoaNhieuLuotNhat.tenKhoa}</b> ({tk.khoaNhieuLuotNhat.luotDat} lượt)
            </Typography>
          )}
          <Grid container spacing={3}>
            <Grid size={{ xs: 12, md: 6 }}>
              <Paper elevation={2} sx={{ p: 2.5 }}>
                <Typography sx={{ fontWeight: 700, mb: 1 }}>Theo khoa</Typography>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>Khoa</TableCell>
                      <TableCell align="right">Lượt đặt</TableCell>
                      <TableCell align="right">Doanh thu</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {tk.theoKhoa.length === 0 && (
                      <TableRow>
                        <TableCell colSpan={3} sx={{ textAlign: 'center', color: 'text.secondary' }}>Chưa có dữ liệu.</TableCell>
                      </TableRow>
                    )}
                    {tk.theoKhoa.map((k) => (
                      <TableRow key={k.khoaId}>
                        <TableCell>{k.tenKhoa}</TableCell>
                        <TableCell align="right">{k.luotDat}</TableCell>
                        <TableCell align="right">{dinhDangTien(k.doanhThu)}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </Paper>
            </Grid>
            <Grid size={{ xs: 12, md: 6 }}>
              <Paper elevation={2} sx={{ p: 2.5 }}>
                <Typography sx={{ fontWeight: 700, mb: 1 }}>
                  Lượt đặt theo ngày ({hienNgay(tk.tu)} – {hienNgay(tk.den)})
                </Typography>
                {tk.theoNgay.length === 0 && <Typography color="text.secondary">Chưa có lượt đặt.</Typography>}
                {tk.theoNgay.map((n) => (
                  <Box key={n.ngay} sx={{ display: 'flex', alignItems: 'center', gap: 1.5, mb: 1 }}>
                    <Typography variant="body2" sx={{ width: 90 }}>{hienNgay(n.ngay)}</Typography>
                    <LinearProgress variant="determinate" value={(n.luotDat / maxNgay) * 100} sx={{ flex: 1, height: 10, borderRadius: 5 }} />
                    <Typography variant="body2" sx={{ width: 32, textAlign: 'right' }}>{n.luotDat}</Typography>
                  </Box>
                ))}
              </Paper>
            </Grid>
          </Grid>
        </>
      )}
    </>
  )
}
