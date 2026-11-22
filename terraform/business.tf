# Business Monitors — Keo Capital
# Generated from monitors/business/*.md

module "card_auth_success_rate" {
  source = "./modules/monitor"

  name    = "Card Authorization Success Rate - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):avg:keo.payments.authorization.success_rate{*} < 99.5"
  message = "Taxa de sucesso de autorização de cartão abaixo de 99.5%. Verificar integração com Mastercard e infraestrutura. @slack-alerts-payments @pagerduty-sre"

  threshold_critical = 99.5
  threshold_warning  = 99.0

  tags = ["service:payment-api", "team:payments", "product:keo-travel,cross-border", "severity:critical"]
}

module "card_auth_latency_p99" {
  source = "./modules/monitor"

  name    = "Card Authorization Latency P99 - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):p99:keo.payments.authorization.latency{*} > 300"
  message = "Latência de autorização de cartão P99 acima de 300ms. Verificar integração com Mastercard e infraestrutura. @slack-alerts-payments @pagerduty-sre"

  threshold_critical = 300
  threshold_warning  = 250

  tags = ["service:payment-api", "team:payments", "product:keo-travel,cross-border", "severity:critical"]
}

module "payment_service_availability" {
  source = "./modules/monitor"

  name    = "Payment Service Availability - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):avg:keo.payments.service.success_rate{*} < 99.9"
  message = "Disponibilidade do serviço de pagamentos abaixo de 99.9%. Incidente crítico! Verificar infraestrutura e dependências. @slack-alerts-payments @pagerduty-sre"

  threshold_critical = 99.9
  threshold_warning  = 99.5

  tags = ["service:payment-api", "team:payments", "severity:critical"]
}

module "workeo_credit_approval_rate" {
  source = "./modules/monitor"

  name    = "Workeo Credit Approval Rate - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_1h):avg:keo.credit.approval_rate{product:workeo} < 85"
  message = "Taxa de aprovação de crédito Workeo abaixo de 85%. Verificar modelo de scoring e dados de entrada. @slack-alerts-credit @pagerduty-sre"

  threshold_critical = 85
  threshold_warning  = 90

  tags = ["service:credit-service", "team:credit", "product:workeo", "severity:high"]
}

module "workeo_disbursement_latency" {
  source = "./modules/monitor"

  name    = "Workeo Disbursement Latency P95 - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):p95:keo.working_capital.disbursement.latency{*} > 5"
  message = "Tempo de processamento de working capital P95 acima de 5s. Verificar disbursement e bancos parceiros. @slack-alerts-credit @pagerduty-sre"

  threshold_critical = 5
  threshold_warning  = 4

  tags = ["service:working-capital-disbursement", "team:credit", "product:workeo", "severity:high"]
}

module "keo_travel_card_creation_latency" {
  source = "./modules/monitor"

  name    = "Keo Travel Card Creation Latency P95 - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):p95:keo.travel.card_creation.latency{*} > 3"
  message = "Tempo de criação de cartão virtual P95 acima de 3s. Verificar integração com Mastercard e infraestrutura. @slack-alerts-travel @pagerduty-sre"

  threshold_critical = 3
  threshold_warning  = 2.5

  tags = ["service:travel-card-service", "team:travel", "product:keo-travel", "severity:high"]
}

module "crossborder_transaction_success_rate" {
  source = "./modules/monitor"

  name    = "Cross-border Transaction Success Rate - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):avg:keo.crossborder.transaction.success_rate{*} < 99.0"
  message = "Taxa de sucesso de transações cross-border abaixo de 99.0%. Verificar câmbio e integração Mastercard. @slack-alerts-payments @pagerduty-sre"

  threshold_critical = 99.0
  threshold_warning  = 99.2

  tags = ["service:cross-border-fx", "team:payments", "product:cross-border", "severity:high"]
}

module "transaction_volume_anomaly" {
  source = "./modules/monitor"

  name    = "Transaction Volume Anomaly - Keo Capital"
  type    = "query alert"
  query   = "avg(last_1h):anomalies(avg:keo.payments.authorization.volume{*}, 'basic', 2) >= 1"
  message = "Anomalia detectada no volume de transações. Verificar se há pico de demanda ou falha no processamento. @slack-alerts-payments"

  tags = ["service:payment-api", "team:payments", "severity:medium"]
}

module "crossborder_fx_error_rate" {
  source = "./modules/monitor"

  name    = "Cross-border FX Error Rate - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):avg:keo.crossborder.fx.error_rate{*} > 0.5"
  message = "Taxa de erro de câmbio acima de 0.5%. Verificar provedor de câmbio. @slack-alerts-payments @pagerduty-sre"

  threshold_critical = 0.5
  threshold_warning  = 0.3

  tags = ["service:cross-border-fx", "team:payments", "product:cross-border", "severity:high"]
}