import MenuItem from '@mui/material/MenuItem'
import TextField from '@mui/material/TextField'
import { useSnackbar } from 'notistack'
import { useEffect, useState } from 'react'
import { layThongBaoLoi } from '../../services/api'
import { nguoiDungApi, type HoSo } from '../../services/nguoiDung'

const KHOA = 'thehos_ho_so_dang_chon'

/** Ô chọn hồ sơ bệnh nhân của tài khoản; nhớ hồ sơ vừa chọn giữa các trang (sessionStorage). */
export default function ChonHoSo({ onChon }: { onChon: (hoSo: HoSo | null) => void }) {
  const { enqueueSnackbar } = useSnackbar()
  const [ds, setDs] = useState<HoSo[] | null>(null)
  const [id, setId] = useState<number | ''>('')

  useEffect(() => {
    nguoiDungApi
      .hoSo()
      .then((d) => {
        setDs(d)
        let nho: number | null = null
        try {
          nho = Number(sessionStorage.getItem(KHOA)) || null
        } catch {
          // bỏ qua
        }
        const chon = d.find((h) => h.id === nho) ?? d[0] ?? null
        setId(chon?.id ?? '')
        onChon(chon)
      })
      .catch((e) => enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' }))
    // chỉ tải một lần khi mở trang
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  return (
    <TextField
      select
      label="Hồ sơ bệnh nhân"
      value={id}
      sx={{ minWidth: 280, mb: 2 }}
      disabled={!ds || ds.length === 0}
      helperText={ds && ds.length === 0 ? 'Chưa có hồ sơ — thêm ở mục Hồ sơ bệnh nhân' : ' '}
      onChange={(e) => {
        const so = Number(e.target.value)
        setId(so)
        try {
          sessionStorage.setItem(KHOA, String(so))
        } catch {
          // bỏ qua
        }
        onChon(ds?.find((h) => h.id === so) ?? null)
      }}
    >
      {ds?.map((h) => (
        <MenuItem key={h.id} value={h.id}>
          {h.hoTen}
        </MenuItem>
      ))}
    </TextField>
  )
}
