import Chip from '@mui/material/Chip'
import Paper from '@mui/material/Paper'
import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableContainer from '@mui/material/TableContainer'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import type { ReactNode } from 'react'
import { hienNgay, TEN_BUOI, tenThu, type Lich } from '../../services/lichKham'
import Loading from './Loading'

/** Bảng lịch khám bác sĩ dùng chung: lịch trực bác sĩ, lịch khoa (trưởng khoa), lịch khám (Admin). */
export default function BangLich({ lich, thaoTac, anBacSi }: { lich: Lich[] | null; thaoTac?: (l: Lich) => ReactNode; anBacSi?: boolean }) {
  if (!lich) return <Loading />
  return (
    <TableContainer component={Paper} elevation={2}>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Ngày</TableCell>
            <TableCell>Buổi</TableCell>
            {!anBacSi && <TableCell>Bác sĩ</TableCell>}
            <TableCell>Phòng</TableCell>
            <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>Dịch vụ</TableCell>
            <TableCell>Đã đặt</TableCell>
            {thaoTac && <TableCell />}
          </TableRow>
        </TableHead>
        <TableBody>
          {lich.length === 0 && (
            <TableRow>
              <TableCell colSpan={7} sx={{ textAlign: 'center', py: 5, color: 'text.secondary' }}>
                Không có lịch khám.
              </TableCell>
            </TableRow>
          )}
          {lich.map((l) => (
            <TableRow key={l.id} hover>
              <TableCell>
                {tenThu(l.thuTrongTuan)}, {hienNgay(l.ngay)}
              </TableCell>
              <TableCell>
                <Chip size="small" label={TEN_BUOI[l.buoi]} color={l.buoi === 'SANG' ? 'info' : 'warning'} />
              </TableCell>
              {!anBacSi && <TableCell>{l.tenBacSi}</TableCell>}
              <TableCell>{l.tenPhong}</TableCell>
              <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>{l.tenDichVu}</TableCell>
              <TableCell>
                {l.soDaDat}/{l.soLuot}
              </TableCell>
              {thaoTac && <TableCell align="right">{thaoTac(l)}</TableCell>}
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  )
}
