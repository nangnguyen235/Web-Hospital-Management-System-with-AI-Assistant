#!/usr/bin/env bash
# Khôi phục MySQL từ một bản sao lưu (4-docker-compose/docker-compose.md mục 6 — thử ít nhất một lần trước buổi bảo vệ).
# Cách dùng (trên VPS, trong /opt/his):
#   bash scripts/khoi-phuc-mysql.sh his-20261201.sql.gz            # lấy từ /var/backups/his, không có thì tải từ R2
#   bash scripts/khoi-phuc-mysql.sh his-20261201.sql.gz his_thu     # khôi phục sang CSDL khác để kiểm, không đè dữ liệu thật
# Khôi phục vào CSDL đang chạy sẽ GHI ĐÈ dữ liệu — script hỏi xác nhận trước.
set -euo pipefail

TEN="${1:?Thiếu tên file, vd his-20261201.sql.gz}"
DICH="${2:-}"
THU_MUC_SAO_LUU=/var/backups/his
REMOTE="${HIS_BACKUP_REMOTE:-r2-sao-luu:thehos-sao-luu}"

cd /opt/his
doc_env() { grep -E "^$1=" .env | head -n1 | cut -d= -f2-; }
DB="$(doc_env MYSQL_DATABASE)"
MAT_KHAU="$(doc_env MYSQL_ROOT_PASSWORD)"

if [ ! -f "$THU_MUC_SAO_LUU/$TEN" ]; then
  echo "Không có $TEN ở máy — tải từ $REMOTE"
  rclone copy "$REMOTE/$TEN" "$THU_MUC_SAO_LUU/"
fi
gzip -t "$THU_MUC_SAO_LUU/$TEN"

if [ -z "$DICH" ]; then
  read -r -p "Ghi đè CSDL '$DB' bằng $TEN? Gõ 'dong y' để tiếp tục: " XAC_NHAN
  [ "$XAC_NHAN" = "dong y" ] || { echo "Đã hủy."; exit 1; }
  zcat "$THU_MUC_SAO_LUU/$TEN" | docker compose exec -T -e MYSQL_PWD="$MAT_KHAU" mysql mysql -uroot
else
  # Đổi tên CSDL trong file dump để khôi phục sang CSDL thử
  docker compose exec -T -e MYSQL_PWD="$MAT_KHAU" mysql mysql -uroot -e "CREATE DATABASE IF NOT EXISTS \`$DICH\`"
  zcat "$THU_MUC_SAO_LUU/$TEN" \
    | sed -e "s/^CREATE DATABASE .*\`$DB\`.*/CREATE DATABASE IF NOT EXISTS \`$DICH\`;/" -e "s/^USE \`$DB\`;/USE \`$DICH\`;/" \
    | docker compose exec -T -e MYSQL_PWD="$MAT_KHAU" mysql mysql -uroot
  DICH_DB="$DICH"
fi

docker compose exec -T -e MYSQL_PWD="$MAT_KHAU" mysql mysql -uroot -N -e \
  "SELECT COUNT(*) AS so_bang FROM information_schema.tables WHERE table_schema='${DICH_DB:-$DB}'"
echo "Khôi phục xong vào '${DICH_DB:-$DB}'."
