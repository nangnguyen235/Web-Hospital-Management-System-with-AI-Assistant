#!/usr/bin/env bash
# Cài VPS DigitalOcean Droplet Ubuntu 24.04 cho HIS — 5-trien-khai-cicd/vps-cloudflare-cicd.md mục 3.
# Droplet mới chỉ có user root (đăng nhập bằng khóa SSH gắn lúc tạo). Script tạo user thường để chạy ứng dụng,
# rồi mới tắt đăng nhập root.
# Chạy lần đầu:  ssh root@<IP>  →  bash setup-vps.sh
# Chạy lại sau đó: ssh his@<IP> →  sudo bash setup-vps.sh   (chạy lại nhiều lần vẫn an toàn)
set -euo pipefail

if [ "$(id -u)" -ne 0 ]; then
  echo "Hãy chạy bằng root hoặc sudo: sudo bash $0"
  exit 1
fi
NGUOI_DUNG="${HIS_USER:-his}"

echo "== 1/7 User $NGUOI_DUNG (khóa SSH chép từ root, sudo không hỏi mật khẩu vì SSH chỉ nhận khóa)"
if ! id "$NGUOI_DUNG" >/dev/null 2>&1; then
  adduser --disabled-password --gecos "" "$NGUOI_DUNG"
fi
usermod -aG sudo "$NGUOI_DUNG"
echo "$NGUOI_DUNG ALL=(ALL) NOPASSWD:ALL" > "/etc/sudoers.d/$NGUOI_DUNG"
chmod 440 "/etc/sudoers.d/$NGUOI_DUNG"
visudo -cf "/etc/sudoers.d/$NGUOI_DUNG" >/dev/null
NHA="/home/$NGUOI_DUNG"
install -d -m 700 -o "$NGUOI_DUNG" -g "$NGUOI_DUNG" "$NHA/.ssh"
touch "$NHA/.ssh/authorized_keys"
if [ -f /root/.ssh/authorized_keys ]; then
  # Thêm từng khóa của root mà user chưa có (giữ khóa deploy đã thêm trước đó)
  while IFS= read -r khoa; do
    if [ -n "$khoa" ] && ! grep -qxF "$khoa" "$NHA/.ssh/authorized_keys"; then
      echo "$khoa" >> "$NHA/.ssh/authorized_keys"
    fi
  done < /root/.ssh/authorized_keys
fi
chown "$NGUOI_DUNG:$NGUOI_DUNG" "$NHA/.ssh/authorized_keys"
chmod 600 "$NHA/.ssh/authorized_keys"

echo "== 2/7 Docker Engine + compose plugin"
if ! command -v docker >/dev/null 2>&1; then
  apt-get update
  apt-get install -y ca-certificates curl
  install -m 0755 -d /etc/apt/keyrings
  curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
  chmod a+r /etc/apt/keyrings/docker.asc
  echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" \
    > /etc/apt/sources.list.d/docker.list
  apt-get update
  apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
fi
usermod -aG docker "$NGUOI_DUNG"
# Xoay vòng log container (mặc định json-file không giới hạn → đầy ổ đĩa)
LOG_CONF='{"log-driver":"json-file","log-opts":{"max-size":"10m","max-file":"3"}}'
if [ "$(cat /etc/docker/daemon.json 2>/dev/null)" != "$LOG_CONF" ]; then
  echo "$LOG_CONF" > /etc/docker/daemon.json
  systemctl restart docker
fi
systemctl enable --now docker

echo "== 3/7 Swap 2 GB (VPS 2 GiB RAM: JVM + MySQL — docker-compose.md mục 2)"
if ! swapon --show | grep -q '/swapfile'; then
  fallocate -l 2G /swapfile
  chmod 600 /swapfile
  mkswap /swapfile
  swapon /swapfile
fi
grep -q '^/swapfile' /etc/fstab || echo '/swapfile none swap sw 0 0' >> /etc/fstab
echo 'vm.swappiness=10' > /etc/sysctl.d/99-his-swap.conf
sysctl -p /etc/sysctl.d/99-his-swap.conf >/dev/null

echo "== 4/7 Đồng hồ: chrony (nguồn NTP mặc định của Ubuntu), múi giờ Việt Nam (cron 02:00 theo giờ VN)"
apt-get install -y chrony
systemctl enable --now chrony
timedatectl set-timezone Asia/Ho_Chi_Minh

echo "== 5/7 rclone (đẩy file sao lưu lên bucket R2)"
command -v rclone >/dev/null 2>&1 || apt-get install -y rclone

echo "== 6/7 Thư mục /opt/his, /var/backups/his và cron sao lưu 02:00"
install -d -o "$NGUOI_DUNG" -g "$NGUOI_DUNG" /opt/his /opt/his/nginx /opt/his/nginx/conf.d /opt/his/scripts /var/backups/his
install -d -m 700 -o "$NGUOI_DUNG" -g "$NGUOI_DUNG" /opt/his/nginx/certs
DONG_CRON='0 2 * * * bash /opt/his/scripts/backup-mysql.sh >> /var/backups/his/backup.log 2>&1'
( crontab -u "$NGUOI_DUNG" -l 2>/dev/null | grep -vF 'backup-mysql.sh' ; echo "$DONG_CRON" ) | crontab -u "$NGUOI_DUNG" -

echo "== 7/7 SSH chỉ nhận khóa, không đăng nhập root"
# Chỉ tắt root khi user đã có khóa — tránh tự khóa mình khỏi máy.
if [ ! -s "$NHA/.ssh/authorized_keys" ]; then
  echo "DỪNG: $NHA/.ssh/authorized_keys trống — chưa tắt đăng nhập root. Thêm khóa SSH rồi chạy lại."
  exit 1
fi
# sshd lấy giá trị ĐẦU TIÊN đọc được: đặt tên 00- để đứng trước 50-cloud-init.conf của Ubuntu.
cat > /etc/ssh/sshd_config.d/00-his.conf <<'CONF'
PasswordAuthentication no
KbdInteractiveAuthentication no
PermitRootLogin no
CONF
sshd -t
systemctl reload ssh

echo
echo "Xong. Mở terminal MỚI và thử: ssh his@<IP> (phải vào được) rồi mới đóng phiên root này."
echo "Kiểm tra: free -m (Swap 2047), chronyc tracking (Leap status: Normal), docker compose version, crontab -l."
