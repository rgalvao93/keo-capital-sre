# Monitor: Redis Cache Hit Rate — payment-api

## Descrição
Monitora a taxa de hit do cache Redis do payment-api. Alerta quando cai abaixo de 80%.

## Tipo
Metric Alert

## Query
```
avg(last_5m):avg:trace.redis.command.hits{service:payment-api} / avg:trace.redis.command.total{service:payment-api} < 0.8
```

## Thresholds
- **Warning**: 85%
- **Critical**: 80%
- **Recovery**: 82%

## Tags
```
service:payment-api
team:payments
severity:medium
```

## Notificação
- **Canal**: Slack #alerts-payments
- **Mensagem**: "Cache hit rate do Redis do payment-api abaixo de 80%. Verificar TTL e padrões de acesso."

## Terraform
```hcl
resource "datadog_monitor" "payment_api_redis_hit_rate" {
  name    = "Payment API Redis Cache Hit Rate - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):avg:trace.redis.command.hits{service:payment-api} / avg:trace.redis.command.total{service:payment-api} < 0.8"
  message = <<-EOT
    Cache hit rate do Redis do payment-api abaixo de 80%.
    Verificar TTL e padrões de acesso.
    @slack-alerts-payments
  EOT

  thresholds = {
    critical = 0.8
    warning  = 0.85
  }

  tags = ["service:payment-api", "team:payments", "severity:medium"]
}
```