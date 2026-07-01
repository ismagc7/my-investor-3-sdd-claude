---
name: skillify
description: helps create the minimal scafolding a new skill
---


- Ask the user the questions neede to creat the skill.
- Ask on question at a time, with closed answers.
- Create the folder for the skill at `.cluade/skills`
- Create the `SKILL.md` index file
- Write the front-matter following these template

```yaml
name: {name-of-the-skill}
description: {short descriptino of the skill goal}
user-invocable: true | false
disable-model-invocation: true | false
```

- Write the skill body following this template

```md
# {Skill name}

## Role
{the role to be adopted by the model}

## Task
{short sentence describing the goal}

## Context

### Input

- {short list of arguments or files given by the caller}

### References

- {short list of files to be read when necessary}

## Steps

### 1 Research

- {short list of tasks to gather information}

### 2 Plan

- {short list of tasks to prepare for the goal}
### 3 Implment

- {short list of tasks to meet the task goal}

## Verification

- [ ] {short list of checks to verify the task goal}

```