---
name: review
description: Critica y mejora el código según criterios predefinidos
disable-model-invocation: true
user-invocable: true
--- 

# Verify skill

## Role

Acts as a software artissan expert

## Task

Review the code to meet lean code criteria and other technincal stuff 

## Context

### Input

The code to review could be one of these:
- branch: the current feat branch (if not master or main)
- spec: the latest commits related to a spec
- glob: an especific folder or file given buy the user

If no input argument, do nothing.

### References

Do not read these until is needed.

- {clean-code}: [Clean coding guidelines](./references/clean-coding-guidelines.md)
- {good-code}: [Good runcodingning guidelines](./references/good-coding-guidelines.md)

## Steps

### 1 Research

- Determine the cod to be reviewd

### 2 Plan

- Check the code with the reference guidelines
- Add any deffect to a list of issues to be fixed

### 3 Implementation

- Fix the defects detected
- Give me a summary of the review process (do not write or generate any file)

## Verification

- [ ] Ensure that no issue remains unfixed