import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Chip from '@mui/material/Chip'
import Dialog from '@mui/material/Dialog'
import DialogActions from '@mui/material/DialogActions'
import DialogContent from '@mui/material/DialogContent'
import DialogTitle from '@mui/material/DialogTitle'
import Paper from '@mui/material/Paper'
import Rating from '@mui/material/Rating'
import Stack from '@mui/material/Stack'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { useSnackbar } from 'notistack'
import { useCallback, useEffect, useState } from 'react'
import BangDonThuoc from '../../components/General/BangDonThuoc'
import BangXetNghiem from '../../components/General/BangXetNghiem'
import ChonHoSo from '../../components/General/ChonHoSo'
import Loading from '../../components/General/Loading'
import { layThongBaoLoi } from '../../services/api'
import { hoSoBenhAnApi, TEN_TRANG_THAI_BENH_AN, type BenhAn, type LichSu } from '../../services/hoSoBenhAn'
import { hienNgay } from '../../services/lichKham'
import type { HoSo } from '../../services/nguoiDung'

/** FR-BN-11 (lịch sử, bệnh án), FR-BN-14 + BR-20 (đánh giá 1–5 điểm, mỗi phiếu một lần, không sửa). */
export default function LichSuKhamPage() {
  const { enqueueSnackbar } = useSnackbar()
  const [hoSo, setHoSo] = useState<HoSo | null>(null)
  const [ds, setDs] = useState<LichSu[] | null>(null)
  const [ba, setBa] = useState<BenhAn | null>(null)
  const [diem, setDiem] = useState<number | null>(5)
  const [nhanXet, setNhanXet] = useState('')

  const tai = useCallback(() => {
    if (!hoSo) return
    hoSoBenhAnApi.lichSu(hoSo.id).then(setDs).catch((e) => enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' }))
  }, [hoSo, enqueueSnackbar])
  useEffect(tai, [tai])

  const mo = (id: number) =>
    hoSoBenhAnApi
      .benhAnCuaToi(id)
      .then((b) => {
        setBa(b)
        setDiem(5)
        setNhanXet('')
      })
      .catch((e) => enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' }))

  const danhGia = async () => {
    if (!ba || !diem) return
    try {
      await hoSoBenhAnApi.danhGia(ba.id, diem, nhanXet)
      enqueueSnackbar('Cảm ơn bạn đã đánh giá', { variant: 'success' })
      await mo(ba.id)
      tai()
    } catch (e) {
      enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' })
    }
  }

  const thanhToanXn = async (ma: string) => {
    try {
      const kq = await hoSoBenhAnApi.thanhToan(ma)
      window.location.assign(kq.urlThanhToan)
    } catch (e) {
      enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' })
    }
  }

  return (
    <>
      <Typography variant="h5" sx={{ fontWeight: 700, mb: 2 }}>
        Lịch sử khám bệnh
      </Typography>
      <ChonHoSo onChon={setHoSo} />
      {!hoSo ? null : !ds ? (
        <Loading />
      ) : ds.length === 0 ? (
        <Typography color="text.secondary" sx={{ py: 4 }}>Chưa có lần khám nào.</Typography>
      ) : (
        <Stack spacing={2}>
          {ds.map((l) => (
            <Paper key={l.benhAnId} elevation={2} sx={{ p: 2.5, display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 2, flexWrap: 'wrap' }}>
              <Box>
                <Typography sx={{ fontWeight: 700 }}>
                  {hienNgay(l.ngayKham)} · {l.tenKhoa ?? ''} · {l.tenBacSi ?? ''}
                </Typography>
                <Typography color="text.secondary">Chẩn đoán: {l.chanDoan ?? 'chưa có'}</Typography>
                {l.ngayTaiKham && <Typography color="text.secondary">Hẹn tái khám: {hienNgay(l.ngayTaiKham)}</Typography>}
              </Box>
              <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
                <Chip size="small" label={TEN_TRANG_THAI_BENH_AN[l.trangThai]} color={l.trangThai === 'DA_KHAM' ? 'success' : 'default'} />
                {l.daDanhGia && <Chip size="small" label="Đã đánh giá" variant="outlined" />}
                <Button variant="outlined" onClick={() => mo(l.benhAnId)}>
                  Xem bệnh án
                </Button>
              </Box>
            </Paper>
          ))}
        </Stack>
      )}
      <Dialog open={Boolean(ba)} onClose={() => setBa(null)} maxWidth="md" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>
          Bệnh án {ba && hienNgay(ba.ngayKham)} · {ba?.hoTen}
        </DialogTitle>
        <DialogContent dividers>
          {ba && (
            <Stack spacing={2}>
              <Typography>
                {ba.tenKhoa} · {ba.tenBacSi} · Phòng {ba.tenPhong} · Mã bệnh nhân {ba.maBenhNhan}
              </Typography>
              <Box>
                <Typography sx={{ fontWeight: 700 }}>Triệu chứng</Typography>
                <Typography sx={{ whiteSpace: 'pre-line' }}>{ba.trieuChung || '—'}</Typography>
              </Box>
              <Box>
                <Typography sx={{ fontWeight: 700 }}>Chẩn đoán</Typography>
                <Typography sx={{ whiteSpace: 'pre-line' }}>{ba.chanDoan || '—'}</Typography>
              </Box>
              <Typography>Ngày tái khám: {hienNgay(ba.ngayTaiKham)}</Typography>
              <Box>
                <Typography sx={{ fontWeight: 700, mb: 1 }}>Xét nghiệm</Typography>
                <BangXetNghiem
                  ds={ba.xetNghiem}
                  thaoTac={(x) =>
                    !x.daThanhToan && (
                      <Button size="small" variant="contained" onClick={() => thanhToanXn(x.maXetNghiem)}>
                        Thanh toán
                      </Button>
                    )
                  }
                />
              </Box>
              <Box>
                <Typography sx={{ fontWeight: 700, mb: 1 }}>Đơn thuốc</Typography>
                <BangDonThuoc don={ba.donThuoc} />
              </Box>
              {ba.trangThai === 'DA_KHAM' && !ba.daDanhGia && (
                <Paper variant="outlined" sx={{ p: 2 }}>
                  <Typography sx={{ fontWeight: 700 }}>Đánh giá bác sĩ</Typography>
                  <Rating value={diem} onChange={(_, v) => setDiem(v)} size="large" />
                  <TextField fullWidth multiline minRows={2} label="Nhận xét" value={nhanXet} onChange={(e) => setNhanXet(e.target.value)} sx={{ my: 1 }} />
                  <Button variant="contained" disabled={!diem} onClick={danhGia}>
                    Gửi đánh giá
                  </Button>
                  <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mt: 1 }}>
                    Mỗi lần khám chỉ đánh giá một lần, không sửa sau khi gửi.
                  </Typography>
                </Paper>
              )}
            </Stack>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setBa(null)}>Đóng</Button>
        </DialogActions>
      </Dialog>
    </>
  )
}
