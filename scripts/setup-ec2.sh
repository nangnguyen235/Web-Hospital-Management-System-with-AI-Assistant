#!/usr/bin/env bash
# Cài máy EC2 Ubuntu 24.04 cho HIS — 5-trien-khai-cicd/vps-cloudflare-cicd.md mục 3.
# Chạy: sudo bash setup-ec2.sh   (chạy lại nhiều lần vẫn an toàn)
set -euo pipefail

if [ "$(id -u)" -ne 0 ]; then
  echo "Hãy chạy bằng sudo: sudo bash $0"
  exit 1
fi
NGUOI_DUNG="${SUDO_USER:-ubuntu}"

echo "== 1/6 Docker Engine + compose plugin"
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
# Xoay vòng log container (mặc định json-file không giới hạn → đầy ổ EBS)
LOG_CONF='{"log-driver":"json-file","log-opts":{"max-size":"10m","max-file":"3"}}'
if [ "$(cat /etc/docker/daemon.json 2>/dev/null)" != "$LOG_CONF" ]; then
  echo "$LOG_CONF" > /etc/docker/daemon.json
  systemctl restart docker
fi
systemctl enable --now docker

echo "== 2/6 Swap 2 GB (t3.small 2 GiB RAM: JVM + MySQL — docker-compose.md mục 2)"
if ! swapon --show | grep -q '/swapfile'; then
  fallocate -l 2G /swapfile
  chmod 600 /swapfile
  mkswap /swapfile
  swapon /swapfile
fi
grep -q '^/swapfile' /etc/fstab || echo '/swapfile none swap sw 0 0' >> /etc/fstab
echo 'vm.swappiness=10' > /etc/sysctl.d/99-his-swap.conf
sysctl -p /etc/sysctl.d/99-his-swap.conf >/dev/null

echo "== 3/6 Đồng hồ: chrony + Amazon Time Sync, múi giờ Việt Nam (cron 02:00 theo giờ VN)"
apt-get install -y chrony
grep -q '169.254.169.123' /etc/chrony/chrony.conf \
  || echo 'server 169.254.169.123 prefer iburst minpoll 4 maxpoll 4' >> /etc/chrony/chrony.conf
systemctl restart chrony
timedatectl set-timezone Asia/Ho_Chi_Minh

echo "== 4/6 rclone (đẩy file sao lưu lên bucket R2)"
command -v rclone >/dev/null 2>&1 || apt-get install -y rclone

echo "== 5/6 Thư mục /opt/his, /var/backups/his và cron sao lưu 02:00"
install -d -o "$NGUOI_DUNG" -g "$NGUOI_DUNG" /opt/his /opt/his/nginx /opt/his/nginx/conf.d /opt/his/scripts /var/backups/his
install -d -m 700 -o "$NGUOI_DUNG" -g "$NGUOI_DUNG" /opt/his/nginx/certs
DONG_CRON='0 2 * * * bash /opt/his/scripts/backup-mysql.sh >> /var/backups/his/backup.log 2>&1'
( crontab -u "$NGUOI_DUNG" -l 2>/dev/null | grep -vF 'backup-mysql.sh' ; echo "$DONG_CRON" ) | crontab -u "$NGUOI_DUNG" -

echo "== 6/6 SSH chỉ nhận khóa, không đăng nhập root"
# sshd lấy giá trị ĐẦU TIÊN đọc được: đặt tên 00- để đứng trước 50-cloud-init.conf của Ubuntu.
rm -f /etc/ssh/sshd_config.d/99-his.conf
cat > /etc/ssh/sshd_config.d/00-his.conf <<'CONF'
PasswordAuthentication no
KbdInteractiveAuthentication no
PermitRootLogin no
CONF
sshd -t
systemctl reload ssh

echo
echo "Xong. Kiểm tra: free -m (thấy Swap 2047), chronyc sources -v (dòng ^* 169.254.169.123), docker compose version, crontab -l."
echo "Đăng xuất rồi SSH lại để dùng docker không cần sudo."
