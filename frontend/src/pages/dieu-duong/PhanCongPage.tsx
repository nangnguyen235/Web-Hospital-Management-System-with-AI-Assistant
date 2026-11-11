import AddRoundedIcon from '@mui/icons-material/AddRounded'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Checkbox from '@mui/material/Checkbox'
import MenuItem from '@mui/material/MenuItem'
import Paper from '@mui/material/Paper'
import Stack from '@mui/material/Stack'
import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableContainer from '@mui/material/TableContainer'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { useSnackbar } from 'notistack'
import { useCallback, useEffect, useState } from 'react'
import DialogConfirm from '../../components/General/DialogConfirm'
import DialogForm from '../../components/General/DialogForm'
import Loading from '../../components/General/Loading'
import PageHeader from '../../components/General/PageHeader'
import { layThongBaoLoi } from '../../services/api'
import { danhMucApi, type Phong } from '../../services/danhMuc'
import { lichKhamApi, TEN_BUOI, tenThu, type Buoi, type LichDieuDuong } from '../../services/lichKham'
import { nguoiDungApi, type NhanVienGon } from '../../services/nguoiDung'
import { GRADIENT } from '../../theme'

const THU = [1, 2, 3, 4, 5, 6, 7]
const BUOI: Buoi[] = ['SANG', 'CHIEU']

