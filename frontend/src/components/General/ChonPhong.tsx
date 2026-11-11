import ListSubheader from '@mui/material/ListSubheader'
import MenuItem from '@mui/material/MenuItem'
import TextField from '@mui/material/TextField'
import { useEffect, useState } from 'react'
import { danhMucApi, type LoaiPhong, type Phong } from '../../services/danhMuc'
import { lichKhamApi } from '../../services/lichKham'

const KHOA = 'thehos_phong_lam_viec'

/**
 * Chọn phòng làm việc của điều dưỡng (BR-08, BR-13): phòng được phân công hôm nay lên đầu;
 * vẫn chọn được phòng khác vì hệ thống không chặn theo phân công.
 */
export default function ChonPhong({ loai, onChon }: { loai: LoaiPhong; onChon: (p: Phong | null) => void }) {
  const [tatCa, setTatCa] = useState<Phong[]>([])
  const [phanCong, setPhanCong] = useState<number[]>([])
  const [id, setId] = useState<number | ''>('')

  useEffect(() => {
    Promise.all([
      danhMucApi.phong(undefined, loai),
      loai === 'PHONG_KHAM' ? lichKhamApi.phongHomNay().catch(() => []) : Promise.resolve([]),
    ])
      .then(([ds, homNay]) => {
        setTatCa(ds)
        const ids = homNay.map((p) => p.phongId)
        setPhanCong(ids)
        let nho: number | null = null
        try {
          nho = Number(localStorage.getItem(`${KHOA}_${loai}`)) || null
        } catch {
          // bỏ qua
        }
        const chon = ds.find((p) => p.id === nho) ?? ds.find((p) => ids.includes(p.id)) ?? null
        setId(chon?.id ?? '')
        onChon(chon)
      })
      .catch(() => setTatCa([]))
    // chỉ tải khi đổi loại phòng
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [loai])

  const duocPhan = tatCa.filter((p) => phanCong.includes(p.id))
  const khac = tatCa.filter((p) => !phanCong.includes(p.id))
  const muc = (p: Phong) => (
    <MenuItem key={p.id} value={p.id}>
      {p.tenPhong} · {p.tenKhoa}
    </MenuItem>
  )

  return (
    <TextField
      select
      label="Phòng làm việc"
      value={id}
      sx={{ minWidth: 300 }}
      onChange={(e) => {
        const so = Number(e.target.value)
        setId(so)
        try {
          localStorage.setItem(`${KHOA}_${loai}`, String(so))
        } catch {
          // bỏ qua
        }
        onChon(tatCa.find((p) => p.id === so) ?? null)
      }}
    >
      {duocPhan.length > 0 && <ListSubheader>Được phân công hôm nay</ListSubheader>}
      {duocPhan.map(muc)}
      {duocPhan.length > 0 && khac.length > 0 && <ListSubheader>Phòng khác</ListSubheader>}
      {khac.map(muc)}
    </TextField>
  )
}
