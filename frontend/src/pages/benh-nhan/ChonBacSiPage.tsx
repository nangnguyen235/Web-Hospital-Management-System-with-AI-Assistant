import { useNavigate } from 'react-router-dom'
import DanhSachBacSi from '../../components/General/DanhSachBacSi'
import BuocDatKham from './BuocDatKham'
import { ghiDon } from './datKham'

/** Bước 2 đặt khám (FR-BN-07, 08): chọn bác sĩ theo chuyên khoa. */
export default function ChonBacSiPage() {
  const navigate = useNavigate()
  return (
    <BuocDatKham buoc={1} tieuDe="Chọn bác sĩ">
      <DanhSachBacSi
        nutChon="Chọn bác sĩ"
        onChon={(b) => {
          ghiDon({ bacSi: b })
          navigate('/dat-kham/chon-ngay')
        }}
      />
    </BuocDatKham>
  )
}
