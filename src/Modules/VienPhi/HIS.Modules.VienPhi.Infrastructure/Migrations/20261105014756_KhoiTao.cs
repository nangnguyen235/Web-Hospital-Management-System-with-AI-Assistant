using System;
using Microsoft.EntityFrameworkCore.Migrations;
using MySql.EntityFrameworkCore.Metadata;

#nullable disable

namespace HIS.Modules.VienPhi.Infrastructure.Migrations
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
                name: "vp_giao_dich",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("MySQL:ValueGenerationStrategy", MySQLValueGenerationStrategy.IdentityColumn),
                    so_tien = table.Column<decimal>(type: "decimal(12,2)", precision: 12, scale: 2, nullable: false),
                    phi = table.Column<decimal>(type: "decimal(12,2)", precision: 12, scale: 2, nullable: false),
                    trang_thai = table.Column<string>(type: "varchar(30)", maxLength: 30, nullable: false),
                    ma_ngan_hang = table.Column<string>(type: "varchar(20)", maxLength: 20, nullable: true),
                    phuong_thuc = table.Column<string>(type: "varchar(20)", maxLength: 20, nullable: false),
                    nguon = table.Column<string>(type: "varchar(255)", maxLength: 255, nullable: true),
                    loai_thanh_toan = table.Column<string>(type: "varchar(30)", maxLength: 30, nullable: false),
                    tai_khoan_id = table.Column<long>(type: "bigint", nullable: true),
                    da_xoa = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    ngay_tao = table.Column<DateTime>(type: "datetime(6)", nullable: false),
                    ngay_cap_nhat = table.Column<DateTime>(type: "datetime(6)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_vp_giao_dich", x => x.id);
                })
                .Annotation("MySQL:Charset", "utf8mb4");

            migrationBuilder.CreateIndex(
                name: "ix_vp_giao_dich_tai_khoan_id",
                table: "vp_giao_dich",
                column: "tai_khoan_id");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "vp_giao_dich");
        }
    }
}
