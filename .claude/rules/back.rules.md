---
name: backendrules
description: Code rules for the back container of AstroBookings
paths:
  - "back/src/**/*.java"
---
# back code rules — AstroBookings

## Summary

`back` is a Spring Boot 3.5 REST API laid out as one feature package per domain concept (`booking`, `launch`, `rocket`, `health`), each repeating the same thin Controller → Service → Repository → Entity layering. The one principle that matters most: **Services are the only place with logic** — Controllers just delegate, Repositories are empty marker interfaces, and entities carry data plus at most one lifecycle method (`cancel()`); everything else (validation, cross-entity lookups, DTO mapping) lives in the Service's short, single-purpose `validate(...)` / `toResponse(...)` private methods.

## Naming

| Element | Convention | Example |
|---------|------------|---------|
| Folders / Files | lowercase feature package, one file per public type | `back/src/main/java/dev/aiddbot/abjavareact/booking/BookingService.java` |
| Types / Classes | PascalCase, suffixed by role | `BookingController`, `BookingService`, `BookingRepository`, `BookingStatus` |
| Functions / Variables | camelCase | `findLaunch`, `passengerName`, `pricePerSeat` |
| Constants | UPPER_SNAKE | `UP`, `DOWN` (in `HealthService`) |
| DTOs | `{Entity}Request` / `{Entity}Response`, Java `record` | `BookingRequest`, `LaunchResponse` |
| Enums | plain `enum`, PascalCase name matching the field it types | `BookingStatus { CREATED, CANCELLED }` |

## Artifact roles

| Role | Structural rule (one line) |
|------|----------------------------|
| Entity (`@Entity`) | Protected no-arg constructor for JPA + one public constructor with required fields; getters only, no `equals`/`hashCode`/`toString`; setters only on entities that are updated wholesale via `PUT` (`Launch`, `Rocket`) — entities with a fixed lifecycle expose an intent method instead (`Booking.cancel()`), never a raw `setStatus`. |
| Controller (`@RestController`) | One per feature, `@RequestMapping("/api/{plural}")`; every method is a one-line delegation to the Service; `POST` builds the `Location` header via `ServletUriComponentsBuilder`; never touches the entity or Repository directly. |
| Service (`@Service`) | Constructor-injected Repository (+ any sibling Repository needed for cross-entity validation, e.g. `BookingService` also takes `LaunchRepository`); public methods in `findAll` / `findById` / `create` / `update` / `delete`-or-`cancel` order, followed by private `validate(request)` and private `toResponse(entity)` at the bottom. Keep each method short — one responsibility per method, extract a private helper (`findLaunch`, `probeDatabase`, ...) rather than growing one method. |
| Repository (`interface extends JpaRepository<Entity, Long>`) | Empty body unless a real query beyond CRUD is needed — none currently define custom methods. |
| Request DTO | `record`, field order matches the entity's constructor / form order; carries raw foreign keys (`launchId`, `rocketId`), not nested objects. |
| Response DTO | `record`, denormalizes the related entity's display fields inline (e.g. `BookingResponse.launchRocketName`, `.launchDate`) so the frontend never needs a second call; enums serialize via `.name()` to `String`. |
| Config (`@Configuration`) | Cross-cutting only, in `shared/`; injects config via `@Value` on the constructor, never `@Autowired` fields. |

## Canonical example

> `BookingService` — shows the full validate → resolve related entity → persist → map pattern shared by every feature.

```java
public BookingResponse create(BookingRequest request) {
  validate(request);
  Launch launch = findLaunch(request.launchId());
  Booking booking =
      new Booking(
          launch, request.passengerName(), request.passengerEmail(), request.passengerPhone());
  return toResponse(repository.save(booking));
}

private Launch findLaunch(Long launchId) {
  return launchRepository
      .findById(launchId)
      .orElseThrow(
          () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Launch not found: " + launchId));
}

private void validate(BookingRequest request) {
  if (request.launchId() == null) {
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Launch is required");
  }
  if (request.passengerName() == null || request.passengerName().isBlank()) {
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passenger name is required");
  }
}
```

## Conventions

- **Wiring**: constructor injection only, no field `@Autowired`. The single exception is `HealthService`, which keeps a public `@Autowired`-annotated constructor for Spring plus a package-private constructor taking an injectable `Clock` purely as a test seam — don't copy the pattern elsewhere unless a similar test seam is genuinely needed.
- **Errors**: throw `org.springframework.web.server.ResponseStatusException` with an `HttpStatus` + message directly from the Service (`400` for invalid/missing input, `404` for missing resource). There is no `@ControllerAdvice`, no custom exception hierarchy, and no Bean Validation (`@Valid`/`@NotNull`) — validation is always a manual `if` in the Service's `validate()` method, not on the DTO or Controller.
- **Testing**: colocated under `src/test/java/.../{feature}/`, mirroring the main package, one file per layer: `{X}ServiceTest` (`@ExtendWith(MockitoExtension.class)`, `@Mock`/`@InjectMocks`, mocks the Repository), `{X}RepositoryTest` (`@DataJpaTest` + `@AutoConfigureTestDatabase(replace = Replace.NONE)` against the real SQLite dialect), `{X}ControllerTest` (`@WebMvcTest({X}Controller.class)` + `MockMvc` + `@MockitoBean` on the Service). Cover: happy path, each validation failure (400), and not-found (404).
- **Avoid**: comments — the codebase is deliberately comment-free (`Do not add comments`); the one existing exception (`HealthService.persistQuietly`) is legacy and should not be treated as license to add more. Leaking JPA entities from a Controller or Response DTO — always map through `toResponse()`. Field/setter-based injection — breaks the constructor-only wiring convention. Adding a custom exception type or `@ControllerAdvice` — `ResponseStatusException` inline is the established, consistent pattern; introducing a second error-handling style would fragment it.
