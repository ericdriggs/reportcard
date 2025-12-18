# Schema Diagram Generation Setup

## Purpose

Automate generation of Mermaid ER diagram from MySQL DDL files.

## Prerequisites

1. **Docker** - For running MySQL container
2. **mermerd** - Go tool for extracting Mermaid diagrams from databases

## Installing mermerd

### macOS (Homebrew)
```bash
brew install mermerd
```

### Linux/macOS (Go install)
```bash
go install github.com/KarnerTh/mermerd@latest
```

### Manual Download
Download from: https://github.com/KarnerTh/mermerd/releases

## Usage

```bash
./generate-schema-diagram.sh
```

This will:
1. Start MySQL 8.0.33 container
2. Apply DDL from `reportcard-server/src/main/resources/db/migration/V1.0__reportcard_mysql_ddl.sql`
3. Extract schema to Mermaid format
4. Save to `docs/schema/schema.mermaid`
5. Clean up container

## Output

The generated `docs/schema/schema.mermaid` file contains:
- All tables with columns and types
- Primary keys (PK)
- Foreign key relationships
- Unique constraints

## Viewing the Diagram

- **GitHub/GitLab**: Renders automatically in markdown
- **VS Code**: Install Mermaid extension
- **IntelliJ**: Built-in support
- **Online**: https://mermaid.live/

## When to Regenerate

Run the script whenever you modify:
- `reportcard-server/src/main/resources/db/migration/V1.0__reportcard_mysql_ddl.sql`

## Troubleshooting

**Port 3307 already in use:**
```bash
# Change MYSQL_PORT in generate-schema-diagram.sh
```

**Container already exists:**
```bash
docker rm -f reportcard-schema-gen
```

**MySQL not ready after 30s:**
```bash
# Increase sleep time in generate-schema-diagram.sh
```
