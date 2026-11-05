import type { VaiTro } from '../auth/jwt'

export interface MucMenu {
  nhan: string
  toi: string
  /** Chỉ hiện khi tài khoản có cờ trưởng khoa / điều dưỡng trưởng. */
  chiTruongKhoa?: boolean
}

/** Menu tài khoản trên header (TheDuck: AccountPopover) theo vai trò. */
export const MENU_TAI_KHOAN: Record<VaiTro, MucMenu[]> = {
  BENH_NHAN: [
    { nhan: 'Đặt khám ngay', toi: '/dat-kham/chon-ho-so' },
    { nhan: 'Hồ sơ bệnh nhân', toi: '/tai-khoan/ho-so' },
    { nhan: 'Phiếu khám bệnh', toi: '/tai-khoan/phieu-kham' },
  ],
  BAC_SI: [
    { nhan: 'Khám chữa bệnh', toi: '/bac-si/benh-nhan' },
    { nhan: 'Lịch trực', toi: '/bac-si/lich-truc' },
  ],
  DIEU_DUONG: [
    { nhan: 'Tiếp nhận bệnh nhân', toi: '/dieu-duong/tiep-nhan' },
    { nhan: 'Quầy dịch vụ', toi: '/dieu-duong/quay' },
    { nhan: 'Lịch trực', toi: '/dieu-duong/lich-truc' },
  ],
  QUAN_TRI: [
    { nhan: 'Trang quản trị', toi: '/admin/khoa' },
  ],
}

export const MENU_BENH_NHAN: MucMenu[] = [
  { nhan: 'Hồ sơ bệnh nhân', toi: '/tai-khoan/ho-so' },
  { nhan: 'Phiếu khám bệnh', toi: '/tai-khoan/phieu-kham' },
  { nhan: 'Lịch sử khám bệnh', toi: '/tai-khoan/lich-su-kham' },
  { nhan: 'Kết quả xét nghiệm', toi: '/tai-khoan/xet-nghiem' },
  { nhan: 'Nhắc uống thuốc', toi: '/tai-khoan/nhac-thuoc' },
  { nhan: 'Thông báo', toi: '/tai-khoan/thong-bao' },
]

export const MENU_BAC_SI: MucMenu[] = [
  { nhan: 'Bệnh nhân', toi: '/bac-si/benh-nhan' },
  { nhan: 'Lịch trực', toi: '/bac-si/lich-truc' },
  { nhan: 'Quản lý lịch khám', toi: '/bac-si/quan-ly-lich', chiTruongKhoa: true },
]

export const MENU_DIEU_DUONG: MucMenu[] = [
  { nhan: 'Tiếp nhận bệnh nhân', toi: '/dieu-duong/tiep-nhan' },
  { nhan: 'Số thứ tự', toi: '/dieu-duong/so-thu-tu' },
  { nhan: 'Quầy dịch vụ', toi: '/dieu-duong/quay' },
  { nhan: 'Phòng xét nghiệm', toi: '/dieu-duong/xet-nghiem' },
  { nhan: 'Lịch trực', toi: '/dieu-duong/lich-truc' },
  { nhan: 'Phân công điều dưỡng', toi: '/dieu-duong/phan-cong', chiTruongKhoa: true },
]

export const MENU_ADMIN: { nhom: string; muc: MucMenu[] }[] = [
  { nhom: 'THỐNG KÊ', muc: [{ nhan: 'Thống kê', toi: '/admin/thong-ke' }] },
  {
    nhom: 'QUẢN LÝ',
    muc: [
      { nhan: 'Tài khoản', toi: '/admin/tai-khoan' },
      { nhan: 'Nhân viên', toi: '/admin/nhan-vien' },
      { nhan: 'Bệnh nhân', toi: '/admin/benh-nhan' },
      { nhan: 'Khoa', toi: '/admin/khoa' },
      { nhan: 'Phòng', toi: '/admin/phong' },
      { nhan: 'Dịch vụ', toi: '/admin/dich-vu' },
      { nhan: 'Thuốc', toi: '/admin/thuoc' },
      { nhan: 'Giao dịch', toi: '/admin/giao-dich' },
      { nhan: 'Lịch khám', toi: '/admin/lich-kham' },
    ],
  },
]
