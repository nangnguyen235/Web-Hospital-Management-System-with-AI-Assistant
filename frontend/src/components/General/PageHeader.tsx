import Box from '@mui/material/Box'
import Typography from '@mui/material/Typography'
import type { ReactNode } from 'react'

/** Tiêu đề trang quản lý kiểu TheDuck: chữ in hoa đậm bên trái, nút thao tác bên phải. */
export default function PageHeader({ tieuDe, hanhDong }: { tieuDe: string; hanhDong?: ReactNode }) {
  return (
    <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 2, mb: 3, flexWrap: 'wrap' }}>
      <Typography variant="h4" sx={{ fontWeight: 700, textTransform: 'uppercase' }}>
        {tieuDe}
      </Typography>
      {hanhDong}
    </Box>
  )
}
