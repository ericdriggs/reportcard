#!/bin/bash
set -e

# Generate Mermaid ER diagram from MySQL schema using mermerd
# Requires: Docker, mermerd (https://github.com/KarnerTh/mermerd)

CONTAINER_NAME="reportcard-schema-gen"
MYSQL_PORT=3307
DDL_FILE="reportcard-server/src/main/resources/db/migration/V1.0__reportcard_mysql_ddl.sql"
OUTPUT_FILE="docs/schema/schema.mermaid"

echo "🚀 Starting MySQL container..."
docker run -d --name $CONTAINER_NAME \
    -e MYSQL_ROOT_PASSWORD=root \
    -e MYSQL_DATABASE=reportcard \
    -p $MYSQL_PORT:3306 \
    mysql:8.0.33

echo "⏳ Waiting for MySQL to be ready..."
sleep 30

echo "📝 Applying DDL..."
docker exec -i $CONTAINER_NAME mysql -uroot -proot reportcard < $DDL_FILE

echo "🎨 Generating Mermaid diagram..."
~/go/bin/mermerd -c "mysql://root:root@tcp(localhost:$MYSQL_PORT)/reportcard" \
    -s reportcard \
    --useAllTables \
    --showAllConstraints \
    --encloseWithMermaidBackticks \
    -o $OUTPUT_FILE

echo "🧹 Cleaning up container..."
docker rm -f $CONTAINER_NAME

echo "✅ Schema diagram generated at $OUTPUT_FILE"
