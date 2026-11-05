import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Typography from '@mui/material/Typography'
import { useNavigate } from 'react-router-dom'

export default function NotFoundPage() {
  const navigate = useNavigate()
  return (
    <Box sx={{ minHeight: '100vh', display: 'grid', placeItems: 'center', textAlign: 'center', p: 3 }}>
      <Box>
        <Typography sx={{ fontSize: 96, fontWeight: 900, color: 'primary.main', lineHeight: 1 }}>404</Typography>
        <Typography variant="h5" sx={{ mb: 3 }}>
          Không tìm thấy trang bạn cần.
        </Typography>
        <Button variant="contained" onClick={() => navigate('/')}>
          Về trang chủ
        </Button>
      </Box>
    </Box>
  )
}
