# Convenção de Tags — Keo Capital Datadog

## Tags Obrigatórias

| Tag | Valores | Descrição |
|---|---|---|
| `env` | `prod`, `staging`, `dev` | Ambiente |
| `region` | `latam`, `canada`, `us`, `eu` | Região geográfica |
| `service` | `<nome-do-servico>` | Nome do serviço |
| `team` | `sre`, `payments`, `credit`, `travel` | Time responsável |

## Tags de Negócio

| Tag | Valores | Descrição |
|---|---|---|
| `product` | `workeo`, `keo-travel`, `cross-border`, `supplier-payments` | Produto |
| `severity` | `critical`, `high`, `medium`, `low` | Severidade do alerta |
| `compliance` | `pci-dss`, `lgpd`, `kyc` | Regulação aplicável |

## Exemplos

### Serviço de Pagamentos
```
env:prod
region:latam
service:payment-api
team:payments
product:keo-travel,cross-border
```

### Serviço de Crédito
```
env:prod
region:canada
service:credit-service
team:credit
product:workeo
```

### Infraestrutura
```
env:prod
region:latam
service:kubernetes
team:sre
```

## Naming Convention de Métricas

```
keo.<domínio>.<ação>.<métrica>
```

### Exemplos
```
keo.payments.authorization.success_rate
keo.payments.authorization.latency_p99
keo.credit.approval_rate
keo.travel.card_creation.latency
keo.crossborder.transaction.volume
keo.working_capital.disbursement.latency_p95
keo.external.mastercard.latency_p99
keo.external.mastercard.timeout_rate
```

## Naming Convention de Serviços

```
<domínio>-<função>-<tipo>
```

### Exemplos
```
payment-api
payment-processor
credit-service
credit-scoring
travel-card-service
cross-border-fx
working-capital-disbursement
```