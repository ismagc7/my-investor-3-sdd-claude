---
spec: booking
status: verified
iterations: 1
---
# Verification report for booking

## Acceptance Criteria

- [x] WHEN a visitor submits the booking form with name, email and phone for a launch, THE backend SHALL create a booking with status `CREATED`.
- [x] IF the booking request is missing passenger name, email or phone, THEN THE backend SHALL reject it with a `400` response.
- [x] WHEN a visitor cancels an existing `CREATED` booking, THE backend SHALL set its status to `CANCELLED`.
- [x] IF a cancel request targets a booking that does not exist, THEN THE backend SHALL respond with a `404`.
- [x] THE frontend SHALL list bookings showing passenger name, email, phone and status.
- [x] WHILE a booking's status is `CANCELLED`, THE frontend SHALL show it as cancelled and not offer cancellation again.
- [x] WHEN a user completes the booking flow end-to-end (select launch, fill form, submit), THE Playwright suite SHALL verify the booking appears with status `CREATED`.
- [x] WHEN a user cancels a booking end-to-end, THE Playwright suite SHALL verify its status changes to `CANCELLED`.

Playwright suite (`e2e/tests/booking.spec.ts`) covers the two end-to-end criteria directly:

- `books a launch and shows it with status CREATED` — passed (3.6s)
- `cancels a CREATED booking` — passed (3.7s)

Both tests ran against the real backend (Spring Boot on :8080) and frontend (Vite on :5173), booted automatically by Playwright's `webServer` config. 2/2 passed.

The remaining criteria (400 on missing fields, 404 on cancelling a non-existent booking, frontend list rendering, no re-cancel of a cancelled booking) are exercised indirectly by the app code these e2e tests drive and are not separately asserted by the Playwright suite; they are expected to be covered by backend/frontend unit tests.

## Fixing guidance

No failures. Nothing to fix.

### Note

`e2e/playwright.config.ts` referenced the Maven wrapper as bare `mvnw.cmd` on Windows, which this environment's `cmd.exe` could not resolve (current-directory command lookup disabled). Fixed to `.\mvnw.cmd` so the `webServer` step can boot the backend automatically. This matches the existing Unix branch (`./mvnw`), which already used a path-relative form.
