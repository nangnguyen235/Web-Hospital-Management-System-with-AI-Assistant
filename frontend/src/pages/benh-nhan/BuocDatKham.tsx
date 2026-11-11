import Box from '@mui/material/Box'
import Container from '@mui/material/Container'
import Step from '@mui/material/Step'
import StepLabel from '@mui/material/StepLabel'
import Stepper from '@mui/material/Stepper'
import Typography from '@mui/material/Typography'
import type { ReactNode } from 'react'
import { MAU } from '../../theme'

const BUOC = ['Chọn hồ sơ', 'Chọn bác sĩ', 'Chọn ngày, giờ', 'Xác nhận, thanh toán']

/** Khung chung cho 4 bước đặt khám kiểu TheDuck: nền xanh nhạt, thanh bước, nội dung. */
export default function BuocDatKham({ buoc, tieuDe, children }: { buoc: number; tieuDe: string; children: ReactNode }) {
  return (
    <Box sx={{ bgcolor: MAU.nenDatKham, py: 4, minHeight: '70vh' }}>
      <Container maxWidth="lg">
        <Stepper activeStep={buoc} alternativeLabel sx={{ mb: 4 }}>
          {BUOC.map((b) => (
            <Step key={b}>
              <StepLabel>{b}</StepLabel>
            </Step>
          ))}
        </Stepper>
        <Typography variant="h5" sx={{ fontWeight: 700, color: MAU.chinh, mb: 3, textAlign: 'center' }}>
          {tieuDe}
        </Typography>
        {children}
      </Container>
    </Box>
  )
}
