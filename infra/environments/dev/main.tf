terraform {
  required_version = ">= 1.7"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.6"
    }
  }

  # Swap for a real backend before the first apply: `terraform init -backend-config=...`
  backend "s3" {
    bucket = "openhrm-terraform-state"
    key    = "dev/terraform.tfstate"
    region = "us-east-1"
  }
}

provider "aws" {
  region = var.region
}

locals {
  name = "openhrm-${var.environment}"
}

module "network" {
  source = "../../modules/network"
  name   = local.name
}

resource "aws_security_group" "app" {
  name_prefix = "${local.name}-app-"
  vpc_id      = module.network.vpc_id

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "random_password" "jwt_secret" {
  length  = 48
  special = false
}

resource "aws_secretsmanager_secret" "jwt_secret" {
  name = "${local.name}/jwt-secret"
}

resource "aws_secretsmanager_secret_version" "jwt_secret" {
  secret_id     = aws_secretsmanager_secret.jwt_secret.id
  secret_string = random_password.jwt_secret.result
}

module "database" {
  source                = "../../modules/database"
  name                  = local.name
  vpc_id                = module.network.vpc_id
  private_subnet_ids    = module.network.private_subnet_ids
  app_security_group_id = aws_security_group.app.id
}

module "ecs" {
  source                = "../../modules/ecs"
  name                  = local.name
  region                = var.region
  vpc_id                = module.network.vpc_id
  public_subnet_ids     = module.network.public_subnet_ids
  private_subnet_ids    = module.network.private_subnet_ids
  app_security_group_id = aws_security_group.app.id
  db_url                = "jdbc:postgresql://${module.database.endpoint}/openhrm"
  db_secret_arn         = module.database.secret_arn
  jwt_secret_arn        = aws_secretsmanager_secret.jwt_secret.arn
  secret_arns           = [module.database.secret_arn, aws_secretsmanager_secret.jwt_secret.arn]
}

module "frontend" {
  source = "../../modules/frontend"
  name   = local.name
}
