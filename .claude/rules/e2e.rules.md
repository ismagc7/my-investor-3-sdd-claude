---
name: e2erules
description: Code rules for the e2e container of AstroBookings
paths: ["e2e/tests/**/*.ts", "e2e/pages/**/*.ts", "e2e/playwright.config.ts"]
---
# e2e code rules — AstroBookings

## Summary

`e2e` is a Playwright + TypeScript suite split into two flat folders — `pages/` (Page Objects) and `tests/` (specs) — one file per feature in each, mirroring `front`'s feature names (`health`, `bookings`). The one principle that matters most: **Page Objects expose locators and intent-revealing actions only, never assertions** — every `expect(...)` call lives in the spec file, so a Page Object can be read as a pure "what's on this view" contract.

## Naming

| Element | Convention | Example |
|---------|------------|---------|
| Folders | lowercase, layer-named | `e2e/pages/`, `e2e/tests/` |
| Page Object files/classes | PascalCase, `{Feature}Page` | `HealthPage.ts`, `BookingsPage.ts` |
| Spec files | lowercase feature name + `.spec.ts` | `health.spec.ts`, `booking.spec.ts` |
| Locator fields | camelCase, noun describing the element | `passengerNameInput`, `submitButton`, `addBookingButton` |
| Action/helper methods | camelCase, verb-first | `goto`, `bookLaunch`, `rowByPassenger`, `statusOf`, `cancelButtonOf` |
| Module-level constants | UPPER_SNAKE | `HEALTH_ROUTE`, `API_URL`, `BASE_URL`, `MVNW` |
| `data-testid` lookups | must match `front`'s kebab-case ids exactly, dynamic ids via `^=` prefix selector | `page.getByTestId('add-booking-btn')`, `` page.locator('[data-testid^="booking-row-"]') `` |

> Note: the spec is named `booking.spec.ts` (singular) while its Page Object is `BookingsPage` (plural, matching `front`'s `features/bookings`) — an existing inconsistency, not a pattern to extend; prefer matching the front feature name (plural) for new spec files.

## Artifact roles

| Role | Structural rule (one line) |
|------|----------------------------|
| Page Object (`pages/{Feature}Page.ts`) | A class taking `page: Page` in its constructor (stored as `private readonly`); `readonly Locator` fields built in the constructor via `page.getByTestId(...)`; a `goto(options?)` method navigating to `'/'`; optional composite action methods (`bookLaunch`) that chain fills/clicks for a full user action; optional locator-returning helpers for dynamic rows (`rowByPassenger`, `statusOf`, `cancelButtonOf`) — these return `Locator`, they never call `expect`. |
| Spec (`tests/{feature}.spec.ts`) | One `test.describe('{Feature name}', ...)` block per feature; each `test(...)` instantiates its Page Object fresh (`const x = new XPage(page)`), calls `.goto()`, drives the flow through Page Object methods, and asserts exclusively via `expect(...)` from `@playwright/test`; uses `page.route(...)` to mock backend responses for states hard to trigger live (loading delay, `DOWN`, network failure); uses `playwright.request.newContext({ baseURL: API_URL })` for direct REST seeding when a flow needs pre-existing data the UI itself doesn't create (see `booking.spec.ts`'s `seedLaunch`). |
| Config (`playwright.config.ts`) | Single `defineConfig({...})`; `BASE_URL`/`API_URL` resolved from `process.env.E2E_BASE_URL` / `E2E_API_URL` with `localhost` fallbacks; `webServer` is an array booting `back` (`mvnw(.cmd) spring-boot:run`) then `front` (`npm run dev`) each with its own readiness `url`; `reuseExistingServer: !process.env.CI` so local runs reuse already-running servers. |

## Canonical example

> `BookingsPage.ts` — the cleanest instance of the Page Object role: locators built once in the constructor, one composite action, and locator-returning helpers for dynamic rows.

```ts
import { type Page, type Locator } from '@playwright/test';

export class BookingsPage {
  readonly addBookingButton: Locator;
  readonly launchSelect: Locator;
  readonly passengerNameInput: Locator;

  constructor(private readonly page: Page) {
    this.addBookingButton = page.getByTestId('add-booking-btn');
    this.launchSelect = page.getByTestId('field-launch');
    this.passengerNameInput = page.getByTestId('field-passenger-name');
  }

  async goto(options?: Parameters<Page['goto']>[1]): Promise<void> {
    await this.page.goto('/', options);
  }

  rowByPassenger(passengerName: string): Locator {
    return this.page.locator('[data-testid^="booking-row-"]').filter({ hasText: passengerName });
  }
}
```

## Conventions

- **Wiring**: no fixtures/DI beyond Playwright's own `page`/`request` test fixtures; each spec instantiates the Page Object it needs directly (`new HealthPage(page)`, `new BookingsPage(page)`) — there is no shared fixture file extending `test`.
- **Test data isolation**: names/emails that must be unique across parallel runs (`fullyParallel: true`) are suffixed with `Date.now()` (+ `Math.random()` for the seeded rocket/launch names) rather than fixed strings — never hard-code a passenger name or rocket name that a parallel test could collide with.
- **Errors / edge states**: simulate backend failure or slow states with `page.route(pattern, handler)` (`route.continue()` after a delay, `route.fulfill({...})`, or `route.abort('failed')`) scoped to one `test()`, not globally; real flows (booking create/cancel) always hit the live `back` + `front` servers, never mocks.
- **Comments**: unlike `back`/`front` (deliberately comment-free), `e2e` allows short comments where the *reason for a wait or mock* isn't obvious from the code itself (see `HealthPage`'s file-level JSDoc, or the `waitUntil: 'commit'` comment in `health.spec.ts`) — keep them to one line explaining "why", not "what".
- **Avoid**: assertions inside a Page Object — keep every `expect(...)` in the spec so the Page Object stays a pure locator/action contract. CSS-class locators for primary elements — prefer `getByTestId`; `BookingsPage.statusOf`'s `.booking-status` class selector is a legacy gap against this rule, not a pattern to copy. Hard-coded `localhost` URLs inside a spec or Page Object — always go through the env-overridable constants (`E2E_BASE_URL`, `E2E_API_URL`) already resolved in `playwright.config.ts` / `booking.spec.ts`.
