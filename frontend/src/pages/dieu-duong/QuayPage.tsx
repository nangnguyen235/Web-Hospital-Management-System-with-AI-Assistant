import AddRoundedIcon from '@mui/icons-material/AddRounded'
import Alert from '@mui/material/Alert'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import MenuItem from '@mui/material/MenuItem'
import Paper from '@mui/material/Paper'
import Stack from '@mui/material/Stack'
import Tab from '@mui/material/Tab'
import Tabs from '@mui/material/Tabs'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { useSnackbar } from 'notistack'
import { useEffect, useState, type FormEvent } from 'react'
import BangHoSo from '../../components/General/BangHoSo'
import BangXetNghiem from '../../components/General/BangXetNghiem'
import ChonPhong from '../../components/General/ChonPhong'
import DialogHoSo from '../../components/General/DialogHoSo'
import PageHeader from '../../components/General/PageHeader'
import QrThanhToan from '../../components/General/QrThanhToan'
import { layThongBaoLoi } from '../../services/api'
import { dinhDangTien, type Phong } from '../../services/danhMuc'
import { hoSoBenhAnApi, type XetNghiem } from '../../services/hoSoBenhAn'
import { lichKhamApi, TEN_BUOI, type KetQuaQuay, type Lich } from '../../services/lichKham'
import { nguoiDungApi, type HoSo, type LuuHoSo } from '../../services/nguoiDung'

interface Qr {
  url: string | null
  giaoDichId: number | null
  soTien: number | null
}

/**
 * FR-DD-08, BR-14: khách vãng lai tại quầy — tìm/tạo hồ sơ không tài khoản, chọn lịch hôm nay,
 * hệ thống lấy khung còn chỗ sớm nhất (hết chỗ thì dồn khung cuối buổi), khách quét QR VNPAY; thanh toán xét nghiệm hộ khách.
 */
