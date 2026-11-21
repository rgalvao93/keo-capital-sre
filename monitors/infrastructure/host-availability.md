# Monitor: Disponibilidade de Hosts/Containers

## Descrição
Monitora a disponibilidade de hosts e containers. Alerta quando um host/container fica offline.

## Tipo
Host Alert

## Query
```
avg(last_5m):avg:system.uptime{*} < 1
```

## Thresholds
- **Critical**: Host offline

## Tags
```
env:prod
team:sre
severity:critical
```

## Notificação
- **Canal**: Slack #alerts-infra
- **On-call**: PagerDuty (SEV-1)
- **Mensagem**: "Host/container offline detectado. Verificar infraestrutura e escalar se necessário."

## Terraform
```hcl
resource "datadog_monitor" "host_availability" {
  name    = "Host/Container Availability - Keo Capital"
  type    = "host"
  query   = "avg(last_5m):avg:system.uptime{*} < 1"
  message = <<-EOT
    Host/container offline detectado.
    Verificar infraestrutura e escalar se necessário.
    @slack-alerts-infra
    @pagerduty-sre
  EOT

  tags = ["env:prod", "team:sre", "severity:critical"]
}
```