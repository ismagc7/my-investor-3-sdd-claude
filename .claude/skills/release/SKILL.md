---
name: release
description: Cuts a full monorepo release (version bump, changelog, tag) for AstroBookings, gated by tests and a push confirmation.
user-invocable: true
disable-model-invocation: true
---

# Release skill

## Role

Acts as a release manager for the AstroBookings monorepo.

## Task

Cut a new release: bump versions, run tests as a gate, update the changelog, commit, document and tag.

## Context

### Input

- {version}: Optional explicit version override (e.g. `1.4.0`). If omitted, derive it from conventional commits.

### References

- `back/pom.xml`: backend version field.
- `front/package.json`: frontend version field.
- Cahnge Log template : `./assets/CHANGELOG.template.md` 
- `CHANGELOG.md`: root changelog file (create if missing).

## Steps

### 1 Research

- Find the last release tag with `git describe --tags --abbrev=0` (or treat as first release if none exists).
- List commits since that tag with `git log <tag>..HEAD --oneline`.
- Classify commits by conventional-commit type (`feat`, `fix`, `chore`, breaking-change markers) to determine the semantic bump: major / minor / patch.
- Save the resulting version in this temporary variable:
- {next-version}: the computed or user-supplied {version}.

### 2 Plan

- Confirm {next-version} with the user before making changes.
- Draft the changelog entry grouped by type (Features, Fixes, Chores) from the commits found in step 1.
- Check if some changes should update the AGENTS or arch.md file

### 3 Implement

- Update the version field in `back/pom.xml` to {next-version}.
- Update the `version` field in `front/package.json` to {next-version}.
- Run `mvnw.cmd test` in `back/`; abort the release and report failures if it does not pass.
- Run `npm run test` in `front/`; abort the release and report failures if it does not pass.
-  Update AGENTS.md or chang.md if ther are significant changes.
- Prepend the drafted changelog entry to `CHANGELOG.md` under a `## {next-version} - {date}` heading.
- Commit the version bump and changelog with a `chore(release): v{next-version}` conventional commit message.
- Create an annotated git tag `v{next-version}`.
- Ask the user to confirm before running `git push && git push --tags`.

## Verification

- [ ] Both `back/pom.xml` and `front/package.json` show {next-version}.
- [ ] `CHANGELOG.md` has a new entry for {next-version}.
- [ ] Both backend and frontend test suites passed before the commit was created.
- [ ] A git tag `v{next-version}` exists locally.
- [ ] The user explicitly confirmed before any push occurred.
