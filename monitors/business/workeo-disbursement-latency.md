# Monitor: Tempo de Processamento de Working Capital (Workeo)

## Descrição
Monitora o tempo de processamento (P95) de disbursement de working capital no Workeo. Alerta quando ultrapassa 5s.

## Tipo
Metric Alert

## Query
```
avg(last_5m):p95:keo.working_capital.disbursement.latency{*} > 5
```

## Thresholds
- **Warning**: 4s
- **Critical**: 5s
- **Recovery**: 4.5s

## Tags
```
service:working-capital-disbursement
team:credit
product:workeo
severity:high
```

## Notificação
- **Canal**: Slack #alerts-credit
- **On-call**: PagerDuty (SEV-2)
- **Mensagem**: "Tempo de processamento de working capital P95 acima de 5s. Verificar disbursement e bancos parceiros."

## Runbook
Ver: `runbooks/working-capital-processing-delay.md` (a criar)

## Terraform
```hcl
resource "datadog_monitor" "workeo_disbursement_latency" {
  name    = "Workeo Disbursement Latency P95 - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):p95:keo.working_capital.disbursement.latency{*} > 5"
  message = <<-EOT
    Tempo de processamento de working capital P95 acima de 5s.
    Verificar disbursement e bancos parceiros.
    @slack-alerts-credit
    @pagerduty-sre
  EOT

  thresholds = {
    critical = 5
    warning  = 4
  }

  tags = ["service:working-capital-disbursement", "team:credit", "product:workeo", "severity:high"]
}
```