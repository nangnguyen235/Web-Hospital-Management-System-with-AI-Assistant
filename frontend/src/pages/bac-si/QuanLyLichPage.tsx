import AddRoundedIcon from '@mui/icons-material/AddRounded'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Chip from '@mui/material/Chip'
import MenuItem from '@mui/material/MenuItem'
import Paper from '@mui/material/Paper'
import Stack from '@mui/material/Stack'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { useSnackbar } from 'notistack'
import { useCallback, useEffect, useState } from 'react'
import BangLich from '../../components/General/BangLich'
import DialogConfirm from '../../components/General/DialogConfirm'
import DialogForm from '../../components/General/DialogForm'
import PageHeader from '../../components/General/PageHeader'
import { layThongBaoLoi } from '../../services/api'
import { danhMucApi, type DichVu, type Phong } from '../../services/danhMuc'
import { congNgay, hienNgay, lichKhamApi, ngayISO, type Lich } from '../../services/lichKham'
import { nguoiDungApi, TEN_HOC_HAM, type NhanVienGon } from '../../services/nguoiDung'
import { GRADIENT } from '../../theme'

interface FormTao {
  bacSiId: number | ''
  dichVuId: number | ''
  phongId: number | ''
  soLuotMoiKhung: number
  ngaySang: string[]
  ngayChieu: string[]
  ngayDangChon: string
}

function ChonNhieuNgay({ nhan, ds, onDoi, ngay, onNgay }: { nhan: string; ds: string[]; onDoi: (d: string[]) => void; ngay: string; onNgay: (n: string) => void }) {
  return (
    <Box>
      <Typography sx={{ fontWeight: 600, mb: 1 }}>{nhan}</Typography>
      <Stack direction="row" spacing={1} sx={{ alignItems: 'center' }}>
        <TextField size="small" type="date" value={ngay} onChange={(e) => onNgay(e.target.value)} slotProps={{ htmlInput: { min: ngayISO(new Date()) } }} />
        <Button size="small" disabled={!ngay || ds.includes(ngay)} onClick={() => onDoi([...ds, ngay].sort())}>
          Thêm
        </Button>
      </Stack>
      <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1, mt: 1 }}>
        {ds.map((d) => (
          <Chip key={d} label={hienNgay(d)} onDelete={() => onDoi(ds.filter((x) => x !== d))} />
        ))}
      </Box>
    </Box>
  )
}

