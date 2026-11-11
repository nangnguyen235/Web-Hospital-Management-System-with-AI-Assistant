import AddRoundedIcon from '@mui/icons-material/AddRounded'
import Button from '@mui/material/Button'
import Chip from '@mui/material/Chip'
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
import { danhMucApi, type Khoa } from '../../services/danhMuc'
import {
  nguoiDungApi,
  TEN_GIOI_TINH,
  TEN_HOC_HAM,
  TEN_LOAI_NV,
  type LoaiNhanVien,
  type LuuNhanVien,
  type NhanVien,
} from '../../services/nguoiDung'
import { GRADIENT } from '../../theme'

const TRONG: LuuNhanVien = {
  hoTen: '', email: '', soDienThoai: '', matKhau: '', gioiTinh: '', ngaySinh: '', soCccd: '', loaiNhanVien: 'BAC_SI', hocHam: '', khoaId: '',
}

/** FR-AD-03, FR-AD-04 (thêm/bớt khỏi khoa, trưởng khoa), BR-04: mỗi khoa 1 trưởng khoa + 1 điều dưỡng trưởng. */
export default function NhanVienPage() {
  const { enqueueSnackbar } = useSnackbar()
  const [loai, setLoai] = useState<LoaiNhanVien | ''>('')
  const ds = useDanhSachPhanTrang((tk, p, n) => nguoiDungApi.timNhanVien(tk, p, n, loai || undefined))
  const [khoa, setKhoa] = useState<Khoa[]>([])
  const [form, setForm] = useState<{ nv: NhanVien | null; g: LuuNhanVien } | null>(null)
  const [daGui, setDaGui] = useState(false)
  const [xacNhan, setXacNhan] = useState<NhanVien | null>(null)
  const { lamMoi } = ds

  useEffect(() => {
    danhMucApi.khoaCongKhai().then(setKhoa).catch(() => undefined)
  }, [])
  useEffect(() => lamMoi(), [loai, lamMoi])

  const sua = (p: Partial<LuuNhanVien>) => form && setForm({ ...form, g: { ...form.g, ...p } })
  const moForm = (nv: NhanVien | null) => {
    setDaGui(false)
    setForm({
      nv,
      g: nv
        ? {
            hoTen: nv.hoTen, email: nv.email ?? '', soDienThoai: nv.soDienThoai, matKhau: '', gioiTinh: nv.gioiTinh, ngaySinh: nv.ngaySinh ?? '',
            soCccd: nv.soCccd ?? '', loaiNhanVien: nv.loaiNhanVien, hocHam: nv.hocHam ?? '', khoaId: nv.khoaId ?? '',
          }
        : TRONG,
    })
  }

  const chay = async (viec: () => Promise<unknown>, thongBao: string) => {
    try {
      await viec()
      enqueueSnackbar(thongBao, { variant: 'success' })
      lamMoi()
    } catch (e) {
      enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' })
    }
  }

  const luu = async () => {
    if (!form) return
    setDaGui(true)
    const g = form.g
    if (!g.hoTen.trim() || !g.soDienThoai.trim() || (!form.nv && g.matKhau.length < 6)) return
    await chay(async () => {
      if (form.nv) await nguoiDungApi.suaNhanVien(form.nv.id, g)
      else await nguoiDungApi.taoNhanVien(g)
      setForm(null)
    }, 'Đã lưu nhân viên')
  }

  const g = form?.g
  return (
    <>
      <PageHeader
        tieuDe="Quản lý nhân viên"
        hanhDong={
          <Button variant="contained" startIcon={<AddRoundedIcon />} onClick={() => moForm(null)} sx={{ background: GRADIENT.nut, fontWeight: 700 }}>
            Thêm
          </Button>
        }
      />
      <BangQuanLy
        ds={ds}
        khoa={(n) => n.id}
        goiYTim="Tìm theo tên, email, số điện thoại"
        boLoc={
          <TextField select label="Loại" value={loai} onChange={(e) => setLoai(e.target.value as LoaiNhanVien | '')} sx={{ minWidth: 180 }}>
            <MenuItem value="">Tất cả</MenuItem>
            {Object.entries(TEN_LOAI_NV).map(([k, v]) => (
              <MenuItem key={k} value={k}>{v}</MenuItem>
            ))}
          </TextField>
        }
        cot={[
          {
            nhan: 'Họ tên',
            hien: (n) => (
              <>
                <b>{n.hocHam ? `${TEN_HOC_HAM[n.hocHam]} ` : ''}{n.hoTen}</b>
                {n.laTruongKhoa && <Chip size="small" color="primary" label={n.loaiNhanVien === 'BAC_SI' ? 'Trưởng khoa' : 'ĐD trưởng'} sx={{ ml: 1 }} />}
              </>
            ),
          },
          { nhan: 'Loại', hien: (n) => TEN_LOAI_NV[n.loaiNhanVien] },
          { nhan: 'Khoa', hien: (n) => n.tenKhoa ?? '—', anDuoi: 'md' },
          { nhan: 'Liên hệ', hien: (n) => `${n.soDienThoai}${n.email ? ` · ${n.email}` : ''}`, anDuoi: 'lg' },
          { nhan: 'Đánh giá', hien: (n) => (n.diemDanhGia != null ? `${n.diemDanhGia} ★` : '—'), anDuoi: 'lg' },
          { nhan: 'Trạng thái', hien: (n) => <ChipDaXoa daXoa={n.daXoa} /> },
        ]}
        thaoTac={[
          { nhan: () => 'Sửa', onChon: moForm },
          {
            nhan: (n) => (n.laTruongKhoa ? 'Bỏ cờ trưởng' : n.loaiNhanVien === 'BAC_SI' ? 'Đặt làm trưởng khoa' : 'Đặt làm điều dưỡng trưởng'),
            an: (n) => n.loaiNhanVien === 'QUAN_TRI' || !n.khoaId || n.daXoa,
            onChon: (n) =>
              chay(() => (n.laTruongKhoa ? nguoiDungApi.boTruongKhoa(n.id) : nguoiDungApi.datTruongKhoa(n.id)), 'Đã cập nhật cờ trưởng'),
          },
          {
            nhan: () => 'Bớt khỏi khoa',
            an: (n) => !n.khoaId || n.daXoa,
            onChon: (n) => chay(() => nguoiDungApi.doiKhoa(n.id, null), 'Đã bớt khỏi khoa'),
          },
          thaoTacXoa<NhanVien>(setXacNhan),
        ]}
      />
      <DialogForm mo={Boolean(form)} tieuDe={form?.nv ? 'Cập nhật nhân viên' : 'Thêm nhân viên'} nutXacNhan="Lưu" onDong={() => setForm(null)} onXacNhan={luu}>
        {g && (
          <Stack spacing={2} sx={{ pt: 1 }}>
            <TextField select label="Loại nhân viên" value={g.loaiNhanVien} disabled={Boolean(form?.nv)} onChange={(e) => sua({ loaiNhanVien: e.target.value as LoaiNhanVien })}>
              {Object.entries(TEN_LOAI_NV).map(([k, v]) => (
                <MenuItem key={k} value={k}>{v}</MenuItem>
              ))}
            </TextField>
            <TextField label="Họ tên" required value={g.hoTen} onChange={(e) => sua({ hoTen: e.target.value })} error={daGui && !g.hoTen.trim()} />
            <TextField label="Số điện thoại" required value={g.soDienThoai} onChange={(e) => sua({ soDienThoai: e.target.value })} error={daGui && !g.soDienThoai.trim()} />
            <TextField label="Email" type="email" value={g.email} onChange={(e) => sua({ email: e.target.value })} />
            {!form?.nv && (
              <TextField
                label="Mật khẩu ban đầu"
                type="password"
                required
                value={g.matKhau}
                onChange={(e) => sua({ matKhau: e.target.value })}
                error={daGui && g.matKhau.length < 6}
                helperText="Tối thiểu 6 ký tự"
              />
            )}
            <Stack direction="row" spacing={2}>
              <TextField select fullWidth label="Giới tính" value={g.gioiTinh} onChange={(e) => sua({ gioiTinh: e.target.value as LuuNhanVien['gioiTinh'] })}>
                {Object.entries(TEN_GIOI_TINH).map(([k, v]) => (
                  <MenuItem key={k} value={k}>{v}</MenuItem>
                ))}
              </TextField>
              <TextField fullWidth label="Ngày sinh" type="date" value={g.ngaySinh} onChange={(e) => sua({ ngaySinh: e.target.value })} slotProps={{ inputLabel: { shrink: true } }} />
            </Stack>
            <TextField label="Số CCCD" value={g.soCccd} onChange={(e) => sua({ soCccd: e.target.value })} />
            {g.loaiNhanVien !== 'QUAN_TRI' && (
              <TextField select label="Khoa" value={g.khoaId} onChange={(e) => sua({ khoaId: e.target.value === '' ? '' : Number(e.target.value) })}>
                <MenuItem value="">Chưa thuộc khoa</MenuItem>
                {khoa.map((k) => (
                  <MenuItem key={k.id} value={k.id}>{k.tenKhoa}</MenuItem>
                ))}
              </TextField>
            )}
            {g.loaiNhanVien === 'BAC_SI' && (
              <TextField select label="Học hàm" value={g.hocHam} onChange={(e) => sua({ hocHam: e.target.value as LuuNhanVien['hocHam'] })}>
                <MenuItem value="">Không</MenuItem>
                {Object.entries(TEN_HOC_HAM).map(([k, v]) => (
                  <MenuItem key={k} value={k}>{v}</MenuItem>
                ))}
              </TextField>
            )}
          </Stack>
        )}
      </DialogForm>
      <DialogConfirm
        mo={Boolean(xacNhan)}
        tieuDe={xacNhan?.daXoa ? 'Khôi phục nhân viên' : 'Xóa nhân viên'}
        noiDung={`${xacNhan?.daXoa ? 'Khôi phục' : 'Xóa'} nhân viên "${xacNhan?.hoTen}"? Tài khoản đăng nhập đi kèm cũng đổi theo.`}
        mauNguyHiem={!xacNhan?.daXoa}
        onDong={() => setXacNhan(null)}
        onXacNhan={() => {
          const nv = xacNhan
          setXacNhan(null)
          if (nv) chay(() => (nv.daXoa ? nguoiDungApi.khoiPhucNhanVien(nv.id) : nguoiDungApi.xoaNhanVien(nv.id)), nv.daXoa ? 'Đã khôi phục' : 'Đã xóa')
        }}
      />
    </>
  )
}
