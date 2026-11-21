# Monitor: Latência de Endpoint (P99) — travel-card-service

## Descrição
Monitora a latência P99 de todos os endpoints do travel-card-service. Alerta quando ultrapassa 500ms.

## Tipo
Metric Alert

## Query
```
avg(last_5m):p99:trace.http.request.duration{service:travel-card-service} > 500
```

## Thresholds
- **Warning**: 400ms
- **Critical**: 500ms
- **Recovery**: 450ms

## Tags
```
service:travel-card-service
team:travel
severity:high
```

## Notificação
- **Canal**: Slack #alerts-travel
- **On-call**: PagerDuty (SEV-2)
- **Mensagem**: "Latência P99 do travel-card-service acima de 500ms. Verificar traces no Datadog APM."

## Terraform
```hcl
resource "datadog_monitor" "travel_card_service_latency_p99" {
  name    = "Travel Card Service Latency P99 - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):p99:trace.http.request.duration{service:travel-card-service} > 500"
  message = <<-EOT
    Latência P99 do travel-card-service acima de 500ms.
    Verificar traces no Datadog APM.
    @slack-alerts-travel
    @pagerduty-sre
  EOT

  thresholds = {
    critical = 500
    warning  = 400
  }

  tags = ["service:travel-card-service", "team:travel", "severity:high"]
}
```