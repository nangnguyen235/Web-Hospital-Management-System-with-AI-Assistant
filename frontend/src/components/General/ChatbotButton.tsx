import SmartToyRoundedIcon from '@mui/icons-material/SmartToyRounded'
import Dialog from '@mui/material/Dialog'
import DialogContent from '@mui/material/DialogContent'
import DialogTitle from '@mui/material/DialogTitle'
import Fab from '@mui/material/Fab'
import Tooltip from '@mui/material/Tooltip'
import Typography from '@mui/material/Typography'
import { useState } from 'react'
import { GRADIENT } from '../../theme'

/** Nút nổi mở chatbot AI 1 (FR-BN-17) — đợt này chỉ là khung, nối module TroLyAi ở sprint sau. */
export default function ChatbotButton() {
  const [mo, setMo] = useState(false)
  return (
    <>
      <Tooltip title="Trợ lý AI tư vấn & đặt khám">
        <Fab
          onClick={() => setMo(true)}
          sx={{ position: 'fixed', right: 24, bottom: 24, color: '#fff', background: GRADIENT.dangNhap }}
          aria-label="Mở trợ lý AI"
        >
          <SmartToyRoundedIcon />
        </Fab>
      </Tooltip>
      <Dialog open={mo} onClose={() => setMo(false)} maxWidth="xs" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>Trợ lý AI TheHos</DialogTitle>
        <DialogContent>
          <Typography>
            Mô tả triệu chứng để được gợi ý chuyên khoa, bác sĩ và khung giờ còn chỗ, rồi đặt khám ngay trong khung chat.
          </Typography>
          <Typography color="text.secondary" sx={{ mt: 2 }}>
            Tính năng đang được xây dựng (FR-BN-17).
          </Typography>
        </DialogContent>
      </Dialog>
    </>
  )
}
