---
name: Bug report
about: Report a reproducible problem in OrderFlow API
title: "[Bug] "
labels: bug
assignees: ""
---

## Summary

<!-- Briefly describe the bug -->

## Environment

- OS:
- Java version:
- Spring profile:
- Docker enabled: Yes/No
- Redis: Local Docker/ElastiCache/Disabled/Other
- Branch or commit:

## Reproducibility

- [ ] Always
- [ ] Sometimes
- [ ] Only under concurrency
- [ ] Only in local development
- [ ] Only in CI
- [ ] Only in AWS
- [ ] Unable to reproduce consistently

## Steps to Reproduce

1. 
2. 
3. 

## Expected Behavior

<!-- What should have happened? -->

## Actual Behavior

<!-- What actually happened? -->

## Affected Area

Select all that apply:

- [ ] Authentication/JWT
- [ ] Authorization/roles
- [ ] Product API
- [ ] Inventory API
- [ ] Order API
- [ ] Payment workflow
- [ ] Reporting API
- [ ] Redis caching
- [ ] Database migrations
- [ ] Docker Compose
- [ ] GitHub Actions
- [ ] AWS deployment
- [ ] OpenAPI/Swagger
- [ ] Tests
- [ ] Other:

## Request/Response Details

Endpoint:

```http
METHOD /api/v1/resource
```

Request body:

```json
{

}
```

Response body:

```json
{

}
```

Status code:

```text

```

## Logs / Error Output

<!-- RELEVANT logs only - Remove secrets/tokens/passwords and private data before pasting -->

```text

```

## Database/Migration Context

- [ ] N/A
- [ ] Migration failed
- [ ] Constraint failed
- [ ] Query returned incorrect data
- [ ] Data inconsistency

Relevant table(s):

```text

```

## Security Impact

- [ ] No known security impact
- [ ] May expose unauthorized data
- [ ] May allow unauthorized action
- [ ] May expose sensitive error details
- [ ] Unsure

Notes:

## Additional Context

<!-- Add screenshots, links, curl commands, or additional related issues -->