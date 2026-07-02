---
name: extract
description: Document one container in depth — C4 L3 components + container code rules. 
user-invocable: false
disable-model-invocation: false
---

# Extract skill

## Context

For **one container at a time**, generate its component architecture and container code-rules documents, acting as a senior architect.

### Glossary
- **Container** — a runnable unit in `system.arch.md` (`back`, `front`, `db`...). Never "tier".
- **Component** — an internal building block of one container (C4 L3).
- **Mode** — `greenfield` (no code → prescribe) or `brownfield` (code exists → extract).

### Input
- The container to document (ask which one if not given).
- Templates: 
- [`container.arch.template.md`](./assets/container.arch.template.md), 
- [`container.rules.template.md`](./assets/container.rules.template.md).
- Mode guide (read the one matching whether this container already has code):
  - [`mode.greenfield.md`](./references/mode.greenfield.md) — no code; prescribe the intended design.
  - [`mode.brownfield.md`](./references/mode.brownfield.md) — existing code; extract facts.

## Steps

### 1. Research
- List containers from `system.arch.md`, pick one (ask if ambiguous). 
- Decide its mode by whether it already has source code (code = `brownfield`, none = `greenfield`) and follow the matching `mode.*.md`.

### 2. Plan
- **Container architecture**: fill `container.arch.template.md` for this container only — C4 L3 components diagram, code organization, key contracts, and domain entities/data schemas/api endpoints where relevant.
- **Container code rules**: fill `container.rules.template.md`, scoped to this container's source glob — naming, artifact roles, conventions, and one canonical example.

### 3. Implement
- Write `{Product_Folder}/arch/{container}.arch.md` and link it from `system.arch.md`.
- Write `{Agents_Folder}/rules/{container}.rules.md`.
- Commit (`docs`); repeat for the next container, or suggest `/specify`.

## Verification
- [ ] Both files exist, are well formatted, and have no `{placeholders}` left.
