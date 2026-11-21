# Monitor: Audit Trail — Mudanças em Limites de Crédito

## Descrição
Monitora mudanças em limites de crédito de clientes. Alerta quando há alteração fora do horário comercial ou por usuário não autorizado.

## Tipo
Log Alert

## Query
```
logs("service:credit-service status:info \"credit limit changed\"").rollup("count").last("5m") > 10
```

## Thresholds
- **Warning**: 5 alterações em 5 min
- **Critical**: 10 alterações em 5 min

## Tags
```
service:credit-service
team:credit
compliance:audit
severity:high
```

## Notificação
- **Canal**: Slack #alerts-security
- **On-call**: PagerDuty (SEV-2)
- **Mensagem**: "Mudanças em limites de crédito detectadas. Verificar audit trail e autorização."

## Terraform
```hcl
resource "datadog_monitor" "credit_limit_change_audit" {
  name    = "Credit Limit Change Audit Trail - Keo Capital"
  type    = "log alert"
  query   = "logs(\"service:credit-service status:info \\\"credit limit changed\\\"\").rollup(\"count\").last(\"5m\") > 10"
  message = <<-EOT
    Mudanças em limites de crédito detectadas.
    Verificar audit trail e autorização.
    @slack-alerts-security
    @pagerduty-sre
  EOT

  thresholds = {
    critical = 10
    warning  = 5
  }

  tags = ["service:credit-service", "team:credit", "compliance:audit", "severity:high"]
}
```