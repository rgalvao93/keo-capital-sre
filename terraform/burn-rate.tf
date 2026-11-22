# Burn Rate Alerts — Metric-based SLOs
# Reference: docs/slos/slos.md, Datadog SLO burn rate methodology
# A queima de error budget acima do limiar dispara alerta para acionar runbooks

resource "datadog_monitor" "card_auth_success_rate_burn_rate" {
  name  = "BURST: Card Authorization Success Rate Burn Rate"
  type  = "slo alert"
  query = "error_budget(\"${datadog_service_level_objective.card_auth_success_rate_metric.id}\").over(\"30d\").burn_rate() > 2"

  message = "Taxa de queima do SLO de autorização de cartão acima do limiar (2x do error budget em 30 dias). Verificar runbook card-authorization-failure. @slack-alerts-payments @pagerduty-sre"

  tags = [
    "service:payment-api",
    "team:payments",
    "product:keo-travel",
    "slo:card-authorization-burn-rate",
    "severity:high",
  ]
}

resource "datadog_monitor" "payment_service_availability_burn_rate" {
  name  = "BURST: Payment Service Availability Burn Rate"
  type  = "slo alert"
  query = "error_budget(\"${datadog_service_level_objective.payment_service_availability_metric.id}\").over(\"30d\").burn_rate() > 2"

  message = "Taxa de queima do SLO de disponibilidade do serviço de pagamentos acima do limiar (2x do error budget em 30 dias). Verificar runbook payment-service-unavailable. @slack-alerts-payments @pagerduty-sre"

  tags = [
    "service:payment-api",
    "team:payments",
    "slo:availability-burn-rate",
    "severity:high",
  ]
}

resource "datadog_monitor" "crossborder_transaction_success_rate_burn_rate" {
  name  = "BURST: Cross-border Transaction Success Rate Burn Rate"
  type  = "slo alert"
  query = "error_budget(\"${datadog_service_level_objective.crossborder_transaction_success_rate_metric.id}\").over(\"30d\").burn_rate() > 2"

  message = "Taxa de queima do SLO de transações cross-border acima do limiar (2x do error budget em 30 dias). Verificar runbook cross-border-transaction-failure. @slack-alerts-payments @pagerduty-sre"

  tags = [
    "service:cross-border-fx",
    "team:payments",
    "product:cross-border",
    "slo:cross-border-success-burn-rate",
    "severity:high",
  ]
}

resource "datadog_monitor" "workeo_disbursement_success_rate_burn_rate" {
  name  = "BURST: Workeo Working Capital Disbursement Success Rate Burn Rate"
  type  = "slo alert"
  query = "error_budget(\"${datadog_service_level_objective.workeo_disbursement_success_rate_metric.id}\").over(\"30d\").burn_rate() > 2"

  message = "Taxa de queima do SLO de taxa de sucesso de disbursement do Workeo acima do limiar (2x do error budget em 30 dias). Verificar runbook working-capital-processing-delay. @slack-alerts-credit @pagerduty-sre"

  tags = [
    "service:credit-service",
    "team:credit",
    "product:workeo",
    "slo:disbursement-success-burn-rate",
    "severity:high",
  ]
}

resource "datadog_monitor" "keo_travel_card_creation_success_rate_burn_rate" {
  name  = "BURST: Keo Travel Card Creation Success Rate Burn Rate"
  type  = "slo alert"
  query = "error_budget(\"${datadog_service_level_objective.keo_travel_card_creation_success_rate_metric.id}\").over(\"30d\").burn_rate() > 2"

  message = "Taxa de queima do SLO de taxa de sucesso de criação de cartão Keo Travel acima do limiar (2x do error budget em 30 dias). Verificar runbook travel-card-creation-failure. @slack-alerts-travel @pagerduty-sre"

  tags = [
    "service:travel-card-service",
    "team:travel",
    "product:keo-travel",
    "slo:card-creation-success-burn-rate",
    "severity:high",
  ]
}