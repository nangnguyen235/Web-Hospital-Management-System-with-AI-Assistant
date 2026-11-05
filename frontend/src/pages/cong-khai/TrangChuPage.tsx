import CreditCardRoundedIcon from '@mui/icons-material/CreditCardRounded'
import ExpandMoreRoundedIcon from '@mui/icons-material/ExpandMoreRounded'
import HowToRegRoundedIcon from '@mui/icons-material/HowToRegRounded'
import LocalHospitalRoundedIcon from '@mui/icons-material/LocalHospitalRounded'
import MedicalServicesRoundedIcon from '@mui/icons-material/MedicalServicesRounded'
import PersonSearchRoundedIcon from '@mui/icons-material/PersonSearchRounded'
import ScienceRoundedIcon from '@mui/icons-material/ScienceRounded'
import SmartToyRoundedIcon from '@mui/icons-material/SmartToyRounded'
import Accordion from '@mui/material/Accordion'
import AccordionDetails from '@mui/material/AccordionDetails'
import AccordionSummary from '@mui/material/AccordionSummary'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Container from '@mui/material/Container'
import Grid from '@mui/material/Grid'
import Paper from '@mui/material/Paper'
import Stack from '@mui/material/Stack'
import Typography from '@mui/material/Typography'
import { useEffect, useState, type ReactNode } from 'react'
import { useNavigate } from 'react-router-dom'
import { BRAND } from '../../config/brand'
import { danhMucApi, type Khoa } from '../../services/danhMuc'
import { thongKeApi, type ThongKeTrangChu } from '../../services/thongKe'
import { GRADIENT, MAU } from '../../theme'

const DICH_VU: { icon: ReactNode; tieuDe: string; moTa: string }[] = [
  { icon: <MedicalServicesRoundedIcon />, tieuDe: 'Đặt khám trực tuyến', moTa: 'Chọn chuyên khoa, bác sĩ, khung giờ và thanh toán VNPAY ngay trên web.' },
  { icon: <SmartToyRoundedIcon />, tieuDe: 'Trợ lý AI', moTa: 'Mô tả triệu chứng để được gợi ý chuyên khoa phù hợp và đặt khám trong khung chat.' },
  { icon: <ScienceRoundedIcon />, tieuDe: 'Kết quả xét nghiệm', moTa: 'Thanh toán và xem kết quả, ảnh X-quang trực tuyến, không phải chờ lấy giấy.' },
]

const QUY_TRINH: { icon: ReactNode; tieuDe: string; moTa: string }[] = [
  { icon: <HowToRegRoundedIcon />, tieuDe: 'Chọn hồ sơ', moTa: 'Đăng nhập, chọn hồ sơ người đi khám (tối đa 10 hồ sơ mỗi tài khoản).' },
  { icon: <PersonSearchRoundedIcon />, tieuDe: 'Chọn bác sĩ, giờ khám', moTa: 'Chọn tối đa 3 chuyên khoa, mỗi chuyên khoa một khung giờ còn chỗ.' },
  { icon: <CreditCardRoundedIcon />, tieuDe: 'Thanh toán VNPAY', moTa: 'Thanh toán trong 15 phút để giữ chỗ, nhận phiếu khám và số thứ tự.' },
  { icon: <LocalHospitalRoundedIcon />, tieuDe: 'Đến khám', moTa: 'Đưa mã phiếu cho điều dưỡng tại phòng khám, chờ gọi số và vào khám.' },
]

const THAC_MAC = [
  { hoi: 'Đặt khám rồi có hủy hoặc hoàn tiền được không?', dap: 'Không. Phiếu đã thanh toán không hủy và không hoàn tiền, vui lòng kiểm tra kỹ trước khi thanh toán.' },
  { hoi: 'Sau khi chọn giờ tôi có bao lâu để thanh toán?', dap: 'Hệ thống giữ chỗ 15 phút. Quá thời gian, chỗ được nhả cho người khác.' },
  { hoi: 'Tôi quên mật khẩu thì làm sao?', dap: 'Chọn "Quên mật khẩu", nhập email để nhận mã OTP 6 số (hiệu lực 5 phút) rồi đặt mật khẩu mới.' },
  { hoi: 'Tôi không đặt trước, đến thẳng bệnh viện được không?', dap: 'Được. Điều dưỡng tại quầy sẽ tạo hồ sơ, chọn lịch khám hôm nay và hiện mã QR VNPAY để bạn thanh toán.' },
]

