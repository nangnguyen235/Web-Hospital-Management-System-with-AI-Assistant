import Button from '@mui/material/Button'
import Dialog from '@mui/material/Dialog'
import DialogActions from '@mui/material/DialogActions'
import DialogContent from '@mui/material/DialogContent'
import DialogTitle from '@mui/material/DialogTitle'
import MenuItem from '@mui/material/MenuItem'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { useSnackbar } from 'notistack'
import { useEffect, useState } from 'react'
import { TEN_VAI_TRO } from '../../auth/jwt'
import BangHoSo from '../../components/General/BangHoSo'
import BangQuanLy from '../../components/General/BangQuanLy'
import ChipDaXoa from '../../components/General/ChipDaXoa'
import { thaoTacXoa, useDanhSachPhanTrang } from '../../hooks/useDanhSachPhanTrang'
import DialogConfirm from '../../components/General/DialogConfirm'
import PageHeader from '../../components/General/PageHeader'
import { layThongBaoLoi } from '../../services/api'
import { hienNgay } from '../../services/lichKham'
import { nguoiDungApi, type HoSo, type TaiKhoanAdmin } from '../../services/nguoiDung'

/** FR-AD-02: lọc tài khoản bệnh nhân / nhân viên, xem chi tiết (hồ sơ), xóa/khôi phục. */
export default function TaiKhoanPage() {
  const { enqueueSnackbar } = useSnackbar()
  const [loai, setLoai] = useState('')
  const ds = useDanhSachPhanTrang((tk, p, n) => nguoiDungApi.timTaiKhoan(tk, p, n, loai || undefined))
  const [chiTiet, setChiTiet] = useState<{ taiKhoan: TaiKhoanAdmin; hoSo: HoSo[] } | null>(null)
  const [xacNhan, setXacNhan] = useState<TaiKhoanAdmin | null>(null)
  const { lamMoi } = ds

  useEffect(() => lamMoi(), [loai, lamMoi])

  const xem = (t: TaiKhoanAdmin) =>
    nguoiDungApi.chiTietTaiKhoan(t.id).then(setChiTiet).catch((e) => enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' }))

  const doiTrangThai = async () => {
    const t = xacNhan
    setXacNhan(null)
    if (!t) return
    try {
      if (t.daXoa) await nguoiDungApi.khoiPhucTaiKhoan(t.id)
      else await nguoiDungApi.xoaTaiKhoan(t.id)
      lamMoi()
    } catch (e) {
      enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' })
    }
  }

  return (
    <>
      <PageHeader tieuDe="Quản lý tài khoản" />
      <BangQuanLy
        ds={ds}
        khoa={(t) => t.id}
        goiYTim="Tìm theo tên, email, số điện thoại"
        boLoc={
          <TextField select label="Loại" value={loai} onChange={(e) => setLoai(e.target.value)} sx={{ minWidth: 180 }}>
            <MenuItem value="">Tất cả</MenuItem>
            <MenuItem value="BENH_NHAN">Bệnh nhân</MenuItem>
            <MenuItem value="NHAN_VIEN">Nhân viên</MenuItem>
          </TextField>
        }
        cot={[
          { nhan: 'Họ tên', hien: (t) => <b>{t.hoTen}</b> },
          { nhan: 'Vai trò', hien: (t) => TEN_VAI_TRO[t.vaiTro] },
          { nhan: 'Email', hien: (t) => t.email ?? '—', anDuoi: 'md' },
          { nhan: 'Số điện thoại', hien: (t) => t.soDienThoai, anDuoi: 'md' },
          { nhan: 'Số hồ sơ', hien: (t) => t.soLuongHoSo, anDuoi: 'lg' },
          { nhan: 'Ngày tạo', hien: (t) => hienNgay(t.ngayTao), anDuoi: 'lg' },
          { nhan: 'Trạng thái', hien: (t) => <ChipDaXoa daXoa={t.daXoa} /> },
        ]}
        thaoTac={[{ nhan: () => 'Xem chi tiết', onChon: xem }, thaoTacXoa<TaiKhoanAdmin>(setXacNhan)]}
      />
      <Dialog open={Boolean(chiTiet)} onClose={() => setChiTiet(null)} maxWidth="md" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>Tài khoản {chiTiet?.taiKhoan.hoTen}</DialogTitle>
        <DialogContent dividers>
          {chiTiet && (
            <>
              <Typography sx={{ mb: 2 }}>
                {TEN_VAI_TRO[chiTiet.taiKhoan.vaiTro]} · {chiTiet.taiKhoan.email ?? 'không có email'} · {chiTiet.taiKhoan.soDienThoai}
              </Typography>
              <Typography sx={{ fontWeight: 600, mb: 1 }}>Hồ sơ bệnh nhân ({chiTiet.hoSo.length})</Typography>
              <BangHoSo hoSo={chiTiet.hoSo} />
            </>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setChiTiet(null)}>Đóng</Button>
        </DialogActions>
      </Dialog>
      <DialogConfirm
        mo={Boolean(xacNhan)}
        tieuDe={xacNhan?.daXoa ? 'Khôi phục tài khoản' : 'Khóa tài khoản'}
        noiDung={xacNhan?.daXoa ? `Khôi phục tài khoản "${xacNhan.hoTen}"?` : `Xóa tài khoản "${xacNhan?.hoTen}"? Tài khoản sẽ không đăng nhập được.`}
        mauNguyHiem={!xacNhan?.daXoa}
        onDong={() => setXacNhan(null)}
        onXacNhan={doiTrangThai}
      />
    </>
  )
}
