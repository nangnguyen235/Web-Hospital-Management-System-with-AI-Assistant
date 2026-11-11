import Alert from '@mui/material/Alert'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Paper from '@mui/material/Paper'
import Stack from '@mui/material/Stack'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { useSnackbar } from 'notistack'
import { useState, type FormEvent } from 'react'
import BangHoSo from '../../components/General/BangHoSo'
import ChonPhong from '../../components/General/ChonPhong'
import DialogHoSo from '../../components/General/DialogHoSo'
import PageHeader from '../../components/General/PageHeader'
import { layThongBaoLoi } from '../../services/api'
import type { Phong } from '../../services/danhMuc'
import { hoSoBenhAnApi, type KetQuaTiepNhan, type PhieuTiepNhan } from '../../services/hoSoBenhAn'
import { hienNgay } from '../../services/lichKham'
import { nguoiDungApi, type BenhNhanAdmin, type HoSo, type LuuHoSo } from '../../services/nguoiDung'

/**
 * FR-DD-03…06, BR-13: chọn phòng → nhập mã phiếu → (lần đầu) nhập CCCD → tiếp nhận, tạo bệnh án chờ khám.
 * Điều dưỡng chỉ thấy thông tin hành chính, không xem nội dung bệnh án.
 */
export default function TiepNhanPage() {
  const { enqueueSnackbar } = useSnackbar()
  const [phong, setPhong] = useState<Phong | null>(null)
  const [maPhieu, setMaPhieu] = useState('')
  const [phieu, setPhieu] = useState<PhieuTiepNhan | null>(null)
  const [cccd, setCccd] = useState('')
  const [benhNhan, setBenhNhan] = useState<BenhNhanAdmin | null | undefined>(undefined)
  const [kq, setKq] = useState<KetQuaTiepNhan | null>(null)
  const [traTen, setTraTen] = useState('')
  const [traCccd, setTraCccd] = useState('')
  const [hoSoTra, setHoSoTra] = useState<HoSo[] | null>(null)
  const [suaHoSo, setSuaHoSo] = useState<HoSo | null>(null)

  const loi = (e: unknown) => enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' })

  const kiemTra = async (e: FormEvent) => {
    e.preventDefault()
    if (!phong || !maPhieu.trim()) return enqueueSnackbar('Chọn phòng và nhập mã phiếu', { variant: 'warning' })
    setKq(null)
    setBenhNhan(undefined)
    try {
      const p = await hoSoBenhAnApi.kiemTraPhieu(maPhieu.trim(), phong.id)
      setPhieu(p)
      setCccd(p.soCccd ?? '')
    } catch (err) {
      setPhieu(null)
      loi(err)
    }
  }

  const traBenhNhan = async () => {
    if (!cccd.trim()) return
    try {
      setBenhNhan(await nguoiDungApi.traCccd(cccd.trim()))
    } catch {
      setBenhNhan(null) // chưa có: hệ thống sẽ tạo mã bệnh nhân mới khi tiếp nhận
    }
  }

  const tiepNhan = async () => {
    if (!phieu || !phong) return
    if (!phieu.daLienKet && !cccd.trim()) return enqueueSnackbar('Hồ sơ chưa liên kết: vui lòng nhập CCCD', { variant: 'warning' })
    try {
      const k = await hoSoBenhAnApi.tiepNhan(phieu.maPhieu, phong.id, phieu.daLienKet ? null : cccd.trim())
      setKq(k)
      setPhieu(null)
      setMaPhieu('')
      enqueueSnackbar('Đã tiếp nhận', { variant: 'success' })
    } catch (err) {
      loi(err)
    }
  }

  const tra = async (e: FormEvent) => {
    e.preventDefault()
    try {
      setHoSoTra(await nguoiDungApi.traHoSo(traTen.trim(), traCccd.trim()))
    } catch (err) {
      loi(err)
    }
  }

  const luuHoSo = async (g: LuuHoSo) => {
    if (!suaHoSo) return
    try {
      const h = await nguoiDungApi.suaHoSoDieuDuong(suaHoSo.id, g)
      setHoSoTra((ds) => ds?.map((x) => (x.id === h.id ? h : x)) ?? null)
      setSuaHoSo(null)
      enqueueSnackbar('Đã cập nhật hồ sơ', { variant: 'success' })
    } catch (err) {
      loi(err)
    }
  }

  return (
    <>
      <PageHeader tieuDe="Tiếp nhận bệnh nhân" />
      <Paper elevation={2} sx={{ p: 2.5, mb: 3 }}>
        <Box component="form" onSubmit={kiemTra}>
          <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
            <ChonPhong loai="PHONG_KHAM" onChon={setPhong} />
            <TextField fullWidth label="Mã phiếu" value={maPhieu} onChange={(e) => setMaPhieu(e.target.value.toUpperCase())} />
            <Button type="submit" variant="contained">Kiểm tra</Button>
          </Stack>
        </Box>
        {kq && (
          <Alert severity="success" sx={{ mt: 2 }}>
            Đã tiếp nhận <b>{kq.hoTen}</b> · Số thứ tự <b>{kq.soThuTu}</b> · Mã bệnh nhân <b>{kq.maBenhNhan}</b>
          </Alert>
        )}
        {phieu && (
          <Paper variant="outlined" sx={{ p: 2, mt: 2 }}>
            <Typography sx={{ fontWeight: 700 }}>
              {phieu.hoTen} · Số thứ tự {phieu.soThuTu}
            </Typography>
            <Typography color="text.secondary">
              Ngày sinh {hienNgay(phieu.ngaySinh)} · {phieu.gioiTinh === 'NU' ? 'Nữ' : 'Nam'} · SĐT {phieu.soDienThoai ?? '—'}
            </Typography>
            {phieu.benhAnId ? (
              <Alert severity="info" sx={{ mt: 1 }}>Phiếu đã được tiếp nhận trước đó.</Alert>
            ) : phieu.daLienKet ? (
              <Typography sx={{ mt: 1 }}>Mã bệnh nhân: {phieu.maBenhNhan}</Typography>
            ) : (
              <Stack direction="row" spacing={2} sx={{ mt: 2, alignItems: 'center' }}>
                <TextField label="Số CCCD (lần đầu khám)" value={cccd} onChange={(e) => setCccd(e.target.value)} onBlur={traBenhNhan} />
                {benhNhan && <Typography>Đã có bệnh nhân: {benhNhan.hoTen} · {benhNhan.maBenhNhan}</Typography>}
                {benhNhan === null && <Typography color="text.secondary">Chưa có — sẽ tạo mã bệnh nhân mới</Typography>}
              </Stack>
            )}
            {!phieu.benhAnId && (
              <Button variant="contained" sx={{ mt: 2 }} onClick={tiepNhan}>
                Tiếp nhận
              </Button>
            )}
          </Paper>
        )}
      </Paper>

      <Typography variant="h6" sx={{ fontWeight: 700, mb: 1 }}>
        Tra cứu, sửa hồ sơ
      </Typography>
      <Paper elevation={2} sx={{ p: 2.5 }}>
        <Box component="form" onSubmit={tra}>
          <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} sx={{ mb: 2 }}>
            <TextField fullWidth label="Họ tên" value={traTen} onChange={(e) => setTraTen(e.target.value)} />
            <TextField fullWidth label="Số CCCD" value={traCccd} onChange={(e) => setTraCccd(e.target.value)} />
            <Button type="submit" variant="outlined">Tìm</Button>
          </Stack>
        </Box>
        {hoSoTra && (
          <BangHoSo
            hoSo={hoSoTra}
            thaoTac={(h) => (
              <Button size="small" onClick={() => setSuaHoSo(h)}>
                Sửa
              </Button>
            )}
          />
        )}
      </Paper>
      <DialogHoSo mo={Boolean(suaHoSo)} hoSo={suaHoSo} onDong={() => setSuaHoSo(null)} onLuu={luuHoSo} />
    </>
  )
}
