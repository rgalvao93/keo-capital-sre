terraform {
  required_providers {
    datadog = {
      source  = "DataDog/datadog"
      version = "~> 3.0"
    }
  }
}

variable "name" {
  description = "Monitor name"
  type        = string
}

variable "type" {
  description = "Monitor type"
  type        = string
}

variable "query" {
  description = "Monitor query"
  type        = string
}

variable "message" {
  description = "Monitor message"
  type        = string
}

variable "threshold_critical" {
  description = "Critical threshold"
  type        = number
  default     = null
}

variable "threshold_warning" {
  description = "Warning threshold"
  type        = number
  default     = null
}

variable "tags" {
  description = "Monitor tags"
  type        = list(string)
  default     = []
}

variable "notify_no_data" {
  description = "Notify when no data"
  type        = bool
  default     = true
}

variable "no_data_timeframe" {
  description = "No data timeframe in minutes"
  type        = number
  default     = 10
}

variable "renotify_interval" {
  description = "Renotify interval in minutes"
  type        = number
  default     = 60
}

variable "timeout_h" {
  description = "Timeout in hours"
  type        = number
  default     = 0
}

resource "datadog_monitor" "this" {
  name    = var.name
  type    = var.type
  query   = var.query
  message = var.message

  monitor_thresholds {
    critical = var.threshold_critical
    warning  = var.threshold_warning
  }

  tags = var.tags

  notify_no_data    = var.notify_no_data
  no_data_timeframe = var.no_data_timeframe
  renotify_interval = var.renotify_interval
  timeout_h         = var.timeout_h
}

output "monitor_id" {
  description = "The ID of the created monitor"
  value       = datadog_monitor.this.id
}

output "monitor_name" {
  description = "The name of the created monitor"
  value       = datadog_monitor.this.name
}