# Monitor: Disponibilidade do Provedor de Câmbio (FX)

## Descrição
Monitora a disponibilidade do provedor de câmbio para transações cross-border. Alerta quando cai abaixo de 99.5%.

## Tipo
Metric Alert

## Query
```
avg(last_5m):avg:keo.external.fx.availability{*} < 99.5
```

## Thresholds
- **Warning**: 99.7%
- **Critical**: 99.5%
- **Recovery**: 99.6%

## Tags
```
service:cross-border-fx
team:payments
dependency:fx-provider
severity:high
```

## Notificação
- **Canal**: Slack #alerts-payments
- **On-call**: PagerDuty (SEV-2)
- **Mensagem**: "Disponibilidade do provedor de câmbio abaixo de 99.5%. Verificar conectividade e status do provedor."

## Terraform
```hcl
resource "datadog_monitor" "fx_provider_availability" {
  name    = "FX Provider Availability - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):avg:keo.external.fx.availability{*} < 99.5"
  message = <<-EOT
    Disponibilidade do provedor de câmbio abaixo de 99.5%.
    Verificar conectividade e status do provedor.
    @slack-alerts-payments
    @pagerduty-sre
  EOT

  thresholds = {
    critical = 99.5
    warning  = 99.7
  }

  tags = ["service:cross-border-fx", "team:payments", "dependency:fx-provider", "severity:high"]
}
```