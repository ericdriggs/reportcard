# Pipeline Dashboard Implementation

## Overview
The Pipeline Dashboard provides aggregated metrics for CI/CD pipelines across multiple organizational levels with time-based intervals for trend analysis.

## Architecture

### Endpoints
The pipeline dashboard follows the metrics pattern with three aggregation levels:

#### JSON API Endpoints
- `GET /v1/api/pipeline/all` - All pipelines across all companies
- `GET /v1/api/pipeline/company/{company}` - Pipelines for specific company
- `GET /v1/api/pipeline/company/{company}/org/{org}` - Pipelines for specific org

#### UI Endpoints  
- `GET /ui/pipeline/all` - HTML view of all pipelines
- `GET /ui/pipeline/company/{company}` - HTML view for company pipelines
- `GET /ui/pipeline/company/{company}/org/{org}` - HTML view for org pipelines

### Query Parameters
All endpoints support:
- `intervalDays` (default: 7) - Days per interval
- `intervalCount` (default: 12) - Number of intervals
- `jobInfos` - Filter by job info (e.g., "pipeline:build_acceptance")
- `companies`, `orgs`, `repos`, `branches` - Include filters
- `notCompanies`, `notOrgs`, `notRepos`, `notBranches` - Exclude filters
- `shouldIncludeDefaultBranches` (default: false) - Include default branches

## Data Models

### Request Models
- `PipelineIntervalRequest` - Interval-based request with time ranges
- `PipelineRequest` - Single time range request
- `PipelineDashboardRequest` - Simple dashboard request (legacy)

### Response Models
- `PipelineIntervalResultCount` - Aggregated metrics across time intervals
- `PipelineResultCount` - Metrics for single time period
- `PipelineDashboardMetrics` - Simple dashboard metrics (legacy)

### Core Metrics
Each pipeline job tracks:
- **Days Since Passing Run** - Days since last successful run
- **Job Pass Percentage** - Percentage of successful runs
- **Test Pass Percentage** - Percentage of tests that passed

## Implementation Details

### GraphService Methods
- `getPipelineAll()` - Aggregates across all companies
- `getPipelineCompany()` - Filters to specific company
- `getPipelineOrg()` - Filters to specific company/org
- `getPipelineCompanyGraphs()` - Core graph query with filtering

### Filtering Strategy
Pipeline filtering uses the `job_info_str` field which contains colon-separated key-value pairs:
```
pipeline:build_acceptance
environment:staging
```

### Database Query Pattern
Uses the existing `getCompanyGraphs()` infrastructure with `TableConditionMap` for efficient single-query, multiple-aggregation approach.

## Key Features

### Domain Agnostic
- No hardcoded pipeline names
- Supports any pipeline type through job_info filtering
- Flexible filtering at multiple organizational levels

### Time-Based Intervals
- Configurable interval duration and count
- Trend analysis across time periods
- Historical data aggregation

### Multiple Aggregation Levels
- Company-level aggregation
- Organization-level aggregation  
- Repository and branch-level detail
- Job-level granular metrics

### Efficient Data Access
- Single database query serves multiple aggregation levels
- Leverages existing graph structure for performance
- Reuses established codebase patterns

## Implementation Status

✅ **COMPLETED**
- JSON API endpoints fully implemented
- UI HTML endpoints fully implemented  
- GraphService methods implemented
- HTML rendering with PipelineDashboardHtmlHelper
- Integration tests created
- Documentation completed

## Usage Examples

### Get Build Acceptance Pipeline Metrics (JSON)
```
GET /v1/api/pipeline/company/mycompany?jobInfos=pipeline:build_acceptance&intervalDays=7&intervalCount=4
```

### View All Pipelines HTML Dashboard
```
GET /ui/pipeline/all?intervalDays=14&intervalCount=8
```

### View Company Pipeline Dashboard
```
GET /ui/pipeline/company/mycompany?jobInfos=pipeline:build_acceptance&intervalDays=7&intervalCount=12
```

### Filter by Multiple Criteria
```
GET /v1/api/pipeline/company/mycompany/org/myorg?jobInfos=pipeline:build_acceptance&branches=main,develop&intervalDays=1&intervalCount=30
```

## Testing
- Unit tests verify method signatures and compilation
- Integration tests validate endpoint functionality
- Follows existing test patterns in codebase

## Future Enhancements
- Additional metrics (build duration, failure patterns)
- Custom time range selection
- Pipeline comparison views
- Alert thresholds and notifications