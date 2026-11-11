import AddRoundedIcon from '@mui/icons-material/AddRounded'
import Button from '@mui/material/Button'
import MenuItem from '@mui/material/MenuItem'
import Stack from '@mui/material/Stack'
import TextField from '@mui/material/TextField'
import { useSnackbar } from 'notistack'
import { useState } from 'react'
import BangQuanLy from '../../components/General/BangQuanLy'
import ChipDaXoa from '../../components/General/ChipDaXoa'
import { thaoTacXoa, useDanhSachPhanTrang } from '../../hooks/useDanhSachPhanTrang'
import DialogConfirm from '../../components/General/DialogConfirm'
import DialogForm from '../../components/General/DialogForm'
import PageHeader from '../../components/General/PageHeader'
import { layThongBaoLoi } from '../../services/api'
import { danhMucApi, dinhDangTien, TEN_DON_VI, type LuuThuoc, type Thuoc } from '../../services/danhMuc'
import { GRADIENT } from '../../theme'

const TRONG: LuuThuoc = { tenThuoc: '', gia: '', soLuong: '', donVi: 'VIEN' }

/** FR-AD-07: danh mục thuốc (BR-18: chỉ kê, không bán — số lượng không bị trừ khi kê). */
export default function ThuocPage() {
  const { enqueueSnackbar } = useSnackbar()
  const ds = useDanhSachPhanTrang(danhMucApi.timThuoc)
  const [form, setForm] = useState<{ t: Thuoc | null; g: LuuThuoc } | null>(null)
  const [daGui, setDaGui] = useState(false)
  const [xacNhan, setXacNhan] = useState<Thuoc | null>(null)

  const sua = (p: Partial<LuuThuoc>) => form && setForm({ ...form, g: { ...form.g, ...p } })
  const moForm = (t: Thuoc | null) => {
    setDaGui(false)
    setForm({ t, g: t ? { tenThuoc: t.tenThuoc, gia: t.gia, soLuong: t.soLuong, donVi: t.donVi } : TRONG })
  }
  const so = (v: string) => (v === '' ? '' : Number(v))

  const luu = async () => {
    if (!form) return
    setDaGui(true)
    const g = form.g
    if (!g.tenThuoc.trim() || g.gia === '' || g.soLuong === '') return
    try {
      if (form.t) await danhMucApi.suaThuoc(form.t.id, g)
      else await danhMucApi.taoThuoc(g)
      enqueueSnackbar('Đã lưu thuốc', { variant: 'success' })
      setForm(null)
      ds.lamMoi()
    } catch (e) {
      enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' })
    }
  }

  const doiTrangThai = async () => {
    if (!xacNhan) return
    try {
      if (xacNhan.daXoa) await danhMucApi.khoiPhucThuoc(xacNhan.id)
      else await danhMucApi.xoaThuoc(xacNhan.id)
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
        tieuDe="Quản lý thuốc"
        hanhDong={
          <Button variant="contained" startIcon={<AddRoundedIcon />} onClick={() => moForm(null)} sx={{ background: GRADIENT.nut, fontWeight: 700 }}>
            Thêm
          </Button>
        }
      />
      <BangQuanLy
        ds={ds}
        khoa={(t) => t.id}
        goiYTim="Tìm theo tên thuốc"
        cot={[
          { nhan: 'Tên thuốc', hien: (t) => <b>{t.tenThuoc}</b> },
          { nhan: 'Đơn vị', hien: (t) => TEN_DON_VI[t.donVi] },
          { nhan: 'Giá', hien: (t) => dinhDangTien(t.gia) },
          { nhan: 'Số lượng', hien: (t) => t.soLuong, anDuoi: 'md' },
          { nhan: 'Trạng thái', hien: (t) => <ChipDaXoa daXoa={t.daXoa} /> },
        ]}
        thaoTac={[{ nhan: () => 'Sửa', onChon: moForm }, thaoTacXoa<Thuoc>(setXacNhan)]}
      />
      <DialogForm mo={Boolean(form)} tieuDe={form?.t ? 'Cập nhật thuốc' : 'Thêm thuốc'} nutXacNhan="Lưu" onDong={() => setForm(null)} onXacNhan={luu}>
        {g && (
          <Stack spacing={2.5} sx={{ pt: 1 }}>
            <TextField label="Tên thuốc" required value={g.tenThuoc} onChange={(e) => sua({ tenThuoc: e.target.value })} error={daGui && !g.tenThuoc.trim()} />
            <TextField select label="Đơn vị" value={g.donVi} onChange={(e) => sua({ donVi: e.target.value as LuuThuoc['donVi'] })}>
              {Object.entries(TEN_DON_VI).map(([k, v]) => (
                <MenuItem key={k} value={k}>{v}</MenuItem>
              ))}
            </TextField>
            <TextField label="Giá (đồng)" type="number" required value={g.gia} onChange={(e) => sua({ gia: so(e.target.value) })} error={daGui && g.gia === ''} />
            <TextField label="Số lượng" type="number" required value={g.soLuong} onChange={(e) => sua({ soLuong: so(e.target.value) })} error={daGui && g.soLuong === ''} />
          </Stack>
        )}
      </DialogForm>
      <DialogConfirm
        mo={Boolean(xacNhan)}
        tieuDe={xacNhan?.daXoa ? 'Khôi phục thuốc' : 'Xóa thuốc'}
        noiDung={`${xacNhan?.daXoa ? 'Khôi phục' : 'Xóa'} thuốc "${xacNhan?.tenThuoc}"?`}
        mauNguyHiem={!xacNhan?.daXoa}
        onDong={() => setXacNhan(null)}
        onXacNhan={doiTrangThai}
      />
    </>
  )
}
