# Monitor: Throughput Anômalo — payment-api

## Descrição
Monitora o throughput (requests/sec) do payment-api. Alerta quando há queda significativa (possível falha) ou pico anômalo.

## Tipo
Anomaly Alert

## Query
```
avg(last_1h):anomalies(sum:trace.http.request.hits{service:payment-api}.as_rate(), 'basic', 2) >= 1
```

## Thresholds
- **Warning**: 1 desvio padrão
- **Critical**: 2 desvios padrão

## Tags
```
service:payment-api
team:payments
severity:medium
```

## Notificação
- **Canal**: Slack #alerts-payments
- **Mensagem**: "Anomalia detectada no throughput do payment-api. Verificar se há queda de tráfego ou pico inesperado."

## Terraform
```hcl
resource "datadog_monitor" "payment_api_throughput_anomaly" {
  name    = "Payment API Throughput Anomaly - Keo Capital"
  type    = "query alert"
  query   = "avg(last_1h):anomalies(sum:trace.http.request.hits{service:payment-api}.as_rate(), 'basic', 2) >= 1"
  message = <<-EOT
    Anomalia detectada no throughput do payment-api.
    Verificar se há queda de tráfego ou pico inesperado.
    @slack-alerts-payments
  EOT

  tags = ["service:payment-api", "team:payments", "severity:medium"]
}
```