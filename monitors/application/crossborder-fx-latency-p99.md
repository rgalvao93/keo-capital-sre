# Monitor: Latência de Endpoint (P99) — cross-border-fx

## Descrição
Monitora a latência P99 de todos os endpoints do cross-border-fx. Alerta quando ultrapassa 1s.

## Tipo
Metric Alert

## Query
```
avg(last_5m):p99:trace.http.request.duration{service:cross-border-fx} > 1000
```

## Thresholds
- **Warning**: 800ms
- **Critical**: 1000ms
- **Recovery**: 900ms

## Tags
```
service:cross-border-fx
team:payments
severity:high
```

## Notificação
- **Canal**: Slack #alerts-payments
- **On-call**: PagerDuty (SEV-2)
- **Mensagem**: "Latência P99 do cross-border-fx acima de 1s. Verificar traces no Datadog APM."

## Terraform
```hcl
resource "datadog_monitor" "crossborder_fx_latency_p99" {
  name    = "Cross-border FX Latency P99 - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):p99:trace.http.request.duration{service:cross-border-fx} > 1000"
  message = <<-EOT
    Latência P99 do cross-border-fx acima de 1s.
    Verificar traces no Datadog APM.
    @slack-alerts-payments
    @pagerduty-sre
  EOT

  thresholds = {
    critical = 1000
    warning  = 800
  }

  tags = ["service:cross-border-fx", "team:payments", "severity:high"]
}
```