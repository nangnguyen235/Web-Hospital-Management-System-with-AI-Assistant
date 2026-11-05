import Box from '@mui/material/Box'
import CircularProgress from '@mui/material/CircularProgress'

export default function Loading({ chieuCao = 240 }: { chieuCao?: number }) {
  return (
    <Box sx={{ minHeight: chieuCao, display: 'grid', placeItems: 'center' }}>
      <CircularProgress />
    </Box>
  )
}
