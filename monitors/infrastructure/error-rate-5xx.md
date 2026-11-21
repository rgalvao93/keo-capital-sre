# Monitor: Error Rate (5xx) — Infraestrutura

## Descrição
Monitora a taxa de erro 5xx geral da infraestrutura. Alerta quando ultrapassa 0.1%.

## Tipo
Metric Alert

## Query
```
avg(last_5m):sum:http.requests{status:5xx}.as_rate() / sum:http.requests{*}.as_rate() > 0.001
```

## Thresholds
- **Warning**: 0.05%
- **Critical**: 0.1%
- **Recovery**: 0.08%

## Tags
```
env:prod
team:sre
severity:high
```

## Notificação
- **Canal**: Slack #alerts-infra
- **On-call**: PagerDuty (SEV-2)
- **Mensagem**: "Error rate 5xx acima de 0.1%. Verificar logs de erro e infraestrutura."

## Terraform
```hcl
resource "datadog_monitor" "error_rate_5xx_infra" {
  name    = "Error Rate 5xx - Infrastructure - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):sum:http.requests{status:5xx}.as_rate() / sum:http.requests{*}.as_rate() > 0.001"
  message = <<-EOT
    Error rate 5xx acima de 0.1%.
    Verificar logs de erro e infraestrutura.
    @slack-alerts-infra
    @pagerduty-sre
  EOT

  thresholds = {
    critical = 0.001
    warning  = 0.0005
  }

  tags = ["env:prod", "team:sre", "severity:high"]
}
```