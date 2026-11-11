import Box from '@mui/material/Box'
import Container from '@mui/material/Container'
import Typography from '@mui/material/Typography'
import { useNavigate } from 'react-router-dom'
import DanhSachBacSi from '../../components/General/DanhSachBacSi'
import { MAU } from '../../theme'
import { ghiDon } from '../benh-nhan/datKham'

/** FR-BN-07: tìm bác sĩ công khai; bấm Đặt khám thì vào luồng đặt khám với bác sĩ đã chọn. */
export default function TimBacSiPage() {
  const navigate = useNavigate()
  return (
    <Box sx={{ bgcolor: MAU.nenDatKham, py: 5, minHeight: '70vh' }}>
      <Container maxWidth="lg">
        <Typography variant="h4" sx={{ fontWeight: 700, color: MAU.chinh, textAlign: 'center', mb: 4 }}>
          Tìm bác sĩ
        </Typography>
        <DanhSachBacSi
          onChon={(b) => {
            ghiDon({ bacSi: b })
            navigate('/dat-kham/chon-ho-so')
          }}
        />
      </Container>
    </Box>
  )
}
