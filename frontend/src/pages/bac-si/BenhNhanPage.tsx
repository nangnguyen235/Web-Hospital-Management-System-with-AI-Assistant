import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Chip from '@mui/material/Chip'
import MenuItem from '@mui/material/MenuItem'
import Paper from '@mui/material/Paper'
import Tab from '@mui/material/Tab'
import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableContainer from '@mui/material/TableContainer'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import Tabs from '@mui/material/Tabs'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { useSnackbar } from 'notistack'
import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Loading from '../../components/General/Loading'
import PageHeader from '../../components/General/PageHeader'
import { layThongBaoLoi } from '../../services/api'
import { hoSoBenhAnApi, TEN_TRANG_THAI_BENH_AN, type BenhAnGon, type TrangThaiBenhAn } from '../../services/hoSoBenhAn'
import { congNgay, hienNgay, lichKhamApi, ngayISO, TEN_BUOI, type Lich } from '../../services/lichKham'
import { TEN_GIOI_TINH, type GioiTinh } from '../../services/nguoiDung'

/** FR-BS-02 (lịch hôm nay), FR-BS-03 (bệnh án của lịch theo trạng thái, sắp theo số), FR-BS-04 (nhận khám). */
export default function BenhNhanPage() {
  const navigate = useNavigate()
  const { enqueueSnackbar } = useSnackbar()
  const homNay = ngayISO(new Date())
  const [lich, setLich] = useState<Lich[] | null>(null)
  const [lichId, setLichId] = useState<number | ''>('')
  const [trangThai, setTrangThai] = useState<TrangThaiBenhAn>('CHO_KHAM')
  const [ds, setDs] = useState<BenhAnGon[] | null>(null)
  const [taiLai, setTaiLai] = useState(0)

  useEffect(() => {
    // lịch hôm nay và hôm qua (BR-16: được nhận bệnh án của lịch hôm qua)
    lichKhamApi
      .lichCuaToi({ tu: congNgay(homNay, -1), den: homNay })
      .then((d) => {
        setLich(d)
        const uuTien = d.find((l) => l.ngay === homNay) ?? d[0]
        setLichId(uuTien?.id ?? '')
      })
      .catch((e) => enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' }))
  }, [homNay, enqueueSnackbar])

  useEffect(() => {
    if (lichId === '') return
    hoSoBenhAnApi
      .benhAnCuaLich(lichId, trangThai)
      .then((d) => setDs([...d].sort((a, b) => a.soThuTu - b.soThuTu)))
      .catch((e) => enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' }))
  }, [lichId, trangThai, taiLai, enqueueSnackbar])

  const nhanKham = async (b: BenhAnGon) => {
    try {
      await hoSoBenhAnApi.nhanKham(b.id)
      navigate(`/bac-si/benh-an/${b.id}`)
    } catch (e) {
      enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' })
      setTaiLai((x) => x + 1)
    }
  }

  return (
    <>
      <PageHeader tieuDe="Bệnh nhân" />
      {!lich ? (
        <Loading />
      ) : lich.length === 0 ? (
        <Typography color="text.secondary">Hôm nay bạn không có lịch khám.</Typography>
      ) : (
        <>
          <TextField select label="Lịch khám" value={lichId} onChange={(e) => setLichId(Number(e.target.value))} sx={{ minWidth: 360, mb: 2 }}>
            {lich.map((l) => (
              <MenuItem key={l.id} value={l.id}>
                {hienNgay(l.ngay)} · Buổi {TEN_BUOI[l.buoi].toLowerCase()} · Phòng {l.tenPhong} · {l.soDaDat} lượt
              </MenuItem>
            ))}
          </TextField>
          <Tabs value={trangThai} onChange={(_, v) => setTrangThai(v)} sx={{ mb: 2 }}>
            {(Object.keys(TEN_TRANG_THAI_BENH_AN) as TrangThaiBenhAn[]).map((t) => (
              <Tab key={t} value={t} label={TEN_TRANG_THAI_BENH_AN[t]} />
            ))}
          </Tabs>
          {!ds ? (
            <Loading />
          ) : (
            <TableContainer component={Paper} elevation={2}>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell width={80}>Số</TableCell>
                    <TableCell>Họ tên</TableCell>
                    <TableCell>Năm sinh</TableCell>
                    <TableCell>Giới tính</TableCell>
                    <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>Mã BN</TableCell>
                    <TableCell />
                  </TableRow>
                </TableHead>
                <TableBody>
                  {ds.length === 0 && (
                    <TableRow>
                      <TableCell colSpan={6} sx={{ textAlign: 'center', py: 5, color: 'text.secondary' }}>
                        Không có bệnh nhân {TEN_TRANG_THAI_BENH_AN[trangThai].toLowerCase()}.
                      </TableCell>
                    </TableRow>
                  )}
                  {ds.map((b) => (
                    <TableRow key={b.id} hover>
                      <TableCell>
                        <Chip label={b.soThuTu} color="primary" />
                      </TableCell>
                      <TableCell sx={{ fontWeight: 600 }}>{b.hoTen}</TableCell>
                      <TableCell>{b.ngaySinh?.slice(0, 4)}</TableCell>
                      <TableCell>{TEN_GIOI_TINH[b.gioiTinh as GioiTinh] ?? b.gioiTinh}</TableCell>
                      <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>{b.maBenhNhan}</TableCell>
                      <TableCell align="right">
                        <Box sx={{ display: 'flex', gap: 1, justifyContent: 'flex-end' }}>
                          {b.trangThai === 'CHO_KHAM' ? (
                            <Button variant="contained" size="small" onClick={() => nhanKham(b)}>
                              Nhận khám
                            </Button>
                          ) : (
                            <Button variant="outlined" size="small" onClick={() => navigate(`/bac-si/benh-an/${b.id}`)}>
                              Mở bệnh án
                            </Button>
                          )}
                        </Box>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </>
      )}
    </>
  )
}
