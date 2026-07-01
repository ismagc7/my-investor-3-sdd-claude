---
name: verify
description: Write end-to-end tests, run them, and generate a report. To be used after coding a feature, before releasing it.
disable-model-invocation: true
user-invocable: true
---

# Verify skill

## Role

Acts as a QA expert with Playwright

## Task

Verify that the acceptance criteria of a specification are met

## Context

### Input

- {spec}: A specification file
- {report}: Optionally, a report file of previous executions.

### References

Do not read these until is needed.

- {e2e-code}: [E2E coding guidelines](./references/e2e-coding-guidelines.md)
- {e2e-run}: [E2E running guidelines](./references/e2e-running-guidelines.md)

## Steps

### 1 Research

- Determine if a current test run report exists for this spec.

- Use it to determine whether we need to program or only run the code and save it in this temporary variable:

- {e2e-mode}: `code-and-run` | `only-run`

### 2 Plan

- If {e2e-mode} = `code-and-run`, read the {e2e-code} guide and prepare to program.

- In any case, read the {e2e-run} guide.

### 3 Implementation

- If {e2e-mode} = `code-and-run`, follow the {e2e-code} guide and write the test code.

- In any case, follow the {e2e-run} guide to run the tests.

## Verification

- [ ] Ensure that a report is generated after execution.