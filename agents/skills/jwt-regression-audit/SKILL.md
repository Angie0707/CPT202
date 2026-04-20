# SKILL: jwt-regression-audit

## Purpose
Use this skill when working on the `CPT202-jwt` repository and the task is about:
- verifying whether JWT migration is real and correctly wired
- auditing whether existing business flows still work after JWT migration
- identifying the smallest fix order before coding
- checking if a bug is caused by auth, route protection, frontend token handling, or stale tests

This skill is primarily **read-first**. Do not rush into code edits.

## Use this skill when
- a branch claims to have implemented JWT
- login works but authenticated features fail
- admin pages or draft/comment flows break after auth migration
- tests still pass but runtime behavior looks wrong
- frontend says “sign in again” or gets 401/403 unexpectedly
- previous feature flows need regression verification on JWT baseline

## Must-inspect files first
At minimum inspect:
- `pom.xml`
- `src/main/java/com/heritage/platform/config/SecurityConfig.java`
- JWT-related files under `config/`, `filter/`, `service/`, `util/`, or equivalent auth packages
- `src/main/java/com/heritage/platform/controller/AuthController.java`
- `src/main/java/com/heritage/platform/service/AuthService.java`
- `src/main/java/com/heritage/platform/dto/response/AuthResponse.java`
- `src/main/resources/static/app.js`
- `src/test/java/com/heritage/platform/PostReviewFlowApiTests.java`

Then inspect the specific controller/service pair for the target feature.

## Expected audit outputs
When using this skill, structure the response like this:
- Summary
- Evidence found
- Regression matrix
- Broken or suspicious flows
- Required fixes
- Suggested implementation order

## Regression matrix to cover
Audit these flows unless the task scope is smaller:
1. public post list/detail
2. public comments with auth-protected submission
3. create draft / update draft / resubmit rejected post / submit review
4. my post list / my post detail
5. admin article management
6. pending review queue
7. admin user management
8. contributor application (user + admin)

For each one, classify as:
- normal
- high risk
- broken
- suspicious / needs runtime verification

## JWT-specific checks
### Backend
- Is Spring Security actually enabled?
- Is there a `SecurityFilterChain`?
- Is the JWT filter on the request chain?
- Is `Authorization: Bearer <token>` parsed centrally?
- Is current user resolved from `SecurityContext`, not from ad hoc request parsing scattered across services?
- Are `/api/admin/**` and `/api/my/**` protected consistently?
- Are public endpoints still public?

### Login contract
- Does login response include token data?
- Is token type explicit or implied?
- Does backend return enough information for frontend storage and role-aware UI?

### Frontend
- Is token persisted centrally?
- Does the shared request helper inject `Authorization: Bearer ...`?
- Are all auth-required requests using the shared helper?
- Are logout / expired token cases handled coherently?
- Has legacy `X-User-Id` been fully removed?

### Tests
- Are tests still injecting identity through legacy headers?
- Are there JWT-aware helpers for integration tests?
- Do existing business rule tests still protect previous admin/article/contributor behavior?

## Root-cause decision guide
If a previously working flow breaks after JWT migration, classify the likely failure layer:
- `401 everywhere` -> login contract, token storage, or filter wiring
- `403 only on admin` -> role claim mapping or admin boundary config
- public pages still work but authenticated actions fail -> frontend request helper or protected route mismatch
- tests pass but browser fails -> tests bypass real auth path
- browser works but tests fail -> test auth helper or test security config drift

## Implementation guidance if fixes are requested later
When this audit leads into a coding task, prefer this order:
1. security chain and auth contract correctness
2. centralized current-user resolution
3. frontend Bearer injection and token persistence
4. test migration to JWT
5. feature-specific regressions

Do **not** start by rewriting business services unless evidence shows business logic is the real issue.

## Anti-patterns to avoid
- adding token parsing logic into multiple controllers/services
- reintroducing `X-User-Id` as a quick compatibility hack
- disabling security broadly just to make tests pass
- patching UI strings without fixing real auth flow
- rewriting article review flow when the actual bug is in JWT wiring

## Good completion criteria
A JWT regression audit is complete when:
- the actual auth architecture is clearly identified
- old-vs-new auth confusion is removed
- affected feature flows are classified
- the next coding task can be scoped narrowly and safely
