import CancelRoundedIcon from '@mui/icons-material/CancelRounded'
import CheckCircleRoundedIcon from '@mui/icons-material/CheckCircleRounded'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Container from '@mui/material/Container'
import Paper from '@mui/material/Paper'
import Typography from '@mui/material/Typography'
import { useEffect, useRef, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../../auth/useAuth'
import Loading from '../../components/General/Loading'
import { layThongBaoLoi } from '../../services/api'
import { dinhDangTien } from '../../services/danhMuc'
import { hienNgay, lichKhamApi, TEN_BUOI, type Phieu } from '../../services/lichKham'
import { vienPhiApi, type KetQuaThanhToan } from '../../services/vienPhi'
import { MAU } from '../../theme'

/** FR-HT-02: VNPAY trả về (return URL) → backend kiểm chữ ký, dùng chung hàm idempotent với IPN. */
export default function KetQuaThanhToanPage() {
  const { search } = useLocation()
  const navigate = useNavigate()
  const { thongTin } = useAuth()
  const [kq, setKq] = useState<KetQuaThanhToan | null>(null)
  const [loi, setLoi] = useState<string | null>(null)
  const [phieu, setPhieu] = useState<Phieu[]>([])
  const daGoi = useRef(false)

  useEffect(() => {
    if (daGoi.current) return
    daGoi.current = true
    vienPhiApi
      .ketQuaVnpay(search)
      .then((d) => {
        setKq(d)
        if (d.thanhCong && d.loaiThanhToan === 'DAT_KHAM' && d.giaoDichId && thongTin) {
          lichKhamApi.phieuCuaGiaoDich(d.giaoDichId).then(setPhieu).catch(() => undefined)
        }
      })
      .catch((e) => setLoi(layThongBaoLoi(e)))
  }, [search, thongTin])

  const thanhCong = kq?.thanhCong
  return (
    <Box sx={{ bgcolor: MAU.nenDatKham, py: 6, minHeight: '70vh' }}>
      <Container maxWidth="sm">
        <Paper elevation={2} sx={{ p: 4, textAlign: 'center' }}>
          {!kq && !loi ? (
            <Loading />
          ) : (
            <>
              {thanhCong ? (
                <CheckCircleRoundedIcon color="success" sx={{ fontSize: 80 }} />
              ) : (
                <CancelRoundedIcon color="error" sx={{ fontSize: 80 }} />
              )}
              <Typography variant="h5" sx={{ fontWeight: 700, my: 2 }}>
                {thanhCong ? 'Thanh toán thành công' : 'Thanh toán không thành công'}
              </Typography>
              <Typography color="text.secondary">{loi ?? kq?.thongBao}</Typography>
              {kq?.tongTien != null && <Typography sx={{ mt: 1, fontWeight: 600 }}>Số tiền: {dinhDangTien(kq.tongTien)}</Typography>}
              {phieu.map((p) => (
                <Paper key={p.id} variant="outlined" sx={{ p: 2, mt: 2, textAlign: 'left' }}>
                  <Typography sx={{ fontWeight: 700 }}>
                    Mã phiếu {p.maPhieu} · Số thứ tự {p.soThuTu > 0 ? p.soThuTu : 'đang cấp'}
                  </Typography>
                  <Typography>
                    {p.tenKhoa} · {p.tenBacSi} · Phòng {p.tenPhong}
                  </Typography>
                  <Typography>
                    {hienNgay(p.ngay)} · {TEN_BUOI[p.buoi]} {p.gioBatDau} - {p.gioKetThuc}
                  </Typography>
                </Paper>
              ))}
              <Box sx={{ display: 'flex', gap: 2, justifyContent: 'center', mt: 3 }}>
                {thongTin?.vaiTro === 'BENH_NHAN' && (
                  <Button variant="contained" onClick={() => navigate('/tai-khoan/phieu-kham')}>
                    Xem phiếu khám
                  </Button>
                )}
                <Button variant="outlined" onClick={() => navigate('/')}>
                  Về trang chủ
                </Button>
              </Box>
            </>
          )}
        </Paper>
      </Container>
    </Box>
  )
}
