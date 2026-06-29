---
description: Prompt to generate launches feature
---
# Launch maintenace

## Rol
Act as a sennior software engineer

## Task
Implement the laucnhes maintenace feature to create and edit rocket launches.

## Context
**Stack:**
- Backend: Spring Boot 3.5 / Java 21 / SQLite (via Hibernate community dialects) / Maven
- Frontend: React 19 / TypeScript / Vite / Vitest + Testing Library
- Package: `dev.aiddbot.abjavareact`
- Backend runs on port 8080, frontend dev server on 5173
**Business**
- AstroBookings is a fictitional space toursim company

## Steps
### 1 - Research
- Read the actual rockets maintenace implmentation

### 2 - Plan
- Think about data model
    - Launch must have a rocket 
    - A date in the future
    - A price per passenger
    - A status: created, confirmed, cancelled, completed
- Build the backend API
- Design the frontend pages
    
### 3 - Implement
- Choose the simplest solution (no overengineering)
- Write the less amount of code (only the needed)
- keep working until the gola task is completed

## Output
- Working source code
- Unit test for the most critical parts

## Verification
- [ ] Source code must compile succesfully
- [ ] Servers must start (smoke test)
- [ ] Unit test should pass