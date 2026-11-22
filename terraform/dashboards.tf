# Dashboards — Keo Capital
# All dashboards defined using datadog_dashboard_json

resource "datadog_dashboard_json" "transaction_overview" {
  dashboard = jsonencode({
    title       = "Transaction Overview - Keo Capital"
    description = "Dashboard de transações para Keo Capital"
    layout_type = "ordered"
    tags        = ["team:payments"]
    widgets = [
      {
        definition = {
          type     = "timeseries"
          title    = "Volume de Transações"
          requests = [{ q = "sum:keo.payments.authorization.volume{*}" }]
        }
      },
      {
        definition = {
          type     = "query_value"
          title    = "Taxa de Sucesso"
          requests = [{ q = "avg:keo.payments.authorization.success_rate{*}" }]
        }
      },
      {
        definition = {
          type     = "timeseries"
          title    = "Latência P99"
          requests = [{ q = "p99:keo.payments.authorization.latency{*}" }]
        }
      }
    ]
  })
}

resource "datadog_dashboard_json" "infrastructure_health" {
  dashboard = jsonencode({
    title       = "Infrastructure Health - Keo Capital"
    description = "Dashboard de infraestrutura"
    layout_type = "ordered"
    tags        = ["team:sre"]
    widgets = [
      {
        definition = {
          type     = "timeseries"
          title    = "CPU Utilization"
          requests = [{ q = "avg:system.cpu.user{*} + avg:system.cpu.system{*}" }]
        }
      },
      {
        definition = {
          type     = "timeseries"
          title    = "Memory Utilization"
          requests = [{ q = "avg:system.mem.used{*} / avg:system.mem.total{*} * 100" }]
        }
      },
      {
        definition = {
          type     = "timeseries"
          title    = "Error Rate 5xx"
          requests = [{ q = "sum:http.requests{status:5xx}.as_rate() / sum:http.requests{*}.as_rate() * 100" }]
        }
      }
    ]
  })
}

resource "datadog_dashboard_json" "slo_compliance" {
  dashboard = jsonencode({
    title       = "SLO Compliance - Keo Capital"
    description = "Dashboard de SLOs"
    layout_type = "ordered"
    tags        = ["team:sre"]
    widgets = [
      {
        definition = {
          type  = "query_value"
          title = "Card Auth Success Rate"
          requests = [{ q = "avg:keo.payments.authorization.success_rate{*}" }]
        }
      },
      {
        definition = {
          type  = "query_value"
          title = "Payment Service Availability"
          requests = [{ q = "avg:keo.payments.service.success_rate{*}" }]
        }
      },
      {
        definition = {
          type  = "query_value"
          title = "Workeo Disbursement Latency P95"
          requests = [{ q = "p95:keo.working_capital.disbursement.latency{*}" }]
        }
      },
      {
        definition = {
          type  = "query_value"
          title = "Keo Travel Card Creation P95"
          requests = [{ q = "p95:keo.travel.card_creation.latency{*}" }]
        }
      },
      {
        definition = {
          type  = "query_value"
          title = "Cross-border Success Rate"
          requests = [{ q = "avg:keo.crossborder.transaction.success_rate{*}" }]
        }
      }
    ]
  })
}

resource "datadog_dashboard_json" "incident_management" {
  dashboard = jsonencode({
    title       = "Incident Management - Keo Capital"
    description = "Dashboard de gestão de incidentes"
    layout_type = "ordered"
    tags        = ["team:sre"]
    widgets = [
      {
        definition = {
          type     = "timeseries"
          title    = "MTTR SEV-1"
          requests = [{ q = "avg:keo.incidents.mttr{severity:sev1}" }]
        }
      },
      {
        definition = {
          type     = "timeseries"
          title    = "MTTD"
          requests = [{ q = "avg:keo.incidents.mttd{*}" }]
        }
      },
      {
        definition = {
          type     = "query_value"
          title    = "Alert Fatigue Rate"
          requests = [{ q = "avg:keo.alerts.false_positive_rate{*}" }]
        }
      }
    ]
  })
}

