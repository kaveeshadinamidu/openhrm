# 2. Self-issued JWT auth instead of Cognito

## Status
Accepted

## Context
Needed multi-tenant auth with roles (ADMIN, HR_MANAGER, MANAGER, EMPLOYEE) scoped
to an organization. AWS Cognito would remove the need to manage credentials, but
it also hides the mechanics that this project exists to demonstrate, and its
per-tenant modeling (user pools vs. groups) adds setup overhead disproportionate
to the auth logic actually needed here.

## Decision
Issue our own JWTs (`io.jsonwebtoken`) signed with a server-held HMAC secret,
stored in AWS Secrets Manager in deployed environments. Passwords are hashed
with BCrypt via Spring Security's `PasswordEncoder`. Tenant id and role are
embedded as token claims and read by `JwtAuthFilter` on every request.

## Consequences
- Full control over the token shape and claims (organization id, employee id,
  role) without an external service round-trip.
- We own token revocation and rotation; there's no built-in refresh-token flow
  yet (tracked as follow-up work) - short (15 min) access token TTLs mitigate
  this in the meantime.
- Would reconsider for a real production SaaS handling many enterprise
  customers who expect SSO/SAML - that's a Cognito or Auth0-shaped problem.
