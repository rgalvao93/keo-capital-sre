# Monitor: Disponibilidade do Mastercard Network

## Descrição
Monitora a disponibilidade do Mastercard Network. Alerta quando cai abaixo de 99.9%.

## Tipo
Metric Alert

## Query
```
avg(last_5m):avg:keo.external.mastercard.availability{*} < 99.9
```

## Thresholds
- **Warning**: 99.95%
- **Critical**: 99.9%
- **Recovery**: 99.95%

## Tags
```
service:payment-api
team:payments
dependency:mastercard
severity:critical
```

## Notificação
- **Canal**: Slack #alerts-payments
- **On-call**: PagerDuty (SEV-1)
- **Mensagem**: "Disponibilidade do Mastercard Network abaixo de 99.9%. Incidente crítico! Verificar status da rede Mastercard."

## Runbook
Ver: `runbooks/mastercard-network-unavailable.md` (a criar)

## Terraform
```hcl
resource "datadog_monitor" "mastercard_availability" {
  name    = "Mastercard Network Availability - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):avg:keo.external.mastercard.availability{*} < 99.9"
  message = <<-EOT
    Disponibilidade do Mastercard Network abaixo de 99.9%.
    Incidente crítico! Verificar status da rede Mastercard.
    @slack-alerts-payments
    @pagerduty-sre
  EOT

  thresholds = {
    critical = 99.9
    warning  = 99.95
  }

  tags = ["service:payment-api", "team:payments", "dependency:mastercard", "severity:critical"]
}
```