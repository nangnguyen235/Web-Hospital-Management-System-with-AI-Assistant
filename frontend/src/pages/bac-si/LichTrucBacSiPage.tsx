import Stack from '@mui/material/Stack'
import TextField from '@mui/material/TextField'
import { useSnackbar } from 'notistack'
import { useEffect, useState } from 'react'
import BangLich from '../../components/General/BangLich'
import PageHeader from '../../components/General/PageHeader'
import { layThongBaoLoi } from '../../services/api'
import { congNgay, lichKhamApi, ngayISO, type Lich } from '../../services/lichKham'

/** FR-BS-02: thời khóa biểu của bác sĩ theo khoảng ngày (mặc định 4 tuần tới). */
export default function LichTrucBacSiPage() {
  const { enqueueSnackbar } = useSnackbar()
  const homNay = ngayISO(new Date())
  const [tu, setTu] = useState(homNay)
  const [den, setDen] = useState(congNgay(homNay, 27))
  const [lich, setLich] = useState<Lich[] | null>(null)

  useEffect(() => {
    lichKhamApi
      .lichCuaToi({ tu, den })
      .then(setLich)
      .catch((e) => {
        setLich([])
        enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' })
      })
  }, [tu, den, enqueueSnackbar])

  return (
    <>
      <PageHeader
        tieuDe="Lịch trực"
        hanhDong={
          <Stack direction="row" spacing={2}>
            <TextField size="small" label="Từ ngày" type="date" value={tu} onChange={(e) => setTu(e.target.value)} slotProps={{ inputLabel: { shrink: true } }} />
            <TextField size="small" label="Đến ngày" type="date" value={den} onChange={(e) => setDen(e.target.value)} slotProps={{ inputLabel: { shrink: true } }} />
          </Stack>
        }
      />
      <BangLich lich={lich} anBacSi />
    </>
  )
}
