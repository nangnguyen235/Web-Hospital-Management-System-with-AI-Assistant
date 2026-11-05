using System;
using Microsoft.EntityFrameworkCore.Migrations;
using MySql.EntityFrameworkCore.Metadata;

#nullable disable

#pragma warning disable CA1814 // Prefer jagged arrays over multidimensional

namespace HIS.Modules.DanhMuc.Infrastructure.Migrations
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
                name: "dm_dan_toc",
                columns: table => new
                {
                    id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("MySQL:ValueGenerationStrategy", MySQLValueGenerationStrategy.IdentityColumn),
                    ten_dan_toc = table.Column<string>(type: "varchar(50)", maxLength: 50, nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_dm_dan_toc", x => x.id);
                })
                .Annotation("MySQL:Charset", "utf8mb4");

            migrationBuilder.CreateTable(
                name: "dm_khoa",
                columns: table => new
                {
                    id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("MySQL:ValueGenerationStrategy", MySQLValueGenerationStrategy.IdentityColumn),
                    ten_khoa = table.Column<string>(type: "varchar(100)", maxLength: 100, nullable: false),
                    mo_ta = table.Column<string>(type: "text", maxLength: 255, nullable: true),
                    tu_khoa_trieu_chung = table.Column<string>(type: "text", maxLength: 255, nullable: true),
                    da_xoa = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    ngay_tao = table.Column<DateTime>(type: "datetime(6)", nullable: false),
                    ngay_cap_nhat = table.Column<DateTime>(type: "datetime(6)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_dm_khoa", x => x.id);
                })
                .Annotation("MySQL:Charset", "utf8mb4");

            migrationBuilder.CreateTable(
                name: "dm_thuoc",
                columns: table => new
                {
                    id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("MySQL:ValueGenerationStrategy", MySQLValueGenerationStrategy.IdentityColumn),
                    ten_thuoc = table.Column<string>(type: "varchar(150)", maxLength: 150, nullable: false),
                    gia = table.Column<decimal>(type: "decimal(12,2)", precision: 12, scale: 2, nullable: false),
                    so_luong = table.Column<int>(type: "int", nullable: false),
                    don_vi = table.Column<string>(type: "varchar(30)", maxLength: 30, nullable: false),
                    da_xoa = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    ngay_tao = table.Column<DateTime>(type: "datetime(6)", nullable: false),
                    ngay_cap_nhat = table.Column<DateTime>(type: "datetime(6)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_dm_thuoc", x => x.id);
                })
                .Annotation("MySQL:Charset", "utf8mb4");

            migrationBuilder.CreateTable(
                name: "dm_dich_vu",
                columns: table => new
                {
                    id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("MySQL:ValueGenerationStrategy", MySQLValueGenerationStrategy.IdentityColumn),
                    ten_dich_vu = table.Column<string>(type: "varchar(150)", maxLength: 150, nullable: false),
                    mo_ta = table.Column<string>(type: "text", maxLength: 255, nullable: true),
                    khoa_id = table.Column<int>(type: "int", nullable: false),
                    loai_dich_vu = table.Column<string>(type: "varchar(30)", maxLength: 30, nullable: false),
                    gia = table.Column<decimal>(type: "decimal(12,2)", precision: 12, scale: 2, nullable: false),
                    da_xoa = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    ngay_tao = table.Column<DateTime>(type: "datetime(6)", nullable: false),
                    ngay_cap_nhat = table.Column<DateTime>(type: "datetime(6)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_dm_dich_vu", x => x.id);
                    table.ForeignKey(
                        name: "fk_dm_dich_vu_dm_khoa_khoa_id",
                        column: x => x.khoa_id,
                        principalTable: "dm_khoa",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Restrict);
                })
                .Annotation("MySQL:Charset", "utf8mb4");

            migrationBuilder.CreateTable(
                name: "dm_phong",
                columns: table => new
                {
                    id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("MySQL:ValueGenerationStrategy", MySQLValueGenerationStrategy.IdentityColumn),
                    ten_phong = table.Column<string>(type: "varchar(100)", maxLength: 100, nullable: false),
                    mo_ta = table.Column<string>(type: "varchar(255)", maxLength: 255, nullable: true),
                    khoa_id = table.Column<int>(type: "int", nullable: false),
                    loai_phong = table.Column<string>(type: "varchar(30)", maxLength: 30, nullable: false),
                    dich_vu_id = table.Column<int>(type: "int", nullable: true),
                    so_xn_dang_goi = table.Column<int>(type: "int", nullable: false),
                    so_xn_lon_nhat = table.Column<int>(type: "int", nullable: false),
                    ngay_dem_xn = table.Column<DateOnly>(type: "date", nullable: true),
                    phien_ban = table.Column<int>(type: "int", nullable: false),
                    da_xoa = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    ngay_tao = table.Column<DateTime>(type: "datetime(6)", nullable: false),
                    ngay_cap_nhat = table.Column<DateTime>(type: "datetime(6)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_dm_phong", x => x.id);
                    table.ForeignKey(
                        name: "fk_dm_phong_dich_vu_dich_vu_id",
                        column: x => x.dich_vu_id,
                        principalTable: "dm_dich_vu",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Restrict);
                    table.ForeignKey(
                        name: "fk_dm_phong_dm_khoa_khoa_id",
                        column: x => x.khoa_id,
                        principalTable: "dm_khoa",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Restrict);
                })
                .Annotation("MySQL:Charset", "utf8mb4");

            migrationBuilder.InsertData(
                table: "dm_dan_toc",
                columns: new[] { "id", "ten_dan_toc" },
                values: new object[,]
                {
                    { 1, "Kinh" },
                    { 2, "Tày" },
                    { 3, "Thái" },
                    { 4, "Mường" },
                    { 5, "Khmer" },
                    { 6, "Hoa" },
                    { 7, "Nùng" },
                    { 8, "H'Mông" },
                    { 9, "Dao" },
                    { 10, "Gia Rai" },
                    { 11, "Ê Đê" },
                    { 12, "Ba Na" },
                    { 13, "Sán Chay" },
                    { 14, "Chăm" },
                    { 15, "Cơ Ho" },
                    { 16, "Xơ Đăng" },
                    { 17, "Sán Dìu" },
                    { 18, "Hrê" },
                    { 19, "Ra Glai" },
                    { 20, "Mnông" },
                    { 21, "Thổ" },
                    { 22, "Xtiêng" },
                    { 23, "Khơ Mú" },
                    { 24, "Bru - Vân Kiều" },
                    { 25, "Cơ Tu" },
                    { 26, "Giáy" },
                    { 27, "Tà Ôi" },
                    { 28, "Mạ" },
                    { 29, "Giẻ Triêng" },
                    { 30, "Co" },
                    { 31, "Chơ Ro" },
                    { 32, "Xinh Mun" },
                    { 33, "Hà Nhì" },
                    { 34, "Chu Ru" },
                    { 35, "Lào" },
                    { 36, "La Chí" },
                    { 37, "Kháng" },
                    { 38, "Phù Lá" },
                    { 39, "La Hủ" },
                    { 40, "La Ha" },
                    { 41, "Pà Thẻn" },
                    { 42, "Lự" },
                    { 43, "Ngái" },
                    { 44, "Chứt" },
                    { 45, "Lô Lô" },
                    { 46, "Mảng" },
                    { 47, "Cơ Lao" },
                    { 48, "Bố Y" },
                    { 49, "Cống" },
                    { 50, "Si La" },
                    { 51, "Pu Péo" },
                    { 52, "Rơ Măm" },
                    { 53, "Brâu" },
                    { 54, "Ơ Đu" }
                });

            migrationBuilder.CreateIndex(
                name: "ix_dm_dich_vu_khoa_id",
                table: "dm_dich_vu",
                column: "khoa_id");

            migrationBuilder.CreateIndex(
                name: "ix_dm_phong_dich_vu_id",
                table: "dm_phong",
                column: "dich_vu_id");

            migrationBuilder.CreateIndex(
                name: "ix_dm_phong_khoa_id",
                table: "dm_phong",
                column: "khoa_id");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "dm_dan_toc");

            migrationBuilder.DropTable(
                name: "dm_phong");

            migrationBuilder.DropTable(
                name: "dm_thuoc");

            migrationBuilder.DropTable(
                name: "dm_dich_vu");

            migrationBuilder.DropTable(
                name: "dm_khoa");
        }
    }
}
