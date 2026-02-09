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
#   ./publish.sh              # Build and publish to Maven Central
#   ./publish.sh cleanup      # Drop all stale staging repos first
#
# Notes:
#   • Must run from the same IP / CI runner that performed the upload.
#   • HTTP 200 means success — check Portal → Deployments for the repo.
# -----------------------------------------------------------------------------

set -euo pipefail

NAMESPACE="io.github.ericdriggs"  # TODO: update if your namespace changes
BASE="https://ossrh-staging-api.central.sonatype.com"
AUTH=$(printf '%s:%s' "$OSSRH_USER" "$OSSRH_PASSWORD" | base64)

# --- Cleanup mode: drop all staging repos ---
if [[ "${1:-}" == "cleanup" || "${1:-}" == "clean" ]]; then
  echo "🧹 Cleanup mode: dropping all staging repositories..."
  echo ""

  SEARCH_URL="$BASE/manual/search/repositories?ip=any&profile_id=$NAMESPACE"
  SEARCH_RESPONSE=$(curl -fsS -H "Authorization: Bearer $AUTH" "$SEARCH_URL")

  REPO_KEYS=$(echo "$SEARCH_RESPONSE" | jq -r '.repositories[].key' 2>/dev/null || true)

  if [[ -z "$REPO_KEYS" ]]; then
    echo "✅ No staging repositories found to clean up"
    exit 0
  fi

  echo "Found repositories to drop:"
  echo "$REPO_KEYS" | while read -r key; do
    echo "   - $key"
  done
  echo ""

  echo "$REPO_KEYS" | while read -r key; do
    if [[ -n "$key" ]]; then
      ENCODED_KEY=$(printf '%s' "$key" | jq -sRr @uri)
      echo "🗑️  Dropping: $key"
      curl -w " (HTTP %{http_code})\n" -X DELETE \
        -H "Authorization: Bearer $AUTH" \
        "$BASE/manual/drop/repository/${ENCODED_KEY}" || true
    fi
  done

  echo ""
  echo "✅ Cleanup complete"
  exit 0
fi

echo "📦 Publishing artifacts to staging..."
# Capture Gradle output to extract the staging repo ID it creates
# Use tee to show output in real-time if tty available, otherwise just capture
if [[ -t 1 ]]; then
  GRADLE_OUTPUT=$(./gradlew publishToSonatype 2>&1 | tee /dev/tty)
else
  echo "(Running Gradle - output will be shown after completion)"
  GRADLE_OUTPUT=$(./gradlew publishToSonatype 2>&1)
  echo "$GRADLE_OUTPUT"
fi

# Extract the repo ID that Gradle actually created
GRADLE_REPO_ID=$(echo "$GRADLE_OUTPUT" | grep -o "io\.github\.ericdriggs--[a-f0-9-]*" | head -1 || true)
echo ""
echo "📋 Gradle created staging repo: ${GRADLE_REPO_ID:-<not found>}"

echo ""
echo "🔍 Searching for staging repos for $NAMESPACE ..."
SEARCH_URL="$BASE/manual/search/repositories?ip=any&profile_id=$NAMESPACE"
echo "   URL: $SEARCH_URL"

SEARCH_RESPONSE=$(curl -fsS -H "Authorization: Bearer $AUTH" "$SEARCH_URL")
echo "   Raw response:"
echo "$SEARCH_RESPONSE" | jq . 2>/dev/null || echo "$SEARCH_RESPONSE"

# Show all available repos
REPO_COUNT=$(echo "$SEARCH_RESPONSE" | jq -r '.repositories | length')
echo ""
echo "   Found $REPO_COUNT repository/repositories:"
echo "$SEARCH_RESPONSE" | jq -r '.repositories[] | "     - \(.key) (created: \(.created // "unknown"))"' 2>/dev/null || true

# Find the repository key that matches the Gradle-created repo ID
if [[ -n "${GRADLE_REPO_ID:-}" ]]; then
  echo ""
  echo "🔎 Looking for staging repo matching Gradle ID: $GRADLE_REPO_ID"

  # Search for the key that contains our Gradle repo ID
  REPO_KEY_RAW=$(echo "$SEARCH_RESPONSE" | jq -r --arg id "$GRADLE_REPO_ID" \
    '.repositories[] | select(.key | contains($id)) | .key' | head -1)

  if [[ -n "$REPO_KEY_RAW" && "$REPO_KEY_RAW" != "null" ]]; then
    echo "✅ Found matching repo key: $REPO_KEY_RAW"
  else
    echo "⚠️  No staging repo found matching Gradle ID"
    echo "   Available repos:"
    echo "$SEARCH_RESPONSE" | jq -r '.repositories[].key' 2>/dev/null || true
    echo ""
    echo "   This might mean artifacts weren't uploaded. Check Gradle output above."
    REPO_KEY_RAW=""
  fi
else
  echo ""
  echo "⚠️  Could not extract Gradle repo ID from build output"
fi

# Fallback to most recent repo if no match found
if [[ -z "$REPO_KEY_RAW" || "$REPO_KEY_RAW" == "null" ]]; then
  REPO_KEY_RAW=$(echo "$SEARCH_RESPONSE" | jq -r '.repositories[-1].key')
  echo "⚠️  Falling back to most recent repo from search: $REPO_KEY_RAW"
fi

if [[ -z "$REPO_KEY_RAW" || "$REPO_KEY_RAW" == "null" ]]; then
  echo "❌ No repositories found for $NAMESPACE"
  exit 1
fi

REPO_KEY=$(printf '%s' "$REPO_KEY_RAW" | jq -sRr @uri)
echo ""
echo "➡️  Uploading repository: $REPO_KEY_RAW"

# Perform the upload and display full response + status code.
UPLOAD_URL="$BASE/manual/upload/repository/${REPO_KEY}?publishing_type=automatic"
echo "   URL: $UPLOAD_URL"
echo ""

curl -w "\nHTTP %{http_code}\n" -X POST -H "Authorization: Bearer $AUTH" "$UPLOAD_URL" \
  | tee /dev/tty \
  | jq . 2>/dev/null || true

echo
echo "Done. Verify in Publisher Portal → Deployments:"
echo "   https://central.sonatype.com/publishing/deployments"
