using System;
using Microsoft.EntityFrameworkCore.Migrations;
using MySql.EntityFrameworkCore.Metadata;

#nullable disable

namespace HIS.Modules.TroLyAi.Infrastructure.Migrations
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
                name: "tl_hoi_thoai",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("MySQL:ValueGenerationStrategy", MySQLValueGenerationStrategy.IdentityColumn),
                    tai_khoan_id = table.Column<long>(type: "bigint", nullable: false),
                    trang_thai = table.Column<string>(type: "varchar(30)", maxLength: 30, nullable: false),
                    so_thu_tu_lon_nhat = table.Column<int>(type: "int", nullable: false),
                    ngay_tao = table.Column<DateTime>(type: "datetime(6)", nullable: false),
                    ngay_cap_nhat = table.Column<DateTime>(type: "datetime(6)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_tl_hoi_thoai", x => x.id);
                })
                .Annotation("MySQL:Charset", "utf8mb4");

            migrationBuilder.CreateTable(
                name: "tl_tin_nhan",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("MySQL:ValueGenerationStrategy", MySQLValueGenerationStrategy.IdentityColumn),
                    hoi_thoai_id = table.Column<long>(type: "bigint", nullable: false),
                    noi_dung = table.Column<string>(type: "text", maxLength: 255, nullable: false),
                    so_thu_tu = table.Column<int>(type: "int", nullable: false),
                    la_tro_ly = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    ngay_tao = table.Column<DateTime>(type: "datetime(6)", nullable: false),
                    ngay_cap_nhat = table.Column<DateTime>(type: "datetime(6)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_tl_tin_nhan", x => x.id);
                    table.ForeignKey(
                        name: "fk_tl_tin_nhan_tl_hoi_thoai_hoi_thoai_id",
                        column: x => x.hoi_thoai_id,
                        principalTable: "tl_hoi_thoai",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Restrict);
                })
                .Annotation("MySQL:Charset", "utf8mb4");

            migrationBuilder.CreateIndex(
                name: "ix_tl_hoi_thoai_tai_khoan_id",
                table: "tl_hoi_thoai",
                column: "tai_khoan_id");

            migrationBuilder.CreateIndex(
                name: "ix_tl_tin_nhan_hoi_thoai_id_so_thu_tu",
                table: "tl_tin_nhan",
                columns: new[] { "hoi_thoai_id", "so_thu_tu" },
                unique: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "tl_tin_nhan");

            migrationBuilder.DropTable(
                name: "tl_hoi_thoai");
        }
    }
}
