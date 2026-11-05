#!/usr/bin/env bash
# Sao lưu MySQL hằng đêm lên bucket R2 sao lưu, giữ 7 bản — 4-docker-compose/docker-compose.md mục 6.
# Cron (crontab -e của user ubuntu):
#   0 2 * * * bash /opt/his/scripts/backup-mysql.sh >> /var/backups/his/backup.log 2>&1
# Mật khẩu đọc từ /opt/his/.env (chmod 600), KHÔNG ghi trong crontab.
# rclone remote "r2-sao-luu" cấu hình bằng token riêng chỉ có quyền bucket sao lưu (docs/trien-khai.md).
set -euo pipefail

THU_MUC_APP=/opt/his
THU_MUC_SAO_LUU=/var/backups/his
REMOTE="${HIS_BACKUP_REMOTE:-r2-sao-luu:thehos-sao-luu}"
GIU=7

cd "$THU_MUC_APP"
doc_env() { grep -E "^$1=" .env | head -n1 | cut -d= -f2-; }
DB="$(doc_env MYSQL_DATABASE)"
TEN="his-$(date +%Y%m%d).sql.gz"

echo "[$(date '+%F %T')] Bắt đầu sao lưu $DB"
docker compose exec -T -e MYSQL_PWD="$(doc_env MYSQL_ROOT_PASSWORD)" mysql \
  mysqldump -uroot --single-transaction --routines --databases "$DB" \
  | gzip > "$THU_MUC_SAO_LUU/$TEN"

rclone copy "$THU_MUC_SAO_LUU/$TEN" "$REMOTE/"

# Giữ 7 bản mới nhất ở máy và trên R2
ls -1t "$THU_MUC_SAO_LUU"/his-*.sql.gz | tail -n +$((GIU + 1)) | xargs -r rm -f
rclone delete "$REMOTE/" --min-age "$((GIU + 1))d"

echo "[$(date '+%F %T')] Xong: $TEN"
