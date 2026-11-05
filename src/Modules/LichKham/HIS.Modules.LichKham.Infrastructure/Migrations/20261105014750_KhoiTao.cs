using System;
using Microsoft.EntityFrameworkCore.Migrations;
using MySql.EntityFrameworkCore.Metadata;

#nullable disable

namespace HIS.Modules.LichKham.Infrastructure.Migrations
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
                name: "lk_lich_dieu_duong",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("MySQL:ValueGenerationStrategy", MySQLValueGenerationStrategy.IdentityColumn),
                    dieu_duong_id = table.Column<long>(type: "bigint", nullable: false),
                    ten_dieu_duong = table.Column<string>(type: "varchar(100)", maxLength: 100, nullable: false),
                    phong_id = table.Column<int>(type: "int", nullable: false),
                    ten_phong = table.Column<string>(type: "varchar(100)", maxLength: 100, nullable: false),
                    thu_trong_tuan = table.Column<byte>(type: "tinyint unsigned", nullable: false),
                    buoi = table.Column<string>(type: "varchar(30)", maxLength: 30, nullable: false),
                    da_xoa = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    ngay_tao = table.Column<DateTime>(type: "datetime(6)", nullable: false),
                    ngay_cap_nhat = table.Column<DateTime>(type: "datetime(6)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_lk_lich_dieu_duong", x => x.id);
                })
                .Annotation("MySQL:Charset", "utf8mb4");

            migrationBuilder.CreateTable(
                name: "lk_lich_kham_bac_si",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("MySQL:ValueGenerationStrategy", MySQLValueGenerationStrategy.IdentityColumn),
                    bac_si_id = table.Column<long>(type: "bigint", nullable: false),
                    phong_id = table.Column<int>(type: "int", nullable: false),
                    dich_vu_id = table.Column<int>(type: "int", nullable: false),
                    so_luot = table.Column<int>(type: "int", nullable: false),
                    so_dang_goi = table.Column<int>(type: "int", nullable: false),
                    thu_trong_tuan = table.Column<byte>(type: "tinyint unsigned", nullable: false),
                    ngay = table.Column<DateOnly>(type: "date", nullable: false),
                    buoi = table.Column<string>(type: "varchar(30)", maxLength: 30, nullable: false),
                    phien_ban = table.Column<int>(type: "int", nullable: false),
                    da_xoa = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    ngay_tao = table.Column<DateTime>(type: "datetime(6)", nullable: false),
                    ngay_cap_nhat = table.Column<DateTime>(type: "datetime(6)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_lk_lich_kham_bac_si", x => x.id);
                })
                .Annotation("MySQL:Charset", "utf8mb4");

            migrationBuilder.CreateTable(
                name: "lk_khung_gio",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("MySQL:ValueGenerationStrategy", MySQLValueGenerationStrategy.IdentityColumn),
                    lich_kham_id = table.Column<long>(type: "bigint", nullable: false),
                    so_luong_toi_da = table.Column<int>(type: "int", nullable: false),
                    so_luong_da_dat = table.Column<int>(type: "int", nullable: false),
                    so_bat_dau = table.Column<int>(type: "int", nullable: false),
                    chi_so_khung = table.Column<byte>(type: "tinyint unsigned", nullable: false),
                    ngay = table.Column<DateOnly>(type: "date", nullable: false),
                    phien_ban = table.Column<int>(type: "int", nullable: false),
                    da_xoa = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    ngay_tao = table.Column<DateTime>(type: "datetime(6)", nullable: false),
                    ngay_cap_nhat = table.Column<DateTime>(type: "datetime(6)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_lk_khung_gio", x => x.id);
                    table.ForeignKey(
                        name: "fk_lk_khung_gio_lk_lich_kham_bac_si_lich_kham_id",
                        column: x => x.lich_kham_id,
                        principalTable: "lk_lich_kham_bac_si",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Restrict);
                })
                .Annotation("MySQL:Charset", "utf8mb4");

            migrationBuilder.CreateTable(
                name: "lk_phieu_dat_kham",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("MySQL:ValueGenerationStrategy", MySQLValueGenerationStrategy.IdentityColumn),
                    ma_phieu = table.Column<string>(type: "char(12)", fixedLength: true, maxLength: 12, nullable: false),
                    so_thu_tu = table.Column<int>(type: "int", nullable: false),
                    gia_dich_vu = table.Column<decimal>(type: "decimal(12,2)", precision: 12, scale: 2, nullable: false),
                    da_danh_gia = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    ho_so_id = table.Column<long>(type: "bigint", nullable: false),
                    khung_gio_id = table.Column<long>(type: "bigint", nullable: false),
                    benh_an_id = table.Column<long>(type: "bigint", nullable: true),
                    giao_dich_id = table.Column<long>(type: "bigint", nullable: false),
                    kenh_dat = table.Column<string>(type: "varchar(30)", maxLength: 30, nullable: false),
                    da_huy = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    da_xoa = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    ngay_tao = table.Column<DateTime>(type: "datetime(6)", nullable: false),
                    ngay_cap_nhat = table.Column<DateTime>(type: "datetime(6)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_lk_phieu_dat_kham", x => x.id);
                    table.ForeignKey(
                        name: "fk_lk_phieu_dat_kham_lk_khung_gio_khung_gio_id",
                        column: x => x.khung_gio_id,
                        principalTable: "lk_khung_gio",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Restrict);
                })
                .Annotation("MySQL:Charset", "utf8mb4");

            migrationBuilder.CreateIndex(
                name: "ix_lk_khung_gio_lich_kham_id_chi_so_khung",
                table: "lk_khung_gio",
                columns: new[] { "lich_kham_id", "chi_so_khung" },
                unique: true);

            migrationBuilder.CreateIndex(
                name: "ix_lk_lich_dieu_duong_dieu_duong_id",
                table: "lk_lich_dieu_duong",
                column: "dieu_duong_id");

            migrationBuilder.CreateIndex(
                name: "ix_lk_lich_dieu_duong_phong_id",
                table: "lk_lich_dieu_duong",
                column: "phong_id");

            migrationBuilder.CreateIndex(
                name: "ix_lk_lich_kham_bac_si_bac_si_id",
                table: "lk_lich_kham_bac_si",
                column: "bac_si_id");

            migrationBuilder.CreateIndex(
                name: "ix_lk_lich_kham_bac_si_dich_vu_id",
                table: "lk_lich_kham_bac_si",
                column: "dich_vu_id");

            migrationBuilder.CreateIndex(
                name: "ix_lk_lich_kham_bac_si_ngay_buoi",
                table: "lk_lich_kham_bac_si",
                columns: new[] { "ngay", "buoi" });

            migrationBuilder.CreateIndex(
                name: "ix_lk_lich_kham_bac_si_phong_id",
                table: "lk_lich_kham_bac_si",
                column: "phong_id");

            migrationBuilder.CreateIndex(
                name: "ix_lk_phieu_dat_kham_benh_an_id",
                table: "lk_phieu_dat_kham",
                column: "benh_an_id");

            migrationBuilder.CreateIndex(
                name: "ix_lk_phieu_dat_kham_giao_dich_id",
                table: "lk_phieu_dat_kham",
                column: "giao_dich_id");

            migrationBuilder.CreateIndex(
                name: "ix_lk_phieu_dat_kham_ho_so_id",
                table: "lk_phieu_dat_kham",
                column: "ho_so_id");

            migrationBuilder.CreateIndex(
                name: "ix_lk_phieu_dat_kham_khung_gio_id_da_xoa_da_huy",
                table: "lk_phieu_dat_kham",
                columns: new[] { "khung_gio_id", "da_xoa", "da_huy" });

            migrationBuilder.CreateIndex(
                name: "ix_lk_phieu_dat_kham_ma_phieu",
                table: "lk_phieu_dat_kham",
                column: "ma_phieu",
                unique: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "lk_lich_dieu_duong");

            migrationBuilder.DropTable(
                name: "lk_phieu_dat_kham");

            migrationBuilder.DropTable(
                name: "lk_khung_gio");

            migrationBuilder.DropTable(
                name: "lk_lich_kham_bac_si");
        }
    }
}
