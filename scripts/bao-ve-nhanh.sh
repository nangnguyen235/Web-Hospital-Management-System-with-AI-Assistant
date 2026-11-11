#!/usr/bin/env bash
# Bảo vệ 2 nhánh bằng ruleset GitHub (cần gh đã đăng nhập, quyền admin repo). Chạy lại nhiều lần an toàn.
#   baseline (bản triển khai): chặn xóa, chặn force-push, bắt buộc PR + 1 approval + CI xanh.
#   main (thành viên đưa lại code theo task): chặn xóa, chặn force-push, bắt buộc PR + 1 approval;
#     CI vẫn chạy cho mọi PR nhưng KHÔNG bắt buộc xanh — khi mới lắp một phần task, build chưa đủ module nên đỏ là bình thường.
#   Chỉ vai trò Admin của repo (trưởng nhóm) được bypass — dùng khi dựng nhánh lần đầu.
# Cách chạy: bash scripts/bao-ve-nhanh.sh [owner/repo]
set -euo pipefail

REPO="${1:-$(gh repo view --json nameWithOwner -q .nameWithOwner)}"

quy_tac_chung='
    { "type": "deletion" },
    { "type": "non_fast_forward" },
    { "type": "pull_request", "parameters": {
        "required_approving_review_count": 1,
        "dismiss_stale_reviews_on_push": true,
        "require_code_owner_review": false,
        "require_last_push_approval": false,
        "required_review_thread_resolution": false } }'

ci_bat_buoc=',
    { "type": "required_status_checks", "parameters": {
        "strict_required_status_checks_policy": false,
        "required_status_checks": [
          { "context": "Backend (Maven verify — unit, Modulith, ArchUnit, Testcontainers)" },
          { "context": "Frontend (lint + test + build)" } ] } }'

dat_ruleset() {  # $1 tên ruleset, $2 nhánh, $3 phần quy tắc thêm
  local ten="$1" nhanh="$2" them="$3" body id
  body=$(cat <<JSON
{
  "name": "$ten",
  "target": "branch",
  "enforcement": "active",
  "conditions": { "ref_name": { "include": ["refs/heads/$nhanh"], "exclude": [] } },
  "bypass_actors": [ { "actor_id": 5, "actor_type": "RepositoryRole", "bypass_mode": "always" } ],
  "rules": [ $quy_tac_chung $them ]
}
JSON
)
  id=$(gh api "repos/$REPO/rulesets" -q ".[] | select(.name == \"$ten\") | .id" || true)
  if [ -n "$id" ]; then
    echo "$body" | gh api -X PUT "repos/$REPO/rulesets/$id" --input - > /dev/null
    echo "Đã cập nhật ruleset $ten (#$id) cho nhánh $nhanh"
  else
    echo "$body" | gh api -X POST "repos/$REPO/rulesets" --input - > /dev/null
    echo "Đã tạo ruleset $ten cho nhánh $nhanh"
  fi
}

dat_ruleset "bao-ve-baseline" "baseline" "$ci_bat_buoc"
dat_ruleset "bao-ve-main" "main" ""

echo "Các ruleset hiện có (ruleset cũ trùng nhánh main thì xem lại trên GitHub → Settings → Rules):"
gh api "repos/$REPO/rulesets" -q '.[] | "  #\(.id) \(.name) (\(.enforcement))"'
