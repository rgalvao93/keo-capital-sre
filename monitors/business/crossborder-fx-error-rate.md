# Monitor: Taxa de Erro de Câmbio (Cross-border)

## Descrição
Monitora a taxa de erro de conversão de câmbio em transações cross-border. Alerta quando ultrapassa 0.5%.

## Tipo
Metric Alert

## Query
```
avg(last_5m):avg:keo.crossborder.fx.error_rate{*} > 0.5
```

## Thresholds
- **Warning**: 0.3%
- **Critical**: 0.5%
- **Recovery**: 0.4%

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
- **Mensagem**: "Taxa de erro de câmbio acima de 0.5%. Verificar provedor de câmbio."

## Terraform
```hcl
resource "datadog_monitor" "crossborder_fx_error_rate" {
  name    = "Cross-border FX Error Rate - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):avg:keo.crossborder.fx.error_rate{*} > 0.5"
  message = <<-EOT
    Taxa de erro de câmbio acima de 0.5%.
    Verificar provedor de câmbio.
    @slack-alerts-payments
    @pagerduty-sre
  EOT

  thresholds = {
    critical = 0.5
    warning  = 0.3
  }

  tags = ["service:cross-border-fx", "team:payments", "product:cross-border", "severity:high"]
}
```