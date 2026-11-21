# Monitor: Taxa de Erro por Endpoint — cross-border-fx

## Descrição
Monitora a taxa de erro (5xx) do cross-border-fx. Alerta quando ultrapassa 1%.

## Tipo
Metric Alert

## Query
```
avg(last_5m):sum:trace.http.request.errors{service:cross-border-fx}.as_rate() > 0.01
```

## Thresholds
- **Warning**: 0.5%
- **Critical**: 1%
- **Recovery**: 0.8%

## Tags
```
service:cross-border-fx
team:payments
severity:high
```

## Notificação
- **Canal**: Slack #alerts-payments
- **On-call**: PagerDuty (SEV-2)
- **Mensagem**: "Taxa de erro do cross-border-fx acima de 1%. Verificar logs de erro no Datadog."

## Terraform
```hcl
resource "datadog_monitor" "crossborder_fx_error_rate_app" {
  name    = "Cross-border FX Error Rate - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):sum:trace.http.request.errors{service:cross-border-fx}.as_rate() > 0.01"
  message = <<-EOT
    Taxa de erro do cross-border-fx acima de 1%.
    Verificar logs de erro no Datadog.
    @slack-alerts-payments
    @pagerduty-sre
  EOT

  thresholds = {
    critical = 0.01
    warning  = 0.005
  }

  tags = ["service:cross-border-fx", "team:payments", "severity:high"]
}
```