import AddRoundedIcon from '@mui/icons-material/AddRounded'
import Button from '@mui/material/Button'
import MenuItem from '@mui/material/MenuItem'
import Stack from '@mui/material/Stack'
import TextField from '@mui/material/TextField'
import { useSnackbar } from 'notistack'
import { useEffect, useState } from 'react'
import BangQuanLy from '../../components/General/BangQuanLy'
import ChipDaXoa from '../../components/General/ChipDaXoa'
import { thaoTacXoa, useDanhSachPhanTrang } from '../../hooks/useDanhSachPhanTrang'
import DialogConfirm from '../../components/General/DialogConfirm'
import DialogForm from '../../components/General/DialogForm'
import PageHeader from '../../components/General/PageHeader'
import { layThongBaoLoi } from '../../services/api'
import { danhMucApi, TEN_LOAI_PHONG, type DichVu, type Khoa, type LuuPhong, type Phong } from '../../services/danhMuc'
import { GRADIENT } from '../../theme'

const TRONG: LuuPhong = { tenPhong: '', moTa: '', khoaId: '', loaiPhong: 'PHONG_KHAM', dichVuId: '' }

/** FR-AD-05, BR-04: phòng xét nghiệm phải gắn một dịch vụ XET_NGHIEM cùng khoa. */
export default function PhongPage() {
  const { enqueueSnackbar } = useSnackbar()
  const ds = useDanhSachPhanTrang(danhMucApi.timPhong)
  const [khoa, setKhoa] = useState<Khoa[]>([])
  const [dichVuXn, setDichVuXn] = useState<DichVu[]>([])
  const [form, setForm] = useState<{ phong: Phong | null; g: LuuPhong } | null>(null)
  const [daGui, setDaGui] = useState(false)
  const [xacNhan, setXacNhan] = useState<Phong | null>(null)

  useEffect(() => {
    danhMucApi.khoaCongKhai().then(setKhoa).catch(() => undefined)
  }, [])

  useEffect(() => {
    if (form?.g.loaiPhong === 'PHONG_XET_NGHIEM' && form.g.khoaId !== '') {
      danhMucApi.dichVu(form.g.khoaId, 'XET_NGHIEM').then(setDichVuXn).catch(() => setDichVuXn([]))
    }
  }, [form?.g.loaiPhong, form?.g.khoaId])

  const sua = (p: Partial<LuuPhong>) => form && setForm({ ...form, g: { ...form.g, ...p } })
  const moForm = (phong: Phong | null) => {
    setDaGui(false)
    setForm({
      phong,
      g: phong
        ? { tenPhong: phong.tenPhong, moTa: phong.moTa ?? '', khoaId: phong.khoaId, loaiPhong: phong.loaiPhong, dichVuId: phong.dichVuId ?? '' }
        : TRONG,
    })
  }

  const luu = async () => {
    if (!form) return
    setDaGui(true)
    const g = form.g
    if (!g.tenPhong.trim() || g.khoaId === '' || (g.loaiPhong === 'PHONG_XET_NGHIEM' && !g.dichVuId)) return
    const body = { ...g, dichVuId: g.loaiPhong === 'PHONG_XET_NGHIEM' ? g.dichVuId : null }
    try {
      if (form.phong) await danhMucApi.suaPhong(form.phong.id, body)
      else await danhMucApi.taoPhong(body)
      enqueueSnackbar('Đã lưu phòng', { variant: 'success' })
      setForm(null)
      ds.lamMoi()
    } catch (e) {
      enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' })
    }
  }

  const doiTrangThai = async () => {
    if (!xacNhan) return
    try {
      if (xacNhan.daXoa) await danhMucApi.khoiPhucPhong(xacNhan.id)
      else await danhMucApi.xoaPhong(xacNhan.id)
      ds.lamMoi()
    } catch (e) {
      enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' })
    } finally {
      setXacNhan(null)
    }
  }

  const g = form?.g
  return (
    <>
      <PageHeader
        tieuDe="Quản lý phòng"
        hanhDong={
          <Button variant="contained" startIcon={<AddRoundedIcon />} onClick={() => moForm(null)} sx={{ background: GRADIENT.nut, fontWeight: 700 }}>
            Thêm
          </Button>
        }
      />
      <BangQuanLy
        ds={ds}
        khoa={(p) => p.id}
        goiYTim="Tìm theo tên phòng"
        cot={[
          { nhan: 'Tên phòng', hien: (p) => <b>{p.tenPhong}</b> },
          { nhan: 'Loại', hien: (p) => TEN_LOAI_PHONG[p.loaiPhong] },
          { nhan: 'Khoa', hien: (p) => p.tenKhoa, anDuoi: 'md' },
          { nhan: 'Dịch vụ xét nghiệm', hien: (p) => p.tenDichVu ?? '—', anDuoi: 'lg' },
          { nhan: 'Trạng thái', hien: (p) => <ChipDaXoa daXoa={p.daXoa} /> },
        ]}
        thaoTac={[{ nhan: () => 'Sửa', onChon: moForm }, thaoTacXoa<Phong>(setXacNhan)]}
      />
      <DialogForm mo={Boolean(form)} tieuDe={form?.phong ? 'Cập nhật phòng' : 'Thêm phòng'} nutXacNhan="Lưu" onDong={() => setForm(null)} onXacNhan={luu}>
        {g && (
          <Stack spacing={2.5} sx={{ pt: 1 }}>
            <TextField label="Tên phòng" required value={g.tenPhong} onChange={(e) => sua({ tenPhong: e.target.value })} error={daGui && !g.tenPhong.trim()} />
            <TextField select label="Loại phòng" value={g.loaiPhong} onChange={(e) => sua({ loaiPhong: e.target.value as LuuPhong['loaiPhong'], dichVuId: '' })}>
              {Object.entries(TEN_LOAI_PHONG).map(([k, v]) => (
                <MenuItem key={k} value={k}>{v}</MenuItem>
              ))}
            </TextField>
            <TextField select label="Khoa" required value={g.khoaId} onChange={(e) => sua({ khoaId: Number(e.target.value), dichVuId: '' })} error={daGui && g.khoaId === ''}>
              {khoa.map((k) => (
                <MenuItem key={k.id} value={k.id}>{k.tenKhoa}</MenuItem>
              ))}
            </TextField>
            {g.loaiPhong === 'PHONG_XET_NGHIEM' && (
              <TextField
                select
                label="Dịch vụ xét nghiệm"
                required
                value={g.dichVuId ?? ''}
                onChange={(e) => sua({ dichVuId: Number(e.target.value) })}
                error={daGui && !g.dichVuId}
                helperText="Dịch vụ loại Xét nghiệm của cùng khoa"
              >
                {dichVuXn.map((d) => (
                  <MenuItem key={d.id} value={d.id}>{d.tenDichVu}</MenuItem>
                ))}
              </TextField>
            )}
            <TextField label="Mô tả" multiline minRows={2} value={g.moTa} onChange={(e) => sua({ moTa: e.target.value })} />
          </Stack>
        )}
      </DialogForm>
      <DialogConfirm
        mo={Boolean(xacNhan)}
        tieuDe={xacNhan?.daXoa ? 'Khôi phục phòng' : 'Xóa phòng'}
        noiDung={`${xacNhan?.daXoa ? 'Khôi phục' : 'Xóa'} phòng "${xacNhan?.tenPhong}"?`}
        mauNguyHiem={!xacNhan?.daXoa}
        onDong={() => setXacNhan(null)}
        onXacNhan={doiTrangThai}
      />
    </>
  )
}
