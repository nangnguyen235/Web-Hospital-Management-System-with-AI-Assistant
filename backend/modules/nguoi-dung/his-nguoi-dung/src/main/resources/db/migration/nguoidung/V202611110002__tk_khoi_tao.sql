-- Module NguoiDung (tiền tố tk_): khởi tạo bảng — chuyển từ migration EF KhoiTao của bản .NET (mo-hinh-du-lieu.md mục 2).
-- KHÔNG sửa file đã chạy; thay đổi sau thêm file V mới (modular-monolith.md mục 4).

CREATE TABLE `tk_benh_nhan` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `ma_benh_nhan` varchar(10) NOT NULL,
    `ho_ten` varchar(100) NOT NULL,
    `so_dien_thoai` varchar(20) NULL,
    `so_cccd` varchar(12) NULL,
    `ngay_sinh` date NOT NULL,
    `gioi_tinh` varchar(30) NOT NULL,
    `da_xoa` tinyint(1) NOT NULL,
    `ngay_tao` datetime(6) NOT NULL,
    `ngay_cap_nhat` datetime(6) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `tk_nguoi_dung_tam` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `email` varchar(150) NOT NULL,
    `otp` varchar(64) NOT NULL,
    `so_lan_nhap_otp` int NOT NULL,
    `otp_tao_luc` datetime(6) NULL,
    `otp_het_han` datetime(6) NULL,
    `da_xoa` tinyint(1) NOT NULL,
    `ngay_tao` datetime(6) NOT NULL,
    `ngay_cap_nhat` datetime(6) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `tk_nhan_vien` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `ho_ten` varchar(100) NOT NULL,
    `so_dien_thoai` varchar(20) NOT NULL,
    `so_cccd` varchar(12) NULL,
    `gioi_tinh` varchar(30) NOT NULL,
    `ngay_sinh` date NULL,
    `anh_dai_dien_key` varchar(255) NULL,
    `loai_nhan_vien` varchar(30) NOT NULL,
    `khoa_id` int NULL,
    `la_truong_khoa` tinyint(1) NOT NULL,
    `da_xoa` tinyint(1) NOT NULL,
    `ngay_tao` datetime(6) NOT NULL,
    `ngay_cap_nhat` datetime(6) NOT NULL,
    `hoc_ham` varchar(30) NULL,
    `diem_danh_gia` decimal(2,1) NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `tk_tai_khoan` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `ho_ten` varchar(100) NOT NULL,
    `email` varchar(150) NULL,
    `mat_khau_hash` varchar(255) NOT NULL,
    `so_dien_thoai` varchar(20) NOT NULL,
    `anh_dai_dien_key` varchar(255) NULL,
    `so_luong_ho_so` int NOT NULL,
    `otp` varchar(64) NULL,
    `so_lan_nhap_otp` int NOT NULL,
    `otp_tao_luc` datetime(6) NULL,
    `otp_het_han` datetime(6) NULL,
    `nhan_vien_id` bigint NULL,
    `da_xoa` tinyint(1) NOT NULL,
    `ngay_tao` datetime(6) NOT NULL,
    `ngay_cap_nhat` datetime(6) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_tk_tai_khoan_nhan_vien_nhan_vien_id` FOREIGN KEY (`nhan_vien_id`) REFERENCES `tk_nhan_vien` (`id`) ON DELETE RESTRICT
);

CREATE TABLE `tk_ho_so_benh_nhan` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `tai_khoan_id` bigint NULL,
    `benh_nhan_id` bigint NULL,
    `ho_ten` varchar(100) NOT NULL,
    `so_dien_thoai` varchar(20) NULL,
    `email` varchar(150) NULL,
    `gioi_tinh` varchar(30) NOT NULL,
    `so_cccd` varchar(12) NULL,
    `dia_chi` varchar(255) NULL,
    `ngay_sinh` date NOT NULL,
    `dan_toc_id` int NULL,
    `otp` varchar(64) NULL,
    `so_lan_nhap_otp` int NOT NULL,
    `otp_tao_luc` datetime(6) NULL,
    `otp_het_han` datetime(6) NULL,
    `da_xoa` tinyint(1) NOT NULL,
    `ngay_tao` datetime(6) NOT NULL,
    `ngay_cap_nhat` datetime(6) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_tk_ho_so_benh_nhan_tk_benh_nhan_benh_nhan_id` FOREIGN KEY (`benh_nhan_id`) REFERENCES `tk_benh_nhan` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_tk_ho_so_benh_nhan_tk_tai_khoan_tai_khoan_id` FOREIGN KEY (`tai_khoan_id`) REFERENCES `tk_tai_khoan` (`id`) ON DELETE RESTRICT
);

CREATE TABLE `tk_thong_bao` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `tai_khoan_id` bigint NOT NULL,
    `tieu_de` varchar(200) NOT NULL,
    `noi_dung` text NOT NULL,
    `du_lieu` varchar(1000) NULL,
    `trang_thai` varchar(30) NOT NULL,
    `da_xoa` tinyint(1) NOT NULL,
    `ngay_tao` datetime(6) NOT NULL,
    `ngay_cap_nhat` datetime(6) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_tk_thong_bao_tk_tai_khoan_tai_khoan_id` FOREIGN KEY (`tai_khoan_id`) REFERENCES `tk_tai_khoan` (`id`) ON DELETE RESTRICT
);

CREATE UNIQUE INDEX `ix_tk_benh_nhan_ma_benh_nhan` ON `tk_benh_nhan` (`ma_benh_nhan`);

CREATE UNIQUE INDEX `ix_tk_benh_nhan_so_cccd` ON `tk_benh_nhan` (`so_cccd`);

CREATE INDEX `ix_tk_ho_so_benh_nhan_benh_nhan_id` ON `tk_ho_so_benh_nhan` (`benh_nhan_id`);

CREATE INDEX `ix_tk_ho_so_benh_nhan_dan_toc_id` ON `tk_ho_so_benh_nhan` (`dan_toc_id`);

CREATE INDEX `ix_tk_ho_so_benh_nhan_tai_khoan_id` ON `tk_ho_so_benh_nhan` (`tai_khoan_id`);

CREATE INDEX `ix_tk_nguoi_dung_tam_email` ON `tk_nguoi_dung_tam` (`email`);

CREATE INDEX `ix_tk_nhan_vien_khoa_id` ON `tk_nhan_vien` (`khoa_id`);

CREATE UNIQUE INDEX `ix_tk_tai_khoan_email` ON `tk_tai_khoan` (`email`);

CREATE UNIQUE INDEX `ix_tk_tai_khoan_nhan_vien_id` ON `tk_tai_khoan` (`nhan_vien_id`);

CREATE UNIQUE INDEX `ix_tk_tai_khoan_so_dien_thoai` ON `tk_tai_khoan` (`so_dien_thoai`);

CREATE INDEX `ix_tk_thong_bao_tai_khoan_id` ON `tk_thong_bao` (`tai_khoan_id`);
