# Application Monitors — Keo Capital
# Generated from monitors/application/*.md

module "payment_api_latency_p99" {
  source = "./modules/monitor"

  name    = "Payment API Latency P99 - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):p99:trace.http.request.duration{service:payment-api} > 500"
  message = "Latência P99 do payment-api acima de 500ms. Verificar traces no Datadog APM. @slack-alerts-payments @pagerduty-sre"

  threshold_critical = 500
  threshold_warning = 400

  tags = ["service:payment-api", "team:payments", "severity:high"]
}

module "payment_api_error_rate" {
  source = "./modules/monitor"

  name    = "Payment API Error Rate - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):sum:trace.http.request.errors{service:payment-api}.as_rate() > 0.01"
  message = "Taxa de erro do payment-api acima de 1%. Verificar logs de erro no Datadog. @slack-alerts-payments @pagerduty-sre"

  threshold_critical = 0.01
  threshold_warning = 0.005

  tags = ["service:payment-api", "team:payments", "severity:high"]
}

module "payment_api_db_latency" {
  source = "./modules/monitor"

  name    = "Payment API Database Query Latency - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):p99:trace.postgres.query.duration{service:payment-api} > 100"
  message = "Latência de queries PostgreSQL do payment-api acima de 100ms. Verificar queries lentas no Datadog DBM. @slack-alerts-payments"

  threshold_critical = 100
  threshold_warning = 80

  tags = ["service:payment-api", "team:payments", "severity:medium"]
}

module "payment_api_redis_hit_rate" {
  source = "./modules/monitor"

  name    = "Payment API Redis Cache Hit Rate - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):avg:trace.redis.command.hits{service:payment-api} / avg:trace.redis.command.total{service:payment-api} < 0.8"
  message = "Cache hit rate do Redis do payment-api abaixo de 80%. Verificar TTL e padrões de acesso. @slack-alerts-payments"

  threshold_critical = 0.8
  threshold_warning = 0.85

  tags = ["service:payment-api", "team:payments", "severity:medium"]
}

module "payment_api_throughput_anomaly" {
  source = "./modules/monitor"

  name    = "Payment API Throughput Anomaly - Keo Capital"
  type    = "query alert"
  query   = "avg(last_1h):anomalies(sum:trace.http.request.hits{service:payment-api}.as_rate(), 'basic', 2) >= 1"
  message = "Anomalia detectada no throughput do payment-api. Verificar se há queda de tráfego ou pico inesperado. @slack-alerts-payments"

  tags = ["service:payment-api", "team:payments", "severity:medium"]
}

module "credit_service_latency_p99" {
  source = "./modules/monitor"

  name    = "Credit Service Latency P99 - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):p99:trace.http.request.duration{service:credit-service} > 1000"
  message = "Latência P99 do credit-service acima de 1s. Verificar traces no Datadog APM. @slack-alerts-credit @pagerduty-sre"

  threshold_critical = 1000
  threshold_warning = 800

  tags = ["service:credit-service", "team:credit", "severity:high"]
}

module "credit_service_error_rate" {
  source = "./modules/monitor"

  name    = "Credit Service Error Rate - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):sum:trace.http.request.errors{service:credit-service}.as_rate() > 0.01"
  message = "Taxa de erro do credit-service acima de 1%. Verificar logs de erro no Datadog. @slack-alerts-credit @pagerduty-sre"

  threshold_critical = 0.01
  threshold_warning = 0.005

  tags = ["service:credit-service", "team:credit", "severity:high"]
}

module "travel_card_service_latency_p99" {
  source = "./modules/monitor"

  name    = "Travel Card Service Latency P99 - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):p99:trace.http.request.duration{service:travel-card-service} > 500"
  message = "Latência P99 do travel-card-service acima de 500ms. Verificar traces no Datadog APM. @slack-alerts-travel @pagerduty-sre"

  threshold_critical = 500
  threshold_warning = 400

  tags = ["service:travel-card-service", "team:travel", "severity:high"]
}

module "travel_card_service_error_rate" {
  source = "./modules/monitor"

  name    = "Travel Card Service Error Rate - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):sum:trace.http.request.errors{service:travel-card-service}.as_rate() > 0.01"
  message = "Taxa de erro do travel-card-service acima de 1%. Verificar logs de erro no Datadog. @slack-alerts-travel @pagerduty-sre"

  threshold_critical = 0.01
  threshold_warning = 0.005

  tags = ["service:travel-card-service", "team:travel", "severity:high"]
}

module "crossborder_fx_latency_p99" {
  source = "./modules/monitor"

  name    = "Cross-border FX Latency P99 - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):p99:trace.http.request.duration{service:cross-border-fx} > 1000"
  message = "Latência P99 do cross-border-fx acima de 1s. Verificar traces no Datadog APM. @slack-alerts-payments @pagerduty-sre"

  threshold_critical = 1000
  threshold_warning = 800

  tags = ["service:cross-border-fx", "team:payments", "severity:high"]
}

module "crossborder_fx_error_rate_app" {
  source = "./modules/monitor"

  name    = "Cross-border FX Error Rate - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):sum:trace.http.request.errors{service:cross-border-fx}.as_rate() > 0.01"
  message = "Taxa de erro do cross-border-fx acima de 1%. Verificar logs de erro no Datadog. @slack-alerts-payments @pagerduty-sre"

  threshold_critical = 0.01
  threshold_warning = 0.005

  tags = ["service:cross-border-fx", "team:payments", "severity:high"]
}