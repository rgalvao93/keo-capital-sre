# Monitor: MTTR (Mean Time to Resolution) — SEV-1

## Descrição
Monitora o MTTR de incidentes SEV-1. Alerta quando ultrapassa 30 minutos.

## Tipo
Metric Alert

## Query
```
avg(last_1d):avg:keo.incidents.mttr{severity:sev1} > 1800
```

## Thresholds
- **Warning**: 25 min
- **Critical**: 30 min

## Tags
```
team:sre
severity:high
```

## Notificação
- **Canal**: Slack #alerts-sre
- **Mensagem**: "MTTR de incidentes SEV-1 acima de 30 minutos. Revisar processo de resposta a incidentes."

## Terraform
```hcl
resource "datadog_monitor" "mttr_sev1" {
  name    = "MTTR SEV-1 - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_1d):avg:keo.incidents.mttr{severity:sev1} > 1800"
  message = <<-EOT
    MTTR de incidentes SEV-1 acima de 30 minutos.
    Revisar processo de resposta a incidentes.
    @slack-alerts-sre
  EOT

  thresholds = {
    critical = 1800
    warning  = 1500
  }

  tags = ["team:sre", "severity:high"]
}
```