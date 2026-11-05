using System;
using Microsoft.EntityFrameworkCore.Migrations;
using MySql.EntityFrameworkCore.Metadata;

#nullable disable

namespace HIS.Modules.NguoiDung.Infrastructure.Migrations
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
                name: "tk_benh_nhan",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("MySQL:ValueGenerationStrategy", MySQLValueGenerationStrategy.IdentityColumn),
                    ma_benh_nhan = table.Column<string>(type: "varchar(10)", maxLength: 10, nullable: false),
                    ho_ten = table.Column<string>(type: "varchar(100)", maxLength: 100, nullable: false),
                    so_dien_thoai = table.Column<string>(type: "varchar(20)", maxLength: 20, nullable: true),
                    so_cccd = table.Column<string>(type: "varchar(12)", maxLength: 12, nullable: true),
                    ngay_sinh = table.Column<DateOnly>(type: "date", nullable: false),
                    gioi_tinh = table.Column<string>(type: "varchar(30)", maxLength: 30, nullable: false),
                    da_xoa = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    ngay_tao = table.Column<DateTime>(type: "datetime(6)", nullable: false),
                    ngay_cap_nhat = table.Column<DateTime>(type: "datetime(6)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_tk_benh_nhan", x => x.id);
                })
                .Annotation("MySQL:Charset", "utf8mb4");

            migrationBuilder.CreateTable(
                name: "tk_nguoi_dung_tam",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("MySQL:ValueGenerationStrategy", MySQLValueGenerationStrategy.IdentityColumn),
                    email = table.Column<string>(type: "varchar(150)", maxLength: 150, nullable: false),
                    otp = table.Column<string>(type: "varchar(64)", maxLength: 64, nullable: false),
                    so_lan_nhap_otp = table.Column<int>(type: "int", nullable: false),
                    otp_tao_luc = table.Column<DateTime>(type: "datetime(6)", nullable: true),
                    otp_het_han = table.Column<DateTime>(type: "datetime(6)", nullable: true),
                    da_xoa = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    ngay_tao = table.Column<DateTime>(type: "datetime(6)", nullable: false),
                    ngay_cap_nhat = table.Column<DateTime>(type: "datetime(6)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_tk_nguoi_dung_tam", x => x.id);
                })
                .Annotation("MySQL:Charset", "utf8mb4");

            migrationBuilder.CreateTable(
                name: "tk_nhan_vien",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("MySQL:ValueGenerationStrategy", MySQLValueGenerationStrategy.IdentityColumn),
                    ho_ten = table.Column<string>(type: "varchar(100)", maxLength: 100, nullable: false),
                    so_dien_thoai = table.Column<string>(type: "varchar(20)", maxLength: 20, nullable: false),
                    so_cccd = table.Column<string>(type: "varchar(12)", maxLength: 12, nullable: true),
                    gioi_tinh = table.Column<string>(type: "varchar(30)", maxLength: 30, nullable: false),
                    ngay_sinh = table.Column<DateOnly>(type: "date", nullable: true),
                    anh_dai_dien_key = table.Column<string>(type: "varchar(255)", maxLength: 255, nullable: true),
                    loai_nhan_vien = table.Column<string>(type: "varchar(30)", maxLength: 30, nullable: false),
                    khoa_id = table.Column<int>(type: "int", nullable: true),
                    la_truong_khoa = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    da_xoa = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    ngay_tao = table.Column<DateTime>(type: "datetime(6)", nullable: false),
                    ngay_cap_nhat = table.Column<DateTime>(type: "datetime(6)", nullable: false),
                    hoc_ham = table.Column<string>(type: "varchar(30)", maxLength: 30, nullable: true),
                    diem_danh_gia = table.Column<decimal>(type: "decimal(2,1)", precision: 2, scale: 1, nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_tk_nhan_vien", x => x.id);
                })
                .Annotation("MySQL:Charset", "utf8mb4");

            migrationBuilder.CreateTable(
                name: "tk_tai_khoan",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("MySQL:ValueGenerationStrategy", MySQLValueGenerationStrategy.IdentityColumn),
                    ho_ten = table.Column<string>(type: "varchar(100)", maxLength: 100, nullable: false),
                    email = table.Column<string>(type: "varchar(150)", maxLength: 150, nullable: true),
                    mat_khau_hash = table.Column<string>(type: "varchar(255)", maxLength: 255, nullable: false),
                    so_dien_thoai = table.Column<string>(type: "varchar(20)", maxLength: 20, nullable: false),
                    anh_dai_dien_key = table.Column<string>(type: "varchar(255)", maxLength: 255, nullable: true),
                    so_luong_ho_so = table.Column<int>(type: "int", nullable: false),
                    otp = table.Column<string>(type: "varchar(64)", maxLength: 64, nullable: true),
                    so_lan_nhap_otp = table.Column<int>(type: "int", nullable: false),
                    otp_tao_luc = table.Column<DateTime>(type: "datetime(6)", nullable: true),
                    otp_het_han = table.Column<DateTime>(type: "datetime(6)", nullable: true),
                    nhan_vien_id = table.Column<long>(type: "bigint", nullable: true),
                    da_xoa = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    ngay_tao = table.Column<DateTime>(type: "datetime(6)", nullable: false),
                    ngay_cap_nhat = table.Column<DateTime>(type: "datetime(6)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_tk_tai_khoan", x => x.id);
                    table.ForeignKey(
                        name: "fk_tk_tai_khoan_nhan_vien_nhan_vien_id",
                        column: x => x.nhan_vien_id,
                        principalTable: "tk_nhan_vien",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Restrict);
                })
                .Annotation("MySQL:Charset", "utf8mb4");

            migrationBuilder.CreateTable(
                name: "tk_ho_so_benh_nhan",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("MySQL:ValueGenerationStrategy", MySQLValueGenerationStrategy.IdentityColumn),
                    tai_khoan_id = table.Column<long>(type: "bigint", nullable: true),
                    benh_nhan_id = table.Column<long>(type: "bigint", nullable: true),
                    ho_ten = table.Column<string>(type: "varchar(100)", maxLength: 100, nullable: false),
                    so_dien_thoai = table.Column<string>(type: "varchar(20)", maxLength: 20, nullable: true),
                    email = table.Column<string>(type: "varchar(150)", maxLength: 150, nullable: true),
                    gioi_tinh = table.Column<string>(type: "varchar(30)", maxLength: 30, nullable: false),
                    so_cccd = table.Column<string>(type: "varchar(12)", maxLength: 12, nullable: true),
                    dia_chi = table.Column<string>(type: "varchar(255)", maxLength: 255, nullable: true),
                    ngay_sinh = table.Column<DateOnly>(type: "date", nullable: false),
                    dan_toc_id = table.Column<int>(type: "int", nullable: true),
                    otp = table.Column<string>(type: "varchar(64)", maxLength: 64, nullable: true),
                    so_lan_nhap_otp = table.Column<int>(type: "int", nullable: false),
                    otp_tao_luc = table.Column<DateTime>(type: "datetime(6)", nullable: true),
                    otp_het_han = table.Column<DateTime>(type: "datetime(6)", nullable: true),
                    da_xoa = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    ngay_tao = table.Column<DateTime>(type: "datetime(6)", nullable: false),
                    ngay_cap_nhat = table.Column<DateTime>(type: "datetime(6)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_tk_ho_so_benh_nhan", x => x.id);
                    table.ForeignKey(
                        name: "fk_tk_ho_so_benh_nhan_tk_benh_nhan_benh_nhan_id",
                        column: x => x.benh_nhan_id,
                        principalTable: "tk_benh_nhan",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Restrict);
                    table.ForeignKey(
                        name: "fk_tk_ho_so_benh_nhan_tk_tai_khoan_tai_khoan_id",
                        column: x => x.tai_khoan_id,
                        principalTable: "tk_tai_khoan",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Restrict);
                })
                .Annotation("MySQL:Charset", "utf8mb4");

            migrationBuilder.CreateTable(
                name: "tk_thong_bao",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("MySQL:ValueGenerationStrategy", MySQLValueGenerationStrategy.IdentityColumn),
                    tai_khoan_id = table.Column<long>(type: "bigint", nullable: false),
                    tieu_de = table.Column<string>(type: "varchar(200)", maxLength: 200, nullable: false),
                    noi_dung = table.Column<string>(type: "text", maxLength: 255, nullable: false),
                    du_lieu = table.Column<string>(type: "varchar(1000)", maxLength: 1000, nullable: true),
                    trang_thai = table.Column<string>(type: "varchar(30)", maxLength: 30, nullable: false),
                    da_xoa = table.Column<bool>(type: "tinyint(1)", nullable: false),
                    ngay_tao = table.Column<DateTime>(type: "datetime(6)", nullable: false),
                    ngay_cap_nhat = table.Column<DateTime>(type: "datetime(6)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("pk_tk_thong_bao", x => x.id);
                    table.ForeignKey(
                        name: "fk_tk_thong_bao_tk_tai_khoan_tai_khoan_id",
                        column: x => x.tai_khoan_id,
                        principalTable: "tk_tai_khoan",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Restrict);
                })
                .Annotation("MySQL:Charset", "utf8mb4");

            migrationBuilder.CreateIndex(
                name: "ix_tk_benh_nhan_ma_benh_nhan",
                table: "tk_benh_nhan",
                column: "ma_benh_nhan",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "ix_tk_benh_nhan_so_cccd",
                table: "tk_benh_nhan",
                column: "so_cccd",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "ix_tk_ho_so_benh_nhan_benh_nhan_id",
                table: "tk_ho_so_benh_nhan",
                column: "benh_nhan_id");

            migrationBuilder.CreateIndex(
                name: "ix_tk_ho_so_benh_nhan_dan_toc_id",
                table: "tk_ho_so_benh_nhan",
                column: "dan_toc_id");

            migrationBuilder.CreateIndex(
                name: "ix_tk_ho_so_benh_nhan_tai_khoan_id",
                table: "tk_ho_so_benh_nhan",
                column: "tai_khoan_id");

            migrationBuilder.CreateIndex(
                name: "ix_tk_nguoi_dung_tam_email",
                table: "tk_nguoi_dung_tam",
                column: "email");

            migrationBuilder.CreateIndex(
                name: "ix_tk_nhan_vien_khoa_id",
                table: "tk_nhan_vien",
                column: "khoa_id");

            migrationBuilder.CreateIndex(
                name: "ix_tk_tai_khoan_email",
                table: "tk_tai_khoan",
                column: "email",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "ix_tk_tai_khoan_nhan_vien_id",
                table: "tk_tai_khoan",
                column: "nhan_vien_id",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "ix_tk_tai_khoan_so_dien_thoai",
                table: "tk_tai_khoan",
                column: "so_dien_thoai",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "ix_tk_thong_bao_tai_khoan_id",
                table: "tk_thong_bao",
                column: "tai_khoan_id");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "tk_ho_so_benh_nhan");

            migrationBuilder.DropTable(
                name: "tk_nguoi_dung_tam");

            migrationBuilder.DropTable(
                name: "tk_thong_bao");

            migrationBuilder.DropTable(
                name: "tk_benh_nhan");

            migrationBuilder.DropTable(
                name: "tk_tai_khoan");

            migrationBuilder.DropTable(
                name: "tk_nhan_vien");
        }
    }
}
