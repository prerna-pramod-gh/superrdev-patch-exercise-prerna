# NOTES.md

## Summary of Changes

Fixed 10 bugs across backend and frontend:

#### Backend (5 fixes):
- Removed Thread.sleep() in TaskController that delayed every request up to 1 second
- Added validation for page/pageSize params (return 400 instead of crashing with 500)
- Wrapped TaskStatus.valueOf() in try/catch to handle invalid status input gracefully
- Fixed SQL AND/OR operator precedence in TaskRepository — archived and status filters were only applying to one search branch due to missing parentheses
- Added @PrePersist to Task entity to ensure createdAt is always populated

#### Frontend (5 fixes):
- Fixed pageSize key typo in api.js — both lines used 'page', causing the app to request the wrong page number
- Added race condition guard (isCurrent flag) in useTasks.js to prevent stale API responses from overwriting fresh data
- Added setLoading(false) in error handler — previously loading spinner stayed on forever after an error
- Added setError(null) at start of each fetch to clear previous errors
- Reset page to 1 in App.jsx when search query or status filter changes

## What I Chose Not to Change

- status as String vs TaskStatus enum — design improvement, not a bug
- Missing debounce on SearchBar — race condition fix already ensures correct results
- In-memory pagination — works for small datasets; Pageable requires coordinated changes
- Oracle PL/SQL artifact — skipped due to time constraints (reference only)

## Biggest Remaining Risk

In-memory pagination loads all matching tasks per search request. With 10k+ tasks, this causes high memory usage and slow responses. Moving pagination to the DB layer (Pageable + LIMIT/OFFSET) is the highest-value improvement remaining.

## Tools/AI Used

Used Claude (Anthropic) to identify bugs, explain root causes, and draft fixes. Reviewed each suggestion, understood the reasoning, and applied changes manually. Handwritten notes are in my own words to verify understanding.
