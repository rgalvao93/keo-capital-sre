# Monitor: Disponibilidade dos Bancos Parceiros (Disbursement)

## Descrição
Monitora a disponibilidade dos bancos parceiros para disbursement de working capital. Alerta quando cai abaixo de 99.5%.

## Tipo
Metric Alert

## Query
```
avg(last_5m):avg:keo.external.bank.availability{*} < 99.5
```

## Thresholds
- **Warning**: 99.7%
- **Critical**: 99.5%
- **Recovery**: 99.6%

## Tags
```
service:working-capital-disbursement
team:credit
dependency:bank
severity:high
```

## Notificação
- **Canal**: Slack #alerts-credit
- **On-call**: PagerDuty (SEV-2)
- **Mensagem**: "Disponibilidade dos bancos parceiros abaixo de 99.5%. Verificar conectividade e status dos bancos."

## Terraform
```hcl
resource "datadog_monitor" "bank_availability" {
  name    = "Bank Partner Availability - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):avg:keo.external.bank.availability{*} < 99.5"
  message = <<-EOT
    Disponibilidade dos bancos parceiros abaixo de 99.5%.
    Verificar conectividade e status dos bancos.
    @slack-alerts-credit
    @pagerduty-sre
  EOT

  thresholds = {
    critical = 99.5
    warning  = 99.7
  }

  tags = ["service:working-capital-disbursement", "team:credit", "dependency:bank", "severity:high"]
}
```