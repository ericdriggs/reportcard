#!/bin/bash
set -e

echo "🚀 Starting full deployment to Maven Central..."

echo "📦 Step 1: Publishing to staging..."
./gradlew publishToSonatype -Si

echo "🔄 Step 2: Uploading to Central Portal..."
./gradlew uploadToPortal -Si

echo "✅ Step 3: Publishing to Maven Central..."
./gradlew publishLatestDeployment

echo "🎉 Deployment complete! Check https://central.sonatype.com/publishing/deployments for status."