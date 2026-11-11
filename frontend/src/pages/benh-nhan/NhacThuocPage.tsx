import AddRoundedIcon from '@mui/icons-material/AddRounded'
import DeleteOutlineRoundedIcon from '@mui/icons-material/DeleteOutlineRounded'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Chip from '@mui/material/Chip'
import IconButton from '@mui/material/IconButton'
import MenuItem from '@mui/material/MenuItem'
import Paper from '@mui/material/Paper'
import Stack from '@mui/material/Stack'
import Tab from '@mui/material/Tab'
import Tabs from '@mui/material/Tabs'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { useSnackbar } from 'notistack'
import { useCallback, useEffect, useState } from 'react'
import ChonHoSo from '../../components/General/ChonHoSo'
import DialogConfirm from '../../components/General/DialogConfirm'
import DialogForm from '../../components/General/DialogForm'
import Loading from '../../components/General/Loading'
import { layThongBaoLoi } from '../../services/api'
import {
  hoSoBenhAnApi,
  type DongThuoc,
  type HanhDongNhac,
  type LanNhac,
  type LichSu,
  type NhacThuoc,
} from '../../services/hoSoBenhAn'
import { hienNgay, hienNgayGio, ngayISO } from '../../services/lichKham'
import type { HoSo } from '../../services/nguoiDung'
import { GRADIENT } from '../../theme'

interface FormNhac {
  benhAnId: number | ''
  dongThuoc: DongThuoc[]
  chiTietId: number | ''
  ngayBatDau: string
  tongSoLuong: string
  cacLan: { gio: string; soLuong: string }[]
}

const lanMacDinh = (d?: DongThuoc) => {
  if (!d) return [{ gio: '08:00', soLuong: '1' }]
  const gio = [d.sang && '07:00', d.trua && '12:00', d.chieu && '17:00', d.toi && '21:00'].filter(Boolean) as string[]
  return (gio.length ? gio : ['08:00']).map((g) => ({ gio: g, soLuong: String(d.soLuongMoiLan) }))
}

function TheLanNhac({ l, onLam }: { l: LanNhac; onLam: (l: LanNhac, h: HanhDongNhac) => void }) {
  const xong = l.daUong || l.boQua
  return (
    <Paper elevation={xong ? 0 : 2} variant={xong ? 'outlined' : 'elevation'} sx={{ p: 2, display: 'flex', justifyContent: 'space-between', gap: 2, flexWrap: 'wrap' }}>
      <Box>
        <Typography sx={{ fontWeight: 700 }}>
          {hienNgayGio(l.thoiDiemNhac)} · {l.tenThuoc}
        </Typography>
        <Typography color="text.secondary">
          {l.hoTen} · uống {l.soLuong} {l.donVi.toLowerCase()}
        </Typography>
      </Box>
      <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
        {l.daUong && <Chip color="success" size="small" label="Đã uống" />}
        {l.boQua && <Chip size="small" label="Bỏ qua" />}
        {!xong && (
          <>
            {!l.daNhan && <Button size="small" onClick={() => onLam(l, 'DA_NHAN')}>Đã nhận</Button>}
            <Button size="small" variant="contained" onClick={() => onLam(l, 'DA_UONG')}>Đã uống</Button>
            <Button size="small" color="inherit" onClick={() => onLam(l, 'BO_QUA')}>Bỏ qua</Button>
          </>
        )}
      </Box>
    </Paper>
  )
}

