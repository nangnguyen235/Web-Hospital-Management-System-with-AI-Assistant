import ConstructionRoundedIcon from '@mui/icons-material/ConstructionRounded'
import Chip from '@mui/material/Chip'
import Paper from '@mui/material/Paper'
import Stack from '@mui/material/Stack'
import Typography from '@mui/material/Typography'

interface Props {
  tieuDe: string
  /** Mã FR của trang (tong-quan-du-an.md mục 6). */
  fr: string[]
  moTa?: string
}

/** Trang chưa nối API: giữ route + mã FR, thay bằng trang thật ở sprint sau (frontend.md mục 5). */
export default function PlaceholderPage({ tieuDe, fr, moTa }: Props) {
  return (
    <Paper sx={{ p: { xs: 3, md: 5 }, textAlign: 'center' }} elevation={0} variant="outlined">
      <ConstructionRoundedIcon sx={{ fontSize: 56, color: 'secondary.main', mb: 1 }} />
      <Typography variant="h5" sx={{ fontWeight: 700, mb: 1 }}>
        {tieuDe}
      </Typography>
      <Typography color="text.secondary" sx={{ mb: 2 }}>
        {moTa ?? 'Trang đang được xây dựng — sẽ nối API khi module backend hoàn thành.'}
      </Typography>
      <Stack direction="row" spacing={1} sx={{ justifyContent: 'center', flexWrap: 'wrap' }}>
        {fr.map((ma) => (
          <Chip key={ma} label={ma} color="primary" variant="outlined" size="small" />
        ))}
      </Stack>
    </Paper>
  )
}
