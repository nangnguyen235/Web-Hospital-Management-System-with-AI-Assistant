import DeleteOutlineRoundedIcon from '@mui/icons-material/DeleteOutlineRounded'
import Box from '@mui/material/Box'
import Chip from '@mui/material/Chip'
import IconButton from '@mui/material/IconButton'
import Pagination from '@mui/material/Pagination'
import Paper from '@mui/material/Paper'
import Stack from '@mui/material/Stack'
import Typography from '@mui/material/Typography'
import { useSnackbar } from 'notistack'
import { useCallback, useEffect, useState } from 'react'
import Loading from '../../components/General/Loading'
import { layThongBaoLoi } from '../../services/api'
import type { PagedResult } from '../../services/danhMuc'
import { hienNgayGio } from '../../services/lichKham'
import { nguoiDungApi, type ThongBao } from '../../services/nguoiDung'

const MOI_TRANG = 10

/** FR-BN-16, BR-22: thông báo in-app (không push); bấm vào thì đánh dấu đã xem; xóa. */
export default function ThongBaoPage() {
  const { enqueueSnackbar } = useSnackbar()
  const [trang, setTrang] = useState(1)
  const [kq, setKq] = useState<PagedResult<ThongBao> | null>(null)

  const tai = useCallback(() => {
    nguoiDungApi.thongBao(trang, MOI_TRANG).then(setKq).catch((e) => enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' }))
  }, [trang, enqueueSnackbar])
  useEffect(tai, [tai])

  const xem = async (t: ThongBao) => {
    if (t.trangThai === 'DA_XEM') return
    await nguoiDungApi.daXemThongBao(t.id).catch(() => undefined)
    tai()
  }
  const xoa = async (t: ThongBao) => {
    try {
      await nguoiDungApi.xoaThongBao(t.id)
      tai()
    } catch (e) {
      enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' })
    }
  }

  return (
    <>
      <Typography variant="h5" sx={{ fontWeight: 700, mb: 2 }}>
        Thông báo
      </Typography>
      {!kq ? (
        <Loading />
      ) : kq.items.length === 0 ? (
        <Typography color="text.secondary" sx={{ py: 4 }}>Chưa có thông báo.</Typography>
      ) : (
        <Stack spacing={1.5}>
          {kq.items.map((t) => (
            <Paper
              key={t.id}
              elevation={t.trangThai === 'DA_XEM' ? 0 : 2}
              variant={t.trangThai === 'DA_XEM' ? 'outlined' : 'elevation'}
              onClick={() => xem(t)}
              sx={{ p: 2, display: 'flex', gap: 2, alignItems: 'flex-start', cursor: 'pointer' }}
            >
              <Box sx={{ flex: 1 }}>
                <Typography sx={{ fontWeight: t.trangThai === 'DA_XEM' ? 500 : 700 }}>
                  {t.tieuDe} {t.trangThai !== 'DA_XEM' && <Chip size="small" color="primary" label="Mới" sx={{ ml: 1 }} />}
                </Typography>
                <Typography color="text.secondary">{t.noiDung}</Typography>
                <Typography variant="caption" color="text.secondary">{hienNgayGio(t.ngayTao)}</Typography>
              </Box>
              <IconButton
                aria-label="Xóa thông báo"
                onClick={(e) => {
                  e.stopPropagation()
                  xoa(t)
                }}
              >
                <DeleteOutlineRoundedIcon />
              </IconButton>
            </Paper>
          ))}
          {kq.totalCount > MOI_TRANG && (
            <Box sx={{ display: 'flex', justifyContent: 'center' }}>
              <Pagination count={Math.ceil(kq.totalCount / MOI_TRANG)} page={trang} onChange={(_, p) => setTrang(p)} />
            </Box>
          )}
        </Stack>
      )}
    </>
  )
}
