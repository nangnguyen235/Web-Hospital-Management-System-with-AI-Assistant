import MenuItem from '@mui/material/MenuItem'
import Stack from '@mui/material/Stack'
import TextField from '@mui/material/TextField'
import { useEffect, useState } from 'react'
import { danhMucApi, type DanToc } from '../../services/danhMuc'
import { TEN_GIOI_TINH, type HoSo, type LuuHoSo } from '../../services/nguoiDung'
import DialogForm from './DialogForm'

const TRONG: LuuHoSo = { hoTen: '', soDienThoai: '', email: '', gioiTinh: '', soCccd: '', diaChi: '', ngaySinh: '', danTocId: '' }

const tuHoSo = (h: HoSo | null): LuuHoSo =>
  h
    ? {
        hoTen: h.hoTen, soDienThoai: h.soDienThoai ?? '', email: h.email ?? '', gioiTinh: h.gioiTinh, soCccd: h.soCccd ?? '',
        diaChi: h.diaChi ?? '', ngaySinh: h.ngaySinh ?? '', danTocId: h.danTocId ?? '',
      }
    : TRONG

interface Props {
  mo: boolean
  hoSo: HoSo | null
  dangGui?: boolean
  onDong: () => void
  onLuu: (g: LuuHoSo) => void
}

/** Form hồ sơ bệnh nhân (BR-03: bắt buộc họ tên, ngày sinh, giới tính; địa chỉ chữ tự do). */
export default function DialogHoSo(props: Props) {
  // key đổi mỗi lần mở → form khởi tạo lại từ hồ sơ đang sửa
  return <NoiDung key={props.mo ? `mo-${props.hoSo?.id ?? 'moi'}` : 'dong'} {...props} />
}

function NoiDung({ mo, hoSo, dangGui, onDong, onLuu }: Props) {
  const [g, setG] = useState<LuuHoSo>(() => tuHoSo(hoSo))
  const [daGui, setDaGui] = useState(false)
  const [danToc, setDanToc] = useState<DanToc[]>([])
  useEffect(() => {
    danhMucApi.danToc().then(setDanToc).catch(() => undefined)
  }, [])

  const sua = (p: Partial<LuuHoSo>) => setG({ ...g, ...p })
  const thieu = { hoTen: !g.hoTen.trim(), ngaySinh: !g.ngaySinh, gioiTinh: !g.gioiTinh }

  return (
    <DialogForm
      mo={mo}
      tieuDe={hoSo ? 'Cập nhật hồ sơ' : 'Thêm hồ sơ bệnh nhân'}
      nutXacNhan="Lưu"
      dangGui={dangGui}
      onDong={onDong}
      onXacNhan={() => {
        setDaGui(true)
        if (!Object.values(thieu).some(Boolean)) onLuu(g)
      }}
    >
      <Stack spacing={2} sx={{ pt: 1 }}>
        <TextField label="Họ và tên" required value={g.hoTen} onChange={(e) => sua({ hoTen: e.target.value })} error={daGui && thieu.hoTen} />
        <Stack direction="row" spacing={2}>
          <TextField
            fullWidth
            label="Ngày sinh"
            type="date"
            required
            value={g.ngaySinh}
            onChange={(e) => sua({ ngaySinh: e.target.value })}
            error={daGui && thieu.ngaySinh}
            slotProps={{ inputLabel: { shrink: true } }}
          />
          <TextField
            fullWidth
            select
            label="Giới tính"
            required
            value={g.gioiTinh}
            onChange={(e) => sua({ gioiTinh: e.target.value as LuuHoSo['gioiTinh'] })}
            error={daGui && thieu.gioiTinh}
          >
            {Object.entries(TEN_GIOI_TINH).map(([k, v]) => (
              <MenuItem key={k} value={k}>{v}</MenuItem>
            ))}
          </TextField>
        </Stack>
        <Stack direction="row" spacing={2}>
          <TextField fullWidth label="Số điện thoại" value={g.soDienThoai} onChange={(e) => sua({ soDienThoai: e.target.value })} />
          <TextField fullWidth label="Email" type="email" value={g.email} onChange={(e) => sua({ email: e.target.value })} helperText="Dùng để tìm lại hồ sơ" />
        </Stack>
        <Stack direction="row" spacing={2}>
          <TextField fullWidth label="Số CCCD" value={g.soCccd} onChange={(e) => sua({ soCccd: e.target.value })} />
          <TextField fullWidth select label="Dân tộc" value={g.danTocId ?? ''} onChange={(e) => sua({ danTocId: e.target.value === '' ? '' : Number(e.target.value) })}>
            <MenuItem value="">Không chọn</MenuItem>
            {danToc.map((d) => (
              <MenuItem key={d.id} value={d.id}>{d.tenDanToc}</MenuItem>
            ))}
          </TextField>
        </Stack>
        <TextField label="Địa chỉ" value={g.diaChi} onChange={(e) => sua({ diaChi: e.target.value })} />
      </Stack>
    </DialogForm>
  )
}
