# Root module - calls all monitor modules

module "business_monitors" {
  source = "./monitors"
  # Business monitors are defined in monitors/business.tf
}

module "application_monitors" {
  source = "./monitors"
  # Application monitors are defined in monitors/application.tf
}

module "infrastructure_monitors" {
  source = "./monitors"
  # Infrastructure monitors are defined in monitors/infrastructure.tf
}