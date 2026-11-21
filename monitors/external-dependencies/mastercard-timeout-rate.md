# Monitor: Timeout Rate do Mastercard Network

## Descrição
Monitora a taxa de timeout nas chamadas ao Mastercard Network. Alerta quando ultrapassa 0.1%.

## Tipo
Metric Alert

## Query
```
avg(last_5m):avg:keo.external.mastercard.timeout_rate{*} > 0.1
```

## Thresholds
- **Warning**: 0.05%
- **Critical**: 0.1%
- **Recovery**: 0.08%

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
- **Mensagem**: "Timeout rate do Mastercard Network acima de 0.1%. Verificar rede e integração."

## Terraform
```hcl
resource "datadog_monitor" "mastercard_timeout_rate" {
  name    = "Mastercard Network Timeout Rate - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):avg:keo.external.mastercard.timeout_rate{*} > 0.1"
  message = <<-EOT
    Timeout rate do Mastercard Network acima de 0.1%.
    Verificar rede e integração.
    @slack-alerts-payments
    @pagerduty-sre
  EOT

  thresholds = {
    critical = 0.1
    warning  = 0.05
  }

  tags = ["service:payment-api", "team:payments", "dependency:mastercard", "severity:critical"]
}
```