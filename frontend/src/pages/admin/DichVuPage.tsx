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
import { danhMucApi, dinhDangTien, TEN_LOAI_DICH_VU, type DichVu, type Khoa, type LuuDichVu } from '../../services/danhMuc'
import { GRADIENT } from '../../theme'

const TRONG: LuuDichVu = { tenDichVu: '', moTa: '', khoaId: '', loaiDichVu: 'KHAM_BENH', gia: '' }

/** FR-AD-06: dịch vụ khám bệnh / xét nghiệm theo khoa, kèm giá. */
export default function DichVuPage() {
  const { enqueueSnackbar } = useSnackbar()
  const ds = useDanhSachPhanTrang(danhMucApi.timDichVu)
  const [khoa, setKhoa] = useState<Khoa[]>([])
  const [form, setForm] = useState<{ dv: DichVu | null; g: LuuDichVu } | null>(null)
  const [daGui, setDaGui] = useState(false)
  const [xacNhan, setXacNhan] = useState<DichVu | null>(null)

  useEffect(() => {
    danhMucApi.khoaCongKhai().then(setKhoa).catch(() => undefined)
  }, [])

  const sua = (p: Partial<LuuDichVu>) => form && setForm({ ...form, g: { ...form.g, ...p } })
  const moForm = (dv: DichVu | null) => {
    setDaGui(false)
    setForm({ dv, g: dv ? { tenDichVu: dv.tenDichVu, moTa: dv.moTa ?? '', khoaId: dv.khoaId, loaiDichVu: dv.loaiDichVu, gia: dv.gia } : TRONG })
  }

  const luu = async () => {
    if (!form) return
    setDaGui(true)
    const g = form.g
    if (!g.tenDichVu.trim() || g.khoaId === '' || g.gia === '' || g.gia < 0) return
    try {
      if (form.dv) await danhMucApi.suaDichVu(form.dv.id, g)
      else await danhMucApi.taoDichVu(g)
      enqueueSnackbar('Đã lưu dịch vụ', { variant: 'success' })
      setForm(null)
      ds.lamMoi()
    } catch (e) {
      enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' })
    }
  }

  const doiTrangThai = async () => {
    if (!xacNhan) return
    try {
      if (xacNhan.daXoa) await danhMucApi.khoiPhucDichVu(xacNhan.id)
      else await danhMucApi.xoaDichVu(xacNhan.id)
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
        tieuDe="Quản lý dịch vụ"
        hanhDong={
          <Button variant="contained" startIcon={<AddRoundedIcon />} onClick={() => moForm(null)} sx={{ background: GRADIENT.nut, fontWeight: 700 }}>
            Thêm
          </Button>
        }
      />
      <BangQuanLy
        ds={ds}
        khoa={(d) => d.id}
        goiYTim="Tìm theo tên dịch vụ"
        cot={[
          { nhan: 'Tên dịch vụ', hien: (d) => <b>{d.tenDichVu}</b> },
          { nhan: 'Loại', hien: (d) => TEN_LOAI_DICH_VU[d.loaiDichVu] },
          { nhan: 'Khoa', hien: (d) => d.tenKhoa, anDuoi: 'md' },
          { nhan: 'Giá', hien: (d) => dinhDangTien(d.gia) },
          { nhan: 'Trạng thái', hien: (d) => <ChipDaXoa daXoa={d.daXoa} /> },
        ]}
        thaoTac={[{ nhan: () => 'Sửa', onChon: moForm }, thaoTacXoa<DichVu>(setXacNhan)]}
      />
      <DialogForm mo={Boolean(form)} tieuDe={form?.dv ? 'Cập nhật dịch vụ' : 'Thêm dịch vụ'} nutXacNhan="Lưu" onDong={() => setForm(null)} onXacNhan={luu}>
        {g && (
          <Stack spacing={2.5} sx={{ pt: 1 }}>
            <TextField label="Tên dịch vụ" required value={g.tenDichVu} onChange={(e) => sua({ tenDichVu: e.target.value })} error={daGui && !g.tenDichVu.trim()} />
            <TextField select label="Loại dịch vụ" value={g.loaiDichVu} onChange={(e) => sua({ loaiDichVu: e.target.value as LuuDichVu['loaiDichVu'] })}>
              {Object.entries(TEN_LOAI_DICH_VU).map(([k, v]) => (
                <MenuItem key={k} value={k}>{v}</MenuItem>
              ))}
            </TextField>
            <TextField select label="Khoa" required value={g.khoaId} onChange={(e) => sua({ khoaId: Number(e.target.value) })} error={daGui && g.khoaId === ''}>
              {khoa.map((k) => (
                <MenuItem key={k.id} value={k.id}>{k.tenKhoa}</MenuItem>
              ))}
            </TextField>
            <TextField
              label="Giá (đồng)"
              type="number"
              required
              value={g.gia}
              onChange={(e) => sua({ gia: e.target.value === '' ? '' : Number(e.target.value) })}
              error={daGui && (g.gia === '' || g.gia < 0)}
            />
            <TextField label="Mô tả" multiline minRows={2} value={g.moTa} onChange={(e) => sua({ moTa: e.target.value })} />
          </Stack>
        )}
      </DialogForm>
      <DialogConfirm
        mo={Boolean(xacNhan)}
        tieuDe={xacNhan?.daXoa ? 'Khôi phục dịch vụ' : 'Xóa dịch vụ'}
        noiDung={`${xacNhan?.daXoa ? 'Khôi phục' : 'Xóa'} dịch vụ "${xacNhan?.tenDichVu}"?`}
        mauNguyHiem={!xacNhan?.daXoa}
        onDong={() => setXacNhan(null)}
        onXacNhan={doiTrangThai}
      />
    </>
  )
}
