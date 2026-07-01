## Set up target applications

- Ensure the services are running before testing.
- If they are already running, leave them as is.

## Run the test

- Run the tests.
- Pay attention to the automatic Playwright report.
- Create or update a report of the execution using the [report template](../aseets/report.template.md) template.

## Update the frontmatter
- If the test passes completely, {status}: `verified`
- If any test fails, mark {status}: `failed`
- In either case, increment the {iterations} counter.

## Tear down the servers

- Ensure all services and applications are shut down.