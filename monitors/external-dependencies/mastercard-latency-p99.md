# Monitor: Latência do Mastercard Network (P99)

## Descrição
Monitora a latência P99 do Mastercard Network. Alerta quando ultrapassa 200ms.

## Tipo
Metric Alert

## Query
```
avg(last_5m):p99:keo.external.mastercard.latency{*} > 200
```

## Thresholds
- **Warning**: 150ms
- **Critical**: 200ms
- **Recovery**: 180ms

## Tags
```
service:payment-api
team:payments
dependency:mastercard
severity:high
```

## Notificação
- **Canal**: Slack #alerts-payments
- **On-call**: PagerDuty (SEV-2)
- **Mensagem**: "Latência P99 do Mastercard Network acima de 200ms. Verificar integração e rede."

## Terraform
```hcl
resource "datadog_monitor" "mastercard_latency_p99" {
  name    = "Mastercard Network Latency P99 - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):p99:keo.external.mastercard.latency{*} > 200"
  message = <<-EOT
    Latência P99 do Mastercard Network acima de 200ms.
    Verificar integração e rede.
    @slack-alerts-payments
    @pagerduty-sre
  EOT

  thresholds = {
    critical = 200
    warning  = 150
  }

  tags = ["service:payment-api", "team:payments", "dependency:mastercard", "severity:high"]
}
```