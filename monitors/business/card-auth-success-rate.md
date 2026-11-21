# Monitor: Taxa de Sucesso de Autorização de Cartão

## Descrição
Monitora a taxa de sucesso de autorizações de cartão. Alerta quando cai abaixo de 99.5%.

## Tipo
Metric Alert

## Query
```
avg(last_5m):avg:keo.payments.authorization.success_rate{*} < 99.5
```

## Thresholds
- **Warning**: 99.0%
- **Critical**: 99.5%
- **Recovery**: 99.6%

## Tags
```
service:payment-api
team:payments
product:keo-travel,cross-border
severity:critical
```

## Notificação
- **Canal**: Slack #alerts-payments
- **On-call**: PagerDuty (SEV-1)
- **Mensagem**: "Taxa de sucesso de autorização de cartão abaixo de 99.5%. Verificar integração com Mastercard e infraestrutura."

## Runbook
Ver: `runbooks/card-authorization-failure.md`

## Terraform
```hcl
resource "datadog_monitor" "card_auth_success_rate" {
  name    = "Card Authorization Success Rate - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):avg:keo.payments.authorization.success_rate{*} < 99.5"
  message = <<-EOT
    Taxa de sucesso de autorização de cartão abaixo de 99.5%.
    Verificar integração com Mastercard e infraestrutura.
    @slack-alerts-payments
    @pagerduty-sre
  EOT

  thresholds = {
    critical = 99.5
    warning  = 99.0
  }

  tags = ["service:payment-api", "team:payments", "severity:critical"]
}
```