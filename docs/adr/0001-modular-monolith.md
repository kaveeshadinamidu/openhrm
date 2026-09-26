# 1. Modular monolith over microservices

## Status
Accepted

## Context
OpenHRM targets startups (10-200 employees). At that scale the operational cost of
microservices (service discovery, distributed tracing, network failure handling,
multiple deploy pipelines) outweighs any benefit. The domain also has heavy
cross-cutting concerns (a leave request touches employees, approvals, and
notifications) that are simpler to model as in-process calls with transactional
consistency.

## Decision
Build a single Spring Boot deployable, organized as package-by-domain modules
(`employee`, `leave`, `auth`, `organization`, `user`) with no cross-module
reach-into-internals. Each module exposes its repository/service layer and
nothing else.

## Consequences
- One deployment pipeline, one database, ACID transactions across domains.
- Module boundaries are enforced by convention/code review, not the network -
  a future extraction to services is possible but not free.
- If a module's load profile genuinely diverges (e.g. reporting needs a read
  replica), that's an argument to revisit, not a default assumption.
