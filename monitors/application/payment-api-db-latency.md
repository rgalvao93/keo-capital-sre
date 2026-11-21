# Monitor: Database Query Latency — payment-api

## Descrição
Monitora a latência P99 de queries PostgreSQL do payment-api. Alerta quando ultrapassa 100ms.

## Tipo
Metric Alert

## Query
```
avg(last_5m):p99:trace.postgres.query.duration{service:payment-api} > 100
```

## Thresholds
- **Warning**: 80ms
- **Critical**: 100ms
- **Recovery**: 90ms

## Tags
```
service:payment-api
team:payments
severity:medium
```

## Notificação
- **Canal**: Slack #alerts-payments
- **Mensagem**: "Latência de queries PostgreSQL do payment-api acima de 100ms. Verificar queries lentas no Datadog DBM."

## Terraform
```hcl
resource "datadog_monitor" "payment_api_db_latency" {
  name    = "Payment API Database Query Latency - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):p99:trace.postgres.query.duration{service:payment-api} > 100"
  message = <<-EOT
    Latência de queries PostgreSQL do payment-api acima de 100ms.
    Verificar queries lentas no Datadog DBM.
    @slack-alerts-payments
  EOT

  thresholds = {
    critical = 100
    warning  = 80
  }

  tags = ["service:payment-api", "team:payments", "severity:medium"]
}
```