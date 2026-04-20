# SKILL: frontend-jwt-integration

## Purpose
Use this skill when editing the static frontend (`app.js`, `index.html`) of `CPT202-jwt` so that JWT-based authenticated flows work without breaking existing admin and content-management behavior.

## Scope
This skill is for minimal frontend changes in the current static UI, especially around:
- login/token persistence
- Bearer token injection
- auth-required requests
- draft flow UI
- admin module UI
- contributor application UI
- small interaction bug fixes

## Assumptions
- The project is not a SPA framework rewrite.
- Shared request helpers in `app.js` are the integration seam.
- The goal is not a visual redesign; it is behavior correctness with minimal disruption.

## Must-preserve flows
- public homepage/list/detail/category/ranking behavior
- draft creation / update / resubmission flow
- my posts list/detail/edit entry
- admin article management
- pending review queue
- admin user management
- contributor application areas

## Required inspection pattern
Before editing, inspect:
- token storage/read helpers
- auth header injection helpers
- login success handling
- logout/session-expiry handling
- every request path touched by the task
- render/update functions for the affected view

Then inspect whether the backend contract has changed:
- login response fields
- auth-required endpoint paths
- new DTO fields
- role names and status values

## Core rules
1. **All authenticated requests must use centralized Bearer injection.**
   - No per-feature token assembly unless unavoidable.

2. **Do not reintroduce legacy auth assumptions.**
   - No `X-User-Id`
   - No fake local user id as identity source

3. **Patch data flow first, UI second.**
   - Fix request/response handling and view state synchronization before polishing copy.

4. **Avoid cross-view state leakage.**
   - Article Management filters should not pollute Pending Review Queue state.
   - User Management state should stay isolated from article management state.

5. **Use backend truth as primary source.**
   - Local cache is fallback only, not the main data source, when a real endpoint exists.

## Common task patterns
### JWT migration fixes
- store token alongside current user data
- inject `Authorization: Bearer <token>` in shared helper
- handle missing/expired token coherently
- remove outdated error mapping that assumes custom header auth

### Draft/edit fixes
- reopen old drafts from real `/api/my/**` endpoints when available
- keep local draft cache only as fallback or unsaved-work protection
- preserve submit-review and rejected-post edit flow

### Admin UI fixes
- keep article management, pending queue, and user management state isolated
- preserve current search/filter/sort context after admin actions when appropriate
- do not let one admin sub-view silently drive another view's request params

### Contributor application UI
- respect role-based visibility
- reflect pending/approved/rejected states from backend truth
- avoid duplicate-submission affordances when backend already blocks them

## Response-field discipline
When backend returns new data:
- map only what the view needs
- keep field names explicit
- do not guess backend data shape if you can inspect it
- if a desired field is missing, report it instead of inventing hidden frontend state

## Testing / verification expectation
After edits, verify at least by code-path review and, if possible, manual click-path reasoning for:
- sign in
- authenticated request header usage
- draft save/update/submit
- admin list/detail/action refresh
- contributor application submit/review visibility

If browser automation is unavailable, say so clearly.

## Anti-patterns to avoid
- spreading token logic across many call sites
- storing auth state in mutually inconsistent formats
- rewriting large chunks of UI markup for a small integration issue
- reviving stale local caches as primary truth source
- fixing only labels when the underlying request path is wrong

## Good completion criteria
A frontend JWT integration task is complete when:
- authenticated requests consistently send Bearer token
- previous admin/content flows remain usable
- state isolation between admin subviews is preserved
- fallbacks are clearly secondary to backend truth