/** FR-DD-12, BR-08: điều dưỡng trưởng phân công điều dưỡng của khoa vào phòng theo thứ + buổi; xóa một hoặc nhiều. */
export default function PhanCongPage() {
  const { enqueueSnackbar } = useSnackbar()
  const [ds, setDs] = useState<LichDieuDuong[] | null>(null)
  const [dieuDuong, setDieuDuong] = useState<NhanVienGon[]>([])
  const [phong, setPhong] = useState<Phong[]>([])
  const [chon, setChon] = useState<number[]>([])
  const [form, setForm] = useState<{ dieuDuongId: number | ''; phongId: number | ''; ca: string[] } | null>(null)
  const [xoa, setXoa] = useState(false)

  const loi = useCallback((e: unknown) => enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' }), [enqueueSnackbar])
  const tai = useCallback(() => {
    lichKhamApi.phanCongKhoa().then(setDs).catch(loi)
  }, [loi])
  useEffect(tai, [tai])

  useEffect(() => {
    nguoiDungApi.nhanVienKhoaToi('DIEU_DUONG').then(setDieuDuong).catch(() => undefined)
    nguoiDungApi
      .toi()
      .then((t) => (t.khoaId ? danhMucApi.phong(t.khoaId).then(setPhong) : undefined))
      .catch(() => undefined)
  }, [])

  const luu = async () => {
    if (!form || form.dieuDuongId === '' || form.phongId === '' || form.ca.length === 0) {
      return enqueueSnackbar('Chọn điều dưỡng, phòng và ít nhất một buổi', { variant: 'warning' })
    }
    try {
      await lichKhamApi.phanCong({
        dieuDuongId: form.dieuDuongId,
        phongId: form.phongId,
        cacCa: form.ca.map((c) => {
          const [thu, buoi] = c.split('-')
          return { thuTrongTuan: Number(thu), buoi: buoi as Buoi }
        }),
      })
      enqueueSnackbar('Đã phân công', { variant: 'success' })
      setForm(null)
      tai()
    } catch (e) {
      loi(e)
    }
  }

  const doiCa = (c: string) => form && setForm({ ...form, ca: form.ca.includes(c) ? form.ca.filter((x) => x !== c) : [...form.ca, c] })

  return (
    <>
      <PageHeader
        tieuDe="Phân công điều dưỡng"
        hanhDong={
          <Stack direction="row" spacing={1}>
            <Button color="error" disabled={chon.length === 0} onClick={() => setXoa(true)}>
              Xóa {chon.length > 0 && `(${chon.length})`}
            </Button>
            <Button variant="contained" startIcon={<AddRoundedIcon />} sx={{ background: GRADIENT.nut, fontWeight: 700 }} onClick={() => setForm({ dieuDuongId: '', phongId: '', ca: [] })}>
              Phân công
            </Button>
          </Stack>
        }
      />
      {!ds ? (
        <Loading />
      ) : (
        <TableContainer component={Paper} elevation={2}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell padding="checkbox">
                  <Checkbox
                    checked={ds.length > 0 && chon.length === ds.length}
                    indeterminate={chon.length > 0 && chon.length < ds.length}
                    onChange={(e) => setChon(e.target.checked ? ds.map((d) => d.id) : [])}
                  />
                </TableCell>
                <TableCell>Điều dưỡng</TableCell>
                <TableCell>Phòng</TableCell>
                <TableCell>Thứ</TableCell>
                <TableCell>Buổi</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {ds.length === 0 && (
                <TableRow>
                  <TableCell colSpan={5} sx={{ textAlign: 'center', py: 5, color: 'text.secondary' }}>
                    Chưa có phân công.
                  </TableCell>
                </TableRow>
              )}
              {[...ds]
                .sort((a, b) => a.thuTrongTuan - b.thuTrongTuan || a.buoi.localeCompare(b.buoi))
                .map((d) => (
                  <TableRow key={d.id} hover>
                    <TableCell padding="checkbox">
                      <Checkbox checked={chon.includes(d.id)} onChange={() => setChon(chon.includes(d.id) ? chon.filter((x) => x !== d.id) : [...chon, d.id])} />
                    </TableCell>
                    <TableCell>{d.tenDieuDuong}</TableCell>
                    <TableCell>{d.tenPhong}</TableCell>
                    <TableCell>{tenThu(d.thuTrongTuan)}</TableCell>
                    <TableCell>{TEN_BUOI[d.buoi]}</TableCell>
                  </TableRow>
                ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}
      <DialogForm mo={Boolean(form)} tieuDe="Phân công điều dưỡng" nutXacNhan="Lưu" onDong={() => setForm(null)} onXacNhan={luu}>
        {form && (
          <Stack spacing={2} sx={{ pt: 1 }}>
            <TextField select label="Điều dưỡng" value={form.dieuDuongId} onChange={(e) => setForm({ ...form, dieuDuongId: Number(e.target.value) })}>
              {dieuDuong.map((d) => (
                <MenuItem key={d.id} value={d.id}>{d.hoTen}</MenuItem>
              ))}
            </TextField>
            <TextField select label="Phòng" value={form.phongId} onChange={(e) => setForm({ ...form, phongId: Number(e.target.value) })}>
              {phong.map((p) => (
                <MenuItem key={p.id} value={p.id}>{p.tenPhong}</MenuItem>
              ))}
            </TextField>
            <Typography sx={{ fontWeight: 600 }}>Thứ và buổi</Typography>
            <Box sx={{ overflowX: 'auto' }}>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell />
                    {THU.map((t) => (
                      <TableCell key={t} align="center">{tenThu(t)}</TableCell>
                    ))}
                  </TableRow>
                </TableHead>
                <TableBody>
                  {BUOI.map((b) => (
                    <TableRow key={b}>
                      <TableCell>{TEN_BUOI[b]}</TableCell>
                      {THU.map((t) => (
                        <TableCell key={t} align="center" padding="checkbox">
                          <Checkbox checked={form.ca.includes(`${t}-${b}`)} onChange={() => doiCa(`${t}-${b}`)} />
                        </TableCell>
                      ))}
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </Box>
          </Stack>
        )}
      </DialogForm>
      <DialogConfirm
        mo={xoa}
        tieuDe="Xóa phân công"
        noiDung={`Xóa ${chon.length} dòng phân công đã chọn?`}
        mauNguyHiem
        onDong={() => setXoa(false)}
        onXacNhan={async () => {
          setXoa(false)
          try {
            await lichKhamApi.xoaPhanCong(chon)
            setChon([])
            tai()
          } catch (e) {
            loi(e)
          }
        }}
      />
    </>
  )
}
