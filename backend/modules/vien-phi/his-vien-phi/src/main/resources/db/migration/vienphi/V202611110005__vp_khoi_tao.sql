-- Module VienPhi (tiền tố vp_): khởi tạo bảng — chuyển từ migration EF KhoiTao của bản .NET (mo-hinh-du-lieu.md mục 2).
-- KHÔNG sửa file đã chạy; thay đổi sau thêm file V mới (modular-monolith.md mục 4).

CREATE TABLE `vp_giao_dich` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `so_tien` decimal(12,2) NOT NULL,
    `phi` decimal(12,2) NOT NULL,
    `trang_thai` varchar(30) NOT NULL,
    `ma_ngan_hang` varchar(20) NULL,
    `phuong_thuc` varchar(20) NOT NULL,
    `nguon` varchar(255) NULL,
    `loai_thanh_toan` varchar(30) NOT NULL,
    `tai_khoan_id` bigint NULL,
    `da_xoa` tinyint(1) NOT NULL,
    `ngay_tao` datetime(6) NOT NULL,
    `ngay_cap_nhat` datetime(6) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE INDEX `ix_vp_giao_dich_tai_khoan_id` ON `vp_giao_dich` (`tai_khoan_id`);
