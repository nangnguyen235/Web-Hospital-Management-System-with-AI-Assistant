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
systemctl enable --now docker

echo "== 2/6 Swap 2 GB (máy chỉ có 1 GiB RAM)"
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

echo "== 5/6 Thư mục /opt/his và /var/backups/his"
install -d -o "$NGUOI_DUNG" -g "$NGUOI_DUNG" /opt/his /opt/his/nginx /opt/his/nginx/conf.d /opt/his/scripts /var/backups/his
install -d -m 700 -o "$NGUOI_DUNG" -g "$NGUOI_DUNG" /opt/his/nginx/certs

echo "== 6/6 SSH chỉ nhận khóa, không đăng nhập root"
cat > /etc/ssh/sshd_config.d/99-his.conf <<'CONF'
PasswordAuthentication no
PermitRootLogin no
CONF
systemctl reload ssh

echo
echo "Xong. Kiểm tra: free -m (thấy Swap 2047), chronyc sources -v (dòng ^* 169.254.169.123), docker compose version."
echo "Đăng xuất rồi SSH lại để dùng docker không cần sudo."
