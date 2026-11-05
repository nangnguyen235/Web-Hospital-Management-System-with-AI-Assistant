using System;
using Microsoft.EntityFrameworkCore.Migrations;
using MySql.EntityFrameworkCore.Metadata;

#nullable disable

namespace HIS.Modules.HoSoBenhAn.Infrastructure.Migrations
{
    /// <inheritdoc />
    public partial class KhoiTao : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AlterDatabase()
                .Annotation("MySQL:Charset", "utf8mb4");

            migrationBuilder.CreateTable(
                name: "ba_benh_an",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("MySQL:ValueGenerationStrategy", MySQLValueGenerationStrategy.IdentityColumn),
                    phieu_dat_kham_id = table.Column<long>(type: "bigint", nullable: false),
                    lich_kham_id = table.Column<long>(type: "bigint", nullable: false),
                    benh_nhan_id = table.Column<long>(type: "bigint", nullable: false),
                    ho_so_id = table.Column<long>(type: "bigint", nullable: false),
                    trieu_chung = table.Column<string>(type: "text", maxLength: 255, nullable: true),
                    chan_doan = table.Column<string>(type: "text", maxLength: 255, nullable: true),
                    ngay_tai_kham = table.Column<DateOnly>(type: "date", nullable: true),
                    lich_kham_truoc_id = table.Column<long>(type: "bigint", nullable: true),
                    trang_thai = table.Column<string>(type: "varchar(30)", maxLength: 30, nullable: false),
                    file_ghi_am_key = table.Column<string>(type: "varchar(255)", maxLength: 255, nullable: true),
                    da_xoa = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    ngay_tao = table.Column<DateTime>(type: "datetime(6)", nullable: false),
                    ngay_cap_nhat = table.Column<DateTime>(type: "datetime(6)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_ba_benh_an", x => x.id);
                })
                .Annotation("MySQL:Charset", "utf8mb4");

            migrationBuilder.CreateTable(
                name: "ba_danh_gia",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("MySQL:ValueGenerationStrategy", MySQLValueGenerationStrategy.IdentityColumn),
                    nhan_xet = table.Column<string>(type: "text", maxLength: 255, nullable: true),
                    so_diem = table.Column<byte>(type: "tinyint unsigned", nullable: false),
                    benh_nhan_id = table.Column<long>(type: "bigint", nullable: false),
                    bac_si_id = table.Column<long>(type: "bigint", nullable: false),
                    phieu_dat_kham_id = table.Column<long>(type: "bigint", nullable: false),
                    da_xoa = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    ngay_tao = table.Column<DateTime>(type: "datetime(6)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_ba_danh_gia", x => x.id);
                })
                .Annotation("MySQL:Charset", "utf8mb4");

            migrationBuilder.CreateTable(
                name: "ba_don_thuoc",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("MySQL:ValueGenerationStrategy", MySQLValueGenerationStrategy.IdentityColumn),
                    ma_don_thuoc = table.Column<string>(type: "char(12)", fixedLength: true, maxLength: 12, nullable: false),
                    tong_tien = table.Column<decimal>(type: "decimal(12,2)", precision: 12, scale: 2, nullable: false),
                    benh_an_id = table.Column<long>(type: "bigint", nullable: false),
                    da_xoa = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    ngay_tao = table.Column<DateTime>(type: "datetime(6)", nullable: false),
                    ngay_cap_nhat = table.Column<DateTime>(type: "datetime(6)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_ba_don_thuoc", x => x.id);
                    table.ForeignKey(
                        name: "fk_ba_don_thuoc_ba_benh_an_benh_an_id",
                        column: x => x.benh_an_id,
                        principalTable: "ba_benh_an",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Restrict);
                })
                .Annotation("MySQL:Charset", "utf8mb4");

            migrationBuilder.CreateTable(
                name: "ba_xet_nghiem",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("MySQL:ValueGenerationStrategy", MySQLValueGenerationStrategy.IdentityColumn),
                    ma_xet_nghiem = table.Column<string>(type: "varchar(14)", maxLength: 14, nullable: false),
                    so_thu_tu = table.Column<int>(type: "int", nullable: false),
                    ghi_chu = table.Column<string>(type: "text", maxLength: 255, nullable: true),
                    ket_qua = table.Column<string>(type: "text", maxLength: 255, nullable: true),
                    gia = table.Column<decimal>(type: "decimal(12,2)", precision: 12, scale: 2, nullable: false),
                    file_ket_qua_key = table.Column<string>(type: "varchar(255)", maxLength: 255, nullable: true),
                    dich_vu_id = table.Column<int>(type: "int", nullable: false),
                    benh_an_id = table.Column<long>(type: "bigint", nullable: false),
                    phong_id = table.Column<int>(type: "int", nullable: false),
                    dieu_duong_id = table.Column<long>(type: "bigint", nullable: true),
                    trang_thai = table.Column<string>(type: "varchar(30)", maxLength: 30, nullable: false),
                    ngay = table.Column<DateOnly>(type: "date", nullable: false),
                    giao_dich_id = table.Column<long>(type: "bigint", nullable: true),
                    da_thanh_toan = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    da_xoa = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    ngay_tao = table.Column<DateTime>(type: "datetime(6)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_ba_xet_nghiem", x => x.id);
                    table.ForeignKey(
                        name: "fk_ba_xet_nghiem_ba_benh_an_benh_an_id",
                        column: x => x.benh_an_id,
                        principalTable: "ba_benh_an",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Restrict);
                })
                .Annotation("MySQL:Charset", "utf8mb4");

            migrationBuilder.CreateTable(
                name: "ba_chi_tiet_don_thuoc",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("MySQL:ValueGenerationStrategy", MySQLValueGenerationStrategy.IdentityColumn),
                    don_thuoc_id = table.Column<long>(type: "bigint", nullable: false),
                    ma_don_thuoc = table.Column<string>(type: "char(12)", fixedLength: true, maxLength: 12, nullable: false),
                    thuoc_id = table.Column<int>(type: "int", nullable: false),
                    so_luong = table.Column<int>(type: "int", nullable: false),
                    gia = table.Column<decimal>(type: "decimal(12,2)", precision: 12, scale: 2, nullable: false),
                    thanh_tien = table.Column<decimal>(type: "decimal(12,2)", precision: 12, scale: 2, nullable: false),
                    so_lan_moi_ngay = table.Column<int>(type: "int", nullable: false),
                    so_ngay = table.Column<int>(type: "int", nullable: false),
                    so_luong_moi_lan = table.Column<decimal>(type: "decimal(5,2)", precision: 5, scale: 2, nullable: false),
                    sang = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    trua = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    chieu = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    toi = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    huong_dan = table.Column<string>(type: "varchar(255)", maxLength: 255, nullable: true),
                    da_xoa = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    ngay_tao = table.Column<DateTime>(type: "datetime(6)", nullable: false),
                    ngay_cap_nhat = table.Column<DateTime>(type: "datetime(6)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_ba_chi_tiet_don_thuoc", x => x.id);
                    table.ForeignKey(
                        name: "fk_ba_chi_tiet_don_thuoc_ba_don_thuoc_don_thuoc_id",
                        column: x => x.don_thuoc_id,
                        principalTable: "ba_don_thuoc",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Restrict);
                })
                .Annotation("MySQL:Charset", "utf8mb4");

            migrationBuilder.CreateTable(
                name: "ba_nhac_thuoc",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("MySQL:ValueGenerationStrategy", MySQLValueGenerationStrategy.IdentityColumn),
                    chi_tiet_don_thuoc_id = table.Column<long>(type: "bigint", nullable: false),
                    ho_so_id = table.Column<long>(type: "bigint", nullable: false),
                    ngay_bat_dau = table.Column<DateOnly>(type: "date", nullable: false),
                    ngay_ket_thuc = table.Column<DateTime>(type: "datetime(6)", nullable: false),
                    tong_so_luong = table.Column<decimal>(type: "decimal(6,2)", precision: 6, scale: 2, nullable: false),
                    so_luong_con_lai = table.Column<decimal>(type: "decimal(6,2)", precision: 6, scale: 2, nullable: false),
                    da_xoa = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    ngay_tao = table.Column<DateTime>(type: "datetime(6)", nullable: false),
                    ngay_cap_nhat = table.Column<DateTime>(type: "datetime(6)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_ba_nhac_thuoc", x => x.id);
                    table.ForeignKey(
                        name: "fk_ba_nhac_thuoc_ba_chi_tiet_don_thuoc_chi_tiet_don_thuoc_id",
                        column: x => x.chi_tiet_don_thuoc_id,
                        principalTable: "ba_chi_tiet_don_thuoc",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Restrict);
                })
                .Annotation("MySQL:Charset", "utf8mb4");

            migrationBuilder.CreateTable(
                name: "ba_chi_tiet_nhac_thuoc",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("MySQL:ValueGenerationStrategy", MySQLValueGenerationStrategy.IdentityColumn),
                    ma_xac_nhan = table.Column<string>(type: "char(36)", fixedLength: true, maxLength: 36, nullable: false),
                    nhac_thuoc_id = table.Column<long>(type: "bigint", nullable: false),
                    thoi_diem_nhac = table.Column<DateTime>(type: "datetime(6)", nullable: false),
                    thu_tu_nhac = table.Column<int>(type: "int", nullable: false),
                    ten_thuoc = table.Column<string>(type: "varchar(150)", maxLength: 150, nullable: false),
                    don_vi = table.Column<string>(type: "varchar(30)", maxLength: 30, nullable: false),
                    ho_ten = table.Column<string>(type: "varchar(100)", maxLength: 100, nullable: false),
                    so_luong = table.Column<decimal>(type: "decimal(5,2)", precision: 5, scale: 2, nullable: false),
                    da_uong = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    bo_qua = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    da_nhan = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    tai_khoan_id = table.Column<long>(type: "bigint", nullable: false),
                    da_xoa = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    ngay_tao = table.Column<DateTime>(type: "datetime(6)", nullable: false),
                    ngay_cap_nhat = table.Column<DateTime>(type: "datetime(6)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_ba_chi_tiet_nhac_thuoc", x => x.id);
                    table.ForeignKey(
                        name: "fk_ba_chi_tiet_nhac_thuoc_ba_nhac_thuoc_nhac_thuoc_id",
                        column: x => x.nhac_thuoc_id,
                        principalTable: "ba_nhac_thuoc",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Restrict);
                })
                .Annotation("MySQL:Charset", "utf8mb4");

            migrationBuilder.CreateIndex(
                name: "ix_ba_benh_an_benh_nhan_id",
                table: "ba_benh_an",
                column: "benh_nhan_id");

            migrationBuilder.CreateIndex(
                name: "ix_ba_benh_an_ho_so_id",
                table: "ba_benh_an",
                column: "ho_so_id");

            migrationBuilder.CreateIndex(
                name: "ix_ba_benh_an_lich_kham_id_trang_thai",
                table: "ba_benh_an",
                columns: new[] { "lich_kham_id", "trang_thai" });

            migrationBuilder.CreateIndex(
                name: "ix_ba_benh_an_phieu_dat_kham_id",
                table: "ba_benh_an",
                column: "phieu_dat_kham_id",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "ix_ba_chi_tiet_don_thuoc_don_thuoc_id",
                table: "ba_chi_tiet_don_thuoc",
                column: "don_thuoc_id");

            migrationBuilder.CreateIndex(
                name: "ix_ba_chi_tiet_don_thuoc_thuoc_id",
                table: "ba_chi_tiet_don_thuoc",
                column: "thuoc_id");

            migrationBuilder.CreateIndex(
                name: "ix_ba_chi_tiet_nhac_thuoc_nhac_thuoc_id",
                table: "ba_chi_tiet_nhac_thuoc",
                column: "nhac_thuoc_id");

            migrationBuilder.CreateIndex(
                name: "ix_ba_chi_tiet_nhac_thuoc_tai_khoan_id_thoi_diem_nhac",
                table: "ba_chi_tiet_nhac_thuoc",
                columns: new[] { "tai_khoan_id", "thoi_diem_nhac" });

            migrationBuilder.CreateIndex(
                name: "ix_ba_danh_gia_bac_si_id",
                table: "ba_danh_gia",
                column: "bac_si_id");

            migrationBuilder.CreateIndex(
                name: "ix_ba_danh_gia_benh_nhan_id",
                table: "ba_danh_gia",
                column: "benh_nhan_id");

            migrationBuilder.CreateIndex(
                name: "ix_ba_danh_gia_phieu_dat_kham_id",
                table: "ba_danh_gia",
                column: "phieu_dat_kham_id",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "ix_ba_don_thuoc_benh_an_id",
                table: "ba_don_thuoc",
                column: "benh_an_id",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "ix_ba_don_thuoc_ma_don_thuoc",
                table: "ba_don_thuoc",
                column: "ma_don_thuoc",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "ix_ba_nhac_thuoc_chi_tiet_don_thuoc_id",
                table: "ba_nhac_thuoc",
                column: "chi_tiet_don_thuoc_id");

            migrationBuilder.CreateIndex(
                name: "ix_ba_nhac_thuoc_ho_so_id",
                table: "ba_nhac_thuoc",
                column: "ho_so_id");

            migrationBuilder.CreateIndex(
                name: "ix_ba_xet_nghiem_benh_an_id",
                table: "ba_xet_nghiem",
                column: "benh_an_id");

            migrationBuilder.CreateIndex(
                name: "ix_ba_xet_nghiem_dich_vu_id",
                table: "ba_xet_nghiem",
                column: "dich_vu_id");

            migrationBuilder.CreateIndex(
                name: "ix_ba_xet_nghiem_dieu_duong_id",
                table: "ba_xet_nghiem",
                column: "dieu_duong_id");

            migrationBuilder.CreateIndex(
                name: "ix_ba_xet_nghiem_giao_dich_id",
                table: "ba_xet_nghiem",
                column: "giao_dich_id",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "ix_ba_xet_nghiem_ma_xet_nghiem",
                table: "ba_xet_nghiem",
                column: "ma_xet_nghiem",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "ix_ba_xet_nghiem_phong_id_ngay",
                table: "ba_xet_nghiem",
                columns: new[] { "phong_id", "ngay" });
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "ba_chi_tiet_nhac_thuoc");

            migrationBuilder.DropTable(
                name: "ba_danh_gia");

            migrationBuilder.DropTable(
                name: "ba_xet_nghiem");

            migrationBuilder.DropTable(
                name: "ba_nhac_thuoc");

            migrationBuilder.DropTable(
                name: "ba_chi_tiet_don_thuoc");

            migrationBuilder.DropTable(
                name: "ba_don_thuoc");

            migrationBuilder.DropTable(
                name: "ba_benh_an");
        }
    }
}
