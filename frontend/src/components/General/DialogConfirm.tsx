import Button from '@mui/material/Button'
import Dialog from '@mui/material/Dialog'
import DialogActions from '@mui/material/DialogActions'
import DialogContent from '@mui/material/DialogContent'
import DialogContentText from '@mui/material/DialogContentText'
import DialogTitle from '@mui/material/DialogTitle'

interface Props {
  mo: boolean
  tieuDe: string
  noiDung: string
  nutXacNhan?: string
  mauNguyHiem?: boolean
  onDong: () => void
  onXacNhan: () => void
}

export default function DialogConfirm({ mo, tieuDe, noiDung, nutXacNhan = 'Đồng ý', mauNguyHiem, onDong, onXacNhan }: Props) {
  return (
    <Dialog open={mo} onClose={onDong} maxWidth="xs" fullWidth>
      <DialogTitle sx={{ fontWeight: 700 }}>{tieuDe}</DialogTitle>
      <DialogContent>
        <DialogContentText>{noiDung}</DialogContentText>
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={onDong} color="inherit">
          Hủy
        </Button>
        <Button onClick={onXacNhan} variant="contained" color={mauNguyHiem ? 'error' : 'primary'}>
          {nutXacNhan}
        </Button>
      </DialogActions>
    </Dialog>
  )
}
