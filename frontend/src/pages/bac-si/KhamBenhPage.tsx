import ArrowBackRoundedIcon from '@mui/icons-material/ArrowBackRounded'
import Alert from '@mui/material/Alert'
import Autocomplete from '@mui/material/Autocomplete'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Checkbox from '@mui/material/Checkbox'
import Chip from '@mui/material/Chip'
import FormControlLabel from '@mui/material/FormControlLabel'
import Grid from '@mui/material/Grid'
import MenuItem from '@mui/material/MenuItem'
import Paper from '@mui/material/Paper'
import Stack from '@mui/material/Stack'
import Tab from '@mui/material/Tab'
import Tabs from '@mui/material/Tabs'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { useSnackbar } from 'notistack'
import { useCallback, useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import BangDonThuoc from '../../components/General/BangDonThuoc'
import BangXetNghiem from '../../components/General/BangXetNghiem'
import DialogConfirm from '../../components/General/DialogConfirm'
import Loading from '../../components/General/Loading'
import { layThongBaoLoi } from '../../services/api'
import { danhMucApi, dinhDangTien, TEN_DON_VI, type DichVu, type Thuoc } from '../../services/danhMuc'
import { hoSoBenhAnApi, TEN_TRANG_THAI_BENH_AN, type BenhAn, type KeThuoc, type LichSu } from '../../services/hoSoBenhAn'
import { hienNgay, ngayISO } from '../../services/lichKham'
import { TEN_GIOI_TINH, type GioiTinh } from '../../services/nguoiDung'

const THUOC_TRONG: Omit<KeThuoc, 'thuocId'> = {
  soLuong: 10, soLanMoiNgay: 2, soNgay: 5, soLuongMoiLan: 1, sang: true, trua: false, chieu: false, toi: true, huongDan: '',
}

/** Màn khám (FR-BS-04, 05, 07, 08, 09, 10; BR-16…19, BR-26 phần thuốc trùng). AI 2 Voice-to-EMR làm ở giai đoạn AI. */
export default function KhamBenhPage() {
  const { id = '' } = useParams()
  const benhAnId = Number(id)
  const navigate = useNavigate()
  const { enqueueSnackbar } = useSnackbar()
  const [ba, setBa] = useState<BenhAn | null>(null)
  const [tab, setTab] = useState(0)
  const [ghi, setGhi] = useState({ trieuChung: '', chanDoan: '', ngayTaiKham: '' })
  const [dvXn, setDvXn] = useState<DichVu[]>([])
  const [xn, setXn] = useState<{ dichVuId: number | ''; ghiChu: string }>({ dichVuId: '', ghiChu: '' })
  const [thuocTim, setThuocTim] = useState<Thuoc[]>([])
  const [thuoc, setThuoc] = useState<Thuoc | null>(null)
  const [ke, setKe] = useState(THUOC_TRONG)
  const [canhBao, setCanhBao] = useState<string | null>(null)
  const [lichSu, setLichSu] = useState<LichSu[] | null>(null)
  const [xacNhanXong, setXacNhanXong] = useState(false)

  const loi = useCallback((e: unknown) => enqueueSnackbar(layThongBaoLoi(e), { variant: 'error' }), [enqueueSnackbar])

  const nap = useCallback(
    (b: BenhAn) => {
      setBa(b)
      setGhi({ trieuChung: b.trieuChung ?? '', chanDoan: b.chanDoan ?? '', ngayTaiKham: b.ngayTaiKham ?? '' })
    },
    [],
  )

  useEffect(() => {
    hoSoBenhAnApi.chiTiet(benhAnId).then(nap).catch(loi)
    danhMucApi.dichVu(undefined, 'XET_NGHIEM').then(setDvXn).catch(() => undefined)
  }, [benhAnId, nap, loi])

  useEffect(() => {
    if (tab === 3 && !lichSu) hoSoBenhAnApi.lichSuTrongKhoa(benhAnId).then(setLichSu).catch(loi)
  }, [tab, lichSu, benhAnId, loi])

  const timThuoc = (tu: string) => danhMucApi.chonThuoc(tu).then(setThuocTim).catch(() => undefined)
  useEffect(() => {
    timThuoc('')
  }, [])

  if (!ba) return <Loading />
  const choKham = ba.trangThai === 'CHO_KHAM'

  const chay = async (viec: () => Promise<BenhAn | void>, thongBao: string) => {
    try {
      const b = await viec()
      if (b) nap(b)
      else nap(await hoSoBenhAnApi.chiTiet(benhAnId))
      enqueueSnackbar(thongBao, { variant: 'success' })
    } catch (e) {
      loi(e)
    }
  }

  const keThuoc = async () => {
    if (!thuoc) return enqueueSnackbar('Vui lòng chọn thuốc', { variant: 'warning' })
    try {
      const kq = await hoSoBenhAnApi.keThuoc(benhAnId, { ...ke, thuocId: thuoc.id })
      setCanhBao(kq.canhBao)
      setBa({ ...ba, donThuoc: kq.donThuoc })
      setThuoc(null)
      setKe(THUOC_TRONG)
      enqueueSnackbar('Đã thêm thuốc', { variant: kq.canhBao ? 'warning' : 'success' })
    } catch (e) {
      loi(e)
    }
  }

  const so = (k: keyof typeof ke, nhan: string, buoc = 1) => (
    <TextField
      label={nhan}
      type="number"
      value={ke[k] as number}
      slotProps={{ htmlInput: { min: buoc, step: buoc } }}
      onChange={(e) => setKe({ ...ke, [k]: Number(e.target.value) })}
      sx={{ width: 150 }}
    />
  )

  return (
    <>
      <Button startIcon={<ArrowBackRoundedIcon />} onClick={() => navigate('/bac-si/benh-nhan')} sx={{ mb: 2 }}>
        Danh sách bệnh nhân
      </Button>
      <Paper elevation={2} sx={{ p: 2.5, mb: 3, display: 'flex', justifyContent: 'space-between', gap: 2, flexWrap: 'wrap' }}>
        <Box>
          <Typography variant="h5" sx={{ fontWeight: 700 }}>{ba.hoTen}</Typography>
          <Typography color="text.secondary">
            {TEN_GIOI_TINH[ba.gioiTinh as GioiTinh] ?? ba.gioiTinh} · Ngày sinh {hienNgay(ba.ngaySinh)} · Mã BN {ba.maBenhNhan} · Khám ngày {hienNgay(ba.ngayKham)}
          </Typography>
        </Box>
        <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
          <Chip label={TEN_TRANG_THAI_BENH_AN[ba.trangThai]} color={ba.trangThai === 'DA_KHAM' ? 'success' : 'primary'} />
          {choKham && (
            <Button variant="contained" onClick={() => chay(() => hoSoBenhAnApi.nhanKham(benhAnId), 'Đã nhận khám')}>
              Nhận khám
            </Button>
          )}
          {ba.trangThai === 'DANG_KHAM' && (
            <Button variant="contained" color="success" onClick={() => setXacNhanXong(true)}>
              Hoàn tất khám
            </Button>
          )}
        </Box>
      </Paper>

      {choKham ? (
        <Alert severity="info">Bấm “Nhận khám” để bắt đầu ghi bệnh án.</Alert>
      ) : (
        <>
          <Tabs value={tab} onChange={(_, v) => setTab(v)} sx={{ mb: 2 }} variant="scrollable">
            <Tab label="Bệnh án" />
            <Tab label={`Xét nghiệm (${ba.xetNghiem.length})`} />
            <Tab label={`Đơn thuốc (${ba.donThuoc?.thuoc.length ?? 0})`} />
            <Tab label="Lịch sử trong khoa" />
          </Tabs>

          {tab === 0 && (
            <Paper elevation={2} sx={{ p: 2.5 }}>
              <Stack spacing={2}>
                <TextField label="Triệu chứng, chỉ số sinh tồn" multiline minRows={4} value={ghi.trieuChung} onChange={(e) => setGhi({ ...ghi, trieuChung: e.target.value })} />
                <TextField label="Chẩn đoán" multiline minRows={3} value={ghi.chanDoan} onChange={(e) => setGhi({ ...ghi, chanDoan: e.target.value })} />
                <TextField
                  label="Ngày tái khám"
                  type="date"
                  value={ghi.ngayTaiKham}
                  onChange={(e) => setGhi({ ...ghi, ngayTaiKham: e.target.value })}
                  slotProps={{ inputLabel: { shrink: true }, htmlInput: { min: ngayISO(new Date()) } }}
                  sx={{ maxWidth: 260 }}
                />
                <Box>
                  <Button variant="contained" onClick={() => chay(() => hoSoBenhAnApi.ghi(benhAnId, { ...ghi, ngayTaiKham: ghi.ngayTaiKham || null }), 'Đã lưu bệnh án')}>
                    Lưu bệnh án
                  </Button>
                </Box>
              </Stack>
            </Paper>
          )}

          {tab === 1 && (
            <Paper elevation={2} sx={{ p: 2.5 }}>
              <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} sx={{ mb: 2 }}>
                <TextField select label="Dịch vụ xét nghiệm" value={xn.dichVuId} onChange={(e) => setXn({ ...xn, dichVuId: Number(e.target.value) })} sx={{ minWidth: 260 }}>
                  {dvXn.map((d) => (
                    <MenuItem key={d.id} value={d.id}>
                      {d.tenDichVu} · {dinhDangTien(d.gia)}
                    </MenuItem>
                  ))}
                </TextField>
                <TextField fullWidth label="Ghi chú" value={xn.ghiChu} onChange={(e) => setXn({ ...xn, ghiChu: e.target.value })} />
                <Button
                  variant="contained"
                  disabled={xn.dichVuId === ''}
                  onClick={() =>
                    chay(async () => {
                      await hoSoBenhAnApi.chiDinh(benhAnId, xn.dichVuId as number, xn.ghiChu)
                      setXn({ dichVuId: '', ghiChu: '' })
                    }, 'Đã chỉ định xét nghiệm')
                  }
                >
                  Chỉ định
                </Button>
              </Stack>
              <BangXetNghiem
                ds={ba.xetNghiem}
                thaoTac={(x) =>
                  !x.daThanhToan && (
                    <Button size="small" color="error" onClick={() => chay(() => hoSoBenhAnApi.xoaXetNghiem(x.id).then(() => undefined), 'Đã xóa xét nghiệm')}>
                      Xóa
                    </Button>
                  )
                }
              />
            </Paper>
          )}

          {tab === 2 && (
            <Paper elevation={2} sx={{ p: 2.5 }}>
              {canhBao && (
                <Alert severity="warning" sx={{ mb: 2 }} onClose={() => setCanhBao(null)}>
                  {canhBao}
                </Alert>
              )}
              <Grid container spacing={2} sx={{ mb: 2 }}>
                <Grid size={{ xs: 12, md: 5 }}>
                  <Autocomplete
                    options={thuocTim}
                    value={thuoc}
                    onChange={(_, v) => setThuoc(v)}
                    onInputChange={(_, v, ly) => ly === 'input' && timThuoc(v)}
                    getOptionLabel={(t) => `${t.tenThuoc} (${TEN_DON_VI[t.donVi]}) · ${dinhDangTien(t.gia)}`}
                    isOptionEqualToValue={(a, b) => a.id === b.id}
                    filterOptions={(x) => x}
                    renderInput={(p) => <TextField {...p} label="Thuốc" />}
                  />
                </Grid>
                <Grid size={{ xs: 12, md: 7 }}>
                  <Stack direction="row" spacing={1.5} sx={{ flexWrap: 'wrap', rowGap: 1.5 }}>
                    {so('soLuong', 'Số lượng')}
                    {so('soLanMoiNgay', 'Lần/ngày')}
                    {so('soNgay', 'Số ngày')}
                    {so('soLuongMoiLan', 'Mỗi lần', 0.25)}
                  </Stack>
                </Grid>
                <Grid size={12}>
                  {(['sang', 'trua', 'chieu', 'toi'] as const).map((b) => (
                    <FormControlLabel
                      key={b}
                      control={<Checkbox checked={ke[b]} onChange={(e) => setKe({ ...ke, [b]: e.target.checked })} />}
                      label={{ sang: 'Sáng', trua: 'Trưa', chieu: 'Chiều', toi: 'Tối' }[b]}
                    />
                  ))}
                  <TextField label="Hướng dẫn" value={ke.huongDan} onChange={(e) => setKe({ ...ke, huongDan: e.target.value })} sx={{ minWidth: 300, ml: 1 }} />
                  <Button variant="contained" onClick={keThuoc} sx={{ ml: 2, mt: 1 }}>
                    Thêm vào đơn
                  </Button>
                </Grid>
              </Grid>
              <BangDonThuoc
                don={ba.donThuoc}
                thaoTac={(d) => (
                  <Button
                    size="small"
                    color="error"
                    onClick={async () => {
                      try {
                        const don = await hoSoBenhAnApi.xoaThuoc(d.id)
                        setBa({ ...ba, donThuoc: don })
                      } catch (e) {
                        loi(e)
                      }
                    }}
                  >
                    Xóa
                  </Button>
                )}
              />
            </Paper>
          )}

          {tab === 3 && (
            <Paper elevation={2} sx={{ p: 2.5 }}>
              {!lichSu ? (
                <Loading chieuCao={120} />
              ) : lichSu.length === 0 ? (
                <Typography color="text.secondary">Chưa có lần khám trước trong khoa.</Typography>
              ) : (
                <Stack spacing={1.5}>
                  {lichSu.map((l) => (
                    <Paper key={l.benhAnId} variant="outlined" sx={{ p: 2 }}>
                      <Typography sx={{ fontWeight: 700 }}>
                        {hienNgay(l.ngayKham)} · {l.tenBacSi ?? ''}
                      </Typography>
                      <Typography color="text.secondary">Chẩn đoán: {l.chanDoan ?? '—'}</Typography>
                    </Paper>
                  ))}
                </Stack>
              )}
            </Paper>
          )}
        </>
      )}
      <DialogConfirm
        mo={xacNhanXong}
        tieuDe="Hoàn tất khám"
        noiDung="Chuyển bệnh án sang Đã khám? Bạn vẫn sửa được bệnh án, đơn thuốc và chỉ định xét nghiệm sau đó (BR-19)."
        nutXacNhan="Hoàn tất"
        onDong={() => setXacNhanXong(false)}
        onXacNhan={() => {
          setXacNhanXong(false)
          chay(() => hoSoBenhAnApi.hoanTat(benhAnId), 'Đã hoàn tất khám')
        }}
      />
    </>
  )
}
