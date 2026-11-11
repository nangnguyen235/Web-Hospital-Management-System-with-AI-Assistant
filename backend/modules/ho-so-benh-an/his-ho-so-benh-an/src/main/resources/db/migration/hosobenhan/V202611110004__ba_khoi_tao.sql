-- Module HoSoBenhAn (tiền tố ba_): khởi tạo bảng — chuyển từ migration EF KhoiTao của bản .NET (mo-hinh-du-lieu.md mục 2).
-- KHÔNG sửa file đã chạy; thay đổi sau thêm file V mới (modular-monolith.md mục 4).

CREATE TABLE `ba_benh_an` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `phieu_dat_kham_id` bigint NOT NULL,
    `lich_kham_id` bigint NOT NULL,
    `benh_nhan_id` bigint NOT NULL,
    `ho_so_id` bigint NOT NULL,
    `trieu_chung` text NULL,
    `chan_doan` text NULL,
    `ngay_tai_kham` date NULL,
    `lich_kham_truoc_id` bigint NULL,
    `trang_thai` varchar(30) NOT NULL,
    `file_ghi_am_key` varchar(255) NULL,
    `da_xoa` tinyint(1) NOT NULL,
    `ngay_tao` datetime(6) NOT NULL,
    `ngay_cap_nhat` datetime(6) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `ba_danh_gia` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `nhan_xet` text NULL,
    `so_diem` tinyint unsigned NOT NULL,
    `benh_nhan_id` bigint NOT NULL,
    `bac_si_id` bigint NOT NULL,
    `phieu_dat_kham_id` bigint NOT NULL,
    `da_xoa` tinyint(1) NOT NULL,
    `ngay_tao` datetime(6) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `ba_don_thuoc` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `ma_don_thuoc` char(12) NOT NULL,
    `tong_tien` decimal(12,2) NOT NULL,
    `benh_an_id` bigint NOT NULL,
    `da_xoa` tinyint(1) NOT NULL,
    `ngay_tao` datetime(6) NOT NULL,
    `ngay_cap_nhat` datetime(6) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_ba_don_thuoc_ba_benh_an_benh_an_id` FOREIGN KEY (`benh_an_id`) REFERENCES `ba_benh_an` (`id`) ON DELETE RESTRICT
);

CREATE TABLE `ba_xet_nghiem` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `ma_xet_nghiem` varchar(14) NOT NULL,
    `so_thu_tu` int NOT NULL,
    `ghi_chu` text NULL,
    `ket_qua` text NULL,
    `gia` decimal(12,2) NOT NULL,
    `file_ket_qua_key` varchar(255) NULL,
    `dich_vu_id` int NOT NULL,
    `benh_an_id` bigint NOT NULL,
    `phong_id` int NOT NULL,
    `dieu_duong_id` bigint NULL,
    `trang_thai` varchar(30) NOT NULL,
    `ngay` date NOT NULL,
    `giao_dich_id` bigint NULL,
    `da_thanh_toan` tinyint(1) NOT NULL,
    `da_xoa` tinyint(1) NOT NULL,
    `ngay_tao` datetime(6) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_ba_xet_nghiem_ba_benh_an_benh_an_id` FOREIGN KEY (`benh_an_id`) REFERENCES `ba_benh_an` (`id`) ON DELETE RESTRICT
);

CREATE TABLE `ba_chi_tiet_don_thuoc` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `don_thuoc_id` bigint NOT NULL,
    `ma_don_thuoc` char(12) NOT NULL,
    `thuoc_id` int NOT NULL,
    `so_luong` int NOT NULL,
    `gia` decimal(12,2) NOT NULL,
    `thanh_tien` decimal(12,2) NOT NULL,
    `so_lan_moi_ngay` int NOT NULL,
    `so_ngay` int NOT NULL,
    `so_luong_moi_lan` decimal(5,2) NOT NULL,
    `sang` tinyint(1) NOT NULL,
    `trua` tinyint(1) NOT NULL,
    `chieu` tinyint(1) NOT NULL,
    `toi` tinyint(1) NOT NULL,
    `huong_dan` varchar(255) NULL,
    `da_xoa` tinyint(1) NOT NULL,
    `ngay_tao` datetime(6) NOT NULL,
    `ngay_cap_nhat` datetime(6) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_ba_chi_tiet_don_thuoc_ba_don_thuoc_don_thuoc_id` FOREIGN KEY (`don_thuoc_id`) REFERENCES `ba_don_thuoc` (`id`) ON DELETE RESTRICT
);

CREATE TABLE `ba_nhac_thuoc` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `chi_tiet_don_thuoc_id` bigint NOT NULL,
    `ho_so_id` bigint NOT NULL,
    `ngay_bat_dau` date NOT NULL,
    `ngay_ket_thuc` datetime(6) NOT NULL,
    `tong_so_luong` decimal(6,2) NOT NULL,
    `so_luong_con_lai` decimal(6,2) NOT NULL,
    `da_xoa` tinyint(1) NOT NULL,
    `ngay_tao` datetime(6) NOT NULL,
    `ngay_cap_nhat` datetime(6) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_ba_nhac_thuoc_ba_chi_tiet_don_thuoc_chi_tiet_don_thuoc_id` FOREIGN KEY (`chi_tiet_don_thuoc_id`) REFERENCES `ba_chi_tiet_don_thuoc` (`id`) ON DELETE RESTRICT
);

