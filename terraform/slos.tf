# Service Level Objectives (SLOs) — Keo Capital
# Monitor-based SLOs derived from the business and external-dependency monitors.
# Reference: docs/slos/slos.md

resource "datadog_service_level_objective" "card_auth_success_rate" {
  name        = "Card Authorization Success Rate"
  type        = "monitor"
  description = "Percentual de autorizações de cartão concluídas com sucesso (SLO 99.5% em 30 dias)."
  monitor_ids = [module.card_auth_success_rate.monitor_id]
  tags        = ["service:payment-api", "team:payments", "product:keo-travel", "slo:card-authorization"]

  thresholds {
    timeframe = "30d"
    target    = 99.5
    warning   = 99.6
  }
}

resource "datadog_service_level_objective" "payment_service_availability" {
  name        = "Payment Service Availability"
  type        = "monitor"
  description = "Disponibilidade do serviço de pagamentos (SLO 99.9% em 30 dias)."
  monitor_ids = [module.payment_service_availability.monitor_id]
  tags        = ["service:payment-api", "team:payments", "slo:availability"]

  thresholds {
    timeframe = "30d"
    target    = 99.9
    warning   = 99.95
  }
}

resource "datadog_service_level_objective" "workeo_disbursement_latency" {
  name        = "Workeo Working Capital Disbursement Latency"
  type        = "monitor"
  description = "Tempo de processamento de working capital P95 abaixo de 5s (SLO 99.0% em 30 dias)."
  monitor_ids = [module.workeo_disbursement_latency.monitor_id]
  tags        = ["service:credit-service", "team:credit", "product:workeo", "slo:disbursement-latency"]

  thresholds {
    timeframe = "30d"
    target    = 99.0
    warning   = 99.1
  }
}

resource "datadog_service_level_objective" "keo_travel_card_creation_latency" {
  name        = "Keo Travel Card Creation Latency"
  type        = "monitor"
  description = "Tempo de criação de cartão virtual Keo Travel P95 abaixo de 3s (SLO 99.5% em 30 dias)."
  monitor_ids = [module.keo_travel_card_creation_latency.monitor_id]
  tags        = ["service:travel-card-service", "team:travel", "product:keo-travel", "slo:card-creation-latency"]

  thresholds {
    timeframe = "30d"
    target    = 99.5
    warning   = 99.6
  }
}

resource "datadog_service_level_objective" "crossborder_transaction_success_rate" {
  name        = "Cross-border Transaction Success Rate"
  type        = "monitor"
  description = "Taxa de sucesso de transações cross-border (SLO 99.0% em 30 dias)."
  monitor_ids = [module.crossborder_transaction_success_rate.monitor_id]
  tags        = ["service:cross-border-fx", "team:payments", "product:cross-border", "slo:cross-border-success"]

  thresholds {
    timeframe = "30d"
    target    = 99.0
    warning   = 99.1
  }
}

resource "datadog_service_level_objective" "mastercard_availability" {
  name        = "Mastercard Network Availability"
  type        = "monitor"
  description = "Disponibilidade da rede Mastercard (SLO 99.9% em 30 dias)."
  monitor_ids = [module.mastercard_availability.monitor_id]
  tags        = ["service:mastercard", "team:payments", "external:mastercard", "slo:external-availability"]

  thresholds {
    timeframe = "30d"
    target    = 99.9
    warning   = 99.95
  }
}

resource "datadog_service_level_objective" "bank_availability" {
  name        = "Partner Bank Availability"
  type        = "monitor"
  description = "Disponibilidade dos bancos parceiros para disbursement (SLO 99.5% em 30 dias)."
  monitor_ids = [module.bank_availability.monitor_id]
  tags        = ["service:credit-service", "team:credit", "external:bank", "slo:external-availability"]

  thresholds {
    timeframe = "30d"
    target    = 99.5
    warning   = 99.6
  }
}

# --- Metric-based SLOs (numerator/denominator) ---
# Reference: docs/slos/slos.md

resource "datadog_service_level_objective" "card_auth_success_rate_metric" {
  name        = "Card Authorization Success Rate (Metric-based)"
  type        = "metric"
  description = "Taxa de sucesso de autorização de cartão baseada em métricas (SLO 99.5% em 30 dias)."
  tags        = ["service:payment-api", "team:payments", "product:keo-travel", "slo:card-authorization-metric"]

  query {
    numerator   = "sum:keo.payments.authorization.success{*}.as_count()"
    denominator = "sum:keo.payments.authorization.success{*}.as_count() + sum:keo.payments.authorization.failure{*}.as_count()"
  }

  thresholds {
    timeframe = "30d"
    target    = 99.5
    warning   = 99.6
  }
}

resource "datadog_service_level_objective" "payment_service_availability_metric" {
  name        = "Payment Service Availability (Metric-based)"
  type        = "metric"
  description = "Disponibilidade do serviço de pagamentos baseada em métricas (SLO 99.9% em 30 dias)."
  tags        = ["service:payment-api", "team:payments", "slo:availability-metric"]

  query {
    numerator   = "sum:keo.payments.processing.success{*}.as_count()"
    denominator = "sum:keo.payments.processing.success{*}.as_count() + sum:keo.payments.processing.failure{*}.as_count()"
  }

  thresholds {
    timeframe = "30d"
    target    = 99.9
    warning   = 99.95
  }
}

resource "datadog_service_level_objective" "crossborder_transaction_success_rate_metric" {
  name        = "Cross-border Transaction Success Rate (Metric-based)"
  type        = "metric"
  description = "Taxa de sucesso de transações cross-border baseada em métricas (SLO 99.0% em 30 dias)."
  tags        = ["service:cross-border-fx", "team:payments", "product:cross-border", "slo:cross-border-success-metric"]

  query {
    numerator   = "sum:keo.crossborder.transaction.success{*}.as_count()"
    denominator = "sum:keo.crossborder.transaction.success{*}.as_count() + sum:keo.crossborder.transaction.failure{*}.as_count()"
  }

  thresholds {
    timeframe = "30d"
    target    = 99.0
    warning   = 99.1
  }
}

resource "datadog_service_level_objective" "workeo_disbursement_success_rate_metric" {
  name        = "Workeo Working Capital Disbursement Success Rate (Metric-based)"
  type        = "metric"
  description = "Taxa de sucesso de disbursement do Workeo baseada em métricas (SLO 99.0% em 30 dias)."
  tags        = ["service:credit-service", "team:credit", "product:workeo", "slo:disbursement-success-metric"]

  query {
    numerator   = "sum:keo.working_capital.disbursement.success{*}.as_count()"
    denominator = "sum:keo.working_capital.disbursement.success{*}.as_count() + sum:keo.working_capital.disbursement.failure{*}.as_count()"
  }

  thresholds {
    timeframe = "30d"
    target    = 99.0
    warning   = 99.1
  }
}

resource "datadog_service_level_objective" "keo_travel_card_creation_success_rate_metric" {
  name        = "Keo Travel Card Creation Success Rate (Metric-based)"
  type        = "metric"
  description = "Taxa de sucesso de criação de cartão Keo Travel baseada em métricas (SLO 99.5% em 30 dias)."
  tags        = ["service:travel-card-service", "team:travel", "product:keo-travel", "slo:card-creation-success-metric"]

  query {
    numerator   = "sum:keo.travel.card_creation.success{*}.as_count()"
    denominator = "sum:keo.travel.card_creation.success{*}.as_count() + sum:keo.travel.card_creation.failure{*}.as_count()"
  }

  thresholds {
    timeframe = "30d"
    target    = 99.5
    warning   = 99.6
  }
}