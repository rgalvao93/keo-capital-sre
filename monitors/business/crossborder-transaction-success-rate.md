# Monitor: Transações Cross-border (Taxa de Sucesso)

## Descrição
Monitora a taxa de sucesso de transações cross-border. Alerta quando cai abaixo de 99.0%.

## Tipo
Metric Alert

## Query
```
avg(last_5m):avg:keo.crossborder.transaction.success_rate{*} < 99.0
```

## Thresholds
- **Warning**: 99.2%
- **Critical**: 99.0%
- **Recovery**: 99.3%

## Tags
```
service:cross-border-fx
team:payments
product:cross-border
severity:high
```

## Notificação
- **Canal**: Slack #alerts-payments
- **On-call**: PagerDuty (SEV-2)
- **Mensagem**: "Taxa de sucesso de transações cross-border abaixo de 99.0%. Verificar câmbio e integração Mastercard."

## Runbook
Ver: `runbooks/cross-border-transaction-failure.md` (a criar)

## Terraform
```hcl
resource "datadog_monitor" "crossborder_transaction_success_rate" {
  name    = "Cross-border Transaction Success Rate - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):avg:keo.crossborder.transaction.success_rate{*} < 99.0"
  message = <<-EOT
    Taxa de sucesso de transações cross-border abaixo de 99.0%.
    Verificar câmbio e integração Mastercard.
    @slack-alerts-payments
    @pagerduty-sre
  EOT

  thresholds = {
    critical = 99.0
    warning  = 99.2
  }

  tags = ["service:cross-border-fx", "team:payments", "product:cross-border", "severity:high"]
}
```