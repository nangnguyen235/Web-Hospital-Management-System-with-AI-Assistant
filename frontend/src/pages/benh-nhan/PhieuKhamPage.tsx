import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Card from '@mui/material/Card'
import CardContent from '@mui/material/CardContent'
import Chip from '@mui/material/Chip'
import Stack from '@mui/material/Stack'
import Typography from '@mui/material/Typography'
import { useSnackbar } from 'notistack'
import { useEffect, useState } from 'react'
import ChonHoSo from '../../components/General/ChonHoSo'
import Loading from '../../components/General/Loading'
import { layThongBaoLoi } from '../../services/api'
import { dinhDangTien } from '../../services/danhMuc'
import {
  hienNgay,
  lichKhamApi,
  MAU_TRANG_THAI_PHIEU,
  ngayISO,
  TEN_BUOI,
  TEN_TRANG_THAI_PHIEU,
  type Phieu,
} from '../../services/lichKham'
import type { HoSo } from '../../services/nguoiDung'

/** FR-BN-09, FR-BN-10, BR-15: phiếu theo hồ sơ; phiếu hôm nay hiện số đang gọi (cập nhật 5 giây) và còn bao nhiêu số. */
export default function PhieuKhamPage() {
  const { enqueueSnackbar } = useSnackbar()
  const [hoSo, setHoSo] = useState<HoSo | null>(null)
  const [ds, setDs] = useState<Phieu[] | null>(null)
  const homNay = ngayISO(new Date())

  useEffect(() => {
    if (!hoSo) return
    let huy = false
    const tai = (lanDau: boolean) =>
      lichKhamApi
        .phieuCuaHoSo(hoSo.id)
        .then((d) => !huy && setDs(d))
        .catch((e) => lanDau && !huy && enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' }))
    tai(true)
    // cập nhật số đang gọi mỗi 5 giây (NFR-01)
    const id = window.setInterval(() => tai(false), 5000)
    return () => {
      huy = true
      window.clearInterval(id)
    }
  }, [hoSo, enqueueSnackbar])

  return (
    <>
      <Typography variant="h5" sx={{ fontWeight: 700, mb: 2 }}>
        Phiếu khám bệnh
      </Typography>
      <ChonHoSo onChon={setHoSo} />
      {!hoSo ? null : !ds ? (
        <Loading />
      ) : ds.length === 0 ? (
        <Typography color="text.secondary" sx={{ py: 4 }}>Chưa có phiếu khám.</Typography>
      ) : (
        <Stack spacing={2}>
          {ds.map((p) => {
            const homNayKham = p.ngay === homNay && p.trangThai !== 'HET_HAN' && p.trangThai !== 'CHO_THANH_TOAN'
            const conLai = p.soThuTu - p.soDangGoi
            return (
              <Card key={p.id} elevation={2}>
                <CardContent>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', flexWrap: 'wrap', gap: 1 }}>
                    <Typography sx={{ fontWeight: 700 }}>
                      Mã phiếu: {p.maPhieu} {p.soThuTu > 0 && `· Số thứ tự ${p.soThuTu}`}
                    </Typography>
                    <Chip size="small" color={MAU_TRANG_THAI_PHIEU[p.trangThai]} label={TEN_TRANG_THAI_PHIEU[p.trangThai]} />
                  </Box>
                  <Typography>
                    {p.tenKhoa} · {p.tenBacSi} · Phòng {p.tenPhong}
                  </Typography>
                  <Typography color="text.secondary">
                    {hienNgay(p.ngay)} · Buổi {TEN_BUOI[p.buoi].toLowerCase()} {p.gioBatDau} - {p.gioKetThuc} · {p.tenDichVu} · {dinhDangTien(p.giaDichVu)}
                  </Typography>
                  {homNayKham && (
                    <Box sx={{ mt: 1.5, p: 1.5, bgcolor: '#e3f2fd', border: '1px solid', borderColor: 'info.light', borderRadius: 2 }}>
                      <Typography>
                        Số đang gọi: <b>{p.soDangGoi > 0 ? p.soDangGoi : 'chưa bắt đầu'}</b>
                        {p.soDangGoi > 0 && (conLai > 0 ? ` · còn ${conLai} số nữa tới lượt bạn` : ' · đã tới lượt bạn')}
                      </Typography>
                      <Button size="small" href={`/man-hinh-so/${p.lichKhamId}`} target="_blank">
                        Mở màn hình số
                      </Button>
                    </Box>
                  )}
                </CardContent>
              </Card>
            )
          })}
        </Stack>
      )}
    </>
  )
}