/** FR-BS-11…14, BR-05…07: trưởng khoa tạo lịch nhiều ngày sáng/chiều, sửa (bác sĩ, số lượt), xóa lịch chưa có phiếu, xem lịch khoa. */
export default function QuanLyLichPage() {
  const { enqueueSnackbar } = useSnackbar()
  const homNay = ngayISO(new Date())
  const [tu, setTu] = useState(homNay)
  const [den, setDen] = useState(congNgay(homNay, 13))
  const [lich, setLich] = useState<Lich[] | null>(null)
  const [bacSi, setBacSi] = useState<NhanVienGon[]>([])
  const [dichVu, setDichVu] = useState<DichVu[]>([])
  const [phong, setPhong] = useState<Phong[]>([])
  const [tao, setTao] = useState<FormTao | null>(null)
  const [sua, setSua] = useState<{ lich: Lich; bacSiId: number; soLuotMoiKhung: number } | null>(null)
  const [xoa, setXoa] = useState<Lich | null>(null)

  const loi = useCallback((e: unknown) => enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' }), [enqueueSnackbar])

  const tai = useCallback(() => {
    lichKhamApi.lichKhoa({ tu, den }).then(setLich).catch((e) => {
      setLich([])
      loi(e)
    })
  }, [tu, den, loi])
  useEffect(tai, [tai])

  useEffect(() => {
    nguoiDungApi.nhanVienKhoaToi('BAC_SI').then(setBacSi).catch(() => undefined)
    nguoiDungApi
      .toi()
      .then((t) => {
        if (!t.khoaId) return
        danhMucApi.dichVu(t.khoaId, 'KHAM_BENH').then(setDichVu).catch(() => undefined)
        danhMucApi.phong(t.khoaId, 'PHONG_KHAM').then(setPhong).catch(() => undefined)
      })
      .catch(() => undefined)
  }, [])

  const luuTao = async () => {
    if (!tao) return
    if (tao.bacSiId === '' || tao.dichVuId === '' || tao.phongId === '') return enqueueSnackbar('Chọn đủ bác sĩ, dịch vụ, phòng', { variant: 'warning' })
    if (tao.ngaySang.length + tao.ngayChieu.length === 0) return enqueueSnackbar('Chọn ít nhất một ngày', { variant: 'warning' })
    try {
      const kq = await lichKhamApi.taoLich({
        bacSiId: tao.bacSiId, dichVuId: tao.dichVuId, phongId: tao.phongId, soLuotMoiKhung: tao.soLuotMoiKhung, ngaySang: tao.ngaySang, ngayChieu: tao.ngayChieu,
      })
      enqueueSnackbar(`Đã tạo ${kq.length} lịch khám`, { variant: 'success' })
      setTao(null)
      tai()
    } catch (e) {
      loi(e)
    }
  }

  const luuSua = async () => {
    if (!sua) return
    try {
      await lichKhamApi.suaLich(sua.lich.id, { bacSiId: sua.bacSiId, soLuotMoiKhung: sua.soLuotMoiKhung })
      enqueueSnackbar('Đã cập nhật lịch', { variant: 'success' })
      setSua(null)
      tai()
    } catch (e) {
      loi(e)
    }
  }

  const chonSelect = <T extends { id: number }>(nhan: string, ds: T[], v: number | '', set: (n: number) => void, ten: (x: T) => string) => (
    <TextField select label={nhan} value={v} onChange={(e) => set(Number(e.target.value))}>
      {ds.map((x) => (
        <MenuItem key={x.id} value={x.id}>{ten(x)}</MenuItem>
      ))}
    </TextField>
  )
  const tenBs = (b: NhanVienGon) => `${b.hocHam ? `${TEN_HOC_HAM[b.hocHam]} ` : ''}${b.hoTen}`

  return (
    <>
      <PageHeader
        tieuDe="Quản lý lịch khám"
        hanhDong={
          <Button
            variant="contained"
            startIcon={<AddRoundedIcon />}
            sx={{ background: GRADIENT.nut, fontWeight: 700 }}
            onClick={() => setTao({ bacSiId: '', dichVuId: dichVu[0]?.id ?? '', phongId: '', soLuotMoiKhung: 5, ngaySang: [], ngayChieu: [], ngayDangChon: homNay })}
          >
            Tạo lịch
          </Button>
        }
      />
      <Paper elevation={2} sx={{ p: 2, mb: 2 }}>
        <Stack direction="row" spacing={2}>
          <TextField size="small" label="Từ ngày" type="date" value={tu} onChange={(e) => setTu(e.target.value)} slotProps={{ inputLabel: { shrink: true } }} />
          <TextField size="small" label="Đến ngày" type="date" value={den} onChange={(e) => setDen(e.target.value)} slotProps={{ inputLabel: { shrink: true } }} />
        </Stack>
      </Paper>
      <BangLich
        lich={lich}
        thaoTac={(l) =>
          l.ngay > homNay && (
            <>
              <Button size="small" onClick={() => setSua({ lich: l, bacSiId: l.bacSiId, soLuotMoiKhung: l.soLuotMoiKhung })}>Sửa</Button>
              <Button size="small" color="error" onClick={() => setXoa(l)}>Xóa</Button>
            </>
          )
        }
      />

      <DialogForm mo={Boolean(tao)} tieuDe="Tạo lịch khám" nutXacNhan="Tạo" onDong={() => setTao(null)} onXacNhan={luuTao}>
        {tao && (
          <Stack spacing={2} sx={{ pt: 1 }}>
            {chonSelect('Bác sĩ', bacSi, tao.bacSiId, (n) => setTao({ ...tao, bacSiId: n }), tenBs)}
            {chonSelect('Dịch vụ khám', dichVu, tao.dichVuId, (n) => setTao({ ...tao, dichVuId: n }), (d) => d.tenDichVu)}
            {chonSelect('Phòng khám', phong, tao.phongId, (n) => setTao({ ...tao, phongId: n }), (p) => p.tenPhong)}
            <TextField
              label="Số lượt mỗi khung giờ"
              type="number"
              value={tao.soLuotMoiKhung}
              onChange={(e) => setTao({ ...tao, soLuotMoiKhung: Number(e.target.value) })}
              helperText="Mỗi buổi 4 khung 1 giờ (sáng 07:00–11:00, chiều 13:00–17:00)"
            />
            <ChonNhieuNgay nhan="Các ngày khám buổi sáng" ds={tao.ngaySang} onDoi={(d) => setTao({ ...tao, ngaySang: d })} ngay={tao.ngayDangChon} onNgay={(n) => setTao({ ...tao, ngayDangChon: n })} />
            <ChonNhieuNgay nhan="Các ngày khám buổi chiều" ds={tao.ngayChieu} onDoi={(d) => setTao({ ...tao, ngayChieu: d })} ngay={tao.ngayDangChon} onNgay={(n) => setTao({ ...tao, ngayDangChon: n })} />
          </Stack>
        )}
      </DialogForm>

      <DialogForm mo={Boolean(sua)} tieuDe="Sửa lịch khám" nutXacNhan="Lưu" onDong={() => setSua(null)} onXacNhan={luuSua}>
        {sua && (
          <Stack spacing={2} sx={{ pt: 1 }}>
            <Typography color="text.secondary">
              {hienNgay(sua.lich.ngay)} · Phòng {sua.lich.tenPhong}
            </Typography>
            {chonSelect('Bác sĩ', bacSi, sua.bacSiId, (n) => setSua({ ...sua, bacSiId: n }), tenBs)}
            <TextField
              label="Số lượt mỗi khung giờ"
              type="number"
              value={sua.soLuotMoiKhung}
              onChange={(e) => setSua({ ...sua, soLuotMoiKhung: Number(e.target.value) })}
              helperText="Chỉ đổi được khi lịch chưa có phiếu (BR-07)"
            />
          </Stack>
        )}
      </DialogForm>

      <DialogConfirm
        mo={Boolean(xoa)}
        tieuDe="Xóa lịch khám"
        noiDung={`Xóa lịch ngày ${xoa ? hienNgay(xoa.ngay) : ''} của ${xoa?.tenBacSi}? Chỉ xóa được lịch chưa có phiếu.`}
        mauNguyHiem
        onDong={() => setXoa(null)}
        onXacNhan={async () => {
          const l = xoa
          setXoa(null)
          if (!l) return
          try {
            await lichKhamApi.xoaLich(l.id)
            tai()
          } catch (e) {
            loi(e)
          }
        }}
      />
    </>
  )
}
