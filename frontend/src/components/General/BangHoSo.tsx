import Chip from '@mui/material/Chip'
import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import type { ReactNode } from 'react'
import { hienNgay } from '../../services/lichKham'
import { TEN_GIOI_TINH, type HoSo } from '../../services/nguoiDung'

/** Bảng hồ sơ gọn dùng trong chi tiết tài khoản/bệnh nhân (Admin) và tra cứu (điều dưỡng). */
export default function BangHoSo({ hoSo, thaoTac }: { hoSo: HoSo[]; thaoTac?: (h: HoSo) => ReactNode }) {
  return (
    <Table size="small">
      <TableHead>
        <TableRow>
          <TableCell>Họ tên</TableCell>
          <TableCell>Ngày sinh</TableCell>
          <TableCell>Giới tính</TableCell>
          <TableCell>CCCD</TableCell>
          <TableCell>Mã BN</TableCell>
          {thaoTac && <TableCell />}
        </TableRow>
      </TableHead>
      <TableBody>
        {hoSo.length === 0 && (
          <TableRow>
            <TableCell colSpan={6} sx={{ textAlign: 'center', color: 'text.secondary' }}>
              Không có hồ sơ.
            </TableCell>
          </TableRow>
        )}
        {hoSo.map((h) => (
          <TableRow key={h.id}>
            <TableCell>
              {h.hoTen} {h.daXoa && <Chip size="small" label="Đã xóa" variant="outlined" />}
            </TableCell>
            <TableCell>{hienNgay(h.ngaySinh)}</TableCell>
            <TableCell>{TEN_GIOI_TINH[h.gioiTinh]}</TableCell>
            <TableCell>{h.soCccd ?? '—'}</TableCell>
            <TableCell>{h.maBenhNhan ?? '—'}</TableCell>
            {thaoTac && <TableCell align="right">{thaoTac(h)}</TableCell>}
          </TableRow>
        ))}
      </TableBody>
    </Table>
  )
}
