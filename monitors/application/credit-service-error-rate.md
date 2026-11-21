# Monitor: Taxa de Erro por Endpoint — credit-service

## Descrição
Monitora a taxa de erro (5xx) do credit-service. Alerta quando ultrapassa 1%.

## Tipo
Metric Alert

## Query
```
avg(last_5m):sum:trace.http.request.errors{service:credit-service}.as_rate() > 0.01
```

## Thresholds
- **Warning**: 0.5%
- **Critical**: 1%
- **Recovery**: 0.8%

## Tags
```
service:credit-service
team:credit
severity:high
```

## Notificação
- **Canal**: Slack #alerts-credit
- **On-call**: PagerDuty (SEV-2)
- **Mensagem**: "Taxa de erro do credit-service acima de 1%. Verificar logs de erro no Datadog."

## Terraform
```hcl
resource "datadog_monitor" "credit_service_error_rate" {
  name    = "Credit Service Error Rate - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):sum:trace.http.request.errors{service:credit-service}.as_rate() > 0.01"
  message = <<-EOT
    Taxa de erro do credit-service acima de 1%.
    Verificar logs de erro no Datadog.
    @slack-alerts-credit
    @pagerduty-sre
  EOT

  thresholds = {
    critical = 0.01
    warning  = 0.005
  }

  tags = ["service:credit-service", "team:credit", "severity:high"]
}
```