import Chip from '@mui/material/Chip'

/** Chip Hoạt động / Đã xóa dùng chung cho các danh mục xóa mềm. */
export default function ChipDaXoa({ daXoa }: { daXoa: boolean }) {
  return <Chip size="small" label={daXoa ? 'Đã xóa' : 'Hoạt động'} color={daXoa ? 'default' : 'success'} variant={daXoa ? 'outlined' : 'filled'} />
}
