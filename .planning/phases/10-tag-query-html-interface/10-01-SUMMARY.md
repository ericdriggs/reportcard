---
phase: 10-tag-query-html-interface
plan: 01
subsystem: UI
tags: [html, ui, tags, search, web, integration]
requires: [09-tag-query-api]
provides: [tag-search-ui, tag-search-forms, tag-results-html, browse-integration]
affects: []
tech-stack:
  added: []
  patterns: [html-helper-pattern, form-based-search, xss-protection]
key-files:
  created:
    - reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/html/TagQueryHtmlHelper.java
    - reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/TagQueryUIController.java
    - reportcard-server/src/test/java/io/github/ericdriggs/reportcard/controller/TagQueryUIControllerTest.java
  modified:
    - reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/browse/BrowseHtmlHelper.java
decisions:
  - key: html-escaping-library
    choice: Spring HtmlUtils
    why: Already available in Spring Web dependency
    alternatives: [Apache Commons Text StringEscapeUtils]
  - key: form-method
    choice: GET with query parameter
    why: Shareable URLs, browser back button support
    alternatives: [POST with body]
  - key: error-handling-ui
    choice: Render error with pre-populated form
    why: User-friendly retry without losing context
    alternatives: [Redirect to error page, Show alert]
metrics:
  duration: "6.9 min"
  completed: "2026-02-17"
---

# Phase 10 Plan 01: Tag Query HTML Interface Summary

HTML interface for tag-based test queries with search forms and hierarchical result rendering.

## What Was Built

Created complete HTML UI for tag query functionality:

**TagQueryHtmlHelper** - HTML rendering utility extending BrowseHtmlHelper
- `renderTagQueryPage()` - Main entry point delegating to form or results
- `getSearchForm()` - Search form with tag expression input and syntax help
- `getResultsHtml()` - Hierarchical results rendering (branch→sha→job→tests)
- `renderErrorPage()` - Error display with pre-populated form for retry
- XSS protection via `HtmlUtils.htmlEscape()` on all user input

**TagQueryUIController** - HTML endpoints at all hierarchy levels
- Five endpoints: company, org, repo, branch, SHA scopes
- Shows form when tags parameter is missing/blank
- Executes query and renders results when tags provided
- Explicit ParseException handling returns 400 with error HTML
- No `/api/v1` prefix (HTML endpoints at root paths)

**Browse Integration** - Bidirectional navigation
- Added "Tag Search" links to company and org navigation fieldsets
- Links route to tag query UI at appropriate scope
- Results link back to browse pages via branch names

**Test Coverage** - Comprehensive unit tests
- Form display verification
- Results rendering with nested fieldsets
- Error handling with pre-populated forms
- All 5 hierarchy levels tested
- XSS protection verification
- Multiple branches and SHAs rendering

## Decisions Made

**1. HTML Escaping Library: Spring HtmlUtils**
- Already in Spring Web dependency
- Considered Apache Commons Text but would add dependency
- `HtmlUtils.htmlEscape()` provides XSS protection

**2. Form Method: GET with query parameters**
- Enables shareable URLs
- Browser back button works correctly
- Query visible in URL for debugging
- Alternative: POST would hide query but lose shareability

**3. Error Handling: Render error with form**
- User-friendly retry experience
- Original query pre-populated in form
- Error message displayed prominently
- Alternative: Redirect would lose user context

## Deviations from Plan

None - plan executed exactly as written.

## Test Results

All unit tests pass (12 tests):
- Form rendering verified
- Results structure validated
- Error handling confirmed
- All hierarchy levels tested
- XSS protection working
- Complex multi-branch scenarios supported

```
./gradlew :reportcard-server:test --tests TagQueryUIControllerTest
BUILD SUCCESSFUL
```

## Technical Patterns

**HTML Helper Extension**
- Extends BrowseHtmlHelper for consistent page structure
- Reuses utility methods: getPage(), getUrl(), getBreadCrumb(), getLink()
- Maintains existing navigation and styling patterns

**Form-Based Search**
- GET method with query parameters
- Input field with placeholder examples
- Inline syntax help documentation
- Submit button triggers search

**Hierarchical Result Rendering**
- Nested fieldsets: branch-fieldset > sha-fieldset > job-result
- Branch names link to browse pages
- SHA displayed as label (no SHA-level browse pages)
- Job info with run date and test lists
- HTML-escaped test names for security

**XSS Protection**
- All user input HTML-escaped before rendering
- Tag expressions, error messages, test names protected
- Test coverage verifies escaping works

## Next Phase Readiness

✅ Tag query feature complete
- API endpoints functional (Phase 09)
- HTML interface accessible (Phase 10)
- Browse integration bidirectional
- User can discover and use tag search

**No blockers for future work.**

## Implementation Notes

**File Locations**
```
reportcard-server/src/main/java/io/github/ericdriggs/reportcard/
├── controller/
│   ├── html/
│   │   └── TagQueryHtmlHelper.java          (209 lines)
│   ├── TagQueryUIController.java            (166 lines)
│   └── browse/
│       └── BrowseHtmlHelper.java            (modified: +6 lines)
└── test/
    └── controller/
        └── TagQueryUIControllerTest.java     (246 lines)
```

**Commits**
- 7fa8f05: TagQueryHtmlHelper with form and results rendering
- 3ff96ef: TagQueryUIController with HTML endpoints
- 5376997: Tag Search links in browse navigation
- 2749ee4: Unit tests for TagQueryUIController

**Key Dependencies**
- Spring Web: HtmlUtils for HTML escaping
- TagQueryService: Backend query execution
- TagQueryResponse: Data model for results
- CompanyOrgRepoBranchJobRunStageDTO: Hierarchy path handling

## Metrics

**Effort**: 6.9 minutes
**Lines Added**: 621
**Lines Modified**: 6
**Test Coverage**: 12 unit tests, all passing
**Commit Count**: 4 atomic commits
