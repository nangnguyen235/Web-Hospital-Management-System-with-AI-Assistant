#!/usr/bin/env bash
# Tạo remote rclone "r2-sao-luu" cho bucket R2 sao lưu (token RIÊNG, chỉ có quyền bucket thehos-sao-luu).
# Khóa sao lưu KHÔNG nằm trong .env của ứng dụng (docker-compose.md mục 4). Chạy trên VPS bằng user his:
#   R2_ACCOUNT_ID=... R2_SAO_LUU_ACCESS_KEY=... R2_SAO_LUU_SECRET_KEY=... bash scripts/cau-hinh-rclone.sh
set -euo pipefail

: "${R2_ACCOUNT_ID:?Thiếu R2_ACCOUNT_ID}"
: "${R2_SAO_LUU_ACCESS_KEY:?Thiếu R2_SAO_LUU_ACCESS_KEY}"
: "${R2_SAO_LUU_SECRET_KEY:?Thiếu R2_SAO_LUU_SECRET_KEY}"

rclone config create r2-sao-luu s3 \
  provider=Cloudflare \
  access_key_id="$R2_SAO_LUU_ACCESS_KEY" \
  secret_access_key="$R2_SAO_LUU_SECRET_KEY" \
  endpoint="https://${R2_ACCOUNT_ID}.r2.cloudflarestorage.com" \
  region=auto \
  no_check_bucket=true \
  --non-interactive >/dev/null
chmod 600 "$(rclone config file | tail -n1)"
rclone lsd r2-sao-luu: >/dev/null 2>&1 || true
rclone ls r2-sao-luu:thehos-sao-luu --max-depth 1 >/dev/null && echo "Remote r2-sao-luu dùng được."
