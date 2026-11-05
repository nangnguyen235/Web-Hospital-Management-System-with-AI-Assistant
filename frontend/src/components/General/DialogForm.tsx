import Button from '@mui/material/Button'
import Dialog from '@mui/material/Dialog'
import DialogActions from '@mui/material/DialogActions'
import DialogContent from '@mui/material/DialogContent'
import DialogTitle from '@mui/material/DialogTitle'
import Slide from '@mui/material/Slide'
import type { TransitionProps } from '@mui/material/transitions'
import { forwardRef, type FormEvent, type ReactElement, type ReactNode, type Ref } from 'react'

const TruotLen = forwardRef(function TruotLen(
  props: TransitionProps & { children: ReactElement },
  ref: Ref<unknown>,
) {
  return <Slide direction="up" ref={ref} {...props} />
})

interface Props {
  mo: boolean
  tieuDe: string
  nutXacNhan: string
  dangGui?: boolean
  onDong: () => void
  onXacNhan: () => void
  children: ReactNode
}

/** Hộp thoại thêm/sửa kiểu TheDuck: trượt lên, nút Hủy + nút xác nhận. */
export default function DialogForm({ mo, tieuDe, nutXacNhan, dangGui, onDong, onXacNhan, children }: Props) {
  const gui = (e: FormEvent) => {
    e.preventDefault()
    onXacNhan()
  }
  return (
    <Dialog open={mo} onClose={onDong} fullWidth maxWidth="sm" slots={{ transition: TruotLen }}>
      <form onSubmit={gui} noValidate>
        <DialogTitle sx={{ fontWeight: 700 }}>{tieuDe}</DialogTitle>
        <DialogContent dividers>{children}</DialogContent>
        <DialogActions sx={{ px: 3, py: 2 }}>
          <Button onClick={onDong} color="inherit">
            Hủy
          </Button>
          <Button type="submit" variant="contained" disabled={dangGui}>
            {nutXacNhan}
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  )
}
