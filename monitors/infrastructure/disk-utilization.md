# Monitor: Disk Utilization

## Descrição
Monitora a utilização de disco dos hosts. Alerta quando ultrapassa 90%.

## Tipo
Metric Alert

## Query
```
avg(last_5m):avg:system.disk.used{*} / avg:system.disk.total{*} * 100 > 90
```

## Thresholds
- **Warning**: 80%
- **Critical**: 90%
- **Recovery**: 85%

## Tags
```
env:prod
team:sre
severity:medium
```

## Notificação
- **Canal**: Slack #alerts-infra
- **Mensagem**: "Disk utilization acima de 90%. Verificar espaço em disco e limpar logs/imagens antigas."

## Terraform
```hcl
resource "datadog_monitor" "disk_utilization" {
  name    = "Disk Utilization - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):avg:system.disk.used{*} / avg:system.disk.total{*} * 100 > 90"
  message = <<-EOT
    Disk utilization acima de 90%.
    Verificar espaço em disco e limpar logs/imagens antigas.
    @slack-alerts-infra
  EOT

  thresholds = {
    critical = 90
    warning  = 80
  }

  tags = ["env:prod", "team:sre", "severity:medium"]
}
```