# Infrastructure Monitors — Keo Capital

module "cpu_utilization_p95" {
  source = "./modules/monitor"

  name    = "CPU Utilization P95 - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):p95:system.cpu.user{*} + p95:system.cpu.system{*} > 80"
  message = "CPU utilization P95 acima de 80%. Verificar pods e escalar se necessário. @slack-alerts-infra"

  threshold_critical = 80
  threshold_warning  = 70

  tags = ["env:prod", "team:sre", "severity:medium"]
}

module "memory_utilization_p95" {
  source = "./modules/monitor"

  name    = "Memory Utilization P95 - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):p95:system.mem.used{*} / p95:system.mem.total{*} * 100 > 85"
  message = "Memory utilization P95 acima de 85%. Verificar pods e escalar se necessário. @slack-alerts-infra"

  threshold_critical = 85
  threshold_warning  = 75

  tags = ["env:prod", "team:sre", "severity:medium"]
}

module "disk_utilization" {
  source = "./modules/monitor"

  name    = "Disk Utilization - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):avg:system.disk.used{*} / avg:system.disk.total{*} * 100 > 90"
  message = "Disk utilization acima de 90%. Verificar espaço em disco e limpar logs/imagens antigas. @slack-alerts-infra"

  threshold_critical = 90
  threshold_warning  = 80

  tags = ["env:prod", "team:sre", "severity:medium"]
}

module "network_latency_inter_service" {
  source = "./modules/monitor"

  name    = "Network Latency Inter-Service - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):p99:trace.http.request.duration{service:service-mesh} > 50"
  message = "Latência de rede inter-serviço acima de 50ms. Verificar service mesh e rede. @slack-alerts-infra"

  threshold_critical = 50
  threshold_warning  = 30

  tags = ["env:prod", "team:sre", "severity:medium"]
}

module "datadog_cost_anomaly" {
  source = "./modules/monitor"

  name    = "Datadog Cost Anomaly - Custom Metrics - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_1d):avg:datadog.estimated_usage.custom_metrics{*} > 100000"
  message = "Uso estimado de custom metrics acima do esperado. Revisar cardinalidade de métricas e tags. @slack-alerts-sre"

  threshold_critical = 100000
  threshold_warning  = 80000

  tags = ["env:prod", "team:sre", "compliance:finops", "severity:medium"]
}

module "host_availability" {
  source = "./modules/monitor"

  name    = "Host/Container Availability - Keo Capital"
  type    = "host"
  query   = "avg(last_5m):avg:system.uptime{*} < 1"
  message = "Host/container offline detectado. Verificar infraestrutura e escalar se necessário. @slack-alerts-infra @pagerduty-sre"

  tags = ["env:prod", "team:sre", "severity:critical"]
}

module "error_rate_5xx_infra" {
  source = "./modules/monitor"

  name    = "Error Rate 5xx - Infrastructure - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):sum:http.requests{status:5xx}.as_rate() / sum:http.requests{*}.as_rate() > 0.001"
  message = "Error rate 5xx acima de 0.1%. Verificar logs de erro e infraestrutura. @slack-alerts-infra @pagerduty-sre"

  threshold_critical = 0.001
  threshold_warning  = 0.0005

  tags = ["env:prod", "team:sre", "severity:high"]
}