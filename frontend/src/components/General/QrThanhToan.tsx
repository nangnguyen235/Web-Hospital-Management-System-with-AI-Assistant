import Alert from '@mui/material/Alert'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import CircularProgress from '@mui/material/CircularProgress'
import Dialog from '@mui/material/Dialog'
import DialogActions from '@mui/material/DialogActions'
import DialogContent from '@mui/material/DialogContent'
import DialogTitle from '@mui/material/DialogTitle'
import Link from '@mui/material/Link'
import Typography from '@mui/material/Typography'
import { QRCodeSVG } from 'qrcode.react'
import { useEffect, useState } from 'react'
import { dinhDangTien } from '../../services/danhMuc'
import { vienPhiApi, type TrangThaiGiaoDich } from '../../services/vienPhi'

interface Props {
  mo: boolean
  url: string | null
  giaoDichId: number | null
  soTien?: number | null
  onDong: () => void
  onXong?: (thanhCong: boolean) => void
}

/** QR VNPAY cho khách quét tại quầy (BR-14); hỏi trạng thái giao dịch mỗi 5 giây (NFR-01). */
export default function QrThanhToan(props: Props) {
  return <NoiDung key={`${props.mo}-${props.giaoDichId}`} {...props} />
}

function NoiDung({ mo, url, giaoDichId, soTien, onDong, onXong }: Props) {
  const [trangThai, setTrangThai] = useState<TrangThaiGiaoDich>('DANG_XU_LY')

  useEffect(() => {
    if (!mo || !giaoDichId) return
    let xong = false
    const hoi = () =>
      vienPhiApi
        .giaoDich(giaoDichId)
        .then((g) => {
          if (xong || g.trangThai === 'DANG_XU_LY') return
          xong = true
          setTrangThai(g.trangThai)
          onXong?.(g.trangThai === 'THANH_CONG')
        })
        .catch(() => undefined)
    const id = window.setInterval(hoi, 5000)
    return () => {
      xong = true
      window.clearInterval(id)
    }
    // onXong đổi theo mỗi lần render của trang cha
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [mo, giaoDichId])

  return (
    <Dialog open={mo} onClose={onDong} maxWidth="xs" fullWidth>
      <DialogTitle sx={{ fontWeight: 700 }}>Thanh toán VNPAY</DialogTitle>
      <DialogContent sx={{ textAlign: 'center' }}>
        {trangThai === 'THANH_CONG' && <Alert severity="success">Đã thanh toán thành công.</Alert>}
        {trangThai === 'THAT_BAI' && <Alert severity="error">Thanh toán thất bại hoặc đã hết hạn.</Alert>}
        {trangThai === 'DANG_XU_LY' && url && (
          <>
            <Typography sx={{ mb: 2 }}>Khách quét mã bằng ứng dụng ngân hàng để thanh toán</Typography>
            <Box sx={{ display: 'inline-block', p: 2, bgcolor: '#fff', border: '1px solid', borderColor: 'divider', borderRadius: 2 }}>
              <QRCodeSVG value={url} size={220} />
            </Box>
            {soTien != null && (
              <Typography variant="h6" sx={{ mt: 2, fontWeight: 700 }}>
                {dinhDangTien(soTien)}
              </Typography>
            )}
            <Box sx={{ mt: 1 }}>
              <Link href={url} target="_blank" rel="noopener noreferrer">
                Mở trang thanh toán
              </Link>
            </Box>
            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 1, mt: 2, color: 'text.secondary' }}>
              <CircularProgress size={16} /> Đang chờ thanh toán (hết hạn sau 15 phút)
            </Box>
          </>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={onDong}>Đóng</Button>
      </DialogActions>
    </Dialog>
  )
}