export default function QuayPage() {
  const { enqueueSnackbar } = useSnackbar()
  const [tab, setTab] = useState(0)
  const [traTen, setTraTen] = useState('')
  const [traCccd, setTraCccd] = useState('')
  const [hoSoTra, setHoSoTra] = useState<HoSo[] | null>(null)
  const [hoSo, setHoSo] = useState<HoSo | null>(null)
  const [moTao, setMoTao] = useState(false)
  const [phong, setPhong] = useState<Phong | null>(null)
  const [lich, setLich] = useState<Lich[]>([])
  const [lichId, setLichId] = useState<number | ''>('')
  const [phieu, setPhieu] = useState<KetQuaQuay | null>(null)
  const [qr, setQr] = useState<Qr | null>(null)
  const [maXn, setMaXn] = useState('')
  const [xn, setXn] = useState<XetNghiem | null>(null)

  const loi = (e: unknown) => enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' })

  useEffect(() => {
    if (!phong) return
    lichKhamApi
      .lichHomNayCuaPhong(phong.id)
      .then((d) => {
        setLich(d)
        setLichId(d[0]?.id ?? '')
      })
      .catch(() => setLich([]))
  }, [phong])

  const tra = async (e: FormEvent) => {
    e.preventDefault()
    if (!traTen.trim() && !traCccd.trim()) return
    try {
      setHoSoTra(await nguoiDungApi.traHoSo(traTen.trim(), traCccd.trim()))
    } catch (err) {
      loi(err)
    }
  }

  const taoHoSo = async (g: LuuHoSo) => {
    try {
      const h = await nguoiDungApi.taoHoSoQuay(g)
      setHoSo(h)
      setMoTao(false)
      enqueueSnackbar('Đã tạo hồ sơ', { variant: 'success' })
    } catch (err) {
      loi(err)
    }
  }

  const taoPhieu = async () => {
    if (!hoSo || lichId === '') return
    try {
      const kq = await lichKhamApi.taoPhieuQuay(hoSo.id, lichId)
      setPhieu(kq)
      if (!kq.daThanhToan && kq.urlThanhToan) {
        // số tiền (gồm phí cổng) hiện trên trang VNPAY khi khách quét
        setQr({ url: kq.urlThanhToan, giaoDichId: kq.phieu.giaoDichId, soTien: null })
      }
    } catch (err) {
      loi(err)
    }
  }

  const traXn = async (e: FormEvent) => {
    e.preventDefault()
    if (!maXn.trim()) return
    try {
      setXn(await hoSoBenhAnApi.traMa(maXn.trim()))
    } catch (err) {
      setXn(null)
      loi(err)
    }
  }

  const thanhToanXn = async () => {
    if (!xn) return
    try {
      const kq = await hoSoBenhAnApi.thanhToan(xn.maXetNghiem)
      setQr({ url: kq.urlThanhToan, giaoDichId: kq.giaoDichId, soTien: kq.tongTien })
    } catch (err) {
      loi(err)
    }
  }

  return (
    <>
      <PageHeader tieuDe="Quầy dịch vụ" />
      <Tabs value={tab} onChange={(_, v) => setTab(v)} sx={{ mb: 2 }}>
        <Tab label="Đăng ký khám tại quầy" />
        <Tab label="Thanh toán xét nghiệm" />
      </Tabs>

      {tab === 0 && (
        <Stack spacing={3}>
          <Paper elevation={2} sx={{ p: 2.5 }}>
            <Typography sx={{ fontWeight: 700, mb: 2 }}>1. Hồ sơ khách</Typography>
            {hoSo ? (
              <Alert
                severity="info"
                action={
                  <Button color="inherit" size="small" onClick={() => setHoSo(null)}>
                    Đổi
                  </Button>
                }
              >
                {hoSo.hoTen} · CCCD {hoSo.soCccd ?? '—'} · Mã BN {hoSo.maBenhNhan ?? 'chưa có'}
              </Alert>
            ) : (
              <>
                <Box component="form" onSubmit={tra}>
                  <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} sx={{ mb: 2 }}>
                    <TextField fullWidth label="Họ tên" value={traTen} onChange={(e) => setTraTen(e.target.value)} />
                    <TextField fullWidth label="Số CCCD" value={traCccd} onChange={(e) => setTraCccd(e.target.value)} />
                    <Button type="submit" variant="outlined">Tìm</Button>
                    <Button startIcon={<AddRoundedIcon />} variant="contained" onClick={() => setMoTao(true)} sx={{ whiteSpace: 'nowrap' }}>
                      Hồ sơ mới
                    </Button>
                  </Stack>
                </Box>
                {hoSoTra && (
                  <BangHoSo
                    hoSo={hoSoTra}
                    thaoTac={(h) => (
                      <Button size="small" variant="contained" onClick={() => setHoSo(h)}>
                        Chọn
                      </Button>
                    )}
                  />
                )}
              </>
            )}
          </Paper>
          <Paper elevation={2} sx={{ p: 2.5 }}>
            <Typography sx={{ fontWeight: 700, mb: 2 }}>2. Lịch khám hôm nay</Typography>
            <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
              <ChonPhong
                loai="PHONG_KHAM"
                onChon={(p) => {
                  setPhong(p)
                  setLich([])
                  setLichId('')
                }}
              />
              <TextField select label="Lịch" value={lichId} onChange={(e) => setLichId(Number(e.target.value))} sx={{ minWidth: 320 }} disabled={lich.length === 0}>
                {lich.map((l) => (
                  <MenuItem key={l.id} value={l.id}>
                    Buổi {TEN_BUOI[l.buoi].toLowerCase()} · {l.tenBacSi} · {l.tenDichVu} · {dinhDangTien(l.gia)}
                  </MenuItem>
                ))}
              </TextField>
              <Button variant="contained" disabled={!hoSo || lichId === ''} onClick={taoPhieu}>
                Tạo phiếu, thanh toán
              </Button>
            </Stack>
            {phieu && (
              <Alert severity={phieu.daThanhToan ? 'success' : 'info'} sx={{ mt: 2 }}>
                Mã phiếu <b>{phieu.phieu.maPhieu}</b> · {phieu.phieu.gioBatDau} - {phieu.phieu.gioKetThuc}
                {phieu.daThanhToan ? ` · Số thứ tự ${phieu.phieu.soThuTu}` : ' · chờ khách thanh toán'}
              </Alert>
            )}
          </Paper>
        </Stack>
      )}

      {tab === 1 && (
        <Paper elevation={2} sx={{ p: 2.5 }}>
          <Box component="form" onSubmit={traXn} sx={{ display: 'flex', gap: 2, mb: 2 }}>
            <TextField fullWidth label="Mã xét nghiệm" value={maXn} onChange={(e) => setMaXn(e.target.value)} />
            <Button type="submit" variant="contained">Tra cứu</Button>
          </Box>
          {xn && (
            <>
              <Typography sx={{ mb: 1 }}>{xn.hoTen}</Typography>
              <BangXetNghiem ds={[xn]} />
              {!xn.daThanhToan && (
                <Button variant="contained" sx={{ mt: 2 }} onClick={thanhToanXn}>
                  Hiện QR thanh toán
                </Button>
              )}
            </>
          )}
        </Paper>
      )}

      <DialogHoSo mo={moTao} hoSo={null} onDong={() => setMoTao(false)} onLuu={taoHoSo} />
      <QrThanhToan
        mo={Boolean(qr)}
        url={qr?.url ?? null}
        giaoDichId={qr?.giaoDichId ?? null}
        soTien={qr?.soTien}
        onDong={() => setQr(null)}
        onXong={async (ok) => {
          if (!ok) return
          enqueueSnackbar('Khách đã thanh toán', { variant: 'success' })
          if (phieu) {
            const p = await lichKhamApi.phieu(phieu.phieu.id).catch(() => null)
            if (p) setPhieu({ ...phieu, phieu: p, daThanhToan: true })
          }
          if (xn) setXn({ ...xn, daThanhToan: true })
        }}
      />
    </>
  )
}
