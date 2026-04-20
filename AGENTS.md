# AGENTS.md

## Project
在线文化资源平台（CPT202-jwt）

## Current repository baseline
- Repository root: `CPT202-jwt`
- Main working branch for JWT baseline: `feature/jwt-auth`
- Current auth baseline: **JWT + Spring Security + Bearer Token**
- Legacy `X-User-Id` flow is obsolete in this repo and must not be reintroduced.

## Product focus
This repo is a cultural heritage content platform with three role layers:
- `USER`
- `CONTRIBUTOR`
- `ADMIN`

The project already has a working admin-oriented feature foundation. Ongoing work should continue to prioritize **administrator-related modules** while minimizing impact on public pages and existing content flows.

## Existing implemented feature baseline
Treat the following as already implemented and worth preserving unless a task explicitly changes them:

### Public side
- Public post list and detail
- Public comment submission
- Category browsing
- Static frontend entry pages

### Authenticated user / contributor side
- Register / login
- Draft creation
- Draft update
- Rejected post update
- Submit for review
- My post list
- My post detail
- Contributor application submission and personal application history

### Admin side
- Article management
- Pending Review Queue
- Post review actions: approve / reject / archive / restore
- Admin post search by title
- Admin post status filter
- Admin post sorting and result feedback
- User management with real backend data
- User role change between `USER` and `CONTRIBUTOR`
- Contributor application review flow

### Backend safety rails already present
- Post status machine:
  - `DRAFT`
  - `PENDING_REVIEW`
  - `PUBLISHED`
  - `ARCHIVED`
  - `REJECTED`
- Public visibility limited to `PUBLISHED`
- Admin endpoints scoped under `/api/admin/**`
- My endpoints scoped under `/api/my/**`
- Existing integration tests already cover a large part of the article review flow and admin/business rules

## Current development objective
Work on top of the JWT branch without breaking the above feature baseline.

The near-term development priority is:
1. **JWT regression audit and hardening**
2. **Restore or fix any previously working admin/user flows broken by JWT migration**
3. **Continue admin-centric PBI development**

## Near-term PBI roadmap to keep in mind
When planning changes, assume later work may include:
- contributor downgrade back to `USER`
- stricter JWT error handling / token expiry UX
- richer admin user management constraints and status control
- admin post pagination / page size control
- pending queue usability improvements
- contributor application reason / review feedback / richer result display
- safer remote/local profile behavior
- optional PDF / attachment-related admin review improvements

Do not block later PBIs by making auth, DTO, or controller designs overly rigid.

## Non-goals unless explicitly requested
Do not expand scope into these areas without direct instruction:
- refresh token system redesign
- OAuth / SSO
- full RBAC rewrite
- frontend framework migration
- full repository refactor
- replacing existing business services wholesale
- introducing Flyway/Liquibase just because of one schema fix

## Core working rules
1. **Prefer smallest safe change.**
   - Patch the integration seam before rewriting business logic.
   - Reuse existing DTOs/services/controllers where possible.

2. **Preserve route stability.**
   - Do not rename or move existing public/admin/my routes without explicit instruction.

3. **Preserve response style.**
   - Keep `ApiResponse` envelope style unless a task explicitly changes API format.

4. **Do not regress the post review state machine.**
   - Any new work must respect the current state transitions.

5. **Auth changes must stay centralized.**
   - JWT parsing, current-user resolution, and security boundaries should stay in auth/security entry points.
   - Do not spread token parsing into business services.

6. **Frontend auth must stay centralized.**
   - Token storage and Bearer header injection should go through shared request helpers in `app.js`.
   - Do not hardcode tokens into individual feature requests.

7. **Admin work should not leak into public flows.**
   - Preserve public homepage/list/detail/ranking behavior unless the task explicitly targets them.

8. **Tests are part of the task.**
   - For backend behavior changes, update or add integration tests when possible.
   - Especially protect JWT auth boundaries and article review flow.

## Required pre-change checks for Codex
Before making non-trivial changes, inspect at least:
- `pom.xml`
- `SecurityConfig.java`
- JWT-related filter/service/util classes
- `AuthController.java`, `AuthService.java`, `AuthResponse.java`
- relevant controller/service/repository classes for the target feature
- `src/main/resources/static/app.js`
- relevant tests, especially `PostReviewFlowApiTests.java`

## Required post-change checks
After code changes, prefer running:
- `./mvnw test`
- targeted grep/search for accidental `X-User-Id` regressions
- targeted review of `Authorization: Bearer` usage in frontend and tests

If full runtime verification is not possible, explicitly report that limitation.

## File and scope boundaries
### Allowed to change when task needs it
- `src/main/java/com/heritage/platform/config/**`
- `src/main/java/com/heritage/platform/filter/**`
- `src/main/java/com/heritage/platform/controller/**`
- `src/main/java/com/heritage/platform/service/**`
- `src/main/java/com/heritage/platform/repository/**`
- `src/main/java/com/heritage/platform/dto/**`
- `src/main/java/com/heritage/platform/entity/**`
- `src/main/resources/static/app.js`
- `src/main/resources/static/index.html`
- `src/main/resources/application*.properties`
- `src/test/java/com/heritage/platform/**`

### Avoid touching unless task explicitly requires it
- large static page sections unrelated to auth/admin work
- non-target uploads / file storage behavior
- unrelated seed data
- remote deployment config

## Repo hygiene
Treat these as local/dev noise unless the task explicitly involves them:
- `target/`
- `.codex/`
- `.m2/`
- `.vscode/`
- `data/`
- runtime logs

Do not stage or commit them in feature branches unless explicitly intended.

## Output expectation for Codex tasks
For substantial tasks, return:
1. changed files
2. what behavior changed
3. what behavior was intentionally preserved
4. test/compile result
5. known non-blocking limitations

## Default strategy by task type
- JWT/auth problem -> inspect security chain first, then frontend request helper, then tests
- Admin feature PBI -> preserve article review and contributor application flows
- Frontend bug -> patch data flow first, avoid broad UI rewrites
- Backend bug -> prove whether it is auth-layer, service-layer, repository-layer, or old local DB drift

## If repository state looks inconsistent
If README/documentation claims JWT but code does not, trust code over docs.
If multiple worktrees/repos exist, first confirm workspace identity before continuing.