CREATE TABLE `ba_chi_tiet_nhac_thuoc` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `ma_xac_nhan` char(36) NOT NULL,
    `nhac_thuoc_id` bigint NOT NULL,
    `thoi_diem_nhac` datetime(6) NOT NULL,
    `thu_tu_nhac` int NOT NULL,
    `ten_thuoc` varchar(150) NOT NULL,
    `don_vi` varchar(30) NOT NULL,
    `ho_ten` varchar(100) NOT NULL,
    `so_luong` decimal(5,2) NOT NULL,
    `da_uong` tinyint(1) NOT NULL,
    `bo_qua` tinyint(1) NOT NULL,
    `da_nhan` tinyint(1) NOT NULL,
    `tai_khoan_id` bigint NOT NULL,
    `da_xoa` tinyint(1) NOT NULL,
    `ngay_tao` datetime(6) NOT NULL,
    `ngay_cap_nhat` datetime(6) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_ba_chi_tiet_nhac_thuoc_ba_nhac_thuoc_nhac_thuoc_id` FOREIGN KEY (`nhac_thuoc_id`) REFERENCES `ba_nhac_thuoc` (`id`) ON DELETE RESTRICT
);

CREATE INDEX `ix_ba_benh_an_benh_nhan_id` ON `ba_benh_an` (`benh_nhan_id`);

CREATE INDEX `ix_ba_benh_an_ho_so_id` ON `ba_benh_an` (`ho_so_id`);

CREATE INDEX `ix_ba_benh_an_lich_kham_id_trang_thai` ON `ba_benh_an` (`lich_kham_id`, `trang_thai`);

CREATE UNIQUE INDEX `ix_ba_benh_an_phieu_dat_kham_id` ON `ba_benh_an` (`phieu_dat_kham_id`);

CREATE INDEX `ix_ba_chi_tiet_don_thuoc_don_thuoc_id` ON `ba_chi_tiet_don_thuoc` (`don_thuoc_id`);

CREATE INDEX `ix_ba_chi_tiet_don_thuoc_thuoc_id` ON `ba_chi_tiet_don_thuoc` (`thuoc_id`);

CREATE INDEX `ix_ba_chi_tiet_nhac_thuoc_nhac_thuoc_id` ON `ba_chi_tiet_nhac_thuoc` (`nhac_thuoc_id`);

CREATE INDEX `ix_ba_chi_tiet_nhac_thuoc_tai_khoan_id_thoi_diem_nhac` ON `ba_chi_tiet_nhac_thuoc` (`tai_khoan_id`, `thoi_diem_nhac`);

CREATE INDEX `ix_ba_danh_gia_bac_si_id` ON `ba_danh_gia` (`bac_si_id`);

CREATE INDEX `ix_ba_danh_gia_benh_nhan_id` ON `ba_danh_gia` (`benh_nhan_id`);

CREATE UNIQUE INDEX `ix_ba_danh_gia_phieu_dat_kham_id` ON `ba_danh_gia` (`phieu_dat_kham_id`);

CREATE UNIQUE INDEX `ix_ba_don_thuoc_benh_an_id` ON `ba_don_thuoc` (`benh_an_id`);

CREATE UNIQUE INDEX `ix_ba_don_thuoc_ma_don_thuoc` ON `ba_don_thuoc` (`ma_don_thuoc`);

CREATE INDEX `ix_ba_nhac_thuoc_chi_tiet_don_thuoc_id` ON `ba_nhac_thuoc` (`chi_tiet_don_thuoc_id`);

CREATE INDEX `ix_ba_nhac_thuoc_ho_so_id` ON `ba_nhac_thuoc` (`ho_so_id`);

CREATE INDEX `ix_ba_xet_nghiem_benh_an_id` ON `ba_xet_nghiem` (`benh_an_id`);

CREATE INDEX `ix_ba_xet_nghiem_dich_vu_id` ON `ba_xet_nghiem` (`dich_vu_id`);

CREATE INDEX `ix_ba_xet_nghiem_dieu_duong_id` ON `ba_xet_nghiem` (`dieu_duong_id`);

CREATE UNIQUE INDEX `ix_ba_xet_nghiem_giao_dich_id` ON `ba_xet_nghiem` (`giao_dich_id`);

CREATE UNIQUE INDEX `ix_ba_xet_nghiem_ma_xet_nghiem` ON `ba_xet_nghiem` (`ma_xet_nghiem`);

CREATE INDEX `ix_ba_xet_nghiem_phong_id_ngay` ON `ba_xet_nghiem` (`phong_id`, `ngay`);
