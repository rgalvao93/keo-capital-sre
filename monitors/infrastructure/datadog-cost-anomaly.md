# Monitor: Anomalia de Custo Datadog (FinOps)

## Descrição
Monitora o uso estimado de recursos Datadog para detectar crescimento anômalo de custo (custom metrics, logs ingeridos, hosts).

## Tipo
Metric Alert

## Query
```
avg(last_1d):avg:datadog.estimated_usage.custom_metrics{*} > 100000
```

## Thresholds
- **Warning**: 80.000 custom metrics
- **Critical**: 100.000 custom metrics

## Tags
```
env:prod
team:sre
compliance:finops
severity:medium
```

## Notificação
- **Canal**: Slack #alerts-sre
- **On-call**: PagerDuty (SEV-3)
- **Mensagem**: "Uso estimado de custom metrics acima do esperado. Revisar cardinalidade de métricas e tags."

## Terraform
```hcl
module "datadog_cost_anomaly" {
  source = "./modules/monitor"

  name    = "Datadog Cost Anomaly - Custom Metrics - Keo Capital"
  type    = "metric alert"
  query   = "avg(last_1d):avg:datadog.estimated_usage.custom_metrics{*} > 100000"
  message = "Uso estimado de custom metrics acima do esperado. Revisar cardinalidade de métricas e tags. @slack-alerts-sre"

  threshold_critical = 100000
  threshold_warning  = 80000

  tags = ["env:prod", "team:sre", "compliance:finops", "severity:medium"]
}
```

## Runbook
1. Verificar `datadog.estimated_usage.*` no dashboard de usage.
2. Identificar métricas com alta cardinalidade (`service`, `customerId`, `transactionId` como tag são suspeitos).
3. Remover tags de alta cardinalidade ou agregar métricas.
4. Revisar retenção de logs e volume ingerido.
