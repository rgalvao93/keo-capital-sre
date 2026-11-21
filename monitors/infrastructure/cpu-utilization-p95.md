# Monitor: CPU Utilization (P95)

## Descrição
Monitora a utilização de CPU (P95) dos hosts/containers. Alerta quando ultrapassa 80%.

## Tipo
Metric Alert

## Query
```
avg(last_5m):p95:system.cpu.user{*} + p95:system.cpu.system{*} > 80
```

## Thresholds
- **Warning**: 70%
- **Critical**: 80%
- **Recovery**: 75%

## Tags
```
env:prod
team:sre
severity:medium
```

## Notificação
- **Canal**: Slack #alerts-infra
- **Mensagem**: "CPU utilization P95 acima de 80%. Verificar pods e escalar se necessário."

## Terraform
```hcl
resource "datadog_monitor" "cpu_utilization_p95" {
  name    = "CPU Utilization P95 - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):p95:system.cpu.user{*} + p95:system.cpu.system{*} > 80"
  message = <<-EOT
    CPU utilization P95 acima de 80%.
    Verificar pods e escalar se necessário.
    @slack-alerts-infra
  EOT

  thresholds = {
    critical = 80
    warning  = 70
  }

  tags = ["env:prod", "team:sre", "severity:medium"]
}
```