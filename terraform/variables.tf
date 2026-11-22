variable "datadog_api_key" {
  description = "Datadog API Key"
  type        = string
  sensitive   = true
}

variable "datadog_app_key" {
  description = "Datadog APP Key"
  type        = string
  sensitive   = true
}

variable "datadog_api_url" {
  description = "Datadog API URL"
  type        = string
  default     = "https://api.datadoghq.com/"
}

variable "pagerduty_subdomain" {
  description = "PagerDuty subdomain (leave empty to skip PagerDuty integration)"
  type        = string
  default     = ""
}

variable "pagerduty_api_token" {
  description = "PagerDuty API token"
  type        = string
  sensitive   = true
  default     = ""
}

variable "pagerduty_services" {
  description = "Map of PagerDuty service name to service key (supply via TF_VAR_pagerduty_services)"
  type        = map(string)
  default     = {}
}

variable "slack_account_name" {
  description = "Slack account name configured in Datadog"
  type        = string
  default     = ""
}

variable "slack_channels" {
  description = "List of Slack channels to integrate with Datadog"
  type        = list(string)
  default = [
    "alerts-infra",
    "alerts-payments",
    "alerts-credit",
    "alerts-travel",
    "alerts-security",
    "alerts-sre",
  ]
}