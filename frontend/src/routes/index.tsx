import type { RouteObject } from 'react-router-dom'
import PlaceholderPage from '../components/General/PlaceholderPage'
import AdminLayout from '../layouts/AdminLayout'
import LogoLayout from '../layouts/LogoLayout'
import MainLayout from '../layouts/MainLayout'
import ProtectedLayout from '../layouts/ProtectedLayout'
import StaffLayout from '../layouts/StaffLayout'
import UserLayout from '../layouts/UserLayout'
import KhoaPage from '../pages/admin/KhoaPage'
import DangNhapPage from '../pages/auth/DangNhapPage'
import ManHinhSoPage from '../pages/cong-khai/ManHinhSoPage'
import NotFoundPage from '../pages/cong-khai/NotFoundPage'
import TrangChuPage from '../pages/cong-khai/TrangChuPage'

const cho = (tieuDe: string, ...fr: string[]) => <PlaceholderPage tieuDe={tieuDe} fr={fr} />

/** Bảng route ↔ FR: frontend.md mục 5. Trang chưa nối API dùng PlaceholderPage. */
export const routes: RouteObject[] = [
  {
    element: <MainLayout />,
    children: [
      { path: '/', element: <TrangChuPage /> },
      { path: '/tim-bac-si', element: cho('Tìm bác sĩ', 'FR-BN-07') },
      { path: '/thanh-toan/ket-qua', element: cho('Kết quả thanh toán VNPAY', 'FR-HT-02') },
      {
        element: <ProtectedLayout vaiTro={['BENH_NHAN']} />,
        children: [
          { path: '/dat-kham/chon-ho-so', element: cho('Đặt khám · Chọn hồ sơ', 'FR-BN-08') },
          { path: '/dat-kham/chon-bac-si', element: cho('Đặt khám · Chọn bác sĩ', 'FR-BN-08') },
          { path: '/dat-kham/chon-ngay', element: cho('Đặt khám · Chọn ngày và khung giờ', 'FR-BN-08') },
          { path: '/dat-kham/xac-nhan', element: cho('Đặt khám · Xác nhận và thanh toán', 'FR-BN-08') },
          { path: '/tim-lai-ho-so', element: cho('Tìm lại hồ sơ đã mất', 'FR-BN-06') },
          {
            path: '/tai-khoan',
            element: <UserLayout />,
            children: [
              { path: 'ho-so', element: cho('Hồ sơ bệnh nhân', 'FR-BN-05') },
              { path: 'phieu-kham', element: cho('Phiếu khám bệnh', 'FR-BN-09', 'FR-BN-10') },
              { path: 'lich-su-kham', element: cho('Lịch sử khám bệnh', 'FR-BN-11', 'FR-BN-14') },
              { path: 'xet-nghiem', element: cho('Kết quả xét nghiệm', 'FR-BN-12', 'FR-BN-13') },
              { path: 'nhac-thuoc', element: cho('Nhắc uống thuốc', 'FR-BN-15') },
              { path: 'thong-bao', element: cho('Thông báo', 'FR-BN-16') },
            ],
          },
        ],
      },
    ],
  },
  {
    element: <LogoLayout />,
    children: [
      { path: '/dang-nhap', element: <DangNhapPage /> },
      { path: '/dang-ky', element: cho('Đăng ký tài khoản', 'FR-BN-01') },
      { path: '/quen-mat-khau', element: cho('Quên mật khẩu', 'FR-BN-04') },
    ],
  },
  { path: '/man-hinh-so/:lichKhamId', element: <ManHinhSoPage /> },
  {
    element: <ProtectedLayout vaiTro={['BAC_SI']} />,
    children: [
      {
        path: '/bac-si',
        element: <StaffLayout />,
        children: [
          { path: 'benh-nhan', element: cho('Bệnh nhân của lịch khám', 'FR-BS-02', 'FR-BS-03') },
          { path: 'benh-an/:id', element: cho('Khám bệnh', 'FR-BS-04', 'FR-BS-05', 'FR-BS-06', 'FR-BS-07', 'FR-BS-08', 'FR-BS-09', 'FR-BS-10') },
          { path: 'lich-truc', element: cho('Lịch trực', 'FR-BS-02') },
          {
            element: <ProtectedLayout vaiTro={['BAC_SI']} canTruongKhoa />,
            children: [{ path: 'quan-ly-lich', element: cho('Quản lý lịch khám của khoa', 'FR-BS-11', 'FR-BS-12', 'FR-BS-13', 'FR-BS-14') }],
          },
        ],
      },
    ],
  },
  {
    element: <ProtectedLayout vaiTro={['DIEU_DUONG']} />,
    children: [
      {
        path: '/dieu-duong',
        element: <StaffLayout />,
        children: [
          { path: 'tiep-nhan', element: cho('Tiếp nhận bệnh nhân', 'FR-DD-03', 'FR-DD-04', 'FR-DD-05', 'FR-DD-06') },
          { path: 'so-thu-tu', element: cho('Số thứ tự', 'FR-DD-07') },
          { path: 'quay', element: cho('Quầy dịch vụ — khách vãng lai', 'FR-DD-08') },
          { path: 'xet-nghiem', element: cho('Phòng xét nghiệm', 'FR-DD-09', 'FR-DD-10', 'FR-DD-11') },
          { path: 'lich-truc', element: cho('Lịch trực', 'FR-DD-02') },
          {
            element: <ProtectedLayout vaiTro={['DIEU_DUONG']} canTruongKhoa />,
            children: [{ path: 'phan-cong', element: cho('Phân công điều dưỡng', 'FR-DD-12') }],
          },
        ],
      },
    ],
  },
  {
    element: <ProtectedLayout vaiTro={['QUAN_TRI']} />,
    children: [
      {
        path: '/admin',
        element: <AdminLayout />,
        children: [
          { path: 'thong-ke', element: cho('Thống kê', 'FR-AD-01') },
          { path: 'tai-khoan', element: cho('Quản lý tài khoản', 'FR-AD-02') },
          { path: 'nhan-vien', element: cho('Quản lý nhân viên', 'FR-AD-03') },
          { path: 'khoa', element: <KhoaPage /> },
          { path: 'phong', element: cho('Quản lý phòng', 'FR-AD-05') },
          { path: 'dich-vu', element: cho('Quản lý dịch vụ', 'FR-AD-06') },
          { path: 'thuoc', element: cho('Quản lý thuốc', 'FR-AD-07') },
          { path: 'benh-nhan', element: cho('Quản lý bệnh nhân', 'FR-AD-08') },
          { path: 'giao-dich', element: cho('Giao dịch', 'FR-AD-09') },
          { path: 'lich-kham', element: cho('Lịch khám', 'FR-AD-10') },
        ],
      },
    ],
  },
  { path: '*', element: <NotFoundPage /> },
]
