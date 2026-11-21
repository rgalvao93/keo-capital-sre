# Monitor: Latência de Autorização de Cartão (P99)

## Descrição
Monitora a latência P99 de autorizações de cartão. Alerta quando ultrapassa 300ms.

## Tipo
Metric Alert

## Query
```
avg(last_5m):p99:keo.payments.authorization.latency{*} > 300
```

## Thresholds
- **Warning**: 250ms
- **Critical**: 300ms
- **Recovery**: 280ms

## Tags
```
service:payment-api
team:payments
product:keo-travel,cross-border
severity:critical
```

## Notificação
- **Canal**: Slack #alerts-payments
- **On-call**: PagerDuty (SEV-2)
- **Mensagem**: "Latência de autorização de cartão P99 acima de 300ms. Verificar integração com Mastercard e infraestrutura."

## Runbook
Ver: `runbooks/card-authorization-failure.md`

## Terraform
```hcl
resource "datadog_monitor" "card_auth_latency_p99" {
  name    = "Card Authorization Latency P99 - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):p99:keo.payments.authorization.latency{*} > 300"
  message = <<-EOT
    Latência de autorização de cartão P99 acima de 300ms.
    Verificar integração com Mastercard e infraestrutura.
    @slack-alerts-payments
    @pagerduty-sre
  EOT

  thresholds = {
    critical = 300
    warning  = 250
  }

  tags = ["service:payment-api", "team:payments", "severity:critical"]
}
```