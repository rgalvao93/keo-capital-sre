# Monitor: Latência de Endpoint (P99) — payment-api

## Descrição
Monitora a latência P99 de todos os endpoints do payment-api. Alerta quando ultrapassa 500ms.

## Tipo
Metric Alert

## Query
```
avg(last_5m):p99:trace.http.request.duration{service:payment-api} > 500
```

## Thresholds
- **Warning**: 400ms
- **Critical**: 500ms
- **Recovery**: 450ms

## Tags
```
service:payment-api
team:payments
severity:high
```

## Notificação
- **Canal**: Slack #alerts-payments
- **On-call**: PagerDuty (SEV-2)
- **Mensagem**: "Latência P99 do payment-api acima de 500ms. Verificar traces no Datadog APM."

## Terraform
```hcl
resource "datadog_monitor" "payment_api_latency_p99" {
  name    = "Payment API Latency P99 - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):p99:trace.http.request.duration{service:payment-api} > 500"
  message = <<-EOT
    Latência P99 do payment-api acima de 500ms.
    Verificar traces no Datadog APM.
    @slack-alerts-payments
    @pagerduty-sre
  EOT

  thresholds = {
    critical = 500
    warning  = 400
  }

  tags = ["service:payment-api", "team:payments", "severity:high"]
}
```