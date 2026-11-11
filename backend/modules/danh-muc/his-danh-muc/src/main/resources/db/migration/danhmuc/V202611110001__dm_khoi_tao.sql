-- Module DanhMuc (tiền tố dm_): khởi tạo bảng — chuyển từ migration EF KhoiTao của bản .NET (mo-hinh-du-lieu.md mục 2).
-- KHÔNG sửa file đã chạy; thay đổi sau thêm file V mới (modular-monolith.md mục 4).

CREATE TABLE `dm_dan_toc` (
    `id` int NOT NULL AUTO_INCREMENT,
    `ten_dan_toc` varchar(50) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `dm_khoa` (
    `id` int NOT NULL AUTO_INCREMENT,
    `ten_khoa` varchar(100) NOT NULL,
    `mo_ta` text NULL,
    `tu_khoa_trieu_chung` text NULL,
    `da_xoa` tinyint(1) NOT NULL,
    `ngay_tao` datetime(6) NOT NULL,
    `ngay_cap_nhat` datetime(6) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `dm_thuoc` (
    `id` int NOT NULL AUTO_INCREMENT,
    `ten_thuoc` varchar(150) NOT NULL,
    `gia` decimal(12,2) NOT NULL,
    `so_luong` int NOT NULL,
    `don_vi` varchar(30) NOT NULL,
    `da_xoa` tinyint(1) NOT NULL,
    `ngay_tao` datetime(6) NOT NULL,
    `ngay_cap_nhat` datetime(6) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `dm_dich_vu` (
    `id` int NOT NULL AUTO_INCREMENT,
    `ten_dich_vu` varchar(150) NOT NULL,
    `mo_ta` text NULL,
    `khoa_id` int NOT NULL,
    `loai_dich_vu` varchar(30) NOT NULL,
    `gia` decimal(12,2) NOT NULL,
    `da_xoa` tinyint(1) NOT NULL,
    `ngay_tao` datetime(6) NOT NULL,
    `ngay_cap_nhat` datetime(6) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_dm_dich_vu_dm_khoa_khoa_id` FOREIGN KEY (`khoa_id`) REFERENCES `dm_khoa` (`id`) ON DELETE RESTRICT
);

CREATE TABLE `dm_phong` (
    `id` int NOT NULL AUTO_INCREMENT,
    `ten_phong` varchar(100) NOT NULL,
    `mo_ta` varchar(255) NULL,
    `khoa_id` int NOT NULL,
    `loai_phong` varchar(30) NOT NULL,
    `dich_vu_id` int NULL,
    `so_xn_dang_goi` int NOT NULL,
    `so_xn_lon_nhat` int NOT NULL,
    `ngay_dem_xn` date NULL,
    `phien_ban` int NOT NULL,
    `da_xoa` tinyint(1) NOT NULL,
    `ngay_tao` datetime(6) NOT NULL,
    `ngay_cap_nhat` datetime(6) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_dm_phong_dich_vu_dich_vu_id` FOREIGN KEY (`dich_vu_id`) REFERENCES `dm_dich_vu` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_dm_phong_dm_khoa_khoa_id` FOREIGN KEY (`khoa_id`) REFERENCES `dm_khoa` (`id`) ON DELETE RESTRICT
);

INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (1, 'Kinh');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (2, 'Tày');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (3, 'Thái');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (4, 'Mường');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (5, 'Khmer');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (6, 'Hoa');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (7, 'Nùng');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (8, 'H''Mông');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (9, 'Dao');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (10, 'Gia Rai');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (11, 'Ê Đê');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (12, 'Ba Na');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (13, 'Sán Chay');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (14, 'Chăm');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (15, 'Cơ Ho');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (16, 'Xơ Đăng');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (17, 'Sán Dìu');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (18, 'Hrê');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (19, 'Ra Glai');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (20, 'Mnông');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (21, 'Thổ');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (22, 'Xtiêng');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (23, 'Khơ Mú');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (24, 'Bru - Vân Kiều');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (25, 'Cơ Tu');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (26, 'Giáy');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (27, 'Tà Ôi');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (28, 'Mạ');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (29, 'Giẻ Triêng');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (30, 'Co');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (31, 'Chơ Ro');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (32, 'Xinh Mun');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (33, 'Hà Nhì');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (34, 'Chu Ru');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (35, 'Lào');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (36, 'La Chí');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (37, 'Kháng');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (38, 'Phù Lá');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (39, 'La Hủ');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (40, 'La Ha');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (41, 'Pà Thẻn');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (42, 'Lự');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (43, 'Ngái');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (44, 'Chứt');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (45, 'Lô Lô');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (46, 'Mảng');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (47, 'Cơ Lao');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (48, 'Bố Y');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (49, 'Cống');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (50, 'Si La');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (51, 'Pu Péo');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (52, 'Rơ Măm');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (53, 'Brâu');
INSERT INTO `dm_dan_toc` (`id`, `ten_dan_toc`)
VALUES (54, 'Ơ Đu');
CREATE INDEX `ix_dm_dich_vu_khoa_id` ON `dm_dich_vu` (`khoa_id`);

CREATE INDEX `ix_dm_phong_dich_vu_id` ON `dm_phong` (`dich_vu_id`);

CREATE INDEX `ix_dm_phong_khoa_id` ON `dm_phong` (`khoa_id`);
