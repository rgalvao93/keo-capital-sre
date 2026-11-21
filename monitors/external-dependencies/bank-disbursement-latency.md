# Monitor: Latência de Disbursement (P95) — Bancos Parceiros

## Descrição
Monitora a latência P95 de disbursement nos bancos parceiros. Alerta quando ultrapassa 3s.

## Tipo
Metric Alert

## Query
```
avg(last_5m):p95:keo.external.bank.disbursement.latency{*} > 3
```

## Thresholds
- **Warning**: 2.5s
- **Critical**: 3s
- **Recovery**: 2.8s

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
- **Mensagem**: "Latência de disbursement P95 acima de 3s. Verificar bancos parceiros."

## Terraform
```hcl
resource "datadog_monitor" "bank_disbursement_latency" {
  name    = "Bank Disbursement Latency P95 - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):p95:keo.external.bank.disbursement.latency{*} > 3"
  message = <<-EOT
    Latência de disbursement P95 acima de 3s.
    Verificar bancos parceiros.
    @slack-alerts-credit
    @pagerduty-sre
  EOT

  thresholds = {
    critical = 3
    warning  = 2.5
  }

  tags = ["service:working-capital-disbursement", "team:credit", "dependency:bank", "severity:high"]
}
```