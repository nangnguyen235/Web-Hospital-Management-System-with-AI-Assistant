import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Container from '@mui/material/Container'
import MenuItem from '@mui/material/MenuItem'
import Paper from '@mui/material/Paper'
import Stack from '@mui/material/Stack'
import Tab from '@mui/material/Tab'
import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import Tabs from '@mui/material/Tabs'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { useSnackbar } from 'notistack'
import { useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import DialogOtp from '../../components/General/DialogOtp'
import { layThongBaoLoi } from '../../services/api'
import { hienNgay } from '../../services/lichKham'
import { nguoiDungApi, TEN_GIOI_TINH, type GioiTinh, type HoSoTimLai } from '../../services/nguoiDung'
import { MAU } from '../../theme'

/** FR-BN-06, BR-03: tìm theo mã bệnh nhân hoặc họ tên + năm sinh + giới tính → OTP email của hồ sơ → gắn vào tài khoản. */
export default function TimLaiHoSoPage() {
  const navigate = useNavigate()
  const { enqueueSnackbar } = useSnackbar()
  const [cach, setCach] = useState<'ma' | 'ten'>('ma')
  const [ma, setMa] = useState('')
  const [hoTen, setHoTen] = useState('')
  const [namSinh, setNamSinh] = useState('')
  const [gioiTinh, setGioiTinh] = useState<GioiTinh>('NAM')
  const [kq, setKq] = useState<HoSoTimLai[] | null>(null)
  const [otp, setOtp] = useState<HoSoTimLai | null>(null)
  const [dangGui, setDangGui] = useState(false)

  const tim = async (e: FormEvent) => {
    e.preventDefault()
    const q = cach === 'ma' ? { maBenhNhan: ma.trim() } : { hoTen: hoTen.trim(), namSinh: Number(namSinh), gioiTinh }
    if (cach === 'ma' ? !ma.trim() : !hoTen.trim() || !namSinh) return enqueueSnackbar('Vui lòng nhập đủ thông tin', { variant: 'warning' })
    try {
      setKq(await nguoiDungApi.timLaiHoSo(q))
    } catch (err) {
      enqueueSnackbar(layThongBaoLoi(err), { variant: 'error' })
    }
  }

  const guiOtp = async (h: HoSoTimLai) => {
    try {
      await nguoiDungApi.guiOtpTimLai(h.id)
      setOtp(h)
      enqueueSnackbar(`Đã gửi mã OTP tới ${h.emailChe}`, { variant: 'info' })
    } catch (err) {
      enqueueSnackbar(layThongBaoLoi(err), { variant: 'error' })
    }
  }

  const xacNhan = async (ma6: string) => {
    if (!otp) return
    setDangGui(true)
    try {
      await nguoiDungApi.xacNhanTimLai(otp.id, ma6)
      enqueueSnackbar('Đã thêm hồ sơ vào tài khoản', { variant: 'success' })
      navigate('/tai-khoan/ho-so')
    } catch (err) {
      enqueueSnackbar(layThongBaoLoi(err), { variant: 'error' })
    } finally {
      setDangGui(false)
    }
  }

  return (
    <Box sx={{ bgcolor: MAU.nenDatKham, py: 5, minHeight: '70vh' }}>
      <Container maxWidth="md">
        <Typography variant="h5" sx={{ fontWeight: 700, color: MAU.chinh, mb: 3, textAlign: 'center' }}>
          Tìm lại hồ sơ đã mất
        </Typography>
        <Paper elevation={2} sx={{ p: 3, mb: 3 }}>
          <Tabs value={cach} onChange={(_, v) => setCach(v)} sx={{ mb: 2 }}>
            <Tab value="ma" label="Theo mã bệnh nhân" />
            <Tab value="ten" label="Theo họ tên, năm sinh" />
          </Tabs>
          <Box component="form" onSubmit={tim}>
            {cach === 'ma' ? (
              <TextField fullWidth label="Mã bệnh nhân" value={ma} onChange={(e) => setMa(e.target.value)} />
            ) : (
              <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                <TextField fullWidth label="Họ và tên" value={hoTen} onChange={(e) => setHoTen(e.target.value)} />
                <TextField label="Năm sinh" type="number" value={namSinh} onChange={(e) => setNamSinh(e.target.value)} sx={{ minWidth: 120 }} />
                <TextField select label="Giới tính" value={gioiTinh} onChange={(e) => setGioiTinh(e.target.value as GioiTinh)} sx={{ minWidth: 120 }}>
                  {Object.entries(TEN_GIOI_TINH).map(([k, v]) => (
                    <MenuItem key={k} value={k}>{v}</MenuItem>
                  ))}
                </TextField>
              </Stack>
            )}
            <Button type="submit" variant="contained" sx={{ mt: 2 }}>
              Tìm hồ sơ
            </Button>
          </Box>
        </Paper>
        {kq && (
          <Paper elevation={2}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Họ tên</TableCell>
                  <TableCell>Ngày sinh</TableCell>
                  <TableCell>Email nhận OTP</TableCell>
                  <TableCell />
                </TableRow>
              </TableHead>
              <TableBody>
                {kq.length === 0 && (
                  <TableRow>
                    <TableCell colSpan={4} sx={{ textAlign: 'center', color: 'text.secondary' }}>
                      Không tìm thấy hồ sơ phù hợp.
                    </TableCell>
                  </TableRow>
                )}
                {kq.map((h) => (
                  <TableRow key={h.id}>
                    <TableCell>{h.hoTen}</TableCell>
                    <TableCell>{hienNgay(h.ngaySinh)}</TableCell>
                    <TableCell>{h.coEmail ? h.emailChe : 'Hồ sơ chưa có email — nhờ điều dưỡng bổ sung khi đến viện'}</TableCell>
                    <TableCell align="right">
                      <Button size="small" variant="outlined" disabled={!h.coEmail} onClick={() => guiOtp(h)}>
                        Lấy lại
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </Paper>
        )}
      </Container>
      <DialogOtp
        mo={Boolean(otp)}
        email={otp?.emailChe}
        dangGui={dangGui}
        onDong={() => setOtp(null)}
        onXacNhan={xacNhan}
        onGuiLai={() => otp && guiOtp(otp)}
      />
    </Box>
  )
}
