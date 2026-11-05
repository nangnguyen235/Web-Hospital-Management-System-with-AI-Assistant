import AddRoundedIcon from '@mui/icons-material/AddRounded'
import MoreVertRoundedIcon from '@mui/icons-material/MoreVertRounded'
import SearchRoundedIcon from '@mui/icons-material/SearchRounded'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Chip from '@mui/material/Chip'
import IconButton from '@mui/material/IconButton'
import InputAdornment from '@mui/material/InputAdornment'
import Menu from '@mui/material/Menu'
import MenuItem from '@mui/material/MenuItem'
import Paper from '@mui/material/Paper'
import Stack from '@mui/material/Stack'
import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableContainer from '@mui/material/TableContainer'
import TableHead from '@mui/material/TableHead'
import TablePagination from '@mui/material/TablePagination'
import TableRow from '@mui/material/TableRow'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { useSnackbar } from 'notistack'
import { useCallback, useEffect, useState } from 'react'
import DialogConfirm from '../../components/General/DialogConfirm'
import DialogForm from '../../components/General/DialogForm'
import Loading from '../../components/General/Loading'
import PageHeader from '../../components/General/PageHeader'
import { layThongBaoLoi } from '../../services/api'
import { danhMucApi, type Khoa, type LuuKhoa, type PagedResult } from '../../services/danhMuc'
import { GRADIENT } from '../../theme'

const FORM_TRONG: LuuKhoa = { tenKhoa: '', moTa: '', tuKhoaTrieuChung: '' }

/**
 * TRANG MẪU danh mục Admin kiểu TheDuck (FR-AD-04): tiêu đề + nút Thêm, ô tìm kiếm, bảng + phân trang,
 * menu ⋮ sửa/xóa/khôi phục, DialogForm thêm/sửa, DialogConfirm. Các trang Phòng, Dịch vụ, Thuốc làm theo mẫu này.
 */
