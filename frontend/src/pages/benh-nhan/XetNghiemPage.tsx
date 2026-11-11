import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Paper from '@mui/material/Paper'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { useSnackbar } from 'notistack'
import { useState, type FormEvent } from 'react'
import BangXetNghiem from '../../components/General/BangXetNghiem'
import { layThongBaoLoi } from '../../services/api'
import { hoSoBenhAnApi, type XetNghiem } from '../../services/hoSoBenhAn'
import { hienNgay } from '../../services/lichKham'

/** FR-BN-12 (tra mã, thanh toán VNPAY phí xét nghiệm), FR-BN-13 (xem kết quả, file qua Presigned URL). */
export default function XetNghiemPage() {
  const { enqueueSnackbar } = useSnackbar()
  const [ma, setMa] = useState('')
  const [xn, setXn] = useState<XetNghiem | null>(null)
  const [dangGui, setDangGui] = useState(false)

  const tra = async (e: FormEvent) => {
    e.preventDefault()
    if (!ma.trim()) return
    try {
      setXn(await hoSoBenhAnApi.traMa(ma.trim()))
    } catch (err) {
      setXn(null)
      enqueueSnackbar(layThongBaoLoi(err), { variant: 'error' })
    }
  }

  const thanhToan = async () => {
    if (!xn) return
    setDangGui(true)
    try {
      const kq = await hoSoBenhAnApi.thanhToan(xn.maXetNghiem)
      window.location.assign(kq.urlThanhToan)
    } catch (err) {
      enqueueSnackbar(layThongBaoLoi(err), { variant: 'error' })
      setDangGui(false)
    }
  }

  return (
    <>
      <Typography variant="h5" sx={{ fontWeight: 700, mb: 2 }}>
        Kết quả xét nghiệm
      </Typography>
      <Paper elevation={2} sx={{ p: 2.5, mb: 3 }}>
        <Box component="form" onSubmit={tra} sx={{ display: 'flex', gap: 2 }}>
          <TextField fullWidth label="Mã xét nghiệm" value={ma} onChange={(e) => setMa(e.target.value)} />
          <Button type="submit" variant="contained">Tra cứu</Button>
        </Box>
        <Typography variant="caption" color="text.secondary">
          Mã xét nghiệm có trên phiếu chỉ định và trong mục Lịch sử khám bệnh → Xem bệnh án.
        </Typography>
      </Paper>
      {xn && (
        <Paper elevation={2} sx={{ p: 2.5 }}>
          <Typography sx={{ fontWeight: 700, mb: 1 }}>
            {xn.hoTen ?? ''} · Ngày {hienNgay(xn.ngay)}
          </Typography>
          <BangXetNghiem ds={[xn]} />
          {!xn.daThanhToan && (
            <Box sx={{ textAlign: 'right', mt: 2 }}>
              <Button variant="contained" disabled={dangGui} onClick={thanhToan}>
                Thanh toán VNPAY (phí 1.500 đ)
              </Button>
            </Box>
          )}
        </Paper>
      )}
    </>
  )
}
