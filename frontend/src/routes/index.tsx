import type { RouteObject } from 'react-router-dom'
import AdminLayout from '../layouts/AdminLayout'
import LogoLayout from '../layouts/LogoLayout'
import MainLayout from '../layouts/MainLayout'
import ProtectedLayout from '../layouts/ProtectedLayout'
import StaffLayout from '../layouts/StaffLayout'
import UserLayout from '../layouts/UserLayout'
import BenhNhanAdminPage from '../pages/admin/BenhNhanPage'
import DichVuPage from '../pages/admin/DichVuPage'
import GiaoDichPage from '../pages/admin/GiaoDichPage'
import KhoaPage from '../pages/admin/KhoaPage'
import LichKhamAdminPage from '../pages/admin/LichKhamAdminPage'
import NhanVienPage from '../pages/admin/NhanVienPage'
import PhongPage from '../pages/admin/PhongPage'
import TaiKhoanPage from '../pages/admin/TaiKhoanPage'
import ThongKePage from '../pages/admin/ThongKePage'
import ThuocPage from '../pages/admin/ThuocPage'
import DangKyPage from '../pages/auth/DangKyPage'
import DangNhapPage from '../pages/auth/DangNhapPage'
import QuenMatKhauPage from '../pages/auth/QuenMatKhauPage'
import BenhNhanPage from '../pages/bac-si/BenhNhanPage'
import KhamBenhPage from '../pages/bac-si/KhamBenhPage'
import LichTrucBacSiPage from '../pages/bac-si/LichTrucBacSiPage'
import QuanLyLichPage from '../pages/bac-si/QuanLyLichPage'
import ChonBacSiPage from '../pages/benh-nhan/ChonBacSiPage'
import ChonHoSoPage from '../pages/benh-nhan/ChonHoSoPage'
import ChonNgayPage from '../pages/benh-nhan/ChonNgayPage'
import HoSoPage from '../pages/benh-nhan/HoSoPage'
import LichSuKhamPage from '../pages/benh-nhan/LichSuKhamPage'
import NhacThuocPage from '../pages/benh-nhan/NhacThuocPage'
import PhieuKhamPage from '../pages/benh-nhan/PhieuKhamPage'
import ThongBaoPage from '../pages/benh-nhan/ThongBaoPage'
import TimLaiHoSoPage from '../pages/benh-nhan/TimLaiHoSoPage'
import XacNhanPage from '../pages/benh-nhan/XacNhanPage'
import XetNghiemPage from '../pages/benh-nhan/XetNghiemPage'
import KetQuaThanhToanPage from '../pages/cong-khai/KetQuaThanhToanPage'
import ManHinhSoPage from '../pages/cong-khai/ManHinhSoPage'
import NotFoundPage from '../pages/cong-khai/NotFoundPage'
import TimBacSiPage from '../pages/cong-khai/TimBacSiPage'
import TrangChuPage from '../pages/cong-khai/TrangChuPage'
import LichTrucDieuDuongPage from '../pages/dieu-duong/LichTrucDieuDuongPage'
import PhanCongPage from '../pages/dieu-duong/PhanCongPage'
import PhongXetNghiemPage from '../pages/dieu-duong/PhongXetNghiemPage'
import QuayPage from '../pages/dieu-duong/QuayPage'
import SoThuTuPage from '../pages/dieu-duong/SoThuTuPage'
import TiepNhanPage from '../pages/dieu-duong/TiepNhanPage'

/** Bảng route ↔ FR: frontend.md mục 5. AI 1 (chatbot, FR-BN-17) và AI 2 (FR-BS-06) làm ở giai đoạn AI. */
export const routes: RouteObject[] = [
  {
    element: <MainLayout />,
    children: [
      { path: '/', element: <TrangChuPage /> },
      { path: '/tim-bac-si', element: <TimBacSiPage /> },
      { path: '/thanh-toan/ket-qua', element: <KetQuaThanhToanPage /> },
      {
        element: <ProtectedLayout vaiTro={['BENH_NHAN']} />,
        children: [
          { path: '/dat-kham/chon-ho-so', element: <ChonHoSoPage /> },
          { path: '/dat-kham/chon-bac-si', element: <ChonBacSiPage /> },
          { path: '/dat-kham/chon-ngay', element: <ChonNgayPage /> },
          { path: '/dat-kham/xac-nhan', element: <XacNhanPage /> },
          { path: '/tim-lai-ho-so', element: <TimLaiHoSoPage /> },
          {
            path: '/tai-khoan',
            element: <UserLayout />,
            children: [
              { path: 'ho-so', element: <HoSoPage /> },
              { path: 'phieu-kham', element: <PhieuKhamPage /> },
              { path: 'lich-su-kham', element: <LichSuKhamPage /> },
              { path: 'xet-nghiem', element: <XetNghiemPage /> },
              { path: 'nhac-thuoc', element: <NhacThuocPage /> },
              { path: 'thong-bao', element: <ThongBaoPage /> },
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
      { path: '/dang-ky', element: <DangKyPage /> },
      { path: '/quen-mat-khau', element: <QuenMatKhauPage /> },
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
          { path: 'benh-nhan', element: <BenhNhanPage /> },
          { path: 'benh-an/:id', element: <KhamBenhPage /> },
          { path: 'lich-truc', element: <LichTrucBacSiPage /> },
          {
            element: <ProtectedLayout vaiTro={['BAC_SI']} canTruongKhoa />,
            children: [{ path: 'quan-ly-lich', element: <QuanLyLichPage /> }],
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
          { path: 'tiep-nhan', element: <TiepNhanPage /> },
          { path: 'so-thu-tu', element: <SoThuTuPage /> },
          { path: 'quay', element: <QuayPage /> },
          { path: 'xet-nghiem', element: <PhongXetNghiemPage /> },
          { path: 'lich-truc', element: <LichTrucDieuDuongPage /> },
          {
            element: <ProtectedLayout vaiTro={['DIEU_DUONG']} canTruongKhoa />,
            children: [{ path: 'phan-cong', element: <PhanCongPage /> }],
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
          { path: 'thong-ke', element: <ThongKePage /> },
          { path: 'tai-khoan', element: <TaiKhoanPage /> },
          { path: 'nhan-vien', element: <NhanVienPage /> },
          { path: 'khoa', element: <KhoaPage /> },
          { path: 'phong', element: <PhongPage /> },
          { path: 'dich-vu', element: <DichVuPage /> },
          { path: 'thuoc', element: <ThuocPage /> },
          { path: 'benh-nhan', element: <BenhNhanAdminPage /> },
          { path: 'giao-dich', element: <GiaoDichPage /> },
          { path: 'lich-kham', element: <LichKhamAdminPage /> },
        ],
      },
    ],
  },
  { path: '*', element: <NotFoundPage /> },
]
