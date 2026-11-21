resource "datadog_monitor" "this" {
  name    = var.name
  type    = var.type
  query   = var.query
  message = var.message

  thresholds = var.thresholds

  tags = var.tags

  notify_no_data    = var.notify_no_data
  no_data_timeframe = var.no_data_timeframe
  renotify_interval = var.renotify_interval
  timeout_h         = var.timeout_h
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

variable "thresholds" {
  description = "Monitor thresholds"
  type        = map(number)
  default     = {}
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