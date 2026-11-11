import Chip from '@mui/material/Chip'
import MenuItem from '@mui/material/MenuItem'
import TextField from '@mui/material/TextField'
import { useEffect, useState } from 'react'
import BangQuanLy from '../../components/General/BangQuanLy'
import { useDanhSachPhanTrang } from '../../hooks/useDanhSachPhanTrang'
import PageHeader from '../../components/General/PageHeader'
import { dinhDangTien } from '../../services/danhMuc'
import { hienNgayGio } from '../../services/lichKham'
import {
  MAU_TRANG_THAI_GD,
  TEN_LOAI_TT,
  TEN_TRANG_THAI_GD,
  vienPhiApi,
  type LoaiThanhToan,
  type TrangThaiGiaoDich,
} from '../../services/vienPhi'

/** FR-AD-09: xem giao dịch VNPAY, lọc theo khoảng ngày, trạng thái, loại. */
export default function GiaoDichPage() {
  const [tu, setTu] = useState('')
  const [den, setDen] = useState('')
  const [trangThai, setTrangThai] = useState<TrangThaiGiaoDich | ''>('')
  const [loai, setLoai] = useState<LoaiThanhToan | ''>('')
  const ds = useDanhSachPhanTrang((_, page, pageSize) =>
    vienPhiApi.timKiem({ tu: tu || undefined, den: den || undefined, trangThai: trangThai || undefined, loai: loai || undefined, page, pageSize }),
  )
  const { lamMoi } = ds
  useEffect(() => lamMoi(), [tu, den, trangThai, loai, lamMoi])

  const ngay = (nhan: string, v: string, set: (s: string) => void) => (
    <TextField label={nhan} type="date" value={v} onChange={(e) => set(e.target.value)} slotProps={{ inputLabel: { shrink: true } }} />
  )

  return (
    <>
      <PageHeader tieuDe="Giao dịch" />
      <BangQuanLy
        ds={ds}
        khoa={(g) => g.id}
        boLoc={
          <>
            {ngay('Từ ngày', tu, setTu)}
            {ngay('Đến ngày', den, setDen)}
            <TextField select label="Trạng thái" value={trangThai} onChange={(e) => setTrangThai(e.target.value as TrangThaiGiaoDich | '')} sx={{ minWidth: 160 }}>
              <MenuItem value="">Tất cả</MenuItem>
              {Object.entries(TEN_TRANG_THAI_GD).map(([k, v]) => (
                <MenuItem key={k} value={k}>{v}</MenuItem>
              ))}
            </TextField>
            <TextField select label="Loại" value={loai} onChange={(e) => setLoai(e.target.value as LoaiThanhToan | '')} sx={{ minWidth: 160 }}>
              <MenuItem value="">Tất cả</MenuItem>
              {Object.entries(TEN_LOAI_TT).map(([k, v]) => (
                <MenuItem key={k} value={k}>{v}</MenuItem>
              ))}
            </TextField>
          </>
        }
        cot={[
          { nhan: 'Mã GD', hien: (g) => `#${g.id}` },
          { nhan: 'Loại', hien: (g) => TEN_LOAI_TT[g.loaiThanhToan] },
          { nhan: 'Số tiền', hien: (g) => dinhDangTien(g.soTien) },
          { nhan: 'Phí', hien: (g) => dinhDangTien(g.phi), anDuoi: 'md' },
          { nhan: 'Ngân hàng', hien: (g) => g.maNganHang ?? '—', anDuoi: 'lg' },
          { nhan: 'Thời gian', hien: (g) => hienNgayGio(g.ngayTao), anDuoi: 'md' },
          { nhan: 'Trạng thái', hien: (g) => <Chip size="small" color={MAU_TRANG_THAI_GD[g.trangThai]} label={TEN_TRANG_THAI_GD[g.trangThai]} /> },
        ]}
      />
    </>
  )
}
