#!/usr/bin/env bash
# Bảo vệ 2 nhánh main và baseline bằng một ruleset GitHub (cần gh đã đăng nhập, quyền admin repo).
#   - Chặn xóa nhánh, chặn force-push.
#   - Mọi thay đổi vào main/baseline phải qua Pull Request, cần 1 approval, CI (backend + frontend) xanh.
#   - Chỉ vai trò Admin của repo (trưởng nhóm) được bypass — dùng khi dựng nhánh lần đầu.
# Chạy lại nhiều lần an toàn: ruleset cùng tên thì cập nhật, chưa có thì tạo.
# Cách chạy: bash scripts/bao-ve-nhanh.sh [owner/repo]
set -euo pipefail

REPO="${1:-$(gh repo view --json nameWithOwner -q .nameWithOwner)}"
TEN="bao-ve-main-baseline"

read -r -d '' BODY <<JSON || true
{
  "name": "$TEN",
  "target": "branch",
  "enforcement": "active",
  "conditions": { "ref_name": { "include": ["refs/heads/main", "refs/heads/baseline"], "exclude": [] } },
  "bypass_actors": [ { "actor_id": 5, "actor_type": "RepositoryRole", "bypass_mode": "always" } ],
  "rules": [
    { "type": "deletion" },
    { "type": "non_fast_forward" },
    { "type": "pull_request", "parameters": {
        "required_approving_review_count": 1,
        "dismiss_stale_reviews_on_push": true,
        "require_code_owner_review": false,
        "require_last_push_approval": false,
        "required_review_thread_resolution": false } },
    { "type": "required_status_checks", "parameters": {
        "strict_required_status_checks_policy": false,
        "required_status_checks": [
          { "context": "Backend (Maven verify — unit, Modulith, ArchUnit, Testcontainers)" },
          { "context": "Frontend (lint + test + build)" } ] } }
  ]
}
JSON

ID=$(gh api "repos/$REPO/rulesets" -q ".[] | select(.name == \"$TEN\") | .id" || true)
if [ -n "$ID" ]; then
  echo "$BODY" | gh api -X PUT "repos/$REPO/rulesets/$ID" --input - > /dev/null
  echo "Đã cập nhật ruleset $TEN (#$ID)"
else
  echo "$BODY" | gh api -X POST "repos/$REPO/rulesets" --input - > /dev/null
  echo "Đã tạo ruleset $TEN"
fi
echo "Các ruleset hiện có:"
gh api "repos/$REPO/rulesets" -q '.[] | "  #\(.id) \(.name) (\(.enforcement))"'
