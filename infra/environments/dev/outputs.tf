output "backend_url" {
  value = "http://${module.ecs.alb_dns_name}"
}

output "frontend_url" {
  value = "https://${module.frontend.distribution_domain_name}"
}

output "ecr_repository_url" {
  value = module.ecs.ecr_repository_url
}
