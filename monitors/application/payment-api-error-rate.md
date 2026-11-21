# Monitor: Taxa de Erro por Endpoint — payment-api

## Descrição
Monitora a taxa de erro (5xx) do payment-api. Alerta quando ultrapassa 1%.

## Tipo
Metric Alert

## Query
```
avg(last_5m):sum:trace.http.request.errors{service:payment-api}.as_rate() > 0.01
```

## Thresholds
- **Warning**: 0.5%
- **Critical**: 1%
- **Recovery**: 0.8%

## Tags
```
service:payment-api
team:payments
severity:high
```

## Notificação
- **Canal**: Slack #alerts-payments
- **On-call**: PagerDuty (SEV-2)
- **Mensagem**: "Taxa de erro do payment-api acima de 1%. Verificar logs de erro no Datadog."

## Terraform
```hcl
resource "datadog_monitor" "payment_api_error_rate" {
  name    = "Payment API Error Rate - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):sum:trace.http.request.errors{service:payment-api}.as_rate() > 0.01"
  message = <<-EOT
    Taxa de erro do payment-api acima de 1%.
    Verificar logs de erro no Datadog.
    @slack-alerts-payments
    @pagerduty-sre
  EOT

  thresholds = {
    critical = 0.01
    warning  = 0.005
  }

  tags = ["service:payment-api", "team:payments", "severity:high"]
}
```