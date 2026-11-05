# Hướng dẫn chạy và triển khai TheHos (HIS)

Tài liệu cho cả nhóm 6. Nguồn chuẩn của các quyết định nằm ở rule của nhóm; file này chỉ ghi **các bước bấm và lệnh chạy**.

```text
Trình duyệt ──► nangnguyen.me ───────────► Cloudflare Pages (frontend React, build từ thư mục frontend/)
           └──► api.nangnguyen.me ──► Cloudflare (DNS proxied, SSL Full Strict)
                                        └──► EC2 t3.micro: NGINX :443 (Origin CA) ──► backend :8080 ──► MySQL
Tệp (ảnh, ghi âm, kết quả xét nghiệm): backend ⇄ Cloudflare R2 (Presigned URL 15 phút)
Push main ──► GitHub Actions: test → build image → ghcr.io/nangnguyen235/his-backend → SSH vào EC2 → compose pull/up
```

## 1. Chạy trên máy dev

Cần: Docker Desktop, .NET SDK 10, Node.js 22 LTS.

1. Tạo file bí mật: `cp .env.example .env`, sửa mật khẩu MySQL (chỉ chữ và số), `Jwt__Key` (≥ 32 ký tự ngẫu nhiên), `Seed__*` (Admin gốc). **Không commit `.env`.**
2. Chạy backend + MySQL + NGINX bằng một lệnh: `docker compose up -d --build`.
   - Compose tự nạp thêm `compose.override.yaml`: build image tại chỗ, NGINX chỉ cổng 80, MySQL mở `127.0.0.1:3317`.
3. Kiểm tra:
   - `http://localhost/health` trả `Healthy`;
   - Swagger ở `http://localhost/swagger`: gọi `POST /api/nguoi-dung/dang-nhap` bằng email + mật khẩu `Seed__*`, rồi bấm **Authorize** và dán `accessToken`.
4. Frontend: `cd frontend && npm ci && npm run dev`, mở `http://localhost:5173` (mặc định gọi API `http://localhost`).
5. Chạy backend trong IDE thay vì container:
   - đặt `ConnectionStrings__MySql=server=localhost;port=3317;database=his;user=his;password=...;AllowPublicKeyRetrieval=true` (user-secrets hoặc biến môi trường), cùng `Jwt__Key`;
   - `dotnet run --project src/HIS.Api` (cổng 5080).
6. Test: `dotnet test` (gồm architecture test chặn tham chiếu chéo module); trong `frontend/`: `npm run lint`, `npm run test`, `npm run build`.
7. Tạo migration khi đổi bảng:
   ```bash
   dotnet ef migrations add <TenMigration> -p src/Modules/<X>/HIS.Modules.<X>.Infrastructure -s src/HIS.Api -c <X>DbContext -o Migrations
   ```
   Trước khi chạy, đặt hai biến `ConnectionStrings__MySql` và `Jwt__Key` (giá trị nào cũng được, lệnh không kết nối CSDL). Backend tự áp migration khi khởi động (`Database__AutoMigrate=true`).

## 2. Cloudflare

### 2.1 Tên miền `nangnguyen.me`

1. Cloudflare Dashboard → **Add a domain** → `nangnguyen.me` → gói **Free**.
2. Namecheap → Domain List → `nangnguyen.me` → **Manage** → Nameservers: **Custom DNS** → dán 2 nameserver Cloudflare đưa → lưu.
3. Chờ Cloudflare báo **Active** (vài phút tới vài giờ).

### 2.2 SSL hai chặng (NFR-02)

1. **SSL/TLS → Overview**: chọn **Full (strict)**. **KHÔNG** dùng Flexible.
2. **SSL/TLS → Edge Certificates**: bật **Always Use HTTPS**.
3. **SSL/TLS → Origin Server → Create Certificate**: RSA, hostname `nangnguyen.me` và `*.nangnguyen.me`, hạn 15 năm.
   - Lưu ô *Origin Certificate* thành `origin.pem`, ô *Private Key* thành `origin.key`. Khóa chỉ hiện **một lần**.
   - Giữ hai file này ở máy, **không commit**; mục 3.5 sẽ copy lên EC2.

### 2.3 DNS cho API

