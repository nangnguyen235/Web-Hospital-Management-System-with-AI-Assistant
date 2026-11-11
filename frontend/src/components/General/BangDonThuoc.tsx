import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import Typography from '@mui/material/Typography'
import type { ReactNode } from 'react'
import { dinhDangTien, TEN_DON_VI, type DonViThuoc } from '../../services/danhMuc'
import type { DonThuoc, DongThuoc } from '../../services/hoSoBenhAn'

const cachDung = (d: DongThuoc) =>
  [d.sang && 'sáng', d.trua && 'trưa', d.chieu && 'chiều', d.toi && 'tối'].filter(Boolean).join(', ')

/** Đơn thuốc (BR-18): giá chỉ để tham khảo, không bán. */
export default function BangDonThuoc({ don, thaoTac }: { don: DonThuoc | null; thaoTac?: (d: DongThuoc) => ReactNode }) {
  if (!don || don.thuoc.length === 0) return <Typography color="text.secondary">Chưa kê thuốc.</Typography>
  return (
    <>
      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell>Thuốc</TableCell>
            <TableCell>Số lượng</TableCell>
            <TableCell>Cách dùng</TableCell>
            <TableCell align="right">Thành tiền</TableCell>
            {thaoTac && <TableCell />}
          </TableRow>
        </TableHead>
        <TableBody>
          {don.thuoc.map((d) => (
            <TableRow key={d.id}>
              <TableCell>{d.tenThuoc}</TableCell>
              <TableCell>
                {d.soLuong} {TEN_DON_VI[d.donVi as DonViThuoc] ?? d.donVi}
              </TableCell>
              <TableCell>
                {d.soLuongMoiLan} × {d.soLanMoiNgay} lần/ngày ({cachDung(d) || 'theo chỉ dẫn'}) × {d.soNgay} ngày
                {d.huongDan && (
                  <Typography variant="caption" color="text.secondary" sx={{ display: 'block' }}>
                    {d.huongDan}
                  </Typography>
                )}
              </TableCell>
              <TableCell align="right">{dinhDangTien(d.thanhTien)}</TableCell>
              {thaoTac && <TableCell align="right">{thaoTac(d)}</TableCell>}
            </TableRow>
          ))}
        </TableBody>
      </Table>
      <Typography sx={{ textAlign: 'right', mt: 1, fontWeight: 600 }}>
        Mã đơn {don.maDonThuoc} · Tổng tham khảo: {dinhDangTien(don.tongTien)}
      </Typography>
    </>
  )
}
