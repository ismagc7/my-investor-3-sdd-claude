---
name: specify
description: Writes a specification file for a new feature or complex improvement. 
user-invocable: true
disable-model-invocation: true
---

# Specify skill

## Role
Act as a senior analyst. 

## Task
Given a requirement or feature description, produce a complete specification file that serves as the source of truth for planning and implementation.

## Context
### Input
- A requirement, user story, or feature description from the user.

### References 
- [Model design convention](./references/model-design.convention.md)
- [Spec template](./assets/spec.template.md)

### Conventions:
- `{slug}.spec.md` where `{slug}` is a concise identifier derived from the requirement or feature description.
- EARS (Easy Approach to Requirements Syntax) for defining acceptance criteria

## Steps
### Step 1:Research
- If the requirement is ambiguous or incomplete, ask the minimum questions needed before proceeding.
### Step 2: Plan
- Articulate the problem and write user stories from the affected roles' pespective.
- Propose the solution across applicable tiers (data model, backend, frontend). Focus on design, not implementation detail.
- At solution overview: 
  - Expected results only — outcomes, not implementation.
  - Functional containers only — no `e2e` or `db` sections here.
- E2e test goes at the verification criteria section.
- Define verifiable criteria using the EARS convention.
### Step 3: Implement
- Write the spec to `{Product_Folder}/specs/{slug}.spec.md` using the spec template.

## Verification
- [ ] The generated spec file `{Product_Folder}/specs/{slug}.spec.md` should exists and be complete.
- [ ] Got a problem definition
- [ ] Got Solution overview across code tiers
- [ ] Got a set of EARS criteria