Sau khi có IP EC2 (mục 3), vào **DNS → Records → Add record**: loại `A`, tên `api`, IPv4 = IP công khai của EC2, **Proxied** (đám mây cam).

### 2.4 Cloudflare Pages (frontend)

1. **Workers & Pages → Create → Pages → Connect to Git** → chọn repo `Web-Hospital-Management-System-with-AI-Assistant`.
2. Cấu hình build:

   | Mục | Giá trị |
   | --- | --- |
   | Production branch | `main` |
   | Root directory | `frontend` |
   | Build command | `npm run build` |
   | Build output directory | `dist` |
   | Environment variable | `VITE_API_URL` = `https://api.nangnguyen.me` |

   Bản Node lấy từ file `frontend/.nvmrc` (22).
3. **Custom domains → Set up a domain** → `nangnguyen.me`.
4. Mỗi Pull Request có bản xem trước `*.pages.dev`. Không cần `404.html`: Pages tự trả `index.html` cho mọi đường dẫn (SPA).

### 2.5 Cloudflare R2

1. **R2 → Create bucket**: tạo 3 bucket **private**: `thehos-dev`, `thehos-prod`, `thehos-sao-luu`.
2. **R2 → Manage API tokens → Create API token**, tạo 2 token:
   - token ứng dụng: *Object Read & Write*, chỉ bucket `thehos-dev` và `thehos-prod` → điền `R2__AccessKey`, `R2__SecretKey` vào `.env`;
   - token sao lưu: *Object Read & Write*, chỉ bucket `thehos-sao-luu` → dùng cho rclone (mục 3.6).
3. Điền `R2__ServiceURL` = `https://<ACCOUNT_ID>.r2.cloudflarestorage.com` (Account ID ở trang R2). Đặt `R2__Bucket` = `thehos-dev` ở máy dev, `thehos-prod` trên EC2.
4. **Bucket → Settings → CORS policy** của `thehos-dev` và `thehos-prod`:
   ```json
   [{ "AllowedOrigins": ["https://nangnguyen.me", "http://localhost:5173"], "AllowedMethods": ["GET", "PUT"], "AllowedHeaders": ["*"], "MaxAgeSeconds": 3600 }]
   ```

## 3. Amazon EC2

### 3.1 Tài khoản và ngân sách

1. Tạo tài khoản AWS mới (gói Free kiểu mới, có credit, dùng 6 tháng). Bật **MFA** cho tài khoản root.
2. Chọn region **Asia Pacific (Singapore) `ap-southeast-1`** ở góc trên phải.
3. **Billing → Budgets → Create budget**: *Monthly cost budget*, 10 USD, gửi email cảnh báo ở 80% và 100%. **Bắt buộc.**

### 3.2 Tạo máy

**EC2 → Launch instance**:

| Mục | Giá trị |
| --- | --- |
| Name | `his-ec2` |
| AMI | Ubuntu Server 24.04 LTS (x86_64) |
| Instance type | `t3.micro` |
| Key pair | tạo mới `his-ec2` (ED25519, `.pem`) — tải về, giữ kín, không commit |
| Security group | SSH 22 (Anywhere — SSH chỉ nhận khóa), HTTP 80, HTTPS 443. **Không** mở 3306, 8080 |
| Storage | 30 GiB gp3 |
| Advanced → Credit specification | **Standard** (không dùng Unlimited) |

**Không** gắn Elastic IP. IP công khai đổi mỗi khi **stop rồi start**, nên mỗi lần như vậy phải sửa bản ghi DNS `api` và secret `EC2_HOST`. Cần khởi động lại thì dùng **reboot**.

### 3.3 Cài máy bằng script

```bash
scp -i his-ec2.pem scripts/setup-ec2.sh ubuntu@<IP>:~
ssh -i his-ec2.pem ubuntu@<IP>
sudo bash setup-ec2.sh          # Docker, swap 2 GB, chrony, rclone, /opt/his, SSH chỉ nhận khóa
exit                            # đăng nhập lại để dùng docker không cần sudo
```

Kiểm tra:
- `free -m`: thấy Swap khoảng 2047;
- `chronyc sources -v`: có dòng `^*` trỏ `169.254.169.123`;
- `docker compose version`.

### 3.4 File `.env` trên EC2

```bash
nano /opt/his/.env      # dán nội dung .env.example rồi sửa giá trị thật
chmod 600 /opt/his/.env
```

