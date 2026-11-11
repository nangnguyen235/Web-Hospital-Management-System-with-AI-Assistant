import DeleteOutlineRoundedIcon from '@mui/icons-material/DeleteOutlineRounded'
import Alert from '@mui/material/Alert'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Checkbox from '@mui/material/Checkbox'
import FormControlLabel from '@mui/material/FormControlLabel'
import IconButton from '@mui/material/IconButton'
import Paper from '@mui/material/Paper'
import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableContainer from '@mui/material/TableContainer'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import Typography from '@mui/material/Typography'
import { useSnackbar } from 'notistack'
import { useState } from 'react'
import { Navigate, useNavigate } from 'react-router-dom'
import { layThongBaoLoi } from '../../services/api'
import { dinhDangTien } from '../../services/danhMuc'
import { hienNgay, lichKhamApi, TEN_BUOI } from '../../services/lichKham'
import { GRADIENT } from '../../theme'
import BuocDatKham from './BuocDatKham'
import { docDon, ghiDon, PHI_DAT_KHAM, TOI_DA_KHUNG, xoaDon } from './datKham'

/** Bước 4 đặt khám (FR-BN-08, BR-09, BR-10): xác nhận, cảnh báo không hủy/không hoàn tiền, chuyển VNPAY. */
export default function XacNhanPage() {
  const navigate = useNavigate()
  const { enqueueSnackbar } = useSnackbar()
  const [don, setDon] = useState(docDon)
  const [dongY, setDongY] = useState(false)
  const [dangGui, setDangGui] = useState(false)

  if (!don.hoSoId) return <Navigate to="/dat-kham/chon-ho-so" replace />

  const tong = don.chon.reduce((s, c) => s + c.gia, 0)
  const bo = (khungId: number) => setDon(ghiDon({ chon: don.chon.filter((c) => c.khungId !== khungId) }))

  const thanhToan = async () => {
    if (!don.hoSoId || don.chon.length === 0) return
    setDangGui(true)
    try {
      const kq = await lichKhamApi.datKham(don.hoSoId, don.chon.map((c) => c.khungId))
      xoaDon()
      window.location.assign(kq.urlThanhToan)
    } catch (e) {
      enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' })
      setDangGui(false)
    }
  }

  return (
    <BuocDatKham buoc={3} tieuDe="Xác nhận thông tin đặt khám">
      <Typography sx={{ mb: 2 }}>
        Bệnh nhân: <b>{don.hoTen}</b>
      </Typography>
      <TableContainer component={Paper} elevation={2} sx={{ mb: 2 }}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Chuyên khoa</TableCell>
              <TableCell>Bác sĩ</TableCell>
              <TableCell>Ngày, giờ</TableCell>
              <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>Phòng</TableCell>
              <TableCell align="right">Giá</TableCell>
              <TableCell />
            </TableRow>
          </TableHead>
          <TableBody>
            {don.chon.length === 0 && (
              <TableRow>
                <TableCell colSpan={6} sx={{ textAlign: 'center', color: 'text.secondary', py: 4 }}>
                  Chưa chọn khung giờ nào.
                </TableCell>
              </TableRow>
            )}
            {don.chon.map((c) => (
              <TableRow key={c.khungId}>
                <TableCell>{c.tenKhoa}</TableCell>
                <TableCell>{c.tenBacSi}</TableCell>
                <TableCell>
                  {hienNgay(c.ngay)} · {TEN_BUOI[c.buoi]} {c.gio}
                </TableCell>
                <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>{c.tenPhong}</TableCell>
                <TableCell align="right">{dinhDangTien(c.gia)}</TableCell>
                <TableCell>
                  <IconButton onClick={() => bo(c.khungId)} aria-label="Bỏ khung giờ">
                    <DeleteOutlineRoundedIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      {don.chon.length < TOI_DA_KHUNG && (
        <Button
          variant="outlined"
          sx={{ mb: 3 }}
          onClick={() => {
            ghiDon({ bacSi: null })
            navigate('/dat-kham/chon-bac-si')
          }}
        >
          + Thêm chuyên khoa khác (tối đa {TOI_DA_KHUNG})
        </Button>
      )}
      <Paper elevation={2} sx={{ p: 2.5, mb: 3 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
          <Typography>Tiền khám</Typography>
          <Typography>{dinhDangTien(tong)}</Typography>
        </Box>
        <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
          <Typography>Phí tiện ích</Typography>
          <Typography>{dinhDangTien(don.chon.length ? PHI_DAT_KHAM : 0)}</Typography>
        </Box>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 1 }}>
          <Typography sx={{ fontWeight: 700 }}>Tổng thanh toán</Typography>
          <Typography sx={{ fontWeight: 700, color: 'primary.main' }}>{dinhDangTien(don.chon.length ? tong + PHI_DAT_KHAM : 0)}</Typography>
        </Box>
      </Paper>
      <Alert severity="warning" sx={{ mb: 1 }}>
        Phiếu khám đã thanh toán <b>không thể hủy và không được hoàn tiền</b>. Chỗ được giữ 15 phút để bạn thanh toán qua VNPAY.
      </Alert>
      <FormControlLabel control={<Checkbox checked={dongY} onChange={(e) => setDongY(e.target.checked)} />} label="Tôi đã đọc và đồng ý" />
      <Box sx={{ textAlign: 'right' }}>
        <Button
          size="large"
          variant="contained"
          disabled={!dongY || don.chon.length === 0 || dangGui}
          onClick={thanhToan}
          sx={{ background: GRADIENT.nut, fontWeight: 700, px: 5 }}
        >
          {dangGui ? 'Đang tạo phiếu...' : 'Thanh toán VNPAY'}
        </Button>
      </Box>
    </BuocDatKham>
  )
}