/** FR-BN-15, FR-HT-08, BR-21: nhắc uống thuốc — không job nền, không push; app hiện các lần đã tới giờ. */
export default function NhacThuocPage() {
  const { enqueueSnackbar } = useSnackbar()
  const [tab, setTab] = useState<'den-gio' | 'theo-ngay' | 'danh-sach'>('den-gio')
  const [hoSo, setHoSo] = useState<HoSo | null>(null)
  const [ngay, setNgay] = useState(ngayISO(new Date()))
  const [lan, setLan] = useState<LanNhac[] | null>(null)
  const [ds, setDs] = useState<NhacThuoc[] | null>(null)
  const [lichSu, setLichSu] = useState<LichSu[]>([])
  const [form, setForm] = useState<FormNhac | null>(null)
  const [xoa, setXoa] = useState<NhacThuoc | null>(null)
  const [taiLai, setTaiLai] = useState(0)

  const loi = useCallback((e: unknown) => enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' }), [enqueueSnackbar])

  useEffect(() => {
    if (tab === 'danh-sach') return
    const goi = tab === 'den-gio' ? hoSoBenhAnApi.lanNhacDenGio() : hoSoBenhAnApi.lanNhacTheoNgay(ngay)
    goi.then(setLan).catch(loi)
  }, [tab, ngay, taiLai, loi])

  useEffect(() => {
    if (tab !== 'danh-sach' || !hoSo) return
    hoSoBenhAnApi.nhacThuoc(hoSo.id).then(setDs).catch(loi)
    hoSoBenhAnApi.lichSu(hoSo.id).then(setLichSu).catch(() => setLichSu([]))
  }, [tab, hoSo, taiLai, loi])

  const lam = async (l: LanNhac, h: HanhDongNhac) => {
    try {
      await hoSoBenhAnApi.xacNhanLan(l.id, h)
      setTaiLai((x) => x + 1)
    } catch (e) {
      loi(e)
    }
  }

  const chonBenhAn = async (benhAnId: number) => {
    if (!form) return
    try {
      const ba = await hoSoBenhAnApi.benhAnCuaToi(benhAnId)
      setForm({ ...form, benhAnId, dongThuoc: ba.donThuoc?.thuoc ?? [], chiTietId: '' })
    } catch (e) {
      loi(e)
    }
  }

  const chonThuoc = (id: number) => {
    if (!form) return
    const d = form.dongThuoc.find((x) => x.id === id)
    setForm({ ...form, chiTietId: id, tongSoLuong: String(d?.soLuong ?? ''), cacLan: lanMacDinh(d) })
  }

  const luu = async () => {
    if (!form || !hoSo || form.chiTietId === '') return enqueueSnackbar('Vui lòng chọn thuốc', { variant: 'warning' })
    try {
      await hoSoBenhAnApi.taoNhacThuoc({
        hoSoId: hoSo.id,
        chiTietDonThuocId: form.chiTietId,
        ngayBatDau: form.ngayBatDau,
        tongSoLuong: Number(form.tongSoLuong),
        cacLan: form.cacLan.map((l) => ({ gio: l.gio, soLuong: Number(l.soLuong) })),
      })
      enqueueSnackbar('Đã bật nhắc uống thuốc', { variant: 'success' })
      setForm(null)
      setTaiLai((x) => x + 1)
    } catch (e) {
      loi(e)
    }
  }

  const doiLan = (i: number, p: Partial<{ gio: string; soLuong: string }>) =>
    form && setForm({ ...form, cacLan: form.cacLan.map((l, j) => (j === i ? { ...l, ...p } : l)) })

  return (
    <>
      <Typography variant="h5" sx={{ fontWeight: 700, mb: 2 }}>
        Nhắc uống thuốc
      </Typography>
      <Tabs value={tab} onChange={(_, v) => setTab(v)} sx={{ mb: 2 }}>
        <Tab value="den-gio" label="Đến giờ uống" />
        <Tab value="theo-ngay" label="Theo ngày" />
        <Tab value="danh-sach" label="Thuốc đang nhắc" />
      </Tabs>

      {tab !== 'danh-sach' && (
        <>
          {tab === 'theo-ngay' && (
            <TextField label="Ngày" type="date" value={ngay} onChange={(e) => setNgay(e.target.value)} slotProps={{ inputLabel: { shrink: true } }} sx={{ mb: 2 }} />
          )}
          {!lan ? (
            <Loading />
          ) : lan.length === 0 ? (
            <Typography color="text.secondary" sx={{ py: 4 }}>Không có lần uống thuốc nào.</Typography>
          ) : (
            <Stack spacing={1.5}>
              {lan.map((l) => (
                <TheLanNhac key={l.id} l={l} onLam={lam} />
              ))}
            </Stack>
          )}
        </>
      )}

      {tab === 'danh-sach' && (
        <>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap' }}>
            <ChonHoSo onChon={setHoSo} />
            <Button
              variant="contained"
              startIcon={<AddRoundedIcon />}
              disabled={!hoSo}
              sx={{ background: GRADIENT.nut, fontWeight: 700 }}
              onClick={() => setForm({ benhAnId: '', dongThuoc: [], chiTietId: '', ngayBatDau: ngayISO(new Date()), tongSoLuong: '', cacLan: lanMacDinh() })}
            >
              Bật nhắc thuốc
            </Button>
          </Box>
          {!hoSo ? null : !ds ? (
            <Loading />
          ) : ds.length === 0 ? (
            <Typography color="text.secondary" sx={{ py: 4 }}>Chưa bật nhắc thuốc nào cho hồ sơ này.</Typography>
          ) : (
            <Stack spacing={1.5}>
              {ds.map((n) => (
                <Paper key={n.id} elevation={2} sx={{ p: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Box>
                    <Typography sx={{ fontWeight: 700 }}>{n.tenThuoc}</Typography>
                    <Typography color="text.secondary">
                      Từ {hienNgay(n.ngayBatDau)} đến {hienNgay(n.ngayKetThuc)} · còn {n.soLuongConLai}/{n.tongSoLuong}
                    </Typography>
                  </Box>
                  <IconButton aria-label="Xóa nhắc thuốc" onClick={() => setXoa(n)}>
                    <DeleteOutlineRoundedIcon />
                  </IconButton>
                </Paper>
              ))}
            </Stack>
          )}
        </>
      )}

      <DialogForm mo={Boolean(form)} tieuDe="Bật nhắc uống thuốc" nutXacNhan="Lưu" onDong={() => setForm(null)} onXacNhan={luu}>
        {form && (
          <Stack spacing={2} sx={{ pt: 1 }}>
            <TextField select label="Lần khám" value={form.benhAnId} onChange={(e) => chonBenhAn(Number(e.target.value))}>
              {lichSu.map((l) => (
                <MenuItem key={l.benhAnId} value={l.benhAnId}>
                  {hienNgay(l.ngayKham)} · {l.tenKhoa ?? ''} · {l.chanDoan ?? 'chưa có chẩn đoán'}
                </MenuItem>
              ))}
            </TextField>
            <TextField
              select
              label="Thuốc trong đơn"
              value={form.chiTietId}
              disabled={form.dongThuoc.length === 0}
              helperText={form.benhAnId !== '' && form.dongThuoc.length === 0 ? 'Lần khám này không có đơn thuốc' : ' '}
              onChange={(e) => chonThuoc(Number(e.target.value))}
            >
              {form.dongThuoc.map((d) => (
                <MenuItem key={d.id} value={d.id}>
                  {d.tenThuoc} ({d.soLuong} {d.donVi.toLowerCase()})
                </MenuItem>
              ))}
            </TextField>
            <Stack direction="row" spacing={2}>
              <TextField fullWidth label="Ngày bắt đầu" type="date" value={form.ngayBatDau} onChange={(e) => setForm({ ...form, ngayBatDau: e.target.value })} slotProps={{ inputLabel: { shrink: true } }} />
              <TextField fullWidth label="Tổng số lượng" type="number" value={form.tongSoLuong} onChange={(e) => setForm({ ...form, tongSoLuong: e.target.value })} />
            </Stack>
            <Typography sx={{ fontWeight: 600 }}>Các giờ uống trong ngày</Typography>
            {form.cacLan.map((l, i) => (
              <Stack key={i} direction="row" spacing={2} sx={{ alignItems: 'center' }}>
                <TextField label="Giờ" type="time" value={l.gio} onChange={(e) => doiLan(i, { gio: e.target.value })} slotProps={{ inputLabel: { shrink: true } }} />
                <TextField label="Số lượng mỗi lần" type="number" value={l.soLuong} onChange={(e) => doiLan(i, { soLuong: e.target.value })} />
                <IconButton aria-label="Bỏ giờ uống" disabled={form.cacLan.length === 1} onClick={() => setForm({ ...form, cacLan: form.cacLan.filter((_, j) => j !== i) })}>
                  <DeleteOutlineRoundedIcon />
                </IconButton>
              </Stack>
            ))}
            <Button onClick={() => setForm({ ...form, cacLan: [...form.cacLan, { gio: '20:00', soLuong: '1' }] })}>+ Thêm giờ uống</Button>
          </Stack>
        )}
      </DialogForm>
      <DialogConfirm
        mo={Boolean(xoa)}
        tieuDe="Tắt nhắc thuốc"
        noiDung={`Tắt nhắc thuốc "${xoa?.tenThuoc}"? Các lần chưa tới giờ sẽ bị xóa.`}
        mauNguyHiem
        onDong={() => setXoa(null)}
        onXacNhan={async () => {
          const n = xoa
          setXoa(null)
          if (!n) return
          try {
            await hoSoBenhAnApi.xoaNhacThuoc(n.id)
            setTaiLai((x) => x + 1)
          } catch (e) {
            loi(e)
          }
        }}
      />
    </>
  )
}
