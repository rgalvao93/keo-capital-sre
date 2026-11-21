# Monitor: Disponibilidade do Provedor de KYC

## Descrição
Monitora a disponibilidade do provedor de KYC para onboarding de clientes. Alerta quando cai abaixo de 99.0%.

## Tipo
Metric Alert

## Query
```
avg(last_5m):avg:keo.external.kyc.availability{*} < 99.0
```

## Thresholds
- **Warning**: 99.5%
- **Critical**: 99.0%
- **Recovery**: 99.2%

## Tags
```
service:credit-service
team:credit
dependency:kyc-provider
severity:medium
```

## Notificação
- **Canal**: Slack #alerts-credit
- **Mensagem**: "Disponibilidade do provedor de KYC abaixo de 99.0%. Verificar conectividade e status do provedor."

## Terraform
```hcl
resource "datadog_monitor" "kyc_provider_availability" {
  name    = "KYC Provider Availability - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):avg:keo.external.kyc.availability{*} < 99.0"
  message = <<-EOT
    Disponibilidade do provedor de KYC abaixo de 99.0%.
    Verificar conectividade e status do provedor.
    @slack-alerts-credit
  EOT

  thresholds = {
    critical = 99.0
    warning  = 99.5
  }

  tags = ["service:credit-service", "team:credit", "dependency:kyc-provider", "severity:medium"]
}
```