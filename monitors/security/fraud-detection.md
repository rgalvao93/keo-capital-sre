# Monitor: Fraud Detection — Padrões Suspeitos de Transação

## Descrição
Monitora padrões suspeitos de transação que podem indicar fraude. Alerta quando detecta múltiplas tentativas de autorização falhadas do mesmo cliente em curto período.

## Tipo
Metric Alert

## Query
```
avg(last_5m):avg:keo.fraud.suspicious_transaction_rate{*} > 0.05
```

## Thresholds
- **Warning**: 3%
- **Critical**: 5%
- **Recovery**: 4%

## Tags
```
service:payment-api
team:sre
compliance:pci-dss
severity:critical
```

## Notificação
- **Canal**: Slack #alerts-security
- **On-call**: PagerDuty (SEV-2)
- **Mensagem**: "Padrão suspeito de transação detectado (possível fraude). Verificar logs e bloquear cliente se necessário."

## Terraform
```hcl
resource "datadog_monitor" "fraud_detection" {
  name    = "Fraud Detection - Suspicious Transactions - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):avg:keo.fraud.suspicious_transaction_rate{*} > 0.05"
  message = <<-EOT
    Padrão suspeito de transação detectado (possível fraude).
    Verificar logs e bloquear cliente se necessário.
    @slack-alerts-security
    @pagerduty-sre
  EOT

  thresholds = {
    critical = 0.05
    warning  = 0.03
  }

  tags = ["service:payment-api", "team:sre", "compliance:pci-dss", "severity:critical"]
}
```