# e2e architecture — AstroBookings

> Container `e2e` from [`arch.md`](../arch.md). Tier: `e2e`.

## Overview

`e2e` is the Playwright end-to-end suite for AstroBookings: it boots the real `back` (Spring Boot) and `front` (Vite dev server) processes and drives the SPA through a real Chromium browser to verify full-stack user flows (health readiness, booking lifecycle). It follows the Page Object Model — one Page Object per SPA view/feature, one spec file per feature — and seeds test data by calling the backend REST API directly when a flow needs a pre-existing entity (e.g. a launch to book).

- **Folder**: `e2e/`
- **Archetype**: TypeScript — Playwright (`@playwright/test`), no bundler/build step (`tsc --noEmit` only, used as `lint`)
- **Talks to**: `front` (drives the UI in a real browser at `E2E_BASE_URL`, default `http://localhost:5173`); `back` (waits on `GET /api/health` for readiness, and calls `POST /api/rockets` / `POST /api/launches` directly via `playwright.request` to seed data at `E2E_API_URL`, default `http://localhost:8080`)

---



### Code organization

**Pattern**: Layer-based (`pages/` vs `tests/`), one file per feature within each layer — mirrors `front`'s `features/health` and `features/bookings`.

```text
e2e/
├── playwright.config.ts   # webServer[] boots back (mvnw spring-boot:run) + front (npm run dev); baseURL/API URL from env
├── tsconfig.json          # strict TS, noEmit; includes tests/, pages/, playwright.config.ts
├── pages/
│   ├── HealthPage.ts      # Page Object — health vitals locators (getByTestId only)
│   └── BookingsPage.ts    # Page Object — booking form/list locators + bookLaunch() composite action
└── tests/
    ├── health.spec.ts     # health view: happy path, loading, DOWN, unreachable (route mocking)
    └── booking.spec.ts    # booking lifecycle: seeds launch via REST, books + cancels through the UI
```

### Key contracts

| Contract | Shape | Direction |
|----------|-------|-----------|
| `GET /api/health` | Playwright `webServer.url` readiness probe (config) | consumes |
| `POST /api/rockets`, `POST /api/launches` | JSON, called via `playwright.request.newContext({ baseURL: E2E_API_URL })` to seed a launch before the booking specs run | consumes |
| `data-testid` locators (`health-status`, `health-database`, `health-uptime`, `health-timestamp`, `health-loading`, `health-error`, `add-booking-btn`, `field-launch`, `field-passenger-name`, `field-passenger-email`, `field-passenger-phone`, `submit-btn`, `booking-row-{id}`, `cancel-btn-{id}`) | Rendered by `front`; located via `page.getByTestId(...)` | consumes |
| `page.route('**/api/health', ...)` | Playwright network mock, used only in `health.spec.ts` to force loading/DOWN/unreachable states | consumes (mocks) |

---

## Data Schemas

Not a database or API container — no owned schema. It consumes `back`'s REST contracts (see `back.arch.md` for `RocketRequest`/`LaunchRequest`/`BookingResponse` shapes) purely for test-data seeding, and reads `front`'s `data-testid` DOM contract (see `front.arch.md`) to locate elements.

> last updated: 2026-07-02
