# Monitor: Network Latency Inter-serviço

## Descrição
Monitora a latência de rede entre serviços (via service mesh). Alerta quando a latência P99 ultrapassa 50ms.

## Tipo
Metric Alert

## Query
```
avg(last_5m):p99:trace.http.request.duration{service:service-mesh} > 50
```

## Thresholds
- **Warning**: 30ms
- **Critical**: 50ms
- **Recovery**: 40ms

## Tags
```
env:prod
team:sre
severity:medium
```

## Notificação
- **Canal**: Slack #alerts-infra
- **Mensagem**: "Latência de rede inter-serviço acima de 50ms. Verificar service mesh e rede."

## Terraform
```hcl
resource "datadog_monitor" "network_latency_inter_service" {
  name    = "Network Latency Inter-Service - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):p99:trace.http.request.duration{service:service-mesh} > 50"
  message = <<-EOT
    Latência de rede inter-serviço acima de 50ms.
    Verificar service mesh e rede.
    @slack-alerts-infra
  EOT

  thresholds = {
    critical = 50
    warning  = 30
  }

  tags = ["env:prod", "team:sre", "severity:medium"]
}
```