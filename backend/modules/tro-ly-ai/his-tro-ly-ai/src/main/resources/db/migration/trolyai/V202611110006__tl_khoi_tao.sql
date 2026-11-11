-- Module TroLyAi (tiền tố tl_): khởi tạo bảng — chuyển từ migration EF KhoiTao của bản .NET (mo-hinh-du-lieu.md mục 2).
-- KHÔNG sửa file đã chạy; thay đổi sau thêm file V mới (modular-monolith.md mục 4).

CREATE TABLE `tl_hoi_thoai` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `tai_khoan_id` bigint NOT NULL,
    `trang_thai` varchar(30) NOT NULL,
    `so_thu_tu_lon_nhat` int NOT NULL,
    `ngay_tao` datetime(6) NOT NULL,
    `ngay_cap_nhat` datetime(6) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `tl_tin_nhan` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `hoi_thoai_id` bigint NOT NULL,
    `noi_dung` text NOT NULL,
    `so_thu_tu` int NOT NULL,
    `la_tro_ly` tinyint(1) NOT NULL,
    `ngay_tao` datetime(6) NOT NULL,
    `ngay_cap_nhat` datetime(6) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_tl_tin_nhan_tl_hoi_thoai_hoi_thoai_id` FOREIGN KEY (`hoi_thoai_id`) REFERENCES `tl_hoi_thoai` (`id`) ON DELETE RESTRICT
);

CREATE INDEX `ix_tl_hoi_thoai_tai_khoan_id` ON `tl_hoi_thoai` (`tai_khoan_id`);

CREATE UNIQUE INDEX `ix_tl_tin_nhan_hoi_thoai_id_so_thu_tu` ON `tl_tin_nhan` (`hoi_thoai_id`, `so_thu_tu`);
