# Monitor: Acesso Anômalo a Dados de Cartão (PAN)

## Descrição
Monitora acessos a dados sensíveis de cartão (PAN - Primary Account Number). Alerta quando há padrão anômalo de acesso.

## Tipo
Log Alert

## Query
```
logs("service:payment-api status:info \"PAN accessed\"").rollup("count").last("5m") > 100
```

## Thresholds
- **Warning**: 50 acessos em 5 min
- **Critical**: 100 acessos em 5 min

## Tags
```
service:payment-api
team:sre
compliance:pci-dss
severity:critical
```

## Notificação
- **Canal**: Slack #alerts-security
- **On-call**: PagerDuty (SEV-1)
- **Mensagem**: "Acesso anômalo a dados de cartão (PAN) detectado. Verificar logs de auditoria PCI-DSS."

## Terraform
```hcl
resource "datadog_monitor" "pan_access_anomaly" {
  name    = "PAN Access Anomaly - Keo Capital"
  type    = "log alert"
  query   = "logs(\"service:payment-api status:info \\\"PAN accessed\\\"\").rollup(\"count\").last(\"5m\") > 100"
  message = <<-EOT
    Acesso anômalo a dados de cartão (PAN) detectado.
    Verificar logs de auditoria PCI-DSS.
    @slack-alerts-security
    @pagerduty-sre
  EOT

  thresholds = {
    critical = 100
    warning  = 50
  }

  tags = ["service:payment-api", "team:sre", "compliance:pci-dss", "severity:critical"]
}
```