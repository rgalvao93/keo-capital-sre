output "monitor_id" {
  description = "The ID of the created monitor"
  value       = datadog_monitor.this.id
}

output "monitor_name" {
  description = "The name of the created monitor"
  value       = datadog_monitor.this.name
}