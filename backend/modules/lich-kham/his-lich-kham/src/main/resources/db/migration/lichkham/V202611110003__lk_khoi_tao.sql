-- Module LichKham (tiền tố lk_): khởi tạo bảng — chuyển từ migration EF KhoiTao của bản .NET (mo-hinh-du-lieu.md mục 2).
-- KHÔNG sửa file đã chạy; thay đổi sau thêm file V mới (modular-monolith.md mục 4).

CREATE TABLE `lk_lich_dieu_duong` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `dieu_duong_id` bigint NOT NULL,
    `ten_dieu_duong` varchar(100) NOT NULL,
    `phong_id` int NOT NULL,
    `ten_phong` varchar(100) NOT NULL,
    `thu_trong_tuan` tinyint unsigned NOT NULL,
    `buoi` varchar(30) NOT NULL,
    `da_xoa` tinyint(1) NOT NULL,
    `ngay_tao` datetime(6) NOT NULL,
    `ngay_cap_nhat` datetime(6) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `lk_lich_kham_bac_si` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `bac_si_id` bigint NOT NULL,
    `phong_id` int NOT NULL,
    `dich_vu_id` int NOT NULL,
    `so_luot` int NOT NULL,
    `so_dang_goi` int NOT NULL,
    `thu_trong_tuan` tinyint unsigned NOT NULL,
    `ngay` date NOT NULL,
    `buoi` varchar(30) NOT NULL,
    `phien_ban` int NOT NULL,
    `da_xoa` tinyint(1) NOT NULL,
    `ngay_tao` datetime(6) NOT NULL,
    `ngay_cap_nhat` datetime(6) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `lk_khung_gio` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `lich_kham_id` bigint NOT NULL,
    `so_luong_toi_da` int NOT NULL,
    `so_luong_da_dat` int NOT NULL,
    `so_bat_dau` int NOT NULL,
    `chi_so_khung` tinyint unsigned NOT NULL,
    `ngay` date NOT NULL,
    `phien_ban` int NOT NULL,
    `da_xoa` tinyint(1) NOT NULL,
    `ngay_tao` datetime(6) NOT NULL,
    `ngay_cap_nhat` datetime(6) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_lk_khung_gio_lk_lich_kham_bac_si_lich_kham_id` FOREIGN KEY (`lich_kham_id`) REFERENCES `lk_lich_kham_bac_si` (`id`) ON DELETE RESTRICT
);

CREATE TABLE `lk_phieu_dat_kham` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `ma_phieu` char(12) NOT NULL,
    `so_thu_tu` int NOT NULL,
    `gia_dich_vu` decimal(12,2) NOT NULL,
    `da_danh_gia` tinyint(1) NOT NULL,
    `ho_so_id` bigint NOT NULL,
    `khung_gio_id` bigint NOT NULL,
    `benh_an_id` bigint NULL,
    `giao_dich_id` bigint NOT NULL,
    `kenh_dat` varchar(30) NOT NULL,
    `da_huy` tinyint(1) NOT NULL,
    `da_xoa` tinyint(1) NOT NULL,
    `ngay_tao` datetime(6) NOT NULL,
    `ngay_cap_nhat` datetime(6) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_lk_phieu_dat_kham_lk_khung_gio_khung_gio_id` FOREIGN KEY (`khung_gio_id`) REFERENCES `lk_khung_gio` (`id`) ON DELETE RESTRICT
);

CREATE UNIQUE INDEX `ix_lk_khung_gio_lich_kham_id_chi_so_khung` ON `lk_khung_gio` (`lich_kham_id`, `chi_so_khung`);

CREATE INDEX `ix_lk_lich_dieu_duong_dieu_duong_id` ON `lk_lich_dieu_duong` (`dieu_duong_id`);

CREATE INDEX `ix_lk_lich_dieu_duong_phong_id` ON `lk_lich_dieu_duong` (`phong_id`);

CREATE INDEX `ix_lk_lich_kham_bac_si_bac_si_id` ON `lk_lich_kham_bac_si` (`bac_si_id`);

CREATE INDEX `ix_lk_lich_kham_bac_si_dich_vu_id` ON `lk_lich_kham_bac_si` (`dich_vu_id`);

CREATE INDEX `ix_lk_lich_kham_bac_si_ngay_buoi` ON `lk_lich_kham_bac_si` (`ngay`, `buoi`);

CREATE INDEX `ix_lk_lich_kham_bac_si_phong_id` ON `lk_lich_kham_bac_si` (`phong_id`);

CREATE INDEX `ix_lk_phieu_dat_kham_benh_an_id` ON `lk_phieu_dat_kham` (`benh_an_id`);

CREATE INDEX `ix_lk_phieu_dat_kham_giao_dich_id` ON `lk_phieu_dat_kham` (`giao_dich_id`);

CREATE INDEX `ix_lk_phieu_dat_kham_ho_so_id` ON `lk_phieu_dat_kham` (`ho_so_id`);

CREATE INDEX `ix_lk_phieu_dat_kham_khung_gio_id_da_xoa_da_huy` ON `lk_phieu_dat_kham` (`khung_gio_id`, `da_xoa`, `da_huy`);

CREATE UNIQUE INDEX `ix_lk_phieu_dat_kham_ma_phieu` ON `lk_phieu_dat_kham` (`ma_phieu`);
