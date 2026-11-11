import Button from '@mui/material/Button'
import Dialog from '@mui/material/Dialog'
import DialogActions from '@mui/material/DialogActions'
import DialogContent from '@mui/material/DialogContent'
import DialogContentText from '@mui/material/DialogContentText'
import DialogTitle from '@mui/material/DialogTitle'
import TextField from '@mui/material/TextField'
import { useState, type FormEvent } from 'react'

interface Props {
  mo: boolean
  email?: string | null
  dangGui?: boolean
  onDong: () => void
  onXacNhan: (otp: string) => void
  onGuiLai?: () => void
}

/** Nhập OTP 6 số gửi qua email (BR-02: hạn 5 phút, sai 5 lần phải xin mã mới). */
export default function DialogOtp({ mo, email, dangGui, onDong, onXacNhan, onGuiLai }: Props) {
  const [otp, setOtp] = useState('')
  const hopLe = /^\d{6}$/.test(otp)
  const gui = (e: FormEvent) => {
    e.preventDefault()
    if (hopLe) onXacNhan(otp)
  }
  return (
    <Dialog open={mo} onClose={onDong} maxWidth="xs" fullWidth>
      <form onSubmit={gui} noValidate>
        <DialogTitle sx={{ fontWeight: 700 }}>Nhập mã OTP</DialogTitle>
        <DialogContent>
          <DialogContentText sx={{ mb: 2 }}>
            Mã gồm 6 số đã gửi tới email {email ?? 'của bạn'}, có hiệu lực trong 5 phút.
          </DialogContentText>
          <TextField
            autoFocus
            fullWidth
            label="Mã OTP"
            value={otp}
            onChange={(e) => setOtp(e.target.value.replace(/\D/g, '').slice(0, 6))}
            slotProps={{ htmlInput: { inputMode: 'numeric', autoComplete: 'one-time-code', style: { letterSpacing: 8, fontSize: 22 } } }}
          />
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          {onGuiLai && (
            <Button onClick={onGuiLai} sx={{ mr: 'auto' }}>
              Gửi lại mã
            </Button>
          )}
          <Button onClick={onDong} color="inherit">
            Hủy
          </Button>
          <Button type="submit" variant="contained" disabled={!hopLe || dangGui}>
            Xác nhận
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  )
}
