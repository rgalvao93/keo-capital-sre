# Monitor: Memory Utilization (P95)

## Descrição
Monitora a utilização de memória (P95) dos hosts/containers. Alerta quando ultrapassa 85%.

## Tipo
Metric Alert

## Query
```
avg(last_5m):p95:system.mem.used{*} / p95:system.mem.total{*} * 100 > 85
```

## Thresholds
- **Warning**: 75%
- **Critical**: 85%
- **Recovery**: 80%

## Tags
```
env:prod
team:sre
severity:medium
```

## Notificação
- **Canal**: Slack #alerts-infra
- **Mensagem**: "Memory utilization P95 acima de 85%. Verificar pods e escalar se necessário."

## Terraform
```hcl
resource "datadog_monitor" "memory_utilization_p95" {
  name    = "Memory Utilization P95 - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):p95:system.mem.used{*} / p95:system.mem.total{*} * 100 > 85"
  message = <<-EOT
    Memory utilization P95 acima de 85%.
    Verificar pods e escalar se necessário.
    @slack-alerts-infra
  EOT

  thresholds = {
    critical = 85
    warning  = 75
  }

  tags = ["env:prod", "team:sre", "severity:medium"]
}
```