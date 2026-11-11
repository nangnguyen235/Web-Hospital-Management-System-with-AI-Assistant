import Button from '@mui/material/Button'
import Dialog from '@mui/material/Dialog'
import DialogActions from '@mui/material/DialogActions'
import DialogContent from '@mui/material/DialogContent'
import DialogTitle from '@mui/material/DialogTitle'
import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import Typography from '@mui/material/Typography'
import { useSnackbar } from 'notistack'
import { useState } from 'react'
import BangHoSo from '../../components/General/BangHoSo'
import BangQuanLy from '../../components/General/BangQuanLy'
import { useDanhSachPhanTrang } from '../../hooks/useDanhSachPhanTrang'
import PageHeader from '../../components/General/PageHeader'
import { layThongBaoLoi } from '../../services/api'
import { TEN_TRANG_THAI_BENH_AN, type TrangThaiBenhAn } from '../../services/hoSoBenhAn'
import { hienNgay } from '../../services/lichKham'
import { nguoiDungApi, TEN_GIOI_TINH, type BenhNhanAdmin, type HoSo } from '../../services/nguoiDung'
import { thongKeApi, type LichSuKhamAdmin } from '../../services/thongKe'

interface ChiTiet {
  benhNhan: BenhNhanAdmin
  hoSo: HoSo[]
  lichSu: LichSuKhamAdmin[]
}

/** FR-AD-08: danh sách bệnh nhân, chi tiết (hồ sơ liên kết, lịch sử khám), xóa/khôi phục hồ sơ. */
export default function BenhNhanPage() {
  const { enqueueSnackbar } = useSnackbar()
  const ds = useDanhSachPhanTrang(nguoiDungApi.timBenhNhan)
  const [chiTiet, setChiTiet] = useState<ChiTiet | null>(null)

  const xem = async (b: BenhNhanAdmin) => {
    try {
      const [ct, lichSu] = await Promise.all([nguoiDungApi.chiTietBenhNhan(b.id), thongKeApi.lichSuBenhNhan(b.id)])
      setChiTiet({ ...ct, lichSu })
    } catch (e) {
      enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' })
    }
  }

  const doiHoSo = async (h: HoSo) => {
    if (!chiTiet) return
    try {
      if (h.daXoa) await nguoiDungApi.khoiPhucHoSoAdmin(h.id)
      else await nguoiDungApi.xoaHoSoAdmin(h.id)
      await xem(chiTiet.benhNhan)
    } catch (e) {
      enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' })
    }
  }

  return (
    <>
      <PageHeader tieuDe="Quản lý bệnh nhân" />
      <BangQuanLy
        ds={ds}
        khoa={(b) => b.id}
        goiYTim="Tìm theo mã bệnh nhân, họ tên, CCCD"
        cot={[
          { nhan: 'Mã BN', hien: (b) => b.maBenhNhan },
          { nhan: 'Họ tên', hien: (b) => <b>{b.hoTen}</b> },
          { nhan: 'Ngày sinh', hien: (b) => hienNgay(b.ngaySinh), anDuoi: 'md' },
          { nhan: 'Giới tính', hien: (b) => TEN_GIOI_TINH[b.gioiTinh], anDuoi: 'md' },
          { nhan: 'CCCD', hien: (b) => b.soCccd ?? '—', anDuoi: 'lg' },
          { nhan: 'Số điện thoại', hien: (b) => b.soDienThoai ?? '—', anDuoi: 'lg' },
        ]}
        thaoTac={[{ nhan: () => 'Xem chi tiết', onChon: xem }]}
      />
      <Dialog open={Boolean(chiTiet)} onClose={() => setChiTiet(null)} maxWidth="md" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>
          {chiTiet?.benhNhan.hoTen} · {chiTiet?.benhNhan.maBenhNhan}
        </DialogTitle>
        <DialogContent dividers>
          {chiTiet && (
            <>
              <Typography sx={{ fontWeight: 600, mb: 1 }}>Hồ sơ liên kết</Typography>
              <BangHoSo
                hoSo={chiTiet.hoSo}
                thaoTac={(h) => (
                  <Button size="small" color={h.daXoa ? 'success' : 'error'} onClick={() => doiHoSo(h)}>
                    {h.daXoa ? 'Khôi phục' : 'Xóa'}
                  </Button>
                )}
              />
              <Typography sx={{ fontWeight: 600, mt: 3, mb: 1 }}>Lịch sử khám</Typography>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Ngày</TableCell>
                    <TableCell>Trạng thái</TableCell>
                    <TableCell>Chẩn đoán</TableCell>
                    <TableCell>Tái khám</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {chiTiet.lichSu.length === 0 && (
                    <TableRow>
                      <TableCell colSpan={4} sx={{ textAlign: 'center', color: 'text.secondary' }}>
                        Chưa có lần khám nào.
                      </TableCell>
                    </TableRow>
                  )}
                  {chiTiet.lichSu.map((l) => (
                    <TableRow key={l.benhAnId}>
                      <TableCell>{hienNgay(l.ngayTao)}</TableCell>
                      <TableCell>{TEN_TRANG_THAI_BENH_AN[l.trangThai as TrangThaiBenhAn] ?? l.trangThai}</TableCell>
                      <TableCell>{l.chanDoan ?? '—'}</TableCell>
                      <TableCell>{hienNgay(l.ngayTaiKham)}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setChiTiet(null)}>Đóng</Button>
        </DialogActions>
      </Dialog>
    </>
  )
}
