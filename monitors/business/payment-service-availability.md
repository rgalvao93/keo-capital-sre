# Monitor: Disponibilidade do Serviço de Pagamentos

## Descrição
Monitora a taxa de sucesso (HTTP 2xx) do serviço de pagamentos. Alerta quando cai abaixo de 99.9%.

## Tipo
Metric Alert

## Query
```
avg(last_5m):avg:keo.payments.service.success_rate{*} < 99.9
```

## Thresholds
- **Warning**: 99.5%
- **Critical**: 99.9%
- **Recovery**: 99.95%

## Tags
```
service:payment-api
team:payments
severity:critical
```

## Notificação
- **Canal**: Slack #alerts-payments
- **On-call**: PagerDuty (SEV-1)
- **Mensagem**: "Disponibilidade do serviço de pagamentos abaixo de 99.9%. Incidente crítico!"

## Runbook
Ver: `runbooks/payment-service-unavailable.md` (a criar)

## Terraform
```hcl
resource "datadog_monitor" "payment_service_availability" {
  name    = "Payment Service Availability - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):avg:keo.payments.service.success_rate{*} < 99.9"
  message = <<-EOT
    Disponibilidade do serviço de pagamentos abaixo de 99.9%.
    Incidente crítico! Verificar infraestrutura e dependências.
    @slack-alerts-payments
    @pagerduty-sre
  EOT

  thresholds = {
    critical = 99.9
    warning  = 99.5
  }

  tags = ["service:payment-api", "team:payments", "severity:critical"]
}
```