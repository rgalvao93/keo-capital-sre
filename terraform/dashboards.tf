# Dashboards — Keo Capital
# All dashboards defined using datadog_dashboard_json

resource "datadog_dashboard_json" "transaction_overview" {
  dashboard = jsonencode({
    title              = "[DEV] Transaction Overview - Keo Capital"
    description        = "Telemetria APM observada em env:dev; inclui tráfego de desenvolvimento e não representa tráfego de produção."
    default_timeframe  = { type = "live", value = 6, unit = "hour" }
    notify_list        = null
    layout_type        = "ordered"
    tags               = ["team:payments"]
    template_variables = null
    widgets = [
      {
        definition = {
          type     = "timeseries"
          title    = "Requisições Payment API"
          requests = [{ q = "sum:trace.servlet.request.hits{service:payment-api,env:dev}" }]
        }
      },
      {
        definition = {
          type     = "query_value"
          title    = "Sucesso de Requisições (%)"
          requests = [{ q = "(1 - sum:trace.servlet.request.errors{service:payment-api,env:dev}.as_count() / sum:trace.servlet.request.hits{service:payment-api,env:dev}.as_count()) * 100" }]
        }
      },
      {
        definition = {
          type     = "timeseries"
          title    = "Latência P99 (ms)"
          requests = [{ q = "p99:trace.servlet.request{service:payment-api,env:dev} * 1000" }]
        }
      }
    ]
  })
}

resource "datadog_dashboard_json" "infrastructure_health" {
  dashboard = jsonencode({
    title              = "[DEV] Infrastructure Health - Keo Capital"
    description        = "Métricas reais do Datadog Agent e erros APM de env:dev; não representam infraestrutura de produção."
    default_timeframe  = { type = "live", value = 6, unit = "hour" }
    notify_list        = null
    layout_type        = "ordered"
    tags               = ["team:sre"]
    template_variables = null
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
          requests = [{ q = "sum:trace.servlet.request.errors{env:dev}.as_count() / sum:trace.servlet.request.hits{env:dev}.as_count() * 100" }]
        }
      }
    ]
  })
}

resource "datadog_dashboard_json" "slo_compliance" {
  dashboard = jsonencode({
    title              = "[DEV] APM Service Reliability - Keo Capital"
    description        = "Indicadores calculados de traces APM em env:dev; são sinais de serviço, não SLOs de produção."
    default_timeframe  = { type = "live", value = 6, unit = "hour" }
    notify_list        = null
    layout_type        = "ordered"
    tags               = ["team:sre"]
    template_variables = null
    widgets = [
      {
        definition = {
          type     = "query_value"
          title    = "Payment API Request Success (%)"
          requests = [{ q = "(1 - sum:trace.servlet.request.errors{service:payment-api,env:dev}.as_count() / sum:trace.servlet.request.hits{service:payment-api,env:dev}.as_count()) * 100" }]
        }
      },
      {
        definition = {
          type     = "query_value"
          title    = "Credit Service Request Success (%)"
          requests = [{ q = "(1 - sum:trace.servlet.request.errors{service:credit-service,env:dev}.as_count() / sum:trace.servlet.request.hits{service:credit-service,env:dev}.as_count()) * 100" }]
        }
      },
      {
        definition = {
          type     = "query_value"
          title    = "Travel Card Service Request Success (%)"
          requests = [{ q = "(1 - sum:trace.servlet.request.errors{service:travel-card-service,env:dev}.as_count() / sum:trace.servlet.request.hits{service:travel-card-service,env:dev}.as_count()) * 100" }]
        }
      },
      {
        definition = {
          type     = "query_value"
          title    = "Cross-border Service Request Success (%)"
          requests = [{ q = "(1 - sum:trace.servlet.request.errors{service:cross-border-fx,env:dev}.as_count() / sum:trace.servlet.request.hits{service:cross-border-fx,env:dev}.as_count()) * 100" }]
        }
      },
      {
        definition = {
          type     = "query_value"
          title    = "Requisições APM (todos os serviços)"
          requests = [{ q = "sum:trace.servlet.request.hits{env:dev}" }]
        }
      }
    ]
  })
}

resource "datadog_dashboard_json" "incident_management" {
  dashboard = jsonencode({
    title              = "[DEV] Incident Signals - Keo Capital"
    description        = "Erros e volume de requisições APM reais em env:dev; não há métricas de incidentes ou MTTR disponíveis."
    default_timeframe  = { type = "live", value = 6, unit = "hour" }
    notify_list        = null
    layout_type        = "ordered"
    tags               = ["team:sre"]
    template_variables = null
    widgets = [
      {
        definition = {
          type     = "timeseries"
          title    = "Erros APM por Serviço"
          requests = [{ q = "sum:trace.servlet.request.errors{env:dev} by {service}" }]
        }
      },
      {
        definition = {
          type     = "timeseries"
          title    = "Requisições APM por Serviço"
          requests = [{ q = "sum:trace.servlet.request.hits{env:dev} by {service}" }]
        }
      },
      {
        definition = {
          type     = "query_value"
          title    = "Taxa Global de Erros APM (%)"
          requests = [{ q = "sum:trace.servlet.request.errors{env:dev}.as_count() / sum:trace.servlet.request.hits{env:dev}.as_count() * 100" }]
        }
      }
    ]
  })
}