export default function KhoaPage() {
  const { enqueueSnackbar } = useSnackbar()
  const [duLieu, setDuLieu] = useState<PagedResult<Khoa> | null>(null)
  const [tuKhoa, setTuKhoa] = useState('')
  const [timTheo, setTimTheo] = useState('')
  const [trang, setTrang] = useState(0)
  const [soDong, setSoDong] = useState(10)
  const [taiLai, setTaiLai] = useState(0)

  const [menu, setMenu] = useState<{ anchor: HTMLElement; khoa: Khoa } | null>(null)
  const [form, setForm] = useState<{ khoa: Khoa | null; giaTri: LuuKhoa } | null>(null)
  const [daGui, setDaGui] = useState(false)
  const [dangGui, setDangGui] = useState(false)
  const [xacNhan, setXacNhan] = useState<Khoa | null>(null)

  useEffect(() => {
    let huy = false
    danhMucApi
      .timKhoa(timTheo, trang + 1, soDong)
      .then((d) => !huy && setDuLieu(d))
      .catch((e) => !huy && enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' }))
    return () => {
      huy = true
    }
  }, [timTheo, trang, soDong, taiLai, enqueueSnackbar])

  const lamMoi = useCallback(() => setTaiLai((x) => x + 1), [])

  const moForm = (khoa: Khoa | null) => {
    setMenu(null)
    setDaGui(false)
    setForm({ khoa, giaTri: khoa ? { tenKhoa: khoa.tenKhoa, moTa: khoa.moTa ?? '', tuKhoaTrieuChung: khoa.tuKhoaTrieuChung ?? '' } : FORM_TRONG })
  }

  const luu = async () => {
    if (!form) return
    setDaGui(true)
    if (!form.giaTri.tenKhoa.trim()) return
    setDangGui(true)
    try {
      if (form.khoa) await danhMucApi.suaKhoa(form.khoa.id, form.giaTri)
      else await danhMucApi.taoKhoa(form.giaTri)
      enqueueSnackbar(form.khoa ? 'Đã cập nhật khoa' : 'Đã thêm khoa', { variant: 'success' })
      setForm(null)
      lamMoi()
    } catch (e) {
      enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' })
    } finally {
      setDangGui(false)
    }
  }

  const doiTrangThai = async () => {
    if (!xacNhan) return
    try {
      if (xacNhan.daXoa) await danhMucApi.khoiPhucKhoa(xacNhan.id)
      else await danhMucApi.xoaKhoa(xacNhan.id)
      enqueueSnackbar(xacNhan.daXoa ? 'Đã khôi phục khoa' : 'Đã xóa khoa', { variant: 'success' })
      lamMoi()
    } catch (e) {
      enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' })
    } finally {
      setXacNhan(null)
    }
  }

  const thieuTen = daGui && !form?.giaTri.tenKhoa.trim()

  return (
    <>
      <PageHeader
        tieuDe="Quản lý khoa"
        hanhDong={
          <Button variant="contained" startIcon={<AddRoundedIcon />} onClick={() => moForm(null)} sx={{ background: GRADIENT.nut, fontWeight: 700 }}>
            Thêm
          </Button>
        }
      />
      <Paper elevation={2}>
        <Box
          component="form"
          sx={{ p: 2.5 }}
          onSubmit={(e) => {
            e.preventDefault()
            setTrang(0)
            setTimTheo(tuKhoa.trim())
          }}
        >
          <TextField
            fullWidth
            placeholder="Tìm theo tên khoa (Enter để tìm)"
            value={tuKhoa}
            onChange={(e) => setTuKhoa(e.target.value)}
            slotProps={{ input: { startAdornment: <InputAdornment position="start"><SearchRoundedIcon /></InputAdornment> } }}
          />
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
                    <TableCell>Tên khoa</TableCell>
                    <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>Mô tả</TableCell>
                    <TableCell sx={{ display: { xs: 'none', lg: 'table-cell' } }}>Từ khóa triệu chứng (AI)</TableCell>
                    <TableCell>Trạng thái</TableCell>
                    <TableCell width={60} />
                  </TableRow>
                </TableHead>
                <TableBody>
                  {duLieu.items.length === 0 && (
                    <TableRow>
                      <TableCell colSpan={6} sx={{ textAlign: 'center', py: 5, color: 'text.secondary' }}>
                        Không có khoa nào.
                      </TableCell>
                    </TableRow>
                  )}
                  {duLieu.items.map((k, i) => (
                    <TableRow key={k.id} hover>
                      <TableCell>{trang * soDong + i + 1}</TableCell>
                      <TableCell sx={{ fontWeight: 600 }}>{k.tenKhoa}</TableCell>
                      <TableCell sx={{ display: { xs: 'none', md: 'table-cell' }, maxWidth: 320 }}>
                        <Typography variant="body2" noWrap>{k.moTa ?? '—'}</Typography>
                      </TableCell>
                      <TableCell sx={{ display: { xs: 'none', lg: 'table-cell' }, maxWidth: 260 }}>
                        <Typography variant="body2" noWrap>{k.tuKhoaTrieuChung ?? '—'}</Typography>
                      </TableCell>
                      <TableCell>
                        <Chip size="small" label={k.daXoa ? 'Đã xóa' : 'Hoạt động'} color={k.daXoa ? 'default' : 'success'} variant={k.daXoa ? 'outlined' : 'filled'} />
                      </TableCell>
                      <TableCell>
                        <IconButton onClick={(e) => setMenu({ anchor: e.currentTarget, khoa: k })} aria-label="Thao tác">
                          <MoreVertRoundedIcon />
                        </IconButton>
                      </TableCell>
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
              onPageChange={(_, p) => setTrang(p)}
              onRowsPerPageChange={(e) => {
                setSoDong(Number(e.target.value))
                setTrang(0)
              }}
              labelRowsPerPage="Số dòng mỗi trang"
              labelDisplayedRows={({ from, to, count }) => `${from}–${to} trên ${count}`}
            />
          </>
        )}
      </Paper>

      <Menu anchorEl={menu?.anchor} open={Boolean(menu)} onClose={() => setMenu(null)}>
        <MenuItem onClick={() => menu && moForm(menu.khoa)}>Sửa</MenuItem>
        <MenuItem
          onClick={() => {
            if (menu) setXacNhan(menu.khoa)
            setMenu(null)
          }}
          sx={{ color: menu?.khoa.daXoa ? 'success.main' : 'error.main' }}
        >
          {menu?.khoa.daXoa ? 'Khôi phục' : 'Xóa'}
        </MenuItem>
      </Menu>

      <DialogForm
        mo={Boolean(form)}
        tieuDe={form?.khoa ? 'Cập nhật khoa' : 'Thêm khoa'}
        nutXacNhan={form?.khoa ? 'Cập nhật' : 'Thêm'}
        dangGui={dangGui}
        onDong={() => setForm(null)}
        onXacNhan={luu}
      >
        <Stack spacing={2.5} sx={{ pt: 1 }}>
          <TextField
            label="Tên khoa"
            required
            autoFocus
            value={form?.giaTri.tenKhoa ?? ''}
            onChange={(e) => form && setForm({ ...form, giaTri: { ...form.giaTri, tenKhoa: e.target.value } })}
            error={thieuTen}
            helperText={thieuTen ? 'Tên khoa là bắt buộc' : ' '}
            slotProps={{ htmlInput: { maxLength: 100 } }}
          />
          <TextField
            label="Mô tả"
            multiline
            minRows={3}
            value={form?.giaTri.moTa ?? ''}
            onChange={(e) => form && setForm({ ...form, giaTri: { ...form.giaTri, moTa: e.target.value } })}
          />
          <TextField
            label="Từ khóa triệu chứng"
            multiline
            minRows={2}
            helperText="Ngăn cách bằng dấu phẩy — chatbot AI dùng để gợi ý khoa (BR-23)"
            value={form?.giaTri.tuKhoaTrieuChung ?? ''}
            onChange={(e) => form && setForm({ ...form, giaTri: { ...form.giaTri, tuKhoaTrieuChung: e.target.value } })}
          />
        </Stack>
      </DialogForm>

      <DialogConfirm
        mo={Boolean(xacNhan)}
        tieuDe={xacNhan?.daXoa ? 'Khôi phục khoa' : 'Xóa khoa'}
        noiDung={
          xacNhan?.daXoa
            ? `Khôi phục khoa "${xacNhan.tenKhoa}"? Khoa sẽ hiện lại khi chọn mới.`
            : `Xóa khoa "${xacNhan?.tenKhoa}"? Khoa sẽ không hiện khi chọn mới, dữ liệu cũ vẫn giữ nguyên.`
        }
        nutXacNhan={xacNhan?.daXoa ? 'Khôi phục' : 'Xóa'}
        mauNguyHiem={!xacNhan?.daXoa}
        onDong={() => setXacNhan(null)}
        onXacNhan={doiTrangThai}
      />
    </>
  )
}