function TieuDeMuc({ id, tieuDe, phu }: { id?: string; tieuDe: string; phu?: string }) {
  return (
    <Box id={id} sx={{ textAlign: 'center', mb: 5, scrollMarginTop: 110 }}>
      <Typography variant="h4" sx={{ fontWeight: 700, color: MAU.chinh, textTransform: 'uppercase' }}>
        {tieuDe}
      </Typography>
      {phu && <Typography sx={{ mt: 1, color: 'text.secondary' }}>{phu}</Typography>}
    </Box>
  )
}

/** Trang chủ (frontend.md mục 5): hero, giới thiệu, số liệu, quy trình, chuyên khoa, thắc mắc. */
export default function TrangChuPage() {
  const navigate = useNavigate()
  const [khoa, setKhoa] = useState<Khoa[]>([])
  const [soLieu, setSoLieu] = useState<ThongKeTrangChu | null>(null)

  useEffect(() => {
    let huy = false
    danhMucApi.khoaCongKhai().then((ds) => !huy && setKhoa(ds)).catch(() => undefined)
    thongKeApi.trangChu().then((tk) => !huy && setSoLieu(tk)).catch(() => undefined)
    return () => {
      huy = true
    }
  }, [])

  return (
    <>
      {/* Hero kiểu TheDuck: nền gradient xanh, chữ in hoa lớn ở giữa */}
      <Box
        sx={{
          position: 'relative',
          minHeight: { xs: 460, md: 600 },
          display: 'grid',
          placeItems: 'center',
          textAlign: 'center',
          color: '#fff',
          px: 2,
          background: `radial-gradient(circle at 20% 30%, rgba(255,255,255,0.18) 0, transparent 40%), radial-gradient(circle at 80% 70%, rgba(255,255,255,0.12) 0, transparent 45%), ${GRADIENT.hero}`,
        }}
      >
        <Box sx={{ maxWidth: 820 }}>
          <Typography sx={{ fontSize: { xs: 34, md: 60 }, fontWeight: 400, lineHeight: 1.15, textTransform: 'uppercase' }}>
            Đặt khám trực tuyến
            <br />
            không còn xếp hàng
          </Typography>
          <Typography sx={{ mt: 3, fontSize: { xs: 16, md: 20 }, opacity: 0.95 }}>
            {BRAND.tenDayDu} — {BRAND.khauHieu}. Chọn bác sĩ, thanh toán VNPAY, theo dõi số thứ tự ngay trên điện thoại.
          </Typography>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ mt: 4, justifyContent: 'center' }}>
            <Button
              size="large"
              variant="contained"
              onClick={() => navigate('/dat-kham/chon-ho-so')}
              sx={{ background: GRADIENT.dangNhap, px: 4, py: 1.5, fontWeight: 700, boxShadow: 4 }}
            >
              Đặt khám ngay
            </Button>
            <Button
              size="large"
              variant="outlined"
              onClick={() => navigate('/#gioi-thieu')}
              sx={{ color: '#fff', borderColor: '#fff', px: 4, py: 1.5, fontWeight: 700 }}
            >
              Tìm hiểu về chúng tôi
            </Button>
          </Stack>
        </Box>
      </Box>

      {/* Giới thiệu */}
      <Container maxWidth="lg" sx={{ py: 8 }}>
        <TieuDeMuc id="gioi-thieu" tieuDe={`Vì sao chọn ${BRAND.ten}`} phu="Mọi bước khám ngoại trú đều đi qua hệ thống, không phải chờ đợi" />
        <Grid container spacing={3}>
          {DICH_VU.map((d) => (
            <Grid key={d.tieuDe} size={{ xs: 12, md: 4 }}>
              <Paper elevation={2} sx={{ p: 4, height: '100%', textAlign: 'center' }}>
                <Box sx={{ width: 64, height: 64, borderRadius: '50%', mx: 'auto', mb: 2, display: 'grid', placeItems: 'center', color: '#fff', background: GRADIENT.dauThe, '& svg': { fontSize: 34 } }}>
                  {d.icon}
                </Box>
                <Typography variant="h6" sx={{ fontWeight: 700, mb: 1 }}>
                  {d.tieuDe}
                </Typography>
                <Typography color="text.secondary">{d.moTa}</Typography>
              </Paper>
            </Grid>
          ))}
        </Grid>

        {/* Số liệu công khai (BR-25) */}
        <Grid container spacing={3} sx={{ mt: 4 }}>
          {[
            { nhan: 'Bác sĩ', so: soLieu?.soBacSi },
            { nhan: 'Chuyên khoa', so: soLieu?.soKhoa },
            { nhan: 'Lượt khám', so: soLieu?.soLuotKham },
          ].map((s) => (
            <Grid key={s.nhan} size={{ xs: 12, sm: 4 }}>
              <Box sx={{ textAlign: 'center', py: 3, borderRadius: 3, bgcolor: MAU.xanhRatNhat }}>
                <Typography sx={{ fontSize: 44, fontWeight: 700, color: MAU.chinh }}>{s.so ?? '—'}</Typography>
                <Typography sx={{ fontWeight: 500 }}>{s.nhan}</Typography>
              </Box>
            </Grid>
          ))}
        </Grid>
      </Container>

      {/* Quy trình */}
      <Box sx={{ bgcolor: MAU.nenDatKham, py: 8 }}>
        <Container maxWidth="lg">
          <TieuDeMuc id="quy-trinh" tieuDe="Quy trình đặt khám" phu="4 bước đơn giản" />
          <Grid container spacing={3}>
            {QUY_TRINH.map((b, i) => (
              <Grid key={b.tieuDe} size={{ xs: 12, sm: 6, md: 3 }}>
                <Paper elevation={0} sx={{ p: 3, height: '100%', position: 'relative' }}>
                  <Typography sx={{ position: 'absolute', top: 12, right: 16, fontSize: 40, fontWeight: 900, color: MAU.xanhRatNhat }}>
                    {String(i + 1).padStart(2, '0')}
                  </Typography>
                  <Box sx={{ color: MAU.xanhSang, mb: 1, '& svg': { fontSize: 40 } }}>{b.icon}</Box>
                  <Typography variant="h6" sx={{ fontWeight: 700, mb: 1 }}>
                    {b.tieuDe}
                  </Typography>
                  <Typography color="text.secondary">{b.moTa}</Typography>
                </Paper>
              </Grid>
            ))}
          </Grid>
        </Container>
      </Box>

      {/* Chuyên khoa từ API */}
      <Container maxWidth="lg" sx={{ py: 8 }}>
        <TieuDeMuc tieuDe="Chuyên khoa" phu="Chọn chuyên khoa phù hợp với triệu chứng của bạn" />
        {khoa.length === 0 ? (
          <Typography sx={{ textAlign: 'center', color: 'text.secondary' }}>Chưa có chuyên khoa nào.</Typography>
        ) : (
          <Grid container spacing={2}>
            {khoa.map((k) => (
              <Grid key={k.id} size={{ xs: 12, sm: 6, md: 4 }}>
                <Paper variant="outlined" sx={{ p: 2.5, height: '100%', display: 'flex', gap: 2, alignItems: 'flex-start' }}>
                  <LocalHospitalRoundedIcon color="secondary" />
                  <Box>
                    <Typography sx={{ fontWeight: 700 }}>{k.tenKhoa}</Typography>
                    {k.moTa && <Typography variant="body2" color="text.secondary">{k.moTa}</Typography>}
                  </Box>
                </Paper>
              </Grid>
            ))}
          </Grid>
        )}
      </Container>

      {/* Thắc mắc */}
      <Box sx={{ bgcolor: '#fff', py: 8 }}>
        <Container maxWidth="md">
          <TieuDeMuc id="thac-mac" tieuDe="Thắc mắc thường gặp" />
          {THAC_MAC.map((t) => (
            <Accordion key={t.hoi} disableGutters elevation={0} sx={{ border: '1px solid', borderColor: 'divider', mb: 1.5, '&:before': { display: 'none' } }}>
              <AccordionSummary expandIcon={<ExpandMoreRoundedIcon />}>
                <Typography sx={{ fontWeight: 600 }}>{t.hoi}</Typography>
              </AccordionSummary>
              <AccordionDetails>
                <Typography color="text.secondary">{t.dap}</Typography>
              </AccordionDetails>
            </Accordion>
          ))}
        </Container>
      </Box>
    </>
  )
}
