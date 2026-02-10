# Tag Query API Design

## Overview

Separate controller for tag-based test queries. Returns tests matching tag criteria, grouped by hierarchy, showing latest run per job.

## Endpoints

Query at any hierarchy level via REST path:

```
GET /api/v1/company/{company}/tags/tests
GET /api/v1/company/{company}/org/{org}/tags/tests
GET /api/v1/company/{company}/org/{org}/repo/{repo}/tags/tests
GET /api/v1/company/{company}/org/{org}/repo/{repo}/branch/{branch}/tags/tests
GET /api/v1/company/{company}/org/{org}/repo/{repo}/branch/{branch}/sha/{sha}/tags/tests
```

Path = query scope. Response = tests within that scope.

## Query Syntax

Boolean expressions with AND, OR, and parentheses:

```
?tags=smoke                                    # single tag
?tags=smoke AND env=prod                       # AND
?tags=smoke OR regression                      # OR
?tags=(smoke OR regression) AND env=prod       # mixed with precedence
?tags=(smoke AND env=prod) OR (regression AND env=staging)
```

URL encoding handled by HTTP clients.

## Response Structure

Tests grouped by remaining hierarchy levels, latest run per job:

```json
{
  "query": {
    "scope": "company/org/repo",
    "tags": "(smoke OR regression) AND env=prod"
  },
  "results": {
    "branch-main": {
      "sha-abc123": {
        "job-nightly": {
          "runDate": "2025-02-09T10:30:00Z",
          "tests": [
            "create dss account",
            "delete dss account",
            "update dss permissions"
          ]
        }
      }
    },
    "branch-feature-x": {
      "sha-def456": {
        "job-pr-check": {
          "runDate": "2025-02-08T14:22:00Z",
          "tests": [
            "create dss account"
          ]
        }
      }
    }
  }
}
```

## Response Fields

- **runDate**: Timestamp of the test run
- **tests**: List of test names matching the tag query

## Query Parsing

1. Tokenize: identifiers, AND, OR, `(`, `)`
2. Parse to AST respecting precedence (AND binds tighter than OR)
3. Convert AST to SQL using MEMBER OF with UNION/INTERSECT

Example: `(smoke OR regression) AND env=prod`

```sql
SELECT * FROM test_result WHERE test_result_id IN (
    SELECT test_result_id FROM test_result WHERE 'smoke' MEMBER OF(tags)
    UNION
    SELECT test_result_id FROM test_result WHERE 'regression' MEMBER OF(tags)
)
AND 'env=prod' MEMBER OF(tags)
```

## Implementation Notes

- New controller: `TagQueryController`
- Reuse existing `StoragePath` for hierarchy context
- Query parser: simple recursive descent or library
- Latest run per job: `MAX(run_id)` grouped by job
