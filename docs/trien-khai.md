# Hướng dẫn chạy và triển khai TheHos

Làm lần lượt từ trên xuống. Mỗi bước ghi rõ ba ý:

- **Mở**: app, trang web hoặc file cần mở.
- **Làm**: bấm gì, gõ lệnh gì.
- **Đúng khi**: dấu hiệu cho biết bước đó đã xong.

Ký hiệu nơi làm:

| Ký hiệu | Là gì | Mở thế nào |
| --- | --- | --- |
| 💻 **Terminal** | Cửa sổ gõ lệnh trên máy mình. Mỗi bước ghi rõ mở ở **thư mục nào** (xem mục ngay dưới) | Xem mục *Cách mở terminal ở đúng thư mục* |
| ☁️ **VPS** | Terminal đã đăng nhập vào máy chủ | Trong Git Bash gõ `ssh -i ~/his-keys/his-vps his@<IP>`. Thấy dấu nhắc `his@thehos-vps:~$` là đã vào. Gõ `exit` để ra |
| 🌐 **Web** | Trình duyệt (Chrome, Edge) | — |
| 📝 **Editor** | VS Code hoặc Antigravity IDE | **File → Open Folder** → chọn thư mục **[Dự án]** |

### Cách mở terminal ở đúng thư mục

Tài liệu dùng 4 thư mục, gọi tắt như sau:

| Gọi tắt | Đường dẫn trên máy Năng | Trong thư mục có |
| --- | --- | --- |
| **[Dự án]** | `E:\2.File_trường\4_KI1\ĐỒ ÁN MINH QUÝ\Web Hospital Management System with AI Assistant` | `compose.yaml`, `.env`, `backend`, `frontend`, `nginx`, `scripts`, `docs` |
| **[backend]** | `E:\2.File_trường\4_KI1\ĐỒ ÁN MINH QUÝ\Web Hospital Management System with AI Assistant\backend` | `pom.xml`, `mvnw`, `Dockerfile`, `shared`, `modules`, `his-app` |
| **[frontend]** | `E:\2.File_trường\4_KI1\ĐỒ ÁN MINH QUÝ\Web Hospital Management System with AI Assistant\frontend` | `package.json`, `src`, `vite.config.ts` |
| **[his-keys]** | `C:\Users\<tên bạn>\his-keys` | `his-vps`, `origin.pem`, `origin.key`, `his-deploy` |

Máy của thành viên khác: **[Dự án]** là thư mục `Web-Hospital-Management-System-with-AI-Assistant` vừa clone ở Phần 0 bước 2, ví dụ `D:\DoAn\Web-Hospital-Management-System-with-AI-Assistant`.

⚠️ Lệnh `docker compose` và `git` gõ ở **[Dự án]**. Lệnh `./mvnw` (build, test) gõ ở **[backend]**. Lệnh `npm` gõ ở **[frontend]**.

Mở terminal ở **[Dự án]**, chọn một trong hai cách:

