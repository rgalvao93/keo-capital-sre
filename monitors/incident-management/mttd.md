# Monitor: MTTD (Mean Time to Detection)

## Descrição
Monitora o MTTD (tempo entre início do incidente e detecção). Alerta quando ultrapassa 5 minutos.

## Tipo
Metric Alert

## Query
```
avg(last_1d):avg:keo.incidents.mttd{*} > 300
```

## Thresholds
- **Warning**: 4 min
- **Critical**: 5 min

## Tags
```
team:sre
severity:high
```

## Notificação
- **Canal**: Slack #alerts-sre
- **Mensagem**: "MTTD acima de 5 minutos. Melhorar cobertura de monitors e alertas."

## Terraform
```hcl
resource "datadog_monitor" "mttd" {
  name    = "MTTD - Mean Time to Detection - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_1d):avg:keo.incidents.mttd{*} > 300"
  message = <<-EOT
    MTTD acima de 5 minutos.
    Melhorar cobertura de monitors e alertas.
    @slack-alerts-sre
  EOT

  thresholds = {
    critical = 300
    warning  = 240
  }

  tags = ["team:sre", "severity:high"]
}
```