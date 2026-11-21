# Monitor: Latência de Endpoint (P99) — credit-service

## Descrição
Monitora a latência P99 de todos os endpoints do credit-service. Alerta quando ultrapassa 1s.

## Tipo
Metric Alert

## Query
```
avg(last_5m):p99:trace.http.request.duration{service:credit-service} > 1000
```

## Thresholds
- **Warning**: 800ms
- **Critical**: 1000ms
- **Recovery**: 900ms

## Tags
```
service:credit-service
team:credit
severity:high
```

## Notificação
- **Canal**: Slack #alerts-credit
- **On-call**: PagerDuty (SEV-2)
- **Mensagem**: "Latência P99 do credit-service acima de 1s. Verificar traces no Datadog APM."

## Terraform
```hcl
resource "datadog_monitor" "credit_service_latency_p99" {
  name    = "Credit Service Latency P99 - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):p99:trace.http.request.duration{service:credit-service} > 1000"
  message = <<-EOT
    Latência P99 do credit-service acima de 1s.
    Verificar traces no Datadog APM.
    @slack-alerts-credit
    @pagerduty-sre
  EOT

  thresholds = {
    critical = 1000
    warning  = 800
  }

  tags = ["service:credit-service", "team:credit", "severity:high"]
}
```