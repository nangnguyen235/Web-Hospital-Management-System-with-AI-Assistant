import Button from '@mui/material/Button'
import Chip from '@mui/material/Chip'
import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import { useSnackbar } from 'notistack'
import type { ReactNode } from 'react'
import { layThongBaoLoi } from '../../services/api'
import { dinhDangTien } from '../../services/danhMuc'
import { hoSoBenhAnApi, TEN_TRANG_THAI_XN, type XetNghiem } from '../../services/hoSoBenhAn'

/** Mở file kết quả xét nghiệm qua Presigned URL 15 phút (NFR-04). */
function useXemKetQua() {
  const { enqueueSnackbar } = useSnackbar()
  return async (id: number) => {
    try {
      const url = await hoSoBenhAnApi.urlKetQua(id)
      window.open(url, '_blank', 'noopener')
    } catch (e) {
      enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' })
    }
  }
}

/** Bảng xét nghiệm của một bệnh án (bác sĩ, bệnh nhân). */
export default function BangXetNghiem({ ds, thaoTac }: { ds: XetNghiem[]; thaoTac?: (x: XetNghiem) => ReactNode }) {
  const xemKetQua = useXemKetQua()
  return (
    <Table size="small">
      <TableHead>
        <TableRow>
          <TableCell>Mã</TableCell>
          <TableCell>Dịch vụ</TableCell>
          <TableCell>Phòng · Số</TableCell>
          <TableCell>Giá</TableCell>
          <TableCell>Trạng thái</TableCell>
          <TableCell>Kết quả</TableCell>
          {thaoTac && <TableCell />}
        </TableRow>
      </TableHead>
      <TableBody>
        {ds.length === 0 && (
          <TableRow>
            <TableCell colSpan={7} sx={{ textAlign: 'center', color: 'text.secondary' }}>
              Chưa có xét nghiệm.
            </TableCell>
          </TableRow>
        )}
        {ds.map((x) => (
          <TableRow key={x.id}>
            <TableCell>{x.maXetNghiem}</TableCell>
            <TableCell>{x.tenDichVu}</TableCell>
            <TableCell>
              {x.tenPhong} · {x.soThuTu}
            </TableCell>
            <TableCell>{dinhDangTien(x.gia)}</TableCell>
            <TableCell>
              <Chip size="small" label={TEN_TRANG_THAI_XN[x.trangThai]} color={x.trangThai === 'HOAN_THANH' ? 'success' : 'default'} />{' '}
              {!x.daThanhToan && <Chip size="small" color="warning" label="Chưa thanh toán" />}
            </TableCell>
            <TableCell>
              {x.ketQua ?? (x.trangThai === 'HOAN_THANH' ? '' : '—')}
              {x.coFileKetQua && (
                <Button size="small" onClick={() => xemKetQua(x.id)}>
                  Xem file
                </Button>
              )}
            </TableCell>
            {thaoTac && <TableCell align="right">{thaoTac(x)}</TableCell>}
          </TableRow>
        ))}
      </TableBody>
    </Table>
  )
}
