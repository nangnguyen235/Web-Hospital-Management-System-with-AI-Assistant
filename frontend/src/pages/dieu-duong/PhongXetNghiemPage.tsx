import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Paper from '@mui/material/Paper'
import Stack from '@mui/material/Stack'
import Tab from '@mui/material/Tab'
import Tabs from '@mui/material/Tabs'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { useSnackbar } from 'notistack'
import { useCallback, useEffect, useState } from 'react'
import BangXetNghiem from '../../components/General/BangXetNghiem'
import ChonPhong from '../../components/General/ChonPhong'
import DialogForm from '../../components/General/DialogForm'
import PageHeader from '../../components/General/PageHeader'
import { layThongBaoLoi } from '../../services/api'
import type { Phong } from '../../services/danhMuc'
import { hoSoBenhAnApi, TEN_TRANG_THAI_XN, type SoPhongXetNghiem, type TrangThaiXetNghiem, type XetNghiem } from '../../services/hoSoBenhAn'
import { GRADIENT } from '../../theme'

const DUOI_TEP = '.pdf,.jpg,.jpeg,.png'
const TOI_DA_MB = 10

/** FR-DD-09…11, BR-17: danh sách hôm nay theo trạng thái, gọi tiếp 5 số, nhận (chỉ khi đã trả), hoàn tất + file lên R2. */
export default function PhongXetNghiemPage() {
  const { enqueueSnackbar } = useSnackbar()
  const [phong, setPhong] = useState<Phong | null>(null)
  const [trangThai, setTrangThai] = useState<TrangThaiXetNghiem>('CHO_THUC_HIEN')
  const [ds, setDs] = useState<XetNghiem[] | null>(null)
  const [so, setSo] = useState<SoPhongXetNghiem | null>(null)
  const [hoanTat, setHoanTat] = useState<{ xn: XetNghiem; ketQua: string; file: File | null } | null>(null)
  const [dangGui, setDangGui] = useState(false)

  const loi = useCallback((e: unknown) => enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' }), [enqueueSnackbar])

  const tai = useCallback(() => {
    if (!phong) return
    hoSoBenhAnApi.xetNghiemCuaPhong(phong.id, trangThai).then((d) => setDs([...d].sort((a, b) => a.soThuTu - b.soThuTu))).catch(loi)
    hoSoBenhAnApi.soPhong(phong.id).then(setSo).catch(() => undefined)
  }, [phong, trangThai, loi])
  useEffect(tai, [tai])

  const chay = async (viec: () => Promise<unknown>) => {
    try {
      await viec()
      tai()
    } catch (e) {
      loi(e)
    }
  }

  const luuKetQua = async () => {
    if (!hoanTat) return
    if (!hoanTat.ketQua.trim() && !hoanTat.file) return enqueueSnackbar('Nhập kết quả hoặc chọn tệp', { variant: 'warning' })
    if (hoanTat.file && hoanTat.file.size > TOI_DA_MB * 1024 * 1024) return enqueueSnackbar(`Tệp tối đa ${TOI_DA_MB} MB`, { variant: 'warning' })
    setDangGui(true)
    try {
      await hoSoBenhAnApi.hoanTatXetNghiem(hoanTat.xn.id, hoanTat.ketQua.trim(), hoanTat.file)
      enqueueSnackbar('Đã hoàn tất xét nghiệm', { variant: 'success' })
      setHoanTat(null)
      tai()
    } catch (e) {
      loi(e)
    } finally {
      setDangGui(false)
    }
  }

  return (
    <>
      <PageHeader tieuDe="Phòng xét nghiệm" />
      <Paper elevation={2} sx={{ p: 2.5, mb: 3, display: 'flex', gap: 3, alignItems: 'center', flexWrap: 'wrap' }}>
        <ChonPhong loai="PHONG_XET_NGHIEM" onChon={setPhong} />
        {so && (
          <>
            <Typography>
              Số đang gọi: <b style={{ fontSize: 28 }}>{so.soDangGoi || '--'}</b> / {so.soLonNhat}
            </Typography>
            <Button variant="contained" onClick={() => phong && chay(() => hoSoBenhAnApi.goiTiepXetNghiem(phong.id))} sx={{ background: GRADIENT.nut, fontWeight: 700 }}>
              Gọi tiếp 5 số
            </Button>
          </>
        )}
      </Paper>
      <Tabs value={trangThai} onChange={(_, v) => setTrangThai(v)} sx={{ mb: 2 }}>
        {(Object.keys(TEN_TRANG_THAI_XN) as TrangThaiXetNghiem[]).map((t) => (
          <Tab key={t} value={t} label={TEN_TRANG_THAI_XN[t]} />
        ))}
      </Tabs>
      <Paper elevation={2} sx={{ p: 2 }}>
        <BangXetNghiem
          ds={ds ?? []}
          thaoTac={(x) => (
            <Box sx={{ display: 'flex', gap: 1, justifyContent: 'flex-end' }}>
              {x.trangThai === 'CHO_THUC_HIEN' && (
                <Button size="small" variant="contained" disabled={!x.daThanhToan} onClick={() => chay(() => hoSoBenhAnApi.nhanXetNghiem(x.id))}>
                  {x.daThanhToan ? 'Nhận' : 'Chờ thanh toán'}
                </Button>
              )}
              {x.trangThai === 'DANG_THUC_HIEN' && (
                <Button size="small" variant="contained" color="success" onClick={() => setHoanTat({ xn: x, ketQua: '', file: null })}>
                  Nhập kết quả
                </Button>
              )}
            </Box>
          )}
        />
      </Paper>
      <DialogForm mo={Boolean(hoanTat)} tieuDe={`Kết quả ${hoanTat?.xn.tenDichVu ?? ''}`} nutXacNhan="Hoàn tất" dangGui={dangGui} onDong={() => setHoanTat(null)} onXacNhan={luuKetQua}>
        {hoanTat && (
          <Stack spacing={2} sx={{ pt: 1 }}>
            <Typography color="text.secondary">
              {hoanTat.xn.hoTen} · Số {hoanTat.xn.soThuTu} · Mã {hoanTat.xn.maXetNghiem}
            </Typography>
            <TextField label="Kết quả" multiline minRows={4} value={hoanTat.ketQua} onChange={(e) => setHoanTat({ ...hoanTat, ketQua: e.target.value })} />
            <Button variant="outlined" component="label">
              {hoanTat.file ? hoanTat.file.name : `Chọn tệp kết quả (pdf, jpg, png ≤ ${TOI_DA_MB} MB)`}
              <input hidden type="file" accept={DUOI_TEP} onChange={(e) => setHoanTat({ ...hoanTat, file: e.target.files?.[0] ?? null })} />
            </Button>
          </Stack>
        )}
      </DialogForm>
    </>
  )
}