resource "datadog_dashboard_json" "workeo" {
  dashboard = jsonencode({
    title       = "Workeo - Working Capital - Keo Capital"
    description = "Dashboard do Workeo"
    layout_type = "ordered"
    tags        = ["team:credit"]
    widgets = [
      {
        definition = {
          type     = "query_value"
          title    = "Taxa de Aprovação de Crédito"
          requests = [{ q = "avg:keo.credit.approval_rate{product:workeo}" }]
        }
      },
      {
        definition = {
          type     = "timeseries"
          title    = "Tempo de Processamento (P95)"
          requests = [{ q = "p95:keo.working_capital.disbursement.latency{*}" }]
        }
      },
      {
        definition = {
          type     = "timeseries"
          title    = "Volume de Solicitações"
          requests = [{ q = "sum:keo.working_capital.request.volume{*}" }]
        }
      }
    ]
  })
}

resource "datadog_dashboard_json" "keo_travel" {
  dashboard = jsonencode({
    title       = "Keo Travel - Corporate Travel - Keo Capital"
    description = "Dashboard do Keo Travel"
    layout_type = "ordered"
    tags        = ["team:travel"]
    widgets = [
      {
        definition = {
          type     = "timeseries"
          title    = "Criação de Cartão (P95)"
          requests = [{ q = "p95:keo.travel.card_creation.latency{*}" }]
        }
      },
      {
        definition = {
          type     = "query_value"
          title    = "Taxa de Sucesso de Criação"
          requests = [{ q = "avg:keo.travel.card_creation.success_rate{*}" }]
        }
      },
      {
        definition = {
          type     = "timeseries"
          title    = "Volume de Transações"
          requests = [{ q = "sum:keo.travel.transaction.volume{*}" }]
        }
      }
    ]
  })
}

resource "datadog_dashboard_json" "cross_border" {
  dashboard = jsonencode({
    title       = "Cross-border - Keo Capital"
    description = "Dashboard de cross-border"
    layout_type = "ordered"
    tags        = ["team:payments"]
    widgets = [
      {
        definition = {
          type     = "query_value"
          title    = "Taxa de Sucesso de Transações"
          requests = [{ q = "avg:keo.crossborder.transaction.success_rate{*}" }]
        }
      },
      {
        definition = {
          type     = "timeseries"
          title    = "Latência de Transação (P99)"
          requests = [{ q = "p99:keo.crossborder.transaction.latency{*}" }]
        }
      },
      {
        definition = {
          type     = "timeseries"
          title    = "Taxa de Erro de Câmbio"
          requests = [{ q = "avg:keo.crossborder.fx.error_rate{*}" }]
        }
      }
    ]
  })
}

resource "datadog_dashboard_json" "external_dependencies" {
  dashboard = jsonencode({
    title       = "External Dependencies - Keo Capital"
    description = "Dashboard de dependências externas"
    layout_type = "ordered"
    tags        = ["team:sre"]
    widgets = [
      {
        definition = {
          type     = "query_value"
          title    = "Mastercard Availability"
          requests = [{ q = "avg:keo.external.mastercard.availability{*}" }]
        }
      },
      {
        definition = {
          type     = "timeseries"
          title    = "Mastercard Latency (P99)"
          requests = [{ q = "p99:keo.external.mastercard.latency{*}" }]
        }
      },
      {
        definition = {
          type     = "query_value"
          title    = "Bank Availability"
          requests = [{ q = "avg:keo.external.bank.availability{*}" }]
        }
      },
      {
        definition = {
          type     = "query_value"
          title    = "FX Provider Availability"
          requests = [{ q = "avg:keo.external.fx.availability{*}" }]
        }
      }
    ]
  })
}