#!/usr/bin/env bash
# upload-latest-repo.sh
# -----------------------------------------------------------------------------
# Automatically find the most recent staging repository for your namespace in
# Sonatype Central (OSSRH Staging API), upload it to the Publisher Portal, and
# print both the response body and HTTP status.
#
# Requires:
#   - jq (for JSON parsing and URI encoding)
#   - environment variables OSSRH_USER and OSSRH_PASSWORD (Portal token pair)
#
# Usage:
#   export OSSRH_USER="portal-token-username"
#   export OSSRH_PASSWORD="portal-token-password"
#   ./upload-latest-repo.sh
#
# Notes:
#   • Must run from the same IP / CI runner that performed the upload.
#   • HTTP 200 means success — check Portal → Deployments for the repo.
# -----------------------------------------------------------------------------

set -euo pipefail

NAMESPACE="io.github.ericdriggs"  # TODO: update if your namespace changes
BASE="https://ossrh-staging-api.central.sonatype.com"
AUTH=$(printf '%s:%s' "$OSSRH_USER" "$OSSRH_PASSWORD" | base64)

echo "🔍 Searching for staging repo for $NAMESPACE ..."
REPO_KEY_RAW=$(curl -fsS -H "Authorization: Bearer $AUTH" \
  "$BASE/manual/search/repositories?ip=any&profile_id=$NAMESPACE" \
  | jq -r '.repositories[0].key')

if [[ -z "$REPO_KEY_RAW" || "$REPO_KEY_RAW" == "null" ]]; then
  echo "No repositories found for $NAMESPACE"
  exit 1
fi

REPO_KEY=$(printf '%s' "$REPO_KEY_RAW" | jq -sRr @uri)
echo "➡️  Uploading repository key: $REPO_KEY_RAW"

# Perform the upload and display full response + status code.
# jq prettifies JSON if possible, ignores plain-text responses.
curl -w "\nHTTP %{http_code}\n" -X POST -H "Authorization: Bearer $AUTH" \
  "$BASE/manual/upload/repository/${REPO_KEY}" \
  | tee /dev/tty \
  | jq . 2>/dev/null || true

echo
echo "Done. Verify in Publisher Portal → Deployments:"
echo "   https://central.sonatype.com/publishing/deployments"
