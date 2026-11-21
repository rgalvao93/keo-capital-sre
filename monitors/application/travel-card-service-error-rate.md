# Monitor: Taxa de Erro por Endpoint — travel-card-service

## Descrição
Monitora a taxa de erro (5xx) do travel-card-service. Alerta quando ultrapassa 1%.

## Tipo
Metric Alert

## Query
```
avg(last_5m):sum:trace.http.request.errors{service:travel-card-service}.as_rate() > 0.01
```

## Thresholds
- **Warning**: 0.5%
- **Critical**: 1%
- **Recovery**: 0.8%

## Tags
```
service:travel-card-service
team:travel
severity:high
```

## Notificação
- **Canal**: Slack #alerts-travel
- **On-call**: PagerDuty (SEV-2)
- **Mensagem**: "Taxa de erro do travel-card-service acima de 1%. Verificar logs de erro no Datadog."

## Terraform
```hcl
resource "datadog_monitor" "travel_card_service_error_rate" {
  name    = "Travel Card Service Error Rate - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):sum:trace.http.request.errors{service:travel-card-service}.as_rate() > 0.01"
  message = <<-EOT
    Taxa de erro do travel-card-service acima de 1%.
    Verificar logs de erro no Datadog.
    @slack-alerts-travel
    @pagerduty-sre
  EOT

  thresholds = {
    critical = 0.01
    warning  = 0.005
  }

  tags = ["service:travel-card-service", "team:travel", "severity:high"]
}
```