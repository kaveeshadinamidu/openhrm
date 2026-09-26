# Infrastructure

Terraform modules under `modules/`, composed per environment under
`environments/<env>/`.

## Modules

- `network` - VPC, public/private subnets, a single NAT gateway
- `database` - RDS PostgreSQL, credentials generated into Secrets Manager
- `ecs` - ECR repo, ECS Fargate cluster/service, ALB, IAM roles, CloudWatch logs
- `frontend` - S3 bucket + CloudFront distribution for the static SPA build

## First-time setup

1. Create the state bucket referenced in `environments/dev/main.tf` (`backend
   "s3"` block) out of band - Terraform won't create its own backend.
2. Configure AWS credentials locally (`aws configure` or SSO).
3. From `environments/dev/`:

```bash
terraform init
terraform plan
terraform apply
```

4. Push a backend image to the ECR repo Terraform created (see
   `terraform output ecr_repository_url`), then update the ECS service, or let
   the `deploy` GitHub Actions workflow do this on merge to `main`.

## CI/CD deploy role

`.github/workflows/deploy.yml` assumes an IAM role via OIDC
(`secrets.AWS_DEPLOY_ROLE_ARN`) rather than storing AWS keys in the repo. That
role needs to trust `token.actions.githubusercontent.com` for this repo and
have permissions for ECR push, ECS service update, S3 sync, and CloudFront
invalidation - not included here since it depends on your account's existing
IAM setup.

## Environments

Only `dev` exists today. A `staging`/`prod` environment is a new directory
under `environments/` reusing the same modules with different variable values
- see [ADR 3](../docs/adr/0003-terraform-for-infra.md).
