## Accessibility (WCAG AA)

- [ ] Color contrast >= 4.5:1 (3:1 large text); never rely on color alone.
- [ ] Every meaningful image has alt text; decorative images use `alt=""`.
- [ ] All functionality keyboard-accessible; visible focus; no focus traps.
- [ ] Form inputs have associated labels; errors described and linked to fields.
- [ ] `lang` set on `<html>`; landmarks present; prefer native elements over ARIA.

## Security

- [ ] User input validated and sanitized.
- [ ] Queries parameterized (no string-built SQL).
- [ ] Auth/authorization checked on protected paths and actions.
- [ ] No hardcoded secrets.
- [ ] Errors don't leak sensitive info.

## Performance

- [ ] No N+1 queries; indexes where needed.
- [ ] Large lists paginated or streamed.
- [ ] Expensive work cached when appropriate.
- [ ] No blocking I/O in hot paths.