# Monitor: Taxa de Aprovação de Crédito (Workeo)

## Descrição
Monitora a taxa de aprovação de crédito do Workeo. Alerta quando cai significativamente abaixo do baseline.

## Tipo
Metric Alert

## Query
```
avg(last_1h):avg:keo.credit.approval_rate{product:workeo} < 85
```

## Thresholds
- **Warning**: 90%
- **Critical**: 85%
- **Recovery**: 88%

## Tags
```
service:credit-service
team:credit
product:workeo
severity:high
```

## Notificação
- **Canal**: Slack #alerts-credit
- **On-call**: PagerDuty (SEV-2)
- **Mensagem**: "Taxa de aprovação de crédito Workeo abaixo de 85%. Verificar modelo de scoring e dados de entrada."

## Runbook
Ver: `runbooks/credit-approval-degradation.md` (a criar)

## Terraform
```hcl
resource "datadog_monitor" "workeo_credit_approval_rate" {
  name    = "Workeo Credit Approval Rate - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_1h):avg:keo.credit.approval_rate{product:workeo} < 85"
  message = <<-EOT
    Taxa de aprovação de crédito Workeo abaixo de 85%.
    Verificar modelo de scoring e dados de entrada.
    @slack-alerts-credit
    @pagerduty-sre
  EOT

  thresholds = {
    critical = 85
    warning  = 90
  }

  tags = ["service:credit-service", "team:credit", "product:workeo", "severity:high"]
}
```