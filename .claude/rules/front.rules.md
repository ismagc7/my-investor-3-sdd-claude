---
name: frontrules
description: Code rules for the front container of AstroBookings
paths: ["front/src/**/*.ts", "front/src/**/*.tsx"]
---
# front code rules — AstroBookings

## Summary

`front` is a React 19 + Vite SPA laid out as one feature folder per domain concept (`health`, `rockets`, `launches`, `bookings`), each repeating the same three-file shape: a Feature Component, a data-fetching `use{Feature}` Hook, and a `{feature}Api` client. The one principle that matters most: **each layer does exactly one job and nothing crosses it** — API clients only translate a REST call into a typed Promise, hooks only manage `data`/`error`/`isLoading`/`refresh` state around one API call, and components own all form/UI state and are the only place that touches `useState` beyond the hook's own state.

## Naming

| Element | Convention | Example |
|---------|------------|---------|
| Feature folders | lowercase plural noun | `front/src/features/bookings/` |
| Component files | PascalCase, matches exported component | `RocketList.tsx`, `HealthStatus.tsx` |
| Hook files | `use` + PascalCase feature, camelCase file | `useRockets.ts`, `useHealth.ts` |
| API client files | camelCase feature + `Api` | `rocketsApi.ts`, `bookingsApi.ts` |
| Colocated styles/tests | same basename as the component | `RocketList.css`, `RocketList.test.tsx` |
| Types / unions | PascalCase | `Rocket`, `RocketStatus`, `BookingRequest` |
| Functions / variables | camelCase | `getRockets`, `formToRequest`, `isSaving` |
| Constants | UPPER_SNAKE for module-level fixed values | `EMPTY_FORM`, `API_BASE_URL` |
| `data-testid` | kebab-case, dynamic ids interpolate the entity id | `"bookings-table"`, `` `cancel-btn-${booking.id}` `` |

## Artifact roles

| Role | Structural rule (one line) |
|------|----------------------------|
| Type module (`shared/types/{entity}.ts`) | One file per entity: the response shape (`Rocket`), any string-literal status/enum unions, and a `{Entity}Request = Omit<Entity, ...derived fields>` — never a hand-duplicated request type. |
| API client (`{feature}Api.ts`) | One `async` function per REST operation, each a one-line call into `httpClient.{get,post,put,del}` with the path and typed generic; no branching, no mapping, no error handling beyond what `httpClient` already does. |
| Hook (`use{Feature}.ts`) | `useState` for `data`/`error`/`isLoading` (+ `refreshKey` if the list can be mutated elsewhere and needs a manual `refresh()`); one `useEffect` that calls exactly one API function, guards state updates with an `active` flag cleared on cleanup, and normalizes thrown values to `Error`; returns `{ data, error, isLoading, refresh? } as const`. Read-only features with nothing to refresh (`useHealth`) omit `refreshKey`/`refresh`. |
| Feature component (`{Feature}List.tsx` / `{Feature}Status.tsx`) | Calls its own `use{Feature}` hook for the list/status; if it needs another feature's data for a dropdown, reads that feature's API function directly in a local `useEffect` (not through that feature's hook); renders three short-circuit branches in order — `isLoading` → `error` → success (empty state or table/detail); CRUD components keep a local `FormState` type, an `EMPTY_FORM` constant, and `formToRequest`/`{entity}ToForm` mapper functions to convert between HTML string inputs and typed request payloads. |
| Shared `httpClient` (`shared/api/httpClient.ts`) | Single module exporting a plain object of functions (`get`/`post`/`put`/`del`), not a class; prefixes every call with `VITE_API_BASE_URL`; throws a plain `Error` on any non-2xx response. |

## Canonical example

> `rocketsApi.ts` — the cleanest instance of the API-client role; every other `{feature}Api.ts` (`launchesApi`, `bookingsApi`, `healthApi`) follows this exact shape, trimmed to whichever HTTP verbs that feature needs.

```ts
import { httpClient } from '../../shared/api/httpClient';
import type { Rocket, RocketRequest } from '../../shared/types/rocket';

export async function getRockets(): Promise<Rocket[]> {
  return httpClient.get<Rocket[]>('/api/rockets');
}

export async function getRocketById(id: number): Promise<Rocket> {
  return httpClient.get<Rocket>(`/api/rockets/${id}`);
}

export async function createRocket(request: RocketRequest): Promise<Rocket> {
  return httpClient.post<Rocket>('/api/rockets', request);
}

export async function updateRocket(id: number, request: RocketRequest): Promise<Rocket> {
  return httpClient.put<Rocket>(`/api/rockets/${id}`, request);
}

export async function deleteRocket(id: number): Promise<void> {
  return httpClient.del(`/api/rockets/${id}`);
}
```

## Conventions

- **Wiring**: no DI container and no React context — components import their hook and API modules directly by relative path. Cross-feature reads (e.g. `LaunchList` needing the rocket list for its `<select>`, `BookingList` needing the launch list) go straight through the sibling feature's API module in a local `useEffect`, not through that feature's hook.
- **Errors**: `httpClient` throws a plain `Error` (`Request to {path} failed with status {code}`) on any non-2xx response. Hooks catch it and normalize (`cause instanceof Error ? cause : new Error(String(cause))`) into their `error` state. List/status components render that state as an early-return `<section>` with `role="alert"` and a `data-testid="{feature}-error"`. Form submission errors are caught locally inside `handleSubmit`, stored in a separate `formError` string, and rendered inline (`data-testid="form-error"`) — they never reach the hook's `error` state. There is no global error boundary or toast system.
- **Testing**: colocated as `{Name}.test.ts` / `{Name}.test.tsx` next to the source file, using Vitest + Testing Library (`globals: true`, `environment: 'jsdom'`, setup in `src/test/setup.ts`). API-client tests mock `httpClient` itself (`vi.mock('.../httpClient', ...)`) and assert the exact path/payload passed to `get`/`post`/`put`/`del`. Hook tests mock the feature's `*Api` module and drive `renderHook` + `waitFor` through loading → success/error/empty/refresh. Component tests mock the `*Api` module (not the hook) and drive `render`/`screen`/`waitFor`/`userEvent` through the same loading/error/empty/success states plus form interactions.
- **Avoid**:
  - Skipping the component test — `BookingList.tsx` currently has no colocated `BookingList.test.tsx`, unlike `RocketList`, `LaunchList` and `HealthStatus`; this is a gap against the dominant pattern, not something to replicate for new features.
  - Adding comments — no file in `front/src` carries one; keep this container comment-free like `back`.
  - Fetching another feature's list through its hook — always go through that feature's `*Api` module directly (see `LaunchList` → `rocketsApi.getRockets()`, `BookingList` → `launchesApi.getLaunches()`), since the hooks are scoped to "this component's own list" and pulling a second hook into one component would trigger a second independent loading/error lifecycle to juggle.
  - Passing raw HTML form values straight into a request — always route through `FormState` + `formToRequest` so type coercion (`Number(...)`, empty-string-to-`null`) happens in one place instead of inline in JSX handlers.
