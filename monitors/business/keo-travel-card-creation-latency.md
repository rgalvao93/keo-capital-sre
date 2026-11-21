# Monitor: Criação de Cartão Virtual (Keo Travel)

## Descrição
Monitora o tempo de criação (P95) de cartões virtuais no Keo Travel. Alerta quando ultrapassa 3s.

## Tipo
Metric Alert

## Query
```
avg(last_5m):p95:keo.travel.card_creation.latency{*} > 3
```

## Thresholds
- **Warning**: 2.5s
- **Critical**: 3s
- **Recovery**: 2.8s

## Tags
```
service:travel-card-service
team:travel
product:keo-travel
severity:high
```

## Notificação
- **Canal**: Slack #alerts-travel
- **On-call**: PagerDuty (SEV-2)
- **Mensagem**: "Tempo de criação de cartão virtual P95 acima de 3s. Verificar integração com Mastercard e infraestrutura."

## Runbook
Ver: `runbooks/travel-card-creation-failure.md` (a criar)

## Terraform
```hcl
resource "datadog_monitor" "keo_travel_card_creation_latency" {
  name    = "Keo Travel Card Creation Latency P95 - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_5m):p95:keo.travel.card_creation.latency{*} > 3"
  message = <<-EOT
    Tempo de criação de cartão virtual P95 acima de 3s.
    Verificar integração com Mastercard e infraestrutura.
    @slack-alerts-travel
    @pagerduty-sre
  EOT

  thresholds = {
    critical = 3
    warning  = 2.5
  }

  tags = ["service:travel-card-service", "team:travel", "product:keo-travel", "severity:high"]
}
```