Khác với máy dev:
- `Cors__AllowedOrigins=https://nangnguyen.me`;
- `R2__Bucket=thehos-prod`;
- mật khẩu MySQL, `Jwt__Key`, `Seed__AdminMatKhau` là chuỗi ngẫu nhiên mới;
- `Swagger__Enabled=true` nếu cần demo Swagger.

### 3.5 Chứng chỉ Origin CA

```bash
scp -i his-ec2.pem origin.pem origin.key ubuntu@<IP>:/opt/his/nginx/certs/
ssh -i his-ec2.pem ubuntu@<IP> "chmod 600 /opt/his/nginx/certs/origin.key"
```

### 3.6 Sao lưu MySQL hằng đêm lên R2

```bash
rclone config create r2-sao-luu s3 provider=Cloudflare \
  access_key_id=<KHOA_TOKEN_SAO_LUU> secret_access_key=<BI_MAT_TOKEN_SAO_LUU> \
  endpoint=https://<ACCOUNT_ID>.r2.cloudflarestorage.com acl=private
crontab -e
# thêm dòng:
0 2 * * * bash /opt/his/scripts/backup-mysql.sh >> /var/backups/his/backup.log 2>&1
```

Script `backup-mysql.sh` được pipeline copy lên `/opt/his/scripts/` mỗi lần deploy. Trước buổi bảo vệ, thử khôi phục một bản sao lưu ra máy dev ít nhất một lần.

## 4. GitHub Actions

1. Tạo khóa SSH riêng cho deploy ở máy mình:
   ```bash
   ssh-keygen -t ed25519 -f his-deploy -C github-actions -N ""
   ssh -i his-ec2.pem ubuntu@<IP> "cat >> ~/.ssh/authorized_keys" < his-deploy.pub
   ```
2. Repo → **Settings → Secrets and variables → Actions → New repository secret**:

   | Secret | Giá trị |
   | --- | --- |
   | `EC2_HOST` | IP công khai hiện tại của EC2 |
   | `EC2_USER` | `ubuntu` |
   | `EC2_SSH_KEY` | toàn bộ nội dung file `his-deploy` (khóa riêng) |

3. Quy trình:
   - làm trên nhánh `feature/*`, đẩy bằng skill `day-ma`;
   - mở Pull Request → `ci.yml` chạy build, test, architecture test, lint/test/build frontend;
   - có người review rồi merge → `deploy.yml` build image, đẩy lên GHCR, SSH vào EC2 chạy `docker compose pull && docker compose up -d`.
4. Image để private trên GHCR. Bước deploy tự `docker login ghcr.io` bằng `GITHUB_TOKEN`, không cần thêm secret.

## 5. Kiểm tra sau khi deploy

- `curl -I https://api.nangnguyen.me/health` trả `200`.
- `https://nangnguyen.me` mở được trang chủ.
- Đăng nhập Admin; trang **Quản trị → Khoa** thêm/sửa/xóa được (kiểm tra luôn CORS).
- Workflow **Deploy** trên tab Actions xanh.
- Trên EC2:
  - `docker compose ps`: 3 container đều `healthy`;
  - `docker stats --no-stream`: đo RAM thật, rồi cập nhật ngân sách RAM trong rule Docker.

## 6. Sự cố thường gặp

| Hiện tượng | Nguyên nhân hay gặp | Cách xử lý |
| --- | --- | --- |
| Cloudflare 521 | NGINX trên EC2 không chạy, hoặc Security Group chưa mở 443 | `docker compose ps`, `docker compose logs nginx`; kiểm tra Security Group |
| Cloudflare 526 | Chứng chỉ ở `/opt/his/nginx/certs` sai hoặc thiếu | Tạo lại Origin CA (mục 2.2), copy lại hai file |
| 502 Bad Gateway | Backend chưa `healthy` (đang chờ MySQL, lỗi migration, thiếu biến `.env`) | `docker compose logs --tail 100 backend` |
| Trình duyệt báo lỗi CORS | `Cors__AllowedOrigins` thiếu origin của frontend | Sửa `.env`, chạy `docker compose up -d` |
| Deploy lỗi SSH | IP EC2 đổi sau khi stop/start | Sửa secret `EC2_HOST` và bản ghi DNS `api` |
