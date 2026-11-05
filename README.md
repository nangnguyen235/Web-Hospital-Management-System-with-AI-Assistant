# TheHos — Hệ thống quản lý bệnh viện tích hợp AI (HIS)

Đồ án Nhóm 6, học phần 608819 — ĐH Xây dựng Hà Nội. Khám **ngoại trú**:
- bệnh nhân đặt khám trực tuyến, thanh toán VNPAY;
- điều dưỡng tiếp nhận, gọi số;
- bác sĩ khám, chỉ định xét nghiệm, kê đơn.

Có thêm 2 tính năng AI: **chatbot tư vấn & đặt khám** cho bệnh nhân, **Voice-to-EMR** cho bác sĩ.

Nghiệp vụ và thiết kế CSDL tham khảo [TheDuckHospital](https://github.com/MinhTuMTN/TheDuckHospital) (chỉ lấy nghiệp vụ và cấu trúc bảng, không chép mã).

| Phần | Công nghệ |
| --- | --- |
| Backend | ASP.NET Core .NET 10, Modular Monolith + Clean Architecture (6 module × 4 tầng + Contracts), EF Core + MySQL 8, MediatR |
| Frontend | React + Vite + TypeScript + MUI, triển khai trên Cloudflare Pages |
| Hạ tầng | Docker Compose (nginx, backend, mysql) trên Amazon EC2, Cloudflare DNS/SSL/R2, GitHub Actions + GHCR |

## Cấu trúc

```text
src/HIS.Api                       Host: Program.cs, xác thực JWT, xử lý lỗi tập trung, Swagger
src/HIS.SharedKernel*             Dùng chung: abstraction / cài đặt hạ tầng / web
src/Modules/<Module>/             NguoiDung, DanhMuc, LichKham, HoSoBenhAn, VienPhi, TroLyAi
  HIS.Modules.<Module>.Domain | .Application | .Infrastructure | .Presentation | .Contracts
tests/                            Architecture test (NetArchTest) + unit test
frontend/                         Giao diện web TheHos
nginx/  compose.yaml  Dockerfile  scripts/  .github/workflows/
```

## Chạy nhanh

```bash
cp .env.example .env               # sửa mật khẩu, Jwt__Key, Seed__* (không commit .env)
docker compose up -d --build       # http://localhost/health, http://localhost/swagger
cd frontend && npm ci && npm run dev   # http://localhost:5173
dotnet test                        # gồm architecture test chặn tham chiếu chéo module
```

Triển khai lên Cloudflare + EC2: xem [docs/trien-khai.md](docs/trien-khai.md).

## Thành viên

| Thành viên | Vai trò |
| --- | --- |
| Nguyễn Minh Năng | Nhóm trưởng, backend |
| Nguyễn Thị Thu Hoài | DevOps |
| Đào Xuân Hoà | Backend, nghiệp vụ |
| Lê Việt Anh | Frontend |
