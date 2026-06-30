# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Product

AstroBookings is a fictional space tourism company; the app manages rockets, launches, and bookings.

## Stack

### Backend (`back/`)
- Java 21
- Spring Boot 3.5 (Web, Data JPA) / SQLite via `sqlite-jdbc` + `hibernate-community-dialects` / Maven
- Commands:

```bash
mvnw.cmd spring-boot:run   # start the API on port 8080
mvnw.cmd test               # run all tests
mvnw.cmd package            # build the jar
```

### Frontend (`front/`)
- TypeScript
- React 19 / Vite / Vitest + Testing Library
- Commands:

```bash
npm run dev      # start the dev server on port 5173
npm run build    # typecheck (tsc -b) then build
npm run lint      # eslint
npm run test      # run Vitest once
```

### E2E (`e2e/`)
- TypeScript
- Playwright
- Commands:

```bash
npm run test          # boots backend + frontend and runs the suite
npm run test:headed   # same, headed browser
npm run report        # open the last HTML report
```

## Folder tree

```txt
/                   root folder
|-CLAUDE.md         this file
|-.claude/          agent rules and skills
|-.product/specs/   feature specifications
|-back/             backend source code (Java / Spring Boot)
|-front/            frontend source code (TypeScript / React)
|-e2e/              end-to-end tests (Playwright)
```

## Naming Conventions

— Use conventional commit messaging
— Code branches prefixes must be: `feat/` | `fix/` | `chore/`
— Generate short slugs for artifacts and names (less-than-20-chars).

## Principles

1. Think Before Coding
Don't assume. Don't hide confusion. Surface tradeoffs.
2. Simplicity First
Minimum code that solves the problem. Nothing speculative.
3. Surgical Changes
Touch only what you must. Clean up only your own mess.
4. Goal-Driven Execution
Keep working until all success criteria are met. Loop until verified.

> Last updated 2026-06-30
