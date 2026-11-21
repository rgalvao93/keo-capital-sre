# Monitor: Alert Fatigue (Taxa de Falsos Positivos)

## Descrição
Monitora a taxa de falsos positivos nos alertas. Alerta quando ultrapassa 10%.

## Tipo
Metric Alert

## Query
```
avg(last_7d):avg:keo.alerts.false_positive_rate{*} > 0.1
```

## Thresholds
- **Warning**: 8%
- **Critical**: 10%

## Tags
```
team:sre
severity:medium
```

## Notificação
- **Canal**: Slack #alerts-sre
- **Mensagem**: "Taxa de falsos positivos acima de 10%. Revisar thresholds dos monitors."

## Terraform
```hcl
resource "datadog_monitor" "alert_fatigue" {
  name    = "Alert Fatigue - False Positive Rate - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_7d):avg:keo.alerts.false_positive_rate{*} > 0.1"
  message = <<-EOT
    Taxa de falsos positivos acima de 10%.
    Revisar thresholds dos monitors.
    @slack-alerts-sre
  EOT

  thresholds = {
    critical = 0.1
    warning  = 0.08
  }

  tags = ["team:sre", "severity:medium"]
}
```