- **Cách 1 (trong IDE, khuyên dùng)**:
  1. Mở VS Code/Antigravity IDE → **File → Open Folder** → chọn thư mục **[Dự án]**.
  2. Menu **Terminal → New Terminal** (hoặc **Ctrl+`**). Terminal mở ra đã đứng sẵn ở **[Dự án]**.
  3. Nên chọn Git Bash: bấm mũi tên ⌄ cạnh dấu `+` của terminal → **Git Bash**.
- **Cách 2 (File Explorer)**: mở thư mục **[Dự án]** trong File Explorer → chuột phải vào **chỗ trống** → **Open Git Bash here**. Windows 11 phải bấm **Show more options** trước.

Kiểm tra đã đứng đúng thư mục:
```bash
pwd     # in ra .../Web Hospital Management System with AI Assistant
ls      # phải thấy backend  compose.yaml  frontend  nginx  scripts ...
```
Nếu `pwd` kết thúc bằng `/backend` hoặc `/frontend`, gõ `cd ..` để lùi về **[Dự án]**.

Để có terminal ở **[backend]** hoặc **[frontend]**: mở terminal ở **[Dự án]** rồi gõ `cd backend` hoặc `cd frontend`. Để có terminal ở **[his-keys]**: gõ `cd ~/his-keys` ở bất kỳ terminal nào.

Trong lệnh, `<IP>` là IP máy chủ, lấy ở Phần B bước 4. `~` trong Git Bash là `C:\Users\<tên bạn>`, nên `~/his-keys` là thư mục `C:\Users\<tên bạn>\his-keys`.

## Bí mật để ở đâu

Các file và giá trị dưới đây **không bao giờ** được commit. `.gitignore` đã chặn sẵn `.env`, `*.pem`, `*.key`.

| Thứ cần giữ kín | Để ở |
| --- | --- |
| Mật khẩu MySQL, JWT, Admin, khóa R2, VNPAY, SMTP key Brevo | File `.env`. Máy mình: `[Dự án]\.env`. Máy chủ: `/opt/his/.env` (bản gốc giữ ở `[Dự án]\.env.production`, đã bị `.gitignore` chặn) |
| Khóa SSH vào máy chủ (`his-vps`), khóa deploy (`his-deploy`), chứng chỉ (`origin.pem`, `origin.key`) | Thư mục `C:\Users\<tên bạn>\his-keys\`, **ngoài** repo |
| Chứng chỉ trên máy chủ | `/opt/his/nginx/certs/` |
| IP, user và khóa SSH cho deploy tự động | GitHub secrets: `VPS_HOST`, `VPS_USER`, `VPS_SSH_KEY` |
| Token R2 dùng cho sao lưu | Cấu hình rclone trên máy chủ (`~/.config/rclone/rclone.conf`) |

---

## Phần 0 — Chuẩn bị máy (mỗi người làm một lần)

### Bước 1. Cài phần mềm
**Mở** 🌐 rồi tải và cài theo bảng (cứ bấm Next):

| Phần mềm | Tải ở | Kiểm tra trong 💻 Git Bash |
| --- | --- | --- |
| Git for Windows (có Git Bash) | <https://git-scm.com/download/win> | `git --version` |
| Docker Desktop | <https://www.docker.com/products/docker-desktop/> | `docker --version` |
| JDK 21 (Eclipse Temurin) | <https://adoptium.net/temurin/releases/?version=21> (Windows x64, `.msi`, tích **Set JAVA_HOME**) | `java -version` → `21.x`. Không cần cài Maven: dùng `./mvnw` có sẵn |
| Node.js 22 LTS | <https://nodejs.org/> | `node -v` → `v22.x` |
| VS Code (nếu chưa có editor) | <https://code.visualstudio.com/> | — |

Cài Docker Desktop xong phải khởi động lại máy.

### Bước 2. Lấy mã nguồn
**Mở** 💻 terminal ở thư mục muốn để dự án, ví dụ mở File Explorer tới `D:\DoAn` → chuột phải chỗ trống → **Open Git Bash here**.

**Làm**:
```bash
git clone https://github.com/nangnguyen235/Web-Hospital-Management-System-with-AI-Assistant.git
cd Web-Hospital-Management-System-with-AI-Assistant
```

**Đúng khi**: có thư mục mới chứa `compose.yaml`, `backend/`, `frontend/`.

---

## Phần A — Chạy toàn bộ web trên máy mình (lần đầu)

> Máy đã chạy dự án ít nhất một lần (đã có `.env`, đã có dữ liệu MySQL) thì bỏ qua Phần A, làm **Phần A2 — Chạy lại**.

### Bước 1. Bật Docker Desktop
**Mở**: Start menu → **Docker Desktop**.

**Đúng khi**: góc dưới bên trái cửa sổ Docker Desktop hiện **Engine running** (màu xanh). Để Docker Desktop chạy suốt lúc làm.

### Bước 2. Tạo file `.env`
**Mở** 💻 terminal ở thư mục **[Dự án]**.

**Làm**:
```bash
cp .env.example .env
```

**Mở** 📝 editor → file `.env` ở thư mục **[Dự án]**, rồi sửa 4 dòng:

| Dòng | Điền |
| --- | --- |
| `MYSQL_ROOT_PASSWORD=` | Mật khẩu tùy ý, **chỉ chữ và số** |
| `MYSQL_PASSWORD=` | Mật khẩu khác, chỉ chữ và số |
| `HIS_JWT_KEY=` | Chuỗi bất kỳ dài ≥ 32 ký tự. Có thể sinh bằng `openssl rand -base64 48` trong Git Bash |
| `HIS_SEED_ADMIN_MAT_KHAU=` | Mật khẩu Admin để đăng nhập thử |

Các dòng `HIS_R2_*`, `HIS_VNPAY_*`, `SPRING_MAIL_*` chưa có khóa thì cứ để trống. Lưu file (Ctrl+S).

### Bước 3. Chạy backend (API + MySQL + NGINX)
**Mở** 💻 terminal ở thư mục **[Dự án]**.

**Làm**:
```bash
docker compose up -d --build     # lần đầu mất 10–30 phút (tải image JDK và thư viện Maven)
docker compose ps
```

**Đúng khi**:
- `docker compose ps` hiện 3 dòng `nginx`, `backend`, `mysql`, cột STATUS đều có `(healthy)`. JVM khởi động chậm: còn `starting` thì đợi 30–60 giây rồi gõ lại;
- 🌐 mở <http://localhost/health> thấy `{"status":"UP"}`.

Xem log: trong Docker Desktop → **Containers** → bấm vào container `backend`. Hoặc gõ `docker compose logs -f backend` (Ctrl+C để thoát).

### Bước 4. Thử API bằng Swagger
**Mở** 🌐 <http://localhost/swagger-ui.html>.

**Làm**:
1. Mở `POST /api/nguoi-dung/dang-nhap` → **Try it out**.
2. Điền email ở dòng `HIS_SEED_ADMIN_EMAIL` và mật khẩu ở dòng `HIS_SEED_ADMIN_MAT_KHAU` trong `.env` → **Execute**.
3. Copy giá trị `accessToken` trong kết quả.
4. Bấm nút **Authorize** ở đầu trang → dán token → **Authorize**.

**Đúng khi**: gọi thử `GET /api/nguoi-dung/toi` trả mã `200`.

### Bước 5. Chạy frontend
**Mở** một terminal **thứ hai** ở **[Dự án]** (trong IDE bấm dấu `+` của terminal). Terminal này sẽ chạy suốt. Lệnh `cd frontend` dưới đây chuyển nó sang **[frontend]**.

**Làm**:
```bash
cd frontend
npm ci          # chỉ cần lần đầu, hoặc khi package.json đổi
npm run dev
```

**Đúng khi**: terminal hiện `Local: http://localhost:5173/`; 🌐 mở <http://localhost:5173> thấy trang chủ TheHos, đăng nhập được bằng tài khoản Admin.

Sửa file trong `frontend/src/` thì trình duyệt tự cập nhật. Bấm Ctrl+C ở cửa sổ này để dừng frontend.

### Bước 6. Sửa backend xong thì chạy lại
**Mở** 💻 terminal ở thư mục **[Dự án]**.

**Làm**:
```bash
docker compose up -d --build backend
```

### Bước 7. Chạy test trước khi đẩy mã
**Mở** 💻 terminal ở thư mục **[Dự án]**.

**Làm**:
```bash
cd backend
./mvnw -B verify      # cần Docker Desktop đang chạy (test tích hợp dùng MySQL thật qua Testcontainers)
cd ../frontend
npm run lint
npm run test
npm run build
cd ..
```

**Đúng khi**: backend in `BUILD SUCCESS`; frontend không có dòng `Failed` hoặc `error`. Có lỗi thì sửa xong mới đẩy mã.

> Windows báo `JAVA_HOME environment variable is not defined correctly`: biến `JAVA_HOME` trỏ tới JDK đã gỡ. Mở **Start → Edit the system environment variables → Environment Variables** → sửa `JAVA_HOME` thành thư mục JDK 21 (vd `C:\Program Files\Java\jdk-21`), mở lại terminal.

### Bước 8. Tắt khi làm xong
**Làm** trong 💻 terminal ở **[Dự án]**:
```bash
docker compose down
```
Lệnh này **không** làm mất dữ liệu MySQL; lần sau làm **Phần A2** là dữ liệu vẫn còn.

> **Thêm hoặc đổi bảng**: viết file SQL Flyway mới (không sửa file cũ) vào thư mục migration của module, tên `V<năm tháng ngày giờ phút>__<tiền tố>_<mo_ta>.sql`. Ví dụ module DanhMuc: `backend/modules/danh-muc/his-danh-muc/src/main/resources/db/migration/danhmuc/V202612011030__dm_them_cot_x.sql`. Sửa entity Java cho khớp. Backend tự chạy Flyway khi khởi động lại (bước 6); lệch giữa entity và bảng thì backend báo lỗi ngay lúc khởi động.

---

## Phần A2 — Chạy lại (máy đã chạy dự án trước đó)

Dùng khi mở máy ra làm tiếp, sau khi `git pull` mã mới, hoặc sau khi đổi tên/chuyển thư mục dự án. **Không** cần tạo lại `.env`, **không** mất dữ liệu cũ.

> ⚠️ **Máy từng chạy bản .NET (trước 11/2026), chạy bản Java lần đầu**: làm 2 việc một lần:
> 1. Đổi tên biến trong `.env` theo `.env.example` mới: `Jwt__Key` → `HIS_JWT_KEY`, `Seed__AdminEmail` → `HIS_SEED_ADMIN_EMAIL`, `Seed__AdminSoDienThoai` → `HIS_SEED_ADMIN_SO_DIEN_THOAI`, `Seed__AdminMatKhau` → `HIS_SEED_ADMIN_MAT_KHAU`, `Cors__AllowedOrigins` → `HIS_CORS_ALLOWED_ORIGINS`, `R2__ServiceURL` → `HIS_R2_ENDPOINT`, `R2__AccessKey`/`SecretKey`/`Bucket` → `HIS_R2_ACCESS_KEY`/`SECRET_KEY`/`BUCKET`, `Smtp__*` → `SPRING_MAIL_*`, `Vnpay__*` → `HIS_VNPAY_*`.
> 2. CSDL dev cũ do EF Core tạo, Flyway sẽ báo `Found non-empty schema(s) ... without schema history table`. Chạy `docker compose down -v` (⚠️ **mất dữ liệu dev**) rồi `docker compose up -d --build` để Flyway tạo lại bảng và seed Admin.

### Bước 1. Bật Docker Desktop
**Mở**: Start menu → **Docker Desktop**.

**Đúng khi**: góc dưới bên trái hiện **Engine running**.

> Container của dự án có chế độ tự khởi động, nên Docker Desktop vừa bật là `his-mysql-1`, `his-backend-1`, `his-nginx-1` có thể đã chạy sẵn. Đó là container **cũ**, có thể đang dùng mã cũ hoặc đường dẫn thư mục cũ. Vẫn phải làm bước 4.

### Bước 2. Lấy mã mới nhất
**Mở** 💻 terminal ở thư mục **[Dự án]**.

**Làm**:
```bash
git status        # có file đang sửa dở thì commit hoặc hỏi nhóm trưởng trước khi pull
git pull
```

**Đúng khi**: `git pull` báo `Already up to date.` hoặc liệt kê các file vừa cập nhật. Để ý trong danh sách có `.env.example`, `frontend/package.json` hoặc `frontend/package-lock.json` không; có thì bước 3, bước 5 phải làm thêm.

### Bước 3. Kiểm tra `.env` còn thiếu dòng nào không
**Làm** trong 💻 terminal ở **[Dự án]**:
```bash
diff <(grep -oE '^[A-Za-z0-9_]+=' .env.example | sort) <(grep -oE '^[A-Za-z0-9_]+=' .env | sort)
```

**Đúng khi**: lệnh không in gì. Nếu in dòng bắt đầu bằng `<` (ví dụ `< HIS_VNPAY_TMN_CODE=`), đó là dòng mới có trong `.env.example` mà `.env` chưa có: **mở** 📝 `.env.example`, copy dòng đó sang `.env`, điền giá trị (chưa có khóa thì để trống), lưu.

⚠️ **Không** sửa `MYSQL_PASSWORD`, `MYSQL_ROOT_PASSWORD` trong `.env` cũ: MySQL vẫn giữ mật khẩu của lần chạy đầu (xem Phần D).

### Bước 4. Chạy backend (API + MySQL + NGINX)
**Làm** trong 💻 terminal ở **[Dự án]**:
```bash
docker compose up -d --build     # mã không đổi thì chỉ mất khoảng 1 phút
docker compose ps
```

Luôn dùng `--build`: lệnh build lại image backend theo mã mới và **tạo lại** container cũ khi cấu hình đổi (kể cả khi thư mục dự án đã đổi tên). Dữ liệu MySQL nằm trong volume `his_mysql_data` nên không mất.

**Đúng khi**:
- `docker compose ps` hiện 3 dòng `nginx`, `backend`, `mysql`, đều `(healthy)`. Còn `starting` thì đợi 30–60 giây rồi gõ lại;
- 🌐 <http://localhost/health> hiện `{"status":"UP"}`;
- 🌐 <http://localhost/swagger-ui.html> đăng nhập được bằng tài khoản Admin **của lần chạy đầu** (như Phần A bước 4).

> Đăng nhập Admin bị từ chối? Dòng `HIS_SEED_ADMIN_EMAIL`, `HIS_SEED_ADMIN_MAT_KHAU` chỉ được dùng **một lần**, khi CSDL chưa có Admin. Đổi hai dòng này sau lần chạy đầu thì không có tác dụng; dùng mật khẩu lúc đầu. Quên hẳn thì xem dòng *Quên mật khẩu Admin trên máy mình* ở Phần D.

### Bước 5. Chạy frontend
**Mở** một terminal **thứ hai** ở **[Dự án]**.

**Làm**:
```bash
cd frontend
npm ci          # CHỈ khi bước 2 có cập nhật package.json/package-lock.json, hoặc chưa có thư mục node_modules
npm run dev
```

**Đúng khi**: 🌐 <http://localhost:5173> hiện trang chủ TheHos, đăng nhập được bằng tài khoản Admin.

### Bước 6. Làm tiếp như bình thường
- Sửa backend → Phần A bước 6; trước khi đẩy mã → Phần A bước 7; làm xong → Phần A bước 8.
- Có migration mới (bước 2 kéo về file `V*.sql` mới trong `db/migration/`): **không** phải làm gì thêm, bước 4 đã tự áp vào CSDL.

---

## Phần B — Đưa web lên `nangnguyen.me` (nhóm trưởng làm một lần)

> Năm 2026-11 nhóm trưởng làm phần này cùng Claude qua dòng lệnh (`ssh`, `gh`, `wrangler`, API Cloudflare); Droplet DigitalOcean tạo trên web. Tài liệu dưới đây là cách làm tay trên giao diện web — dùng khi cần làm lại hoặc kiểm tra. Kết quả hai cách giống nhau.

Trước khi bắt đầu, tạo thư mục **[his-keys]**. **Làm** trong 💻 terminal bất kỳ:
```bash
mkdir -p ~/his-keys
```

### Bước 1. Chuyển tên miền sang Cloudflare
**Mở** 🌐 <https://dash.cloudflare.com> → đăng ký hoặc đăng nhập.

**Làm**:
1. Bấm **Add a domain** → gõ `nangnguyen.me` → chọn gói **Free** → **Continue** cho tới khi Cloudflare hiện 2 nameserver (dạng `xxx.ns.cloudflare.com`).
2. Ở **DNS → Records**, Cloudflare đã chép sẵn bản ghi cũ từ Namecheap. **Xóa** 4 bản ghi `A` của `nangnguyen.me` trỏ `185.199.108–111.153` (GitHub Pages cũ), bản ghi `CNAME www`, 5 bản ghi `MX eforward*` và bản ghi `TXT v=spf1 ...` (chuyển tiếp email Namecheap — nhóm không dùng). Bước 9 sẽ gắn `nangnguyen.me`, `www` vào Pages.
3. Mở tab mới: 🌐 <https://ap.www.namecheap.com/domains/list/> → dòng `nangnguyen.me` → **Manage**.
4. Mục **Nameservers** → chọn **Custom DNS** → dán 2 nameserver → bấm dấu ✓ xanh.

**Đúng khi**: Cloudflare gửi email báo `nangnguyen.me` **Active** (vài phút tới vài giờ).

### Bước 2. Bật SSL và lấy chứng chỉ
**Mở** 🌐 <https://dash.cloudflare.com> → `nangnguyen.me`.

**Làm**:
1. Menu trái **SSL/TLS → Overview** → **Configure** → chọn **Full (strict)** → lưu.
2. **SSL/TLS → Edge Certificates** → bật **Always Use HTTPS**.
3. **SSL/TLS → Origin Server** → **Create Certificate** → giữ mặc định → **Create**.
4. Cloudflare hiện 2 ô. Lưu từng ô thành một file:
   - **Mở** Notepad → dán ô **Origin Certificate** → **File → Save As** → thư mục `C:\Users\<tên bạn>\his-keys`, tên `origin.pem`, ô *Save as type* chọn **All Files**;
   - làm tương tự với ô **Private Key**, lưu tên `origin.key`.

⚠️ Ô Private Key chỉ hiện **một lần**. Lỡ đóng mà chưa lưu thì tạo lại chứng chỉ.

**Đúng khi**: trong `his-keys` có 2 file `origin.pem` và `origin.key`, **không** có đuôi `.txt`.

### Bước 3. Tạo kho lưu file R2
**Mở** 🌐 <https://dash.cloudflare.com> → menu trái **R2 Object Storage**. Lần đầu Cloudflare đòi thêm thẻ; gói free không trừ tiền.

**Làm**:
1. Bấm **Create bucket**, tạo 3 bucket: `thehos-dev`, `thehos-prod`, `thehos-sao-luu`.
2. Ở trang R2 chính, copy **Account ID** (cột phải) vào một file nháp.
3. **Manage API tokens** → **Create API token**, tạo 2 token. Sau mỗi lần tạo, copy ngay **Access Key ID** và **Secret Access Key** vào file nháp, vì chúng chỉ hiện một lần:
   - **token app**: quyền *Object Read & Write*, chọn bucket `thehos-dev` và `thehos-prod`;
   - **token sao lưu**: quyền *Object Read & Write*, chỉ chọn bucket `thehos-sao-luu`.
4. Mở bucket `thehos-dev` → **Settings** → **CORS Policy** → **Add** → dán đoạn dưới → **Save**. Làm lại y hệt cho bucket `thehos-prod`.
   ```json
   [{ "AllowedOrigins": ["https://nangnguyen.me", "https://www.nangnguyen.me", "http://localhost:5173"], "AllowedMethods": ["GET", "PUT"], "AllowedHeaders": ["*"], "MaxAgeSeconds": 3600 }]
   ```

**Đúng khi**: file nháp có Account ID và 2 cặp khóa. Xóa file nháp sau khi làm xong bước 6 và bước 10.

### Bước 4. Tạo máy chủ DigitalOcean Droplet
Máy chủ là một Droplet của DigitalOcean (VPS Ubuntu). Giá Basic 2 GiB là 12 USD/tháng, tính theo giây. ⚠️ Droplet **tắt vẫn bị tính tiền**; muốn dừng tính tiền phải **Destroy**.

**Làm** trong 💻 terminal bất kỳ — tạo khóa SSH để vào máy chủ:
```bash
cd ~/his-keys
ssh-keygen -t ed25519 -f his-vps -N "" -C his-vps
cat his-vps.pub          # copy dòng bắt đầu bằng ssh-ed25519
```

**Mở** 🌐 <https://cloud.digitalocean.com/registrations/new> → đăng ký, xác minh email, thêm thẻ hoặc PayPal theo yêu cầu.

**Làm**:
1. Avatar → **My Account** → **Security** → bật **Two-factor authentication**.
2. **Billing** → tab **Spend alerts** → **Create alert**: tên `thehos`, Monthly budget `15`, Scope **Total spend** → **Create**. ⚠️ **Bắt buộc** làm bước này. Đây chỉ là email cảnh báo, không tự chặn chi tiêu.
3. **Settings** → **Security** → **SSH Keys** → **Add SSH Key** → dán dòng `his-vps.pub` vừa copy, Name `his-vps`.
4. **Create** (góc trên phải) → **Droplets**, chọn:

   | Ô | Chọn |
   | --- | --- |
   | Region | **Singapore** (SGP1) |
   | Image | **Ubuntu 24.04 (LTS) x64** |
   | Size | **Basic** → **Regular** → 12 USD/mo (2 GB, 1 CPU, 50 GB SSD) — JVM + MySQL không vừa 1 GiB |
   | Authentication | **SSH Key** → tích `his-vps` (không chọn Password) |
   | Tùy chọn thêm | Tích **Monitoring** (miễn phí); không tích Backups (tốn thêm tiền, đã có sao lưu lên R2 ở bước 10) |
   | Hostname | `thehos-vps` |
   | Tags | `thehos` |

   Bấm **Create Droplet**.
5. Menu trái **Networking** → **Firewalls** → **Create Firewall**: tên `thehos-fw`; **Inbound Rules** giữ SSH 22, thêm **HTTP** 80 và **HTTPS** 443 (nguồn All IPv4, All IPv6); Outbound giữ mặc định; **Apply to Droplets** gõ tag `thehos` → **Create Firewall**.

**Đúng khi**: Droplet ở trạng thái **On**, Firewall `thehos-fw` gắn 1 Droplet. Copy **ipv4** của Droplet; đây là `<IP>` trong mọi lệnh phía dưới. IP giữ nguyên khi tắt/bật hay khởi động lại, chỉ mất khi Destroy.

### Bước 5. Cài máy chủ bằng script có sẵn
**Mở** 💻 terminal ở thư mục **[Dự án]**. Script nằm ở `scripts/setup-vps.sh`.

Droplet mới chỉ có user `root`. Script tạo user `his` (dùng chung khóa SSH của root), cài Docker, swap, chrony, rclone, tạo `/opt/his`, cron sao lưu, rồi **tắt đăng nhập root**.

**Làm**:
```bash
scp -i ~/his-keys/his-vps scripts/setup-vps.sh root@<IP>:~
ssh -i ~/his-keys/his-vps root@<IP>        # lần đầu hỏi "Are you sure..." → gõ yes
```
Lúc này đang ở ☁️ VPS (user root). Gõ tiếp:
```bash
bash setup-vps.sh      # khoảng 3–5 phút
```
**Chưa** gõ `exit`. Mở một 💻 terminal **mới** và thử vào bằng user `his`:
```bash
ssh -i ~/his-keys/his-vps his@<IP>
```

**Đúng khi**: script in `Xong` ở cuối, không có dòng lỗi màu đỏ, và terminal mới vào được `his@thehos-vps`. Lúc đó mới `exit` phiên root. Từ nay `ssh root@<IP>` bị từ chối là đúng.

### Bước 6. Tạo `.env` trên máy chủ
**Mở** 📝 editor → file `.env.example` ở thư mục **[Dự án]** → **Ctrl+A**, **Ctrl+C** để copy hết.

**Làm** trong 💻 terminal bất kỳ:
```bash
ssh -i ~/his-keys/his-vps his@<IP>
```
Ở ☁️ VPS:
```bash
nano /opt/his/.env
```
Chuột phải để dán nội dung vừa copy, rồi dùng phím mũi tên đi tới từng dòng và sửa:

| Dòng | Điền |
| --- | --- |
| `MYSQL_ROOT_PASSWORD`, `MYSQL_PASSWORD` | Mật khẩu **mới**, chỉ chữ và số, khác máy mình |
| `HIS_JWT_KEY` | Chuỗi **mới** ≥ 32 ký tự |
| `HIS_SEED_ADMIN_EMAIL`, `HIS_SEED_ADMIN_MAT_KHAU` | Tài khoản Admin thật, mật khẩu mạnh |
| `HIS_CORS_ALLOWED_ORIGINS` | `https://nangnguyen.me,https://www.nangnguyen.me` |
| `HIS_R2_ENDPOINT` | `https://<Account ID>.r2.cloudflarestorage.com` |
| `HIS_R2_ACCESS_KEY`, `HIS_R2_SECRET_KEY` | Cặp khóa **token app** ở bước 3 |
| `HIS_R2_BUCKET` | `thehos-prod` |
| `HIS_VNPAY_TMN_CODE`, `HIS_VNPAY_HASH_SECRET` | Mã và khóa VNPAY sandbox của nhóm (bước 11) |
| `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD`, `HIS_EMAIL_NGUOI_GUI` | Login, SMTP key Brevo và địa chỉ gửi (bước 12); `SPRING_MAIL_HOST`/`PORT` giữ `smtp-relay.brevo.com`/`2525` |

Lưu bằng **Ctrl+O** → **Enter**, thoát bằng **Ctrl+X**. Gõ tiếp:
```bash
chmod 600 /opt/his/.env
exit
```

### Bước 7. Đưa chứng chỉ lên máy chủ và trỏ tên miền `api`
**Làm** trong 💻 terminal bất kỳ (lệnh `cd ~/his-keys` đưa terminal tới **[his-keys]**):
```bash
cd ~/his-keys
scp -i his-vps origin.pem origin.key his@<IP>:/opt/his/nginx/certs/
```

**Mở** 🌐 <https://dash.cloudflare.com> → `nangnguyen.me` → **DNS → Records** → **Add record**:

| Ô | Điền |
| --- | --- |
| Type | `A` |
| Name | `api` |
| IPv4 address | `<IP>` |
| Proxy status | **Proxied** (đám mây màu cam) |

Bấm **Save**.

### Bước 8. Cho GitHub tự deploy
**Làm** trong 💻 terminal bất kỳ (lệnh `cd ~/his-keys` đưa terminal tới **[his-keys]**):
```bash
cd ~/his-keys
ssh-keygen -t ed25519 -f his-deploy -N ""
ssh -i his-vps his@<IP> "cat >> ~/.ssh/authorized_keys" < his-deploy.pub
cat his-deploy
```
Bôi đen **toàn bộ** nội dung vừa in, từ `-----BEGIN` tới `-----END ...-----`, rồi copy.

**Mở** 🌐 <https://github.com/nangnguyen235/Web-Hospital-Management-System-with-AI-Assistant/settings/secrets/actions> → **New repository secret**, tạo lần lượt 3 secret:

| Name | Secret |
| --- | --- |
| `VPS_HOST` | `<IP>` |
| `VPS_USER` | `his` |
| `VPS_SSH_KEY` | Nội dung vừa copy |

Thêm 2 secret cho bước đưa frontend lên Pages (`CLOUDFLARE_API_TOKEN` là token đã tạo ở Phần B, Account ID ở trang chủ Cloudflare, cột phải):

| Name | Secret |
| --- | --- |
| `CLOUDFLARE_API_TOKEN` | API token Cloudflare (quyền Cloudflare Pages Edit) |
| `CLOUDFLARE_ACCOUNT_ID` | Account ID |

Sau đó **mở** 🌐 <https://github.com/nangnguyen235/Web-Hospital-Management-System-with-AI-Assistant/actions> → bên trái chọn **Deploy** → **Run workflow** → chọn nhánh **`baseline`**.

**Đúng khi**: lần chạy chuyển xanh (khoảng 8–12 phút), và 🌐 <https://api.nangnguyen.me/health> hiện `{"status":"UP"}`.

### Bước 9. Frontend trên Cloudflare Pages
Frontend **không** nối Git với Pages: workflow **Deploy** (chạy từ nhánh `baseline`) tự build rồi đẩy `frontend/dist` lên project Pages `thehos` (Direct Upload). Chỉ cần làm một lần:

1. 🌐 <https://dash.cloudflare.com> → **Workers & Pages** → **Create** → **Pages** → **Upload assets** → tên project `thehos` → tải lên tạm một file bất kỳ để tạo project (lần Deploy sau sẽ thay).
2. Tab **Custom domains** → **Set up a custom domain** → `nangnguyen.me` → **Activate domain**. Làm thêm lần nữa với `www.nangnguyen.me`.

**Đúng khi**: 🌐 <https://nangnguyen.me> hiện trang chủ TheHos; đăng nhập Admin được, vào **Quản trị → Khoa** thêm và sửa được.

### Bước 10. Bật sao lưu CSDL hằng đêm
Cron 02:00 đã được `setup-vps.sh` cài sẵn; chỉ còn cấu hình rclone. **Làm** trong 💻 terminal ở **[Dự án]**: `scp -i ~/his-keys/his-vps scripts/*.sh his@<IP>:/opt/his/scripts/` rồi `ssh -i ~/his-keys/his-vps his@<IP>`. Ở ☁️ VPS, thay 3 chỗ `<...>` bằng **token sao lưu** và Account ID ở bước 3:
```bash
R2_ACCOUNT_ID=<Account ID> R2_SAO_LUU_ACCESS_KEY=<Access Key ID sao lưu> R2_SAO_LUU_SECRET_KEY=<Secret sao lưu> \
  bash /opt/his/scripts/cau-hinh-rclone.sh
bash /opt/his/scripts/backup-mysql.sh      # chạy thử một lần
crontab -l                                 # thấy dòng 0 2 * * * ... backup-mysql.sh
```

**Đúng khi**: lệnh chạy thử in `Xong: his-....sql.gz`, và 🌐 Cloudflare → R2 → bucket `thehos-sao-luu` có file đó.

Thử khôi phục (bắt buộc ít nhất một lần trước buổi bảo vệ) sang CSDL tạm, không đụng dữ liệu thật:
```bash
cd /opt/his && bash scripts/khoi-phuc-mysql.sh his-<ngày>.sql.gz his_thu   # in số bảng ≥ 25
```

### Bước 11. Tài khoản VNPAY sandbox của nhóm
**Mở** 🌐 <https://sandbox.vnpayment.vn/devreg/> → điền thông tin, email của nhóm → VNPAY gửi email có **vnp_TmnCode**, **vnp_HashSecret** và tài khoản trang quản trị merchant sandbox.

**Làm**:
1. Điền 2 giá trị vào `HIS_VNPAY_TMN_CODE`, `HIS_VNPAY_HASH_SECRET` của `/opt/his/.env` (bước 6), rồi ☁️ `cd /opt/his && docker compose up -d`.
2. Trang quản trị merchant sandbox → cấu hình **IPN URL**: `https://api.nangnguyen.me/api/vien-phi/vnpay/ipn`.

⚠️ **Không** dùng mã/khóa sandbox có sẵn trong repo TheDuckHospital (rule 6).

**Đúng khi**: đặt khám thử trên `https://nangnguyen.me`, thanh toán bằng thẻ test của VNPAY (trong email) → về trang kết quả "Thanh toán thành công"; ☁️ `docker compose logs backend | grep -i ipn` thấy IPN gọi vào.

### Bước 12. Brevo gửi email OTP
DigitalOcean chặn cổng SMTP 25/465/587 trên mọi Droplet (<https://docs.digitalocean.com/support/why-is-smtp-blocked/>), nên máy chủ gửi OTP qua **Brevo** cổng **2525** (gói free khoảng 300 email/ngày). Máy mình vẫn dùng được Gmail + App Password (`smtp.gmail.com`, `587`).

**Mở** 🌐 <https://www.brevo.com> → **Sign up free** → xác minh email; Brevo có thể duyệt tài khoản trước khi cho gửi.

**Làm**:
1. **Senders, Domains & Dedicated IPs** → **Domains** → **Add a domain** → `nangnguyen.me` → thêm các bản ghi DNS Brevo đưa ra (TXT `brevo-code`, DKIM, DMARC) vào 🌐 Cloudflare → `nangnguyen.me` → **DNS → Records** (Proxy status: **DNS only**) → quay lại Brevo bấm **Authenticate**.
2. Tab **Senders** → **Add a sender**: `TheHos Hospital`, `no-reply@nangnguyen.me`.
3. **SMTP & API** → **SMTP** → **Generate a new SMTP key** → copy key (chỉ hiện một lần) và dòng **Login** (`...@smtp-brevo.com`).
4. Điền vào `/opt/his/.env`: `SPRING_MAIL_HOST=smtp-relay.brevo.com`, `SPRING_MAIL_PORT=2525`, `SPRING_MAIL_USERNAME=<Login>`, `SPRING_MAIL_PASSWORD=<SMTP key>`, `HIS_EMAIL_NGUOI_GUI=no-reply@nangnguyen.me`, rồi ☁️ `cd /opt/his && docker compose up -d`.

**Đúng khi**: đăng ký tài khoản bệnh nhân trên `https://nangnguyen.me` nhận được email OTP từ `no-reply@nangnguyen.me`.

---

## Phần C — Hai nhánh `baseline` và `main`

| Nhánh | Chứa gì | CI (test) | Deploy lên VPS + Pages |
| --- | --- | --- | --- |
| `baseline` | Bản hoàn chỉnh nhóm trưởng dựng — bản demo, bản đang chạy trên `nangnguyen.me` | Có (mọi PR vào `baseline`) | **Có** — mỗi lần `baseline` đổi |
| `main` | Các thành viên đưa lại code theo nhiệm vụ của mình (skill `day-ma`, `nhiemvuthanhvien.md`) | Có (mọi PR vào `main` và mỗi lần `main` đổi) | **Không** — `main` thay đổi không ảnh hưởng `baseline` và VPS |

Cả hai nhánh được bảo vệ bằng ruleset (`scripts/bao-ve-nhanh.sh`): chặn xóa, chặn force-push, phải qua Pull Request có **1 approval**; riêng `baseline` còn bắt buộc CI xanh (`main` vẫn chạy CI nhưng không bắt buộc, vì khi mới lắp một phần task thì build chưa đủ module). Chỉ Admin repo (nhóm trưởng) được bypass.

**Thành viên làm hằng ngày**:
1. 💻 `git switch main && git pull`, rồi tạo nhánh riêng: `git switch -c feature/<ten>-<viec>`.
2. Lắp code theo task của mình (lệnh trong `nhiemvuthanhvien.md`), chạy Phần A bước 7 (test).
3. `git push -u origin feature/<ten>-<viec>` → 🌐 mở **Pull Request vào `main`**. CI tự chạy.
4. CI xanh và nhóm trưởng approve thì bấm **Merge**. Không có gì được deploy.

**Đưa `main` thành bản triển khai chính thức** (khi `main` đã đủ và kiểm thử xong): nhóm trưởng mở Pull Request **`main` → `baseline`**, CI xanh + approve → Merge → workflow **Deploy** đưa lên `nangnguyen.me`.

## Phần D — Gặp lỗi

| Thấy gì | Làm gì |
| --- | --- |
| `docker compose` báo không kết nối được Docker | Mở Docker Desktop, chờ **Engine running** |
| Log backend có `Access denied for user 'his'` | Đã đổi mật khẩu MySQL trong `.env` sau lần chạy đầu; MySQL vẫn giữ mật khẩu cũ. Trên máy mình: 💻 terminal ở **[Dự án]** chạy `docker compose down -v` rồi `docker compose up -d` (mất dữ liệu dev, backend tự seed lại). ⚠️ **Không** chạy `down -v` trên VPS; ở đó phải trả lại mật khẩu cũ |
| Container `backend` không lên `healthy` trên máy mình | 💻 `docker compose logs backend`, thường do sai hoặc thiếu dòng trong `.env` (Phần A2 bước 3). `HIS_JWT_KEY` thiếu hoặc ngắn hơn 32 ký tự thì backend dừng ngay |
| Log backend có `Found non-empty schema(s) ... without schema history table` | CSDL do bản .NET cũ tạo → xem ghi chú đầu Phần A2 (chỉ trên máy mình) |
| Log backend có `Schema-validation: missing column` / `wrong column type` | Entity Java lệch bảng → thêm file Flyway mới cho khớp (Phần A, ghi chú cuối) |
| Sau khi đổi tên hoặc chuyển thư mục dự án, `nginx` báo `(unhealthy)`, <http://localhost/health> không mở được | Container cũ vẫn trỏ tới thư mục cũ; Docker tự tạo thư mục rỗng ở đường dẫn cũ nên NGINX không có cấu hình. 💻 terminal ở **[Dự án]** (thư mục **mới**) chạy `docker compose up -d --build`. Thư mục rỗng ở đường dẫn cũ có thể xóa tay sau đó |
| `docker compose up` báo cổng `80` hoặc `3317` đã bị dùng | Đang chạy dự án khác chiếm cổng. Tắt dự án đó trong Docker Desktop → **Containers**, rồi chạy lại |
| Quên mật khẩu Admin trên máy mình | 💻 ở **[Dự án]**: `docker compose down -v` rồi `docker compose up -d --build`. ⚠️ Mất **toàn bộ** dữ liệu dev; backend tạo lại bảng và Admin theo `HIS_SEED_ADMIN_*` trong `.env` hiện tại. **Không** làm trên VPS |
| Cloudflare **521** | ☁️ `cd /opt/his && docker compose ps`; 🌐 kiểm DigitalOcean → **Networking → Firewalls** → `thehos-fw` đã mở cổng 443 và gắn Droplet |
| Cloudflare **526** | Chứng chỉ sai hoặc thiếu → làm lại bước 2 và bước 7 của Phần B |
| **502 Bad Gateway** | ☁️ `cd /opt/his && docker compose logs --tail 100 backend` |
| Trình duyệt báo lỗi **CORS** | ☁️ sửa dòng `HIS_CORS_ALLOWED_ORIGINS` trong `/opt/his/.env`, rồi `cd /opt/his && docker compose up -d` |
| Không nhận được email OTP | ☁️ `docker compose logs backend | grep -i email`: `CHUA_CAU_HINH_EMAIL` → thiếu `SPRING_MAIL_*`; `GUI_EMAIL_THAT_BAI` → sai Login/SMTP key Brevo, cổng không phải 2525, hoặc tài khoản Brevo chưa được duyệt (Phần B bước 12) |
| Workflow Deploy đỏ ở bước SSH | Kiểm secret `VPS_HOST` đúng IP Droplet, `VPS_USER` là `his`, `VPS_SSH_KEY` đủ từ `-----BEGIN` tới `-----END`; khóa `his-deploy.pub` có trong `/home/his/.ssh/authorized_keys`. Droplet đã Destroy rồi tạo lại thì IP mới → sửa `VPS_HOST` và bản ghi DNS `api` |
| Sửa `.env` mà không thấy tác dụng | Chạy `docker compose up -d` ở thư mục chứa `.env` đó |
