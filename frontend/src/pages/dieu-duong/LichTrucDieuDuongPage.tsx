import Paper from '@mui/material/Paper'
import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import Typography from '@mui/material/Typography'
import { useSnackbar } from 'notistack'
import { useEffect, useState } from 'react'
import BangLich from '../../components/General/BangLich'
import Loading from '../../components/General/Loading'
import PageHeader from '../../components/General/PageHeader'
import { layThongBaoLoi } from '../../services/api'
import { lichKhamApi, TEN_BUOI, tenThu, type LichDieuDuong, type PhongHomNay } from '../../services/lichKham'

const THU = [1, 2, 3, 4, 5, 6, 7]

/** FR-DD-02, BR-08: lịch phân công theo thứ + buổi và lịch khám hôm nay của các phòng được phân công. */
export default function LichTrucDieuDuongPage() {
  const { enqueueSnackbar } = useSnackbar()
  const [phanCong, setPhanCong] = useState<LichDieuDuong[] | null>(null)
  const [homNay, setHomNay] = useState<PhongHomNay[] | null>(null)

  useEffect(() => {
    const loi = (e: unknown) => enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' })
    lichKhamApi.phanCongCuaToi().then(setPhanCong).catch(loi)
    lichKhamApi.phongHomNay().then(setHomNay).catch(loi)
  }, [enqueueSnackbar])

  const o = (thu: number, buoi: 'SANG' | 'CHIEU') =>
    phanCong?.filter((p) => p.thuTrongTuan === thu && p.buoi === buoi).map((p) => p.tenPhong).join(', ') || '—'

  return (
    <>
      <PageHeader tieuDe="Lịch trực" />
      <Paper elevation={2} sx={{ p: 2.5, mb: 3, overflowX: 'auto' }}>
        <Typography sx={{ fontWeight: 700, mb: 1 }}>Lịch phân công hằng tuần</Typography>
        {!phanCong ? (
          <Loading chieuCao={120} />
        ) : (
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>Buổi</TableCell>
                {THU.map((t) => (
                  <TableCell key={t}>{tenThu(t)}</TableCell>
                ))}
              </TableRow>
            </TableHead>
            <TableBody>
              {(['SANG', 'CHIEU'] as const).map((b) => (
                <TableRow key={b}>
                  <TableCell sx={{ fontWeight: 600 }}>{TEN_BUOI[b]}</TableCell>
                  {THU.map((t) => (
                    <TableCell key={t}>{o(t, b)}</TableCell>
                  ))}
                </TableRow>
              ))}
            </TableBody>
          </Table>
        )}
      </Paper>
      <Typography variant="h6" sx={{ fontWeight: 700, mb: 1 }}>
        Lịch khám hôm nay của phòng được phân công
      </Typography>
      {!homNay ? <Loading /> : <BangLich lich={homNay.flatMap((p) => p.lich)} />}
    </>
  )
}