resource "datadog_dashboard_json" "workeo" {
  dashboard = jsonencode({
    title              = "[DEV] Workeo - Working Capital - Keo Capital"
    description        = "Telemetria APM real do credit-service em env:dev; não equivale a métricas de negócio ou tráfego de produção."
    default_timeframe  = { type = "live", value = 6, unit = "hour" }
    notify_list        = null
    layout_type        = "ordered"
    tags               = ["team:credit"]
    template_variables = null
    widgets = [
      {
        definition = {
          type     = "query_value"
          title    = "Requisições do Credit Service"
          requests = [{ q = "sum:trace.servlet.request.hits{service:credit-service,env:dev}" }]
        }
      },
      {
        definition = {
          type     = "timeseries"
          title    = "Sucesso de Requisições (%)"
          requests = [{ q = "(1 - sum:trace.servlet.request.errors{service:credit-service,env:dev}.as_count() / sum:trace.servlet.request.hits{service:credit-service,env:dev}.as_count()) * 100" }]
        }
      },
      {
        definition = {
          type     = "timeseries"
          title    = "Latência APM P95 (ms)"
          requests = [{ q = "p95:trace.servlet.request{service:credit-service,env:dev} * 1000" }]
        }
      }
    ]
  })
}

resource "datadog_dashboard_json" "keo_travel" {
  dashboard = jsonencode({
    title              = "[DEV] Keo Travel - Corporate Travel - Keo Capital"
    description        = "Telemetria APM real do travel-card-service em env:dev; não equivale a métricas de negócio ou tráfego de produção."
    default_timeframe  = { type = "live", value = 6, unit = "hour" }
    notify_list        = null
    layout_type        = "ordered"
    tags               = ["team:travel"]
    template_variables = null
    widgets = [
      {
        definition = {
          type     = "timeseries"
          title    = "Requisições do Travel Card Service"
          requests = [{ q = "sum:trace.servlet.request.hits{service:travel-card-service,env:dev}" }]
        }
      },
      {
        definition = {
          type     = "query_value"
          title    = "Sucesso de Requisições (%)"
          requests = [{ q = "(1 - sum:trace.servlet.request.errors{service:travel-card-service,env:dev}.as_count() / sum:trace.servlet.request.hits{service:travel-card-service,env:dev}.as_count()) * 100" }]
        }
      },
      {
        definition = {
          type     = "timeseries"
          title    = "Latência APM P95 (ms)"
          requests = [{ q = "p95:trace.servlet.request{service:travel-card-service,env:dev} * 1000" }]
        }
      }
    ]
  })
}

resource "datadog_dashboard_json" "cross_border" {
  dashboard = jsonencode({
    title              = "[DEV] Cross-border - Keo Capital"
    description        = "Telemetria APM real do cross-border-fx em env:dev; não equivale a métricas de negócio ou tráfego de produção."
    default_timeframe  = { type = "live", value = 6, unit = "hour" }
    notify_list        = null
    layout_type        = "ordered"
    tags               = ["team:payments"]
    template_variables = null
    widgets = [
      {
        definition = {
          type     = "query_value"
          title    = "Sucesso de Requisições (%)"
          requests = [{ q = "(1 - sum:trace.servlet.request.errors{service:cross-border-fx,env:dev}.as_count() / sum:trace.servlet.request.hits{service:cross-border-fx,env:dev}.as_count()) * 100" }]
        }
      },
      {
        definition = {
          type     = "timeseries"
          title    = "Latência APM P95 (ms)"
          requests = [{ q = "p95:trace.servlet.request{service:cross-border-fx,env:dev} * 1000" }]
        }
      },
      {
        definition = {
          type     = "timeseries"
          title    = "Requisições do Cross-border Service"
          requests = [{ q = "sum:trace.servlet.request.hits{service:cross-border-fx,env:dev}" }]
        }
      }
    ]
  })
}

resource "datadog_dashboard_json" "external_dependencies" {
  dashboard = jsonencode({
    title              = "[DEV] Outbound Dependencies - Keo Capital"
    description        = "Chamadas de saída observadas no traffic-simulator e execuções do Synthetic Test; não há métricas reais por provedor externo."
    default_timeframe  = { type = "live", value = 6, unit = "hour" }
    notify_list        = null
    layout_type        = "ordered"
    tags               = ["team:sre"]
    template_variables = null
    widgets = [
      {
        definition = {
          type     = "query_value"
          title    = "Chamadas de Saída (traffic-simulator)"
          requests = [{ q = "sum:trace.netty.client.request.hits{service:traffic-simulator,env:dev}" }]
        }
      },
      {
        definition = {
          type     = "timeseries"
          title    = "Latência P95 de Chamadas de Saída (ms)"
          requests = [{ q = "p95:trace.netty.client.request{service:traffic-simulator,env:dev} * 1000" }]
        }
      },
      {
        definition = {
          type     = "query_value"
          title    = "Execuções do Synthetic Test"
          requests = [{ q = "sum:synthetics.test_runs{test_name:keo_capital_-_payment_api_health}" }]
        }
      }
    ]
  })
}