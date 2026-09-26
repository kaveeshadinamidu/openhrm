output "backend_url" {
  value = "http://${module.ecs.alb_dns_name}"
}

output "frontend_url" {
  value = "https://${module.frontend.distribution_domain_name}"
}

output "frontend_bucket_name" {
  value = module.frontend.bucket_name
}

output "frontend_distribution_id" {
  value = module.frontend.distribution_id
}

output "ecr_repository_url" {
  value = module.ecs.ecr_repository_url
}
