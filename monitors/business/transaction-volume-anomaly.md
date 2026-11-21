# Monitor: Volume de Transações (Anomalia)

## Descrição
Monitora o volume de transações e alerta sobre anomalias (picos ou quedas inesperadas) usando detecção de anomalias do Datadog.

## Tipo
Anomaly Alert

## Query
```
avg(last_1h):anomalies(avg:keo.payments.authorization.volume{*}, 'basic', 2) >= 1
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
- **Mensagem**: "Anomalia detectada no volume de transações. Verificar se há pico de demanda ou falha no processamento."

## Terraform
```hcl
resource "datadog_monitor" "transaction_volume_anomaly" {
  name    = "Transaction Volume Anomaly - Keo Capital"
  type    = "query alert"
  query   = "avg(last_1h):anomalies(avg:keo.payments.authorization.volume{*}, 'basic', 2) >= 1"
  message = <<-EOT
    Anomalia detectada no volume de transações.
    Verificar se há pico de demanda ou falha no processamento.
    @slack-alerts-payments
  EOT

  tags = ["service:payment-api", "team:payments", "severity:medium"]
}
```