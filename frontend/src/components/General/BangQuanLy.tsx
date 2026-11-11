import MoreVertRoundedIcon from '@mui/icons-material/MoreVertRounded'
import SearchRoundedIcon from '@mui/icons-material/SearchRounded'
import Box from '@mui/material/Box'
import IconButton from '@mui/material/IconButton'
import InputAdornment from '@mui/material/InputAdornment'
import Menu from '@mui/material/Menu'
import MenuItem from '@mui/material/MenuItem'
import Paper from '@mui/material/Paper'
import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableContainer from '@mui/material/TableContainer'
import TableHead from '@mui/material/TableHead'
import TablePagination from '@mui/material/TablePagination'
import TableRow from '@mui/material/TableRow'
import TextField from '@mui/material/TextField'
import { useState, type ReactNode } from 'react'
import type { Cot, DanhSachPhanTrang, ThaoTac } from '../../hooks/useDanhSachPhanTrang'
import Loading from './Loading'

const hienCot = (an?: 'sm' | 'md' | 'lg') => (an ? { display: { xs: 'none', [an]: 'table-cell' } } : undefined)

interface Props<T> {
  ds: DanhSachPhanTrang<T>
  cot: Cot<T>[]
  thaoTac?: ThaoTac<T>[]
  goiYTim?: string
  boLoc?: ReactNode
  khoa: (dong: T) => string | number
}

/** Bảng danh mục kiểu TheDuck: ô tìm kiếm + bảng + phân trang + menu ⋮. */
export default function BangQuanLy<T>({ ds, cot, thaoTac = [], goiYTim, boLoc, khoa }: Props<T>) {
  const [tuKhoa, setTuKhoa] = useState('')
  const [menu, setMenu] = useState<{ anchor: HTMLElement; dong: T } | null>(null)
  const { duLieu, trang, soDong } = ds
  const soCot = cot.length + 1 + (thaoTac.length ? 1 : 0)

  return (
    <Paper elevation={2}>
      <Box sx={{ p: 2.5, display: 'flex', gap: 2, flexWrap: 'wrap' }}>
        {goiYTim && (
          <Box
            component="form"
            sx={{ flex: 1, minWidth: 240 }}
            onSubmit={(e) => {
              e.preventDefault()
              ds.tim(tuKhoa)
            }}
          >
            <TextField
              fullWidth
              placeholder={`${goiYTim} (Enter để tìm)`}
              value={tuKhoa}
              onChange={(e) => setTuKhoa(e.target.value)}
              slotProps={{ input: { startAdornment: <InputAdornment position="start"><SearchRoundedIcon /></InputAdornment> } }}
            />
          </Box>
        )}
        {boLoc}
      </Box>
      {!duLieu ? (
        <Loading />
      ) : (
        <>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell width={70}>STT</TableCell>
                  {cot.map((c) => (
                    <TableCell key={c.nhan} width={c.rong} sx={hienCot(c.anDuoi)}>
                      {c.nhan}
                    </TableCell>
                  ))}
                  {thaoTac.length > 0 && <TableCell width={60} />}
                </TableRow>
              </TableHead>
              <TableBody>
                {duLieu.items.length === 0 && (
                  <TableRow>
                    <TableCell colSpan={soCot} sx={{ textAlign: 'center', py: 5, color: 'text.secondary' }}>
                      Không có dữ liệu.
                    </TableCell>
                  </TableRow>
                )}
                {duLieu.items.map((d, i) => (
                  <TableRow key={khoa(d)} hover>
                    <TableCell>{trang * soDong + i + 1}</TableCell>
                    {cot.map((c) => (
                      <TableCell key={c.nhan} sx={hienCot(c.anDuoi)}>
                        {c.hien(d)}
                      </TableCell>
                    ))}
                    {thaoTac.length > 0 && (
                      <TableCell>
                        <IconButton onClick={(e) => setMenu({ anchor: e.currentTarget, dong: d })} aria-label="Thao tác">
                          <MoreVertRoundedIcon />
                        </IconButton>
                      </TableCell>
                    )}
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
          <TablePagination
            component="div"
            count={duLieu.totalCount}
            page={trang}
            rowsPerPage={soDong}
            rowsPerPageOptions={[10, 20, 50]}
            onPageChange={(_, p) => ds.setTrang(p)}
            onRowsPerPageChange={(e) => {
              ds.setSoDong(Number(e.target.value))
              ds.setTrang(0)
            }}
            labelRowsPerPage="Số dòng mỗi trang"
            labelDisplayedRows={({ from, to, count }) => `${from}–${to} trên ${count}`}
          />
        </>
      )}
      <Menu anchorEl={menu?.anchor} open={Boolean(menu)} onClose={() => setMenu(null)}>
        {menu &&
          thaoTac
            .filter((t) => !t.an?.(menu.dong))
            .map((t) => (
              <MenuItem
                key={t.nhan(menu.dong)}
                sx={{ color: t.mau?.(menu.dong) }}
                onClick={() => {
                  setMenu(null)
                  t.onChon(menu.dong)
                }}
              >
                {t.nhan(menu.dong)}
              </MenuItem>
            ))}
      </Menu>
    </Paper>
  )
